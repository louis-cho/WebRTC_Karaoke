package com.ssafy.server.user.service;

import com.ssafy.server.user.document.UserDocument;
import com.ssafy.server.user.model.*;
import com.ssafy.server.user.repository.*;
import com.ssafy.server.user.secure.RSA_2048;
import com.ssafy.server.user.util.RSAKeyManager;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.unit.Fuzziness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.spec.RSAPublicKeySpec;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.fuzzyQuery;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    private UserElasticsearchRepository userElasticsearchRepository;
    private RSAKeyManager keyManager = RSAKeyManager.getInstnace();
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private UserPkMappingRepository userPkMappingRepository;

    @Autowired
    private UserKeyMappingRepository userKeyMappingRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String[] getPublicKey(String ip) {
        if(keyManager.getPublicKey(ip) == null) {
            keyManager.createKeyPair(ip);
        }

        RSAPublicKeySpec publicSpec = keyManager.getPublicKeySpace(ip);
        String modulus = publicSpec.getModulus().toString(16);
        String exponent = publicSpec.getPublicExponent().toString(16);

        return new String[] {modulus, exponent};
    }

    @Override
    public String getPrivateKey(String ip) throws Exception {
        if(keyManager.getPrivateKey(ip) == null)
            return null;

        String privateKey = RSA_2048.keyToString(keyManager.getPrivateKey(ip));
        return privateKey;
    }

    @Override
    public UUID validatePassword(String id, String pw, String ip)  {
        // String id로부터 int 형태의 pk 가져오기
        Optional<UserPkMapping> optionalData = userPkMappingRepository.findById(id);

        if (optionalData.isPresent()) {
            UserPkMapping data = optionalData.get();
            int userPk = data.getUserPk();
            // 사용자 ID로부터 데이터베이스에서 사용자 정보를 가져옵니다.
            UserAuth userAuth = userAuthRepository.findById(userPk).orElse(null);

            if(userAuth != null) {
                // 사용자 정보가 존재하고
                String privateKey = RSA_2048.keyToString(keyManager.getPrivateKey(ip));
                String decryptedPw = RSA_2048.decrypt(pw, privateKey);
                if(checkPassword(userAuth.getUserPassword(), decryptedPw)) { // 비밀번호가 일치하는 경우
                    Optional<UserKeyMapping> result = userKeyMappingRepository.findByUserPk(userPk);
                    if(result.isPresent()) {
                        return result.get().getUuid();
                    }
                  }
            }
        } else {
            // Optional에 값이 없는 경우, 원하는 로직을 수행하거나 에러 처리를 수행할 수 있음
            throw new RuntimeException("User not found for ID: " + id);
        }

        return null;
    }




    private boolean checkPassword(String storedPassword, String inputPassword) {
        // BCryptPasswordEncoder를 사용하여 비밀번호를 비교합니다.
        return passwordEncoder.matches(inputPassword, storedPassword);
    }


    @Override
    public UserAuth createUserAuth(String id, String rawPassword, String ip) throws Exception {

        String privateKey = RSA_2048.keyToString(keyManager.getPrivateKey(ip));
        String password = RSA_2048.decrypt(rawPassword, privateKey);

        // 비밀번호를 해싱하여 저장
        String hashedPassword = passwordEncoder.encode(password);

        // 생성된 해시된 비밀번호를 사용하여 사용자 엔터티를 생성하고 저장
        UserAuth userAuth = new UserAuth(id, hashedPassword);
        userAuthRepository.save(userAuth);



        return userAuth;
    }

    @Override
    public User getUser(int userPk) throws Exception {
       return userRepository.findByUserPk(userPk);
    }

    @Override
    public User getUserfromUUID(String uuid) throws Exception {
        return userRepository.findByUserPk(getUserPk(UUID.fromString(uuid)));
    }

    @Override
    public User createUser(UserAuth userAuth, String nickname) throws Exception {

        int userPk = userAuth.getUserPk();

        // userRepository 및 userPkMapping 테이블에도 저장하기
        User user = new User();
        UserDocument userDocument = new UserDocument();

        user.setUserPk(userPk);
        userDocument.setUserPk(userPk);

        user.setUserPk(userAuth.getUserPk());
        user.setUserKey(UUID.randomUUID());
        user.setNickname(nickname);
        userDocument.setNickname(nickname);
        userRepository.save(user);

        UserPkMapping uUserId = new UserPkMapping();
        uUserId.setUserId(userAuth.getUserId());
        uUserId.setUserPk(user.getUserPk());
        userPkMappingRepository.save(uUserId);

        UserKeyMapping uUUID = new UserKeyMapping();
        uUUID.setUserPk(user.getUserPk());
        uUUID.setUuid(user.getUserKey());
        userKeyMappingRepository.save(uUUID);

        userElasticsearchRepository.save(userDocument);

        return user;
    }

    @Override
    public UserAuth getUserAuth(String id, String pw, String ip) throws Exception {

        return null;
    }

    @Override
    public boolean saveUser(User user)  {
        if(userRepository.save(user) != null)
            return true;
        return false;
    }

    /**
     * 시간 없어서 hard delete
     * @param userPk
     */
    @Override
    public void deleteUser(int userPk) {
        userRepository.deleteById(userPk);
        userAuthRepository.deleteById(userPk);
    }

    @Override
    public void updateUser(User user) {
        System.out.println("업데이트요청들어옴!!");
        User fetchedUser = userRepository.getReferenceById(user.getUserPk());
        if(user.getNickname() != null)
            fetchedUser.setNickname(user.getNickname());
        if(user.getIntroduction() != null)
            fetchedUser.setIntroduction(user.getIntroduction());
        if(user.getProfileImgUrl() != null)
            fetchedUser.setProfileImgUrl(user.getProfileImgUrl());
        userRepository.save(fetchedUser);
    }

    /**
     * 유저 UUID로부터 user primary key를 반환한다.
     * @param uuid UUID
     * @return user pk
     */
    @Override
    public int getUserPk(UUID uuid) {
        Optional<UserKeyMapping> optionalData = userKeyMappingRepository.findByUuid(uuid);

        if (optionalData.isPresent()) {
            UserKeyMapping data = optionalData.get();
            return data.getUserPk();
        } else {
            throw new RuntimeException("User not found for UUID: " + uuid);
        }
    }

    /**
     * 유저 키(로그인 시 입력하는 아이디)로 부터
     * UUID를 반환한다.
     * @param userId 로그인 시 입력하는 user id
     * @return UUID
     */
    @Override
    public UUID getUUID(String userId) {
        // userId로부터 userPk를 가져오는 예제 코드
        UserPkMapping result = userPkMappingRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found for userId: " + userId));
        int userPk = result.getUserPk();

        // userPk로부터 UUID를 가져오는 예제 코드
        UserKeyMapping userKeyMapping = userKeyMappingRepository.findByUserPk(userPk)
                .orElseThrow(() -> new RuntimeException("UUID not found for userPk: " + userPk));

        return userKeyMapping.getUuid();
    }

    @Override
    public List<UserDocument> searchUsersByNickname(String nickname) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(fuzzyQuery("nickname", nickname).fuzziness(Fuzziness.AUTO)) // 조절 가능한 fuzziness 값
                .build();

        SearchHits<UserDocument> searchHits = elasticsearchRestTemplate.search(searchQuery, UserDocument.class);
        return searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

    @Override
    public String getUserNickname(int userPk) throws Exception {
        return getUser(userPk).getNickname();
    }

    @Override
    public UUID getUUIDByUserPk(Integer userPk) {
        // userPk로부터 UUID를 가져오는 예제 코드
        UserKeyMapping userKeyMapping = userKeyMappingRepository.findByUserPk(userPk)
                .orElseThrow(() -> new RuntimeException("UUID not found for userPk: " + userPk));
        return userKeyMapping.getUuid();
    }
}
