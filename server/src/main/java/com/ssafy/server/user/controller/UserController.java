package com.ssafy.server.user.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ssafy.server.user.model.UserAuth;
import com.ssafy.server.user.secure.RSA_2048;
import com.ssafy.server.user.service.UserService;
import com.ssafy.server.user.util.RSAKeyManager;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.StringWriter;
import java.security.PublicKey;
import java.util.Base64;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> login(HttpServletRequest servletRequest,  @RequestBody JsonNode request) throws Exception{

        String type = request.get("type").asText();

        switch(type) {
            case "getPublicKey": {
                String ip = servletRequest.getRemoteAddr();
                String[] publicKey = userService.getPublicKey(ip);

                ObjectNode jsonResponse = JsonNodeFactory.instance.objectNode();

                jsonResponse.put("modulus", publicKey[0]);
                jsonResponse.put("exponent", publicKey[1]);
                return ResponseEntity.ok(jsonResponse.toString());
            }

            case "login": {
                String id = request.get("id").asText();
                String pw = request.get("pw").asText();

                // 암호화된 pw를 해석하고 bencrypt와 결과가 맞나 확인하기

                String ip = servletRequest.getRemoteAddr();
                String privateKey = RSA_2048.keyToString(RSAKeyManager.getInstnace().getPrivateKey(ip));

                // 암호 복호화
                String decrypted = RSA_2048.decrypt(request.get("pw").asText(), privateKey);

                // DB 저장 데이터 비교하기

                // DB 저장 데이터와 일치하는 경우 토큰 발급

                // DB 저장 데이터와 일치하지 않는 경우 로그인 실패
            }

            default: {
                throw new IllegalArgumentException("Invalid request type");
            }
        }
    }
    // register
    @PostMapping("/register")
    public ResponseEntity<String> register(HttpServletRequest servletRequest,  @RequestBody JsonNode request) throws Exception{

        String type = request.get("type").asText();

        switch(type) {
                case "register": {
                String ip = servletRequest.getRemoteAddr();

                // JsonNode로 응답 생성
                ObjectNode jsonResponse = JsonNodeFactory.instance.objectNode();

                String id = request.get("id").asText();
                String pw = request.get("pw").asText();

               UserAuth userAuth = userService.createUser(id, pw, ip);

                if(userAuth != null) {
                    // JsonNode를 String으로 변환하여 반환
                    // 추후 자체 발행 토큰을 넣어줄 것
                    return ResponseEntity.ok(jsonResponse.toString());
                } else {
                    log.error("유저가 생성되지 않았습니다");
                    return ResponseEntity.status(400).body(jsonResponse.toString());
                }
            }

            default: {
                throw new IllegalArgumentException("Invalid request type");
            }
        }
    }
}
