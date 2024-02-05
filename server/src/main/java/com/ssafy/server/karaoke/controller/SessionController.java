package com.ssafy.server.karaoke.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ssafy.server.karaoke.model.OpenViduModel;
import com.ssafy.server.karaoke.model.SessionSetting;
import io.openvidu.java.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/karaoke/sessions")
public class SessionController {

    private final OpenViduModel openViduModel;

    @Autowired
    public SessionController(OpenViduModel openViduModel) {
        this.openViduModel = openViduModel;
    }

    @RequestMapping(value = "/createSession", method = RequestMethod.POST)
    public ResponseEntity<String> createSession(@RequestBody Map<String, Object> params) {
        System.out.println("Create Session : " + params);

        String sessionName = (String) params.get("sessionName");

        // 이미 존재하는 세션 예외처리 필요함
        if (openViduModel.getMapSessions().containsKey(sessionName)) {
            return ResponseEntity.ok("이미 존재하는 세션입니다.");
        }

        // SessionProperties를 설정해준다
        SessionProperties sessionProperties = new SessionProperties.Builder().customSessionId(sessionName).build();

        try {
            Session session = openViduModel.getOpenvidu().createSession(sessionProperties);
            SessionSetting setting;

            int numberOfParticipants = (int) params.get("numberOfParticipants");
            boolean isPublic = (boolean) params.get("isPublic");
            if (isPublic) {
                String password = (String) params.get("password");
                setting = new SessionSetting(numberOfParticipants, isPublic, password);
            } else {
                setting = new SessionSetting(numberOfParticipants, isPublic);
            }

            // 세션을 컬렉션에 저장함
            openViduModel.getMapSessions().put(sessionName, session);
            openViduModel.getMapSessionSettings().put(sessionName, setting);
            openViduModel.getMapSessionNamesTokens().put(sessionName, new ConcurrentHashMap<>());

            return ResponseEntity.ok(sessionName);
        } catch (Exception e) {
            // 오류가 발생하면 오류 메시지를 생성하고 클라이언트에 반환함
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 세션에 대한 토큰을 얻는 메소드
    @RequestMapping(value = "/getToken", method = RequestMethod.POST)
    public ResponseEntity<String> getToken(@RequestBody Map<String, Object> params) {
        System.out.println("Get Token : " + params);

        String sessionName = (String) params.remove("sessionName");
        String isModerator = "{\"isModerator\": false}";

        if (!openViduModel.getMapSessions().containsKey(sessionName)) {
            System.out.println("세션 존재 x");
            return ResponseEntity.ok("세션이 존재하지 않습니다.");
        }

        // 세션에 아무도 없으면 방장 설정
        if (openViduModel.getMapSessionNamesTokens().get(sessionName).isEmpty()) {
            isModerator = "{\"isModerator\": true}";
        }
        params.put("isModerator", isModerator);

        // ConnectionProperties를 설정해준다
        ConnectionProperties connectionProperties = ConnectionProperties.fromJson(params).build();
        OpenViduRole role = OpenViduRole.PUBLISHER;

        try {
            String token = openViduModel.getMapSessions().get(sessionName).createConnection(connectionProperties).getToken();
            System.out.println(token);

            // 새로운 토큰을 저장하는 컬렉션을 업데이트함
            openViduModel.getMapSessionNamesTokens().get(sessionName).put(token, role);

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
                return ResponseEntity.ok("세션이 만료되었습니다.");
            }
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
                    openViduModel.getMapSessionSettings().remove(sessionName);
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
            openViduModel.getMapSessionSettings().remove(sessionName);
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

    @RequestMapping(value = "/checkPrivate", method = RequestMethod.POST)
    public ResponseEntity<Boolean> checkPrivate(@RequestBody Map<String, Object> params) {
        System.out.println("Check Password : " + params);

        String sessionName = (String) params.get("sessionName");

        if (openViduModel.getMapSessionSettings().containsKey(sessionName)) {
            boolean isPrivate = openViduModel.getMapSessionSettings().get(sessionName).isPrivate();
            if (isPrivate) {
                return ResponseEntity.ok(true);
            }
        }

        return ResponseEntity.ok(false);
    }

    @RequestMapping(value = "/checkPassword", method = RequestMethod.POST)
    public ResponseEntity<Boolean> checkPassword(@RequestBody Map<String, Object> params) {
        System.out.println("Check Password : " + params);

        String sessionName = (String) params.get("sessionName");
        String password = (String) params.get("password");

        if (openViduModel.getMapSessionSettings().containsKey(sessionName)) {
            String answer = openViduModel.getMapSessionSettings().get(sessionName).getPassword();
            if (Objects.equals(answer, password)) {
                return ResponseEntity.ok(true);
            }
        }

        return ResponseEntity.ok(false);
    }

    // 모든 세션 정보를 가져오는 엔드포인트
    @RequestMapping(value = "/sessionList", method = RequestMethod.GET)
    public ResponseEntity<?> fetchAll() {
        try {
            System.out.println("Session List 호출!");
            boolean changed = openViduModel.getOpenvidu().fetch();
            System.out.println("변경사항있음: " + changed);
            JsonArray jsonArray = new JsonArray();
            for (Session s : openViduModel.getOpenvidu().getActiveSessions()) {
                JsonObject jsonObject = this.sessionToJson(s);

                // 세션 설정 추가
                if (openViduModel.getMapSessionSettings().containsKey(s.getSessionId())) {
                    SessionSetting sessionSetting = openViduModel.getMapSessionSettings().get(s.getSessionId());

                    jsonObject.addProperty("numberOfParticipants", sessionSetting.getNumberOfParticipants());
                    jsonObject.addProperty("isPrivate", sessionSetting.isPrivate());
                }

                jsonArray.add(jsonObject);
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
