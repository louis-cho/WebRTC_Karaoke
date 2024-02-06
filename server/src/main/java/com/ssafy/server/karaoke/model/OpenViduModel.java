package com.ssafy.server.karaoke.model;

import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduRole;
import io.openvidu.java.client.Session;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Component
public class OpenViduModel {

    private final OpenVidu openvidu;
    private final Map<String, Session> mapSessions;
    private final Map<String, SessionSetting> mapSessionSettings;
    private final Map<String, Map<String, Boolean>> mapSessionNamesTokens;
    private final Map<String, Boolean> sessionRecordings;
    private final Map<String, Map<String, String>> mapSessionTokenConnectionId;

    public OpenViduModel(@Value("${OPENVIDU_URL}") String openviduUrl, @Value("${OPENVIDU_SECRET}") String openviduSecret) {
        // OpenVidu 객체를 생성하고 초기화합니다.
        openvidu = new OpenVidu(openviduUrl, openviduSecret);
        mapSessions = new ConcurrentHashMap<>();
        mapSessionNamesTokens = new ConcurrentHashMap<>();
        mapSessionSettings = new ConcurrentHashMap<>();
        sessionRecordings = new ConcurrentHashMap<>();
        mapSessionTokenConnectionId = new ConcurrentHashMap<>();
    }

}
