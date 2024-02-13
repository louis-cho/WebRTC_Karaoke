package com.ssafy.server.user.service;


import com.ssafy.server.user.document.UserDocument;
import com.ssafy.server.user.model.User;
import com.ssafy.server.user.model.UserAuth;

import java.security.PublicKey;
import java.util.List;
import java.util.UUID;

public interface UserService {


    String[] getPublicKey(String ip) throws Exception;


    String getPrivateKey(String ip) throws Exception;


    UUID validatePassword(String id, String pw, String ip);

    UserAuth createUserAuth(String id, String pw, String ip) throws Exception;

    User getUserfromUUID(String uuid) throws Exception;

    User getUser(int userPk) throws Exception;

    User createUser(UserAuth userAuth,String nickname) throws Exception;

    UserAuth getUserAuth(String id, String pw, String ip) throws Exception;

    boolean saveUser(User user);

    void deleteUser(int userPk);

    void updateUser(User user);

    int getUserPk(UUID uuid);

    UUID getUUID(String userId);

    List<UserDocument> searchUsersByNickname(String nickname);

    String getUserNickname(int userPk) throws Exception;

    UUID getUUIDByUserPk(Integer userPk);
}
