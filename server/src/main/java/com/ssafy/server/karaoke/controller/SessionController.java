package com.ssafy.server.karaoke.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ssafy.server.karaoke.model.OpenViduModel;
import io.openvidu.java.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/karaoke/sessions")
public class SessionController {

    private final OpenViduModel openViduModel;

    @Autowired
    public SessionController(@Value("${OPENVIDU_URL}") String openviduUrl, @Value("${OPENVIDU_SECRET}") String openviduSecret, OpenViduModel openViduModel) {
        this.openViduModel = openViduModel;
    }

    // 사용자의 세션에 대한 토큰을 가져오는 엔드포인트
    @RequestMapping(value = "/getToken", method = RequestMethod.POST)
    public ResponseEntity<String> getToken(@RequestBody Map<String, Object> params) {

        System.out.println("Getting sessionId and token | {sessionName}=" + params);

        // 연결할 비디오 콜("TUTORIAL")의 세션 이름
        String sessionName = (String) params.get("sessionName");

        // 이 사용자에 대한 역할
        OpenViduRole role = OpenViduRole.PUBLISHER;

        // 서버 데이터와 역할을 사용하여 connectionProperties 객체를 빌드함
        ConnectionProperties connectionProperties = new ConnectionProperties.Builder().type(ConnectionType.WEBRTC).role(role).data("user_data").build();

        JsonObject responseJson = new JsonObject();

        if (openViduModel.getMapSessions().get(sessionName) != null) {
            // 이미 세션이 존재하는 경우
            System.out.println("존재하는 세션 : " + sessionName);
            try {

                // 최근에 생성된 connectionProperties를 사용하여 새로운 토큰을 생성함
                String token = openViduModel.getMapSessions().get(sessionName).createConnection(connectionProperties).getToken();

                // 새로운 토큰을 저장하는 컬렉션을 업데이트함
                openViduModel.getMapSessionNamesTokens().get(sessionName).put(token, role);

                // 클라이언트에 응답을 반환함
                return ResponseEntity.ok(token);

            } catch (OpenViduJavaClientException e1) {
                // 내부 오류가 발생하면 오류 메시지를 생성하고 클라이언트에 반환함
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (OpenViduHttpException e2) {
                if (404 == e2.getStatus()) {
                    // 유효하지 않은 세션 ID(사용자가 예기치 않게 나감). 세션 객체는 더 이상 유효하지 않음.
                    // 컬렉션을 정리하고 새 세션으로 계속 진행함
                    openViduModel.getMapSessions().remove(sessionName);
                    openViduModel.getMapSessionNamesTokens().remove(sessionName);
                }
            }
        }

        // 새로운 세션인 경우
        System.out.println("새로운 세션 : " + sessionName);
        try {
            // 새로운 OpenVidu 세션을 생성함
            Session session = openViduModel.getOpenvidu().createSession();
            // 최근에 생성된 connectionProperties를 사용하여 새로운 토큰을 생성함
            String token = session.createConnection(connectionProperties).getToken();

            // 세션과 토큰을 컬렉션에 저장함
            openViduModel.getMapSessions().put(sessionName, session);
            openViduModel.getMapSessionNamesTokens().put(sessionName, new ConcurrentHashMap<>());
            openViduModel.getMapSessionNamesTokens().get(sessionName).put(token, role);

            // 클라이언트에 응답을 반환함
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            // 오류가 발생하면 오류 메시지를 생성하고 클라이언트에 반환함
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 세션에서 사용자를 제거하는 엔드포인트
    @RequestMapping(value = "/removeToken", method = RequestMethod.POST)
    public ResponseEntity<JsonObject> removeUser(@RequestBody Map<String, Object> params) throws Exception {

        System.out.println("Removing user | {sessionName, token}=" + params);

        // BODY에서 파라미터를 검색함
        String sessionName = (String) params.get("sessionName");
        String token = (String) params.get("token");

        // 세션이 존재하는 경우
        if (openViduModel.getMapSessions().get(sessionName) != null && openViduModel.getMapSessionNamesTokens().get(sessionName) != null) {

            // 토큰이 존재하는 경우
            if (openViduModel.getMapSessionNamesTokens().get(sessionName).remove(token) != null) {
                // 사용자가 세션을 나감
                if (openViduModel.getMapSessionNamesTokens().get(sessionName).isEmpty()) {
                    // 마지막 사용자가 나감: 세션을 제거해야 함
                    Session s = openViduModel.getMapSessions().get(sessionName);
                    s.close();
                    openViduModel.getMapSessions().remove(sessionName);
                    openViduModel.getSessionRecordings().remove(s.getSessionId());
                    System.out.println("세션을 제거했습니다.");
                }
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                // 토큰이 유효하지 않았음
                System.out.println("토큰이 유효하지 않습니다.");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } else {
            // 세션이 존재하지 않음
            System.out.println("세션이 존재하지 않습니다.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 세션을 닫는 엔드포인트(방장)
    @RequestMapping(value = "/closeSession", method = RequestMethod.POST)
    public ResponseEntity<JsonObject> closeSession(@RequestBody Map<String, Object> params) throws Exception {

        System.out.println("Closing session | {sessionName}=" + params);

        // BODY에서 파라미터를 검색함
        String sessionName = (String) params.get("sessionName");

        // 세션이 존재하는 경우
        if (openViduModel.getMapSessions().get(sessionName) != null && openViduModel.getMapSessionNamesTokens().get(sessionName) != null) {
            Session s = openViduModel.getMapSessions().get(sessionName);
            s.close();
            openViduModel.getMapSessions().remove(sessionName);
            openViduModel.getMapSessionNamesTokens().remove(sessionName);
            openViduModel.getSessionRecordings().remove(s.getSessionId());
            System.out.println("세션을 제거했습니다.");
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            // 세션이 존재하지 않음
            System.out.println("세션이 존재하지 않습니다.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 모든 세션 정보를 가져오는 엔드포인트
    @RequestMapping(value = "/sessionList", method = RequestMethod.GET)
    public ResponseEntity<?> fetchAll() {
        try {
            System.out.println("Fetching all session info");
            boolean changed = openViduModel.getOpenvidu().fetch();
            System.out.println("Any change: " + changed);
            JsonArray jsonArray = new JsonArray();
            for (Session s : openViduModel.getOpenvidu().getActiveSessions()) {
                jsonArray.add(this.sessionToJson(s));
            }
            return new ResponseEntity<>(jsonArray.toString(), HttpStatus.OK);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 세션 정보를 가져오는 엔드포인트
    @RequestMapping(value = "/sessionInfo", method = RequestMethod.POST)
    public ResponseEntity<String> fetchInfo(@RequestBody Map<String, Object> params) {
        try {
            System.out.println("Fetching session info | {sessionName}=" + params);

            // BODY에서 파라미터를 검색함
            String sessionName = (String) params.get("sessionName");

            // 세션이 존재하는 경우
            if (openViduModel.getMapSessions().get(sessionName) != null && openViduModel.getMapSessionNamesTokens().get(sessionName) != null) {
                Session s = openViduModel.getMapSessions().get(sessionName);
                boolean changed = s.fetch();
                System.out.println("Any change: " + changed);
                return ResponseEntity.ok(this.sessionToJson(s).toString());
            } else {
                // 세션이 존재하지 않음
                System.out.println("세션이 존재하지 않습니다.");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // OpenVidu 세션 정보를 JSON 형태로 변환하는 메소드
    protected JsonObject sessionToJson(Session session) {
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("sessionId", session.getSessionId());
        json.addProperty("customSessionId", session.getProperties().customSessionId());
        json.addProperty("recording", session.isBeingRecorded());
        json.addProperty("mediaMode", session.getProperties().mediaMode().name());
        json.addProperty("recordingMode", session.getProperties().recordingMode().name());
        json.add("defaultRecordingProperties", gson.toJsonTree(session.getProperties().defaultRecordingProperties()).getAsJsonObject());
        JsonObject connections = new JsonObject();
        connections.addProperty("numberOfElements", session.getConnections().size());
        JsonArray jsonArrayConnections = new JsonArray();
        session.getConnections().forEach(con -> {
            JsonObject c = new JsonObject();
            c.addProperty("connectionId", con.getConnectionId());
            c.addProperty("role", con.getRole().name());
            c.addProperty("token", con.getToken());
            c.addProperty("clientData", con.getClientData());
            c.addProperty("serverData", con.getServerData());
            JsonArray pubs = new JsonArray();
            con.getPublishers().forEach(p -> {
                pubs.add(gson.toJsonTree(p).getAsJsonObject());
            });
            JsonArray subs = new JsonArray();
            con.getSubscribers().forEach(s -> {
                subs.add(s);
            });
            c.add("publishers", pubs);
            c.add("subscribers", subs);
            jsonArrayConnections.add(c);
        });
        connections.add("content", jsonArrayConnections);
        json.add("connections", connections);
        return json;
    }

}
