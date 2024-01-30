package com.ssafy.server.karaoke.controller;

import io.openvidu.java.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Map;

//@CrossOrigin(origins = "*")
@RestController
public class OpenViduAPI {

    @Value("${OPENVIDU_URL}")
    private String OPENVIDU_URL;

    @Value("${OPENVIDU_SECRET}")
    private String OPENVIDU_SECRET;

    private OpenVidu openvidu;

    @PostConstruct
    public void init() {
        // OpenVidu 객체를 생성하고 초기화합니다.
        this.openvidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
    }

    /**
     * 새로운 OpenVidu 세션을 생성합니다.
     *
     * @param params 세션 속성
     * @return 생성된 세션의 ID를 반환합니다.
     */
    @PostMapping("/api/sessions")
    public ResponseEntity<String> initializeSession(@RequestBody(required = false) Map<String, Object> params)
            throws OpenViduJavaClientException, OpenViduHttpException {
        // 전달받은 파라미터를 사용하여 세션 속성을 생성합니다.
        SessionProperties properties = SessionProperties.fromJson(params).build();
        // OpenVidu 객체를 사용하여 세션을 생성합니다.
        Session session = openvidu.createSession(properties);
        // 생성된 세션의 ID를 반환합니다.
        return new ResponseEntity<>(session.getSessionId(), HttpStatus.OK);
    }

    /**
     * 지정된 세션에 새로운 연결을 생성합니다.
     *
     * @param sessionId 연결을 생성할 세션의 ID
     * @param params    연결 속성
     * @return 생성된 연결에 대한 토큰을 반환합니다.
     */
    @PostMapping("/api/sessions/{sessionId}/connections")
    public ResponseEntity<String> createConnection(@PathVariable("sessionId") String sessionId,
                                                   @RequestBody(required = false) Map<String, Object> params)
            throws OpenViduJavaClientException, OpenViduHttpException {
        // 지정된 세션 ID를 사용하여 활성 세션을 가져옵니다.
        Session session = openvidu.getActiveSession(sessionId);
        // 만약 세션이 존재하지 않으면 NOT_FOUND 상태 코드를 반환합니다.
        if (session == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // 전달받은 파라미터를 사용하여 연결 속성을 생성합니다.
        ConnectionProperties properties = ConnectionProperties.fromJson(params).build();
        // 세션 객체를 사용하여 연결을 생성하고 해당 연결에 대한 토큰을 반환합니다.
        Connection connection = session.createConnection(properties);
        return new ResponseEntity<>(connection.getToken(), HttpStatus.OK);
    }

}