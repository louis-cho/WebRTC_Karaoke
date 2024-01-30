package com.ssafy.server.user.service;


import com.ssafy.server.user.model.User;
import com.ssafy.server.user.model.UserAuth;

import java.security.PublicKey;
import java.util.UUID;

public interface UserService {


    String[] getPublicKey(String ip) throws Exception;


    String getPrivateKey(String ip) throws Exception;


    UUID validatePassword(String id, String pw, String ip);

    UserAuth createUserAuth(String id, String pw, String ip) throws Exception;

    User createUser(UserAuth userAuth) throws Exception;

    UserAuth getUserAuth(String id, String pw, String ip) throws Exception;

    boolean saveUser(User user);

    int getUserPk(UUID uuid);

    UUID getUUID(String userId);



    //
}
