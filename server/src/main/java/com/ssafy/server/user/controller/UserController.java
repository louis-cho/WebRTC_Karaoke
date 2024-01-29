package com.ssafy.server.user.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ssafy.server.user.document.UserDocument;
import com.ssafy.server.user.model.User;
import com.ssafy.server.user.model.UserAuth;
import com.ssafy.server.user.secure.RSA_2048;
import com.ssafy.server.user.service.UserService;
import com.ssafy.server.user.util.RSAKeyManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

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

                // 로그인 시 사용한 user id로 부터 int user id 반환하기

                // JsonNode로 응답 생성
                ObjectNode jsonResponse = JsonNodeFactory.instance.objectNode();

                // DB 저장 데이터 비교하기
                UUID uuid = userService.validatePassword(id, pw, ip);
                if(uuid != null) {
                    System.out.println("uuid 찾음 >> " + uuid);
                    // DB 저장 데이터와 일치하는 경우 토큰 발급
                    jsonResponse.put("uuid", uuid.toString());
                    return ResponseEntity.ok(jsonResponse.toString());
                    // DB 저장 데이터와 일치하지 않는 경우 로그인 실패
                } else {
                    System.out.println("uuid 없음");
                    jsonResponse.put("msg", "fail to login");
                    return ResponseEntity.status(400).body(jsonResponse.toString());
                }
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

                /*
                String nickname = request.get("nickname").asText();
                String profile_img_url = request.get("profile_img_url").asText();
                String introduction = request.get("introduction").asText();
                */

                try {
                    UserAuth userAuth = userService.createUserAuth(id, pw, ip);
                    User user = userService.createUser(userAuth);

                    if(userAuth != null && user != null) {
                        // JsonNode를 String으로 변환하여 반환
                        // 추후 자체 발행 토큰을 넣어줄 것
                        return ResponseEntity.ok(jsonResponse.toString());
                    } else {
                        log.error("유저가 생성되지 않았습니다");
                        return ResponseEntity.status(400).body(jsonResponse.toString());
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    System.out.println("유저 생성 중 에러 발생");

                    return ResponseEntity.status(400).body(jsonResponse.toString());
                }
            }

            default: {
                throw new IllegalArgumentException("Invalid request type");
            }
        }
    }


    @GetMapping("/search/{nickname}")
    public ResponseEntity<List<UserDocument>> searchUsersByNickname(@PathVariable String nickname) {
        List<UserDocument> users = userService.searchUsersByNickname(nickname);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
