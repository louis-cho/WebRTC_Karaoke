package com.ssafy.server.user.service;


import com.ssafy.server.user.model.User;
import com.ssafy.server.user.model.UserAuth;

import java.security.PublicKey;

public interface UserService {


    String[] getPublicKey(String ip) throws Exception;


    String getPrivateKey(String ip) throws Exception;


    boolean validatePassword(Integer id, String pw);

    UserAuth createUser(String id, String pw, String ip) throws Exception;
}
