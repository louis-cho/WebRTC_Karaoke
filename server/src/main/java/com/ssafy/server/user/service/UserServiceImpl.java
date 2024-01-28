package com.ssafy.server.user.service;

import com.ssafy.server.user.model.UserAuth;
import com.ssafy.server.user.repository.UserAuthRepository;
import com.ssafy.server.user.repository.UserRepository;
import com.ssafy.server.user.secure.RSA_2048;
import com.ssafy.server.user.util.RSAKeyManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private RSAKeyManager keyManager = RSAKeyManager.getInstnace();
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAuthRepository userAuthRepository;

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
    public boolean validatePassword(Integer id, String pw)  {
        // 사용자 ID로부터 데이터베이스에서 사용자 정보를 가져옵니다.
        UserAuth userAuth = userAuthRepository.findById(id).orElse(null);

        if(userAuth != null) {                                  // 사용자 정보가 존재하고
            if(checkPassword(userAuth.getUserPassword(), pw)) { // 비밀번호가 일치하는 경우
                return true;                                    // true를 반환
            }
        }

        return false;
    }

    private boolean checkPassword(String storedPassword, String inputPassword) {
        // BCryptPasswordEncoder를 사용하여 비밀번호를 비교합니다.
        return passwordEncoder.matches(inputPassword, storedPassword);
    }

    // 실제로는 패스워드를 해싱하고 저장하는 로직이 들어가야 합니다.
    public UserAuth createUser(String id, String rawPassword, String ip) {

        String privateKey = RSA_2048.keyToString(keyManager.getPrivateKey(ip));
        String password = RSA_2048.decrypt(rawPassword, privateKey);

        // 비밀번호를 해싱하여 저장
        String hashedPassword = passwordEncoder.encode(password);

        // 생성된 해시된 비밀번호를 사용하여 사용자 엔터티를 생성하고 저장
        UserAuth userAuth = new UserAuth(id, hashedPassword);
        userAuthRepository.save(userAuth);

        return userAuth;
    }
}
