package com.ssafy.server.song.controller;

import com.ssafy.server.auth.model.dto.Token;
import com.ssafy.server.auth.util.JwtUtil;
import com.ssafy.server.song.model.entity.Song;
import com.ssafy.server.song.model.entity.SongInfo;
import com.ssafy.server.song.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/login")
    public ResponseEntity<String> getToken() {
        int userPk = 001;
        Token token = jwtUtil.generateToken(userPk);
        HttpHeaders responseHeader = new HttpHeaders();
        System.out.println("TTTESTTT");

        return ResponseEntity.ok("");
    }

//    @GetMapping("/songInfo/{songId}")
//    public ResponseEntity<SongInfo> getSongInfoById(@PathVariable int songId) {
//
//        return ResponseEntity.ok((songInfo));
//    }

//    @GetMapping("/refresh")
//     public ResponseEntity<String> refreshAuth(HttpServletRequest request) {
//         HttpHeaders responseHeader = new HttpHeaders();
//         String token = request.getHeader(TokenKey.REFRESH.getKey());
//
//         if (token != null && tokenProvider.validateToken(token) == JwtCode.ACCESS) {
//             String email = tokenProvider.getUid(token);
//             Token newToken = tokenProvider.generateToken(email, Role.USER.getKey());
//
//             responseHeader.set(TokenKey.ACCESS.getKey(), newToken.getAccessToken());
//             responseHeader.set(TokenKey.REFRESH.getKey(), newToken.getRefreshToken());
//             responseHeader.setContentType(MediaType.APPLICATION_JSON);
//
//             return ResponseEntity.ok()
//                     .headers(responseHeader)
//                     .body("new Token");
//         }
//         throw new RuntimeException();
//     }
}
