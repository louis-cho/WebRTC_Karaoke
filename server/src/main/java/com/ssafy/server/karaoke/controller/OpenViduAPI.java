package com.ssafy.server.karaoke.controller;

import com.ssafy.server.karaoke.model.OpenViduModel;
import io.openvidu.java.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class OpenViduAPI {

    private final OpenViduModel openViduModel;

    @Autowired
    public OpenViduAPI(@Value("${OPENVIDU_URL}") String openviduUrl, @Value("${OPENVIDU_SECRET}") String openviduSecret, OpenViduModel openViduModel) {
        this.openViduModel = openViduModel;
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
        Session session = openViduModel.getOpenvidu().createSession(properties);
        System.out.println("세션 생성 완료. session :"+session);
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
        System.out.println("지정된 세션에 새로운 연결을 생성해볼까요. sessionId : "+sessionId);
        // 지정된 세션 ID를 사용하여 활성 세션을 가져옵니다.
        Session session = openViduModel.getOpenvidu().getActiveSession(sessionId);
        // 만약 세션이 존재하지 않으면 NOT_FOUND 상태 코드를 반환합니다.
        if (session == null) {
            System.out.println("세션이 없어요~!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // 전달받은 파라미터를 사용하여 연결 속성을 생성합니다.
        ConnectionProperties properties = ConnectionProperties.fromJson(params).build();
        // 세션 객체를 사용하여 연결을 생성하고 해당 연결에 대한 토큰을 반환합니다.
        Connection connection = session.createConnection(properties);
        System.out.println("연결 생성 완료. connection :"+connection);
        return new ResponseEntity<>(connection.getToken(), HttpStatus.OK);
    }
}