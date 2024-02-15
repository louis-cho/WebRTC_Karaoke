package com.ssafy.server.karaoke.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ssafy.server.karaoke.model.OpenViduModel;
import com.ssafy.server.karaoke.model.SessionSetting;
import com.ssafy.server.song.model.ReserveModel;
import io.openvidu.java.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/karaoke/sessions")
public class SessionController {

    private final OpenViduModel openViduModel;
    private final ReserveModel reserveModel;

    @Autowired
    public SessionController(OpenViduModel openViduModel, ReserveModel reserveModel) {
        this.openViduModel = openViduModel;
        this.reserveModel = reserveModel;
    }

    @RequestMapping(value = "/createSession", method = RequestMethod.POST)
    public ResponseEntity<String> createSession(@RequestBody Map<String, Object> params) {
        String sessionName = (String) params.get("sessionName");

        if (openViduModel.getMapSessions().containsKey(sessionName)) {
            return ResponseEntity.status(500).body("이미 존재하는 세션입니다.");
        }

        // SessionProperties를 설정해준다
        SessionProperties sessionProperties = new SessionProperties.Builder().customSessionId(sessionName).build();

        try {
            Session session = openViduModel.getOpenvidu().createSession(sessionProperties);
            SessionSetting setting;

            String manager = (String) params.get("userName");
            int numberOfParticipants = Integer.parseInt((String) params.get("numberOfParticipants"));
            boolean isPrivate = (boolean) params.get("isPrivate");
            if (isPrivate) {
                String password = (String) params.get("password");
                setting = new SessionSetting(manager, numberOfParticipants, isPrivate, password);
            } else {
                setting = new SessionSetting(manager, numberOfParticipants, isPrivate);
            }

            // 세션을 컬렉션에 저장함
            openViduModel.getMapSessions().put(sessionName, session);
            openViduModel.getMapSessionSettings().put(sessionName, setting);
            openViduModel.getMapSessionNamesTokens().put(sessionName, new ConcurrentHashMap<>());
            openViduModel.getMapSessionTokenConnectionId().put(sessionName, new ConcurrentHashMap<>());
            reserveModel.getMapSongReserveDeq().put(sessionName, new ArrayDeque<>());

            return ResponseEntity.ok(sessionName);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("생성 중 오류 발생");
        }
    }

    // 세션에 대한 토큰을 얻는 메소드
    @RequestMapping(value = "/getToken", method = RequestMethod.POST)
    public ResponseEntity<String> getToken(@RequestBody Map<String, Object> params) {
        String sessionName = (String) params.remove("sessionName");
        boolean isModerator = false;

        if (!openViduModel.getMapSessions().containsKey(sessionName)) {
            return ResponseEntity.status(500).body("존재하지 않는 세션입니다.");
        }

        // 세션에 아무도 없으면 방장 설정
        if (openViduModel.getMapSessionNamesTokens().get(sessionName).isEmpty()) {
            isModerator = true;
        }

        // ConnectionProperties를 설정해준다
        ConnectionProperties connectionProperties = ConnectionProperties.fromJson(params).build();

        try {
            Connection connection = openViduModel.getMapSessions().get(sessionName).createConnection(connectionProperties);
            String token = connection.getToken();
            String connectionId = connection.getConnectionId();

            // 새로운 토큰을 저장하는 컬렉션을 업데이트함
            openViduModel.getMapSessionNamesTokens().get(sessionName).put(token, isModerator);
            openViduModel.getMapSessionTokenConnectionId().get(sessionName).put(token, connectionId);

            return ResponseEntity.ok(token);
        } catch (OpenViduJavaClientException e1) {
            return ResponseEntity.status(500).body("토큰 발행 중 오류 발생");
        } catch (OpenViduHttpException e2) {
            if (404 == e2.getStatus()) {
                // 유효하지 않은 세션 ID(사용자가 예기치 않게 나감). 세션 객체는 더 이상 유효하지 않음.
                // 컬렉션을 정리하고 새 세션으로 계속 진행함
                openViduModel.getMapSessions().remove(sessionName);
                openViduModel.getMapSessionNamesTokens().remove(sessionName);
                openViduModel.getMapSessionTokenConnectionId().remove(sessionName);
                return ResponseEntity.status(500).body("만료된 세션입니다.");
            }
        }
        return ResponseEntity.status(500).body("입장 중 오류 발생");
    }

    // 세션에서 사용자를 제거하는 엔드포인트
    @RequestMapping(value = "/removeToken", method = RequestMethod.POST)
    public ResponseEntity<String> removeUser(@RequestBody Map<String, Object> params) throws Exception {
        // BODY에서 파라미터를 검색함
        String sessionName = (String) params.get("sessionName");
        String token = (String) params.get("token");

        // 세션이 존재하는 경우
        if (openViduModel.getMapSessions().get(sessionName) != null && openViduModel.getMapSessionNamesTokens().get(sessionName) != null) {
            // 토큰이 존재하는 경우
            if (openViduModel.getMapSessionNamesTokens().get(sessionName).remove(token) != null) {
                Session s = openViduModel.getMapSessions().get(sessionName);
                String connectionId = openViduModel.getMapSessionTokenConnectionId().get(sessionName).remove(token);
                s.forceDisconnect(connectionId);

                // 모든 유저가 나간 경우
                if (openViduModel.getMapSessionNamesTokens().get(sessionName).isEmpty()) {
                    openViduModel.getMapSessions().remove(sessionName);
                    openViduModel.getMapSessionSettings().remove(sessionName);
                    openViduModel.getSessionRecordings().remove(s.getSessionId());
                    openViduModel.getMapSessionTokenConnectionId().remove(sessionName);
                    reserveModel.getMapSongReserveDeq().remove(sessionName);
                    return ResponseEntity.ok("세션을 제거했습니다.");
                }
                return ResponseEntity.ok("토큰을 제거했습니다.");
            } else {
                return ResponseEntity.status(500).body("토큰이 유효하지 않습니다.");
            }
        } else {
            return ResponseEntity.status(500).body("존재하지 않는 세션입니다.");
        }
    }

    // 세션을 강제로 닫는 메소드(방장)
    @RequestMapping(value = "/closeSession", method = RequestMethod.POST)
    public ResponseEntity<String> closeSession(@RequestBody Map<String, Object> params) throws Exception {
        // BODY에서 파라미터를 검색함
        String sessionName = (String) params.get("sessionName");
        String token = (String) params.get("token");

        if (!openViduModel.getMapSessionNamesTokens().get(sessionName).get(token)) {
            return ResponseEntity.status(500).body("방장이 아닙니다.");
        }

        // 세션이 존재하는 경우
        if (openViduModel.getMapSessions().get(sessionName) != null && openViduModel.getMapSessionNamesTokens().get(sessionName) != null) {
            Session s = openViduModel.getMapSessions().get(sessionName);
            s.close();
            openViduModel.getMapSessions().remove(sessionName);
            openViduModel.getMapSessionSettings().remove(sessionName);
            openViduModel.getMapSessionNamesTokens().remove(sessionName);
            openViduModel.getSessionRecordings().remove(s.getSessionId());
            openViduModel.getMapSessionTokenConnectionId().remove(sessionName);
            reserveModel.getMapSongReserveDeq().remove(sessionName);
            return ResponseEntity.ok("세션을 제거했습니다.");
        } else {
            return ResponseEntity.status(500).body("존재하지 않는 세션입니다.");
        }
    }

    // 세션 정보를 수정하는 메소드(방장)
    @RequestMapping(value = "/updateSession", method = RequestMethod.POST)
    public ResponseEntity<String> updateSession(@RequestBody Map<String, Object> params) {
        String sessionName = (String) params.get("sessionName");

        if (!openViduModel.getMapSessions().containsKey(sessionName)) {
            return ResponseEntity.status(500).body("존재하지 않는 세션입니다.");
        }
        if(!openViduModel.getMapSessionSettings().containsKey(sessionName)) {
            return ResponseEntity.status(500).body("존재하지 않는 세션입니다.");
        }

        try {
            SessionSetting setting;

            String manager = (String) params.get("userName");
            if(!openViduModel.getMapSessionSettings().get(sessionName).getManager().equals(manager)){
                return ResponseEntity.status(500).body("방장이 아닙니다.");
            }

            int numberOfParticipants = Integer.parseInt((String) params.get("numberOfParticipants"));
            boolean isPrivate = (boolean) params.get("isPrivate");
            if (isPrivate) {
                String password = (String) params.get("password");
                setting = new SessionSetting(manager, numberOfParticipants, isPrivate, password);
            } else {
                setting = new SessionSetting(manager, numberOfParticipants, isPrivate);
            }

            // 세션을 컬렉션에 저장함
            openViduModel.getMapSessionSettings().put(sessionName, setting);

            return ResponseEntity.ok("변경되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("수정 중 오류 발생");
        }
    }

    // 사용자를 강퇴하는 메소드(방장)
    @RequestMapping(value = "/kickUser", method = RequestMethod.POST)
    public ResponseEntity<String> kickUser(@RequestBody Map<String, Object> params) {
        try {
            String sessionName = (String) params.get("sessionName");
            String reqUser = (String) params.get("reqUser");
            String connectionId = (String) params.get("connectionId");

            // 요청한 유저가 방장이 아니면 요청 수행하지 않음
            if (!openViduModel.getMapSessionNamesTokens().get(sessionName).get(reqUser)) {
                return ResponseEntity.status(500).body("방장이 아닙니다.");
            }

            // 세션이 존재하는 경우
            if (openViduModel.getMapSessions().get(sessionName) != null && openViduModel.getMapSessionNamesTokens().get(sessionName) != null) {
                Session s = openViduModel.getMapSessions().get(sessionName);
                s.forceDisconnect(connectionId);
                return ResponseEntity.ok("강퇴 성공");
            } else {
                return ResponseEntity.status(500).body("존재하지 않는 세션입니다.");
            }
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            return ResponseEntity.status(500).body("강퇴 중 오류 발생");
        }
    }

    @RequestMapping(value = "/checkRecording", method = RequestMethod.POST)
    public ResponseEntity<Boolean> checkRecording(@RequestBody Map<String, Object> params) {
        String sessionName = (String) params.get("sessionName");

        if (openViduModel.getMapSessions().containsKey(sessionName)) {
            Session session = openViduModel.getMapSessions().get(sessionName);

            if (session.isBeingRecorded()) {
                return ResponseEntity.ok(true);
            }
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(null);
    }

    @RequestMapping(value = "/checkNumber", method = RequestMethod.POST)
    public ResponseEntity<Boolean> checkNumber(@RequestBody Map<String, Object> params) {
        String sessionName = (String) params.get("sessionName");

        if (openViduModel.getMapSessionSettings().containsKey(sessionName)) {
            int numberOfParticipants = openViduModel.getMapSessionSettings().get(sessionName).getNumberOfParticipants();
            int numberOfElements = openViduModel.getMapSessions().get(sessionName).getConnections().size();

            // 현재 인원이 전체 인원 이상이면 접속 불가능
            if (numberOfElements >= numberOfParticipants) {
                return ResponseEntity.ok(false);
            }
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }

    @RequestMapping(value = "/checkPrivate", method = RequestMethod.POST)
    public ResponseEntity<Boolean> checkPrivate(@RequestBody Map<String, Object> params) {
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

    // 오픈비두 서버에서 모든 세션 정보를 가져오는 메소드
    // 백엔드 서버에서 세션 정보 가져오는 메소드로 수정 예정
    @RequestMapping(value = "/sessionList", method = RequestMethod.GET)
    public ResponseEntity<?> fetchAll() {
        JsonArray jsonArray = new JsonArray();
        for (Session s : openViduModel.getMapSessions().values()) {
            JsonObject jsonObject = this.sessionToJson(s);

            // 세션 설정 추가
            if (openViduModel.getMapSessionSettings().containsKey(s.getSessionId())) {
                SessionSetting sessionSetting = openViduModel.getMapSessionSettings().get(s.getSessionId());

                jsonObject.addProperty("manager", sessionSetting.getManager());
                jsonObject.addProperty("numberOfParticipants", sessionSetting.getNumberOfParticipants());
                jsonObject.addProperty("isPrivate", sessionSetting.isPrivate());
            }

            jsonArray.add(jsonObject);
        }
        return new ResponseEntity<>(jsonArray.toString(), HttpStatus.OK);
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
