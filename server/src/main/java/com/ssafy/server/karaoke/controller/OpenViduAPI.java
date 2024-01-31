package com.ssafy.server.karaoke.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.openvidu.java.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//@CrossOrigin(origins = "*")
@RestController
public class OpenViduAPI {

    @Value("${OPENVIDU_URL}")
    private String OPENVIDU_URL;

    @Value("${OPENVIDU_SECRET}")
    private String OPENVIDU_SECRET;

    private OpenVidu openvidu;

    // 세션 이름과 OpenVidu 세션 객체를 매핑하는 컬렉션
    private Map<String, Session> mapSessions;
    // 세션 이름과 토큰(내부 맵은 토큰과 연결된 역할을 매핑)을 매핑하는 컬렉션
    private Map<String, Map<String, OpenViduRole>> mapSessionNamesTokens;
    // 세션 이름과 녹화 여부를 매핑하는 컬렉션
    private Map<String, Boolean> sessionRecordings;

    @PostConstruct
    public void init() {
        // OpenVidu 객체를 생성하고 초기화합니다.
        this.openvidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
        this.mapSessions = new ConcurrentHashMap<>();
        this.mapSessionNamesTokens = new ConcurrentHashMap<>();
        this.sessionRecordings = new ConcurrentHashMap<>();
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

    // 세션에서 사용자를 제거하는 엔드포인트
    @RequestMapping(value = "/remove-user", method = RequestMethod.POST)
    public ResponseEntity<JsonObject> removeUser(@RequestBody Map<String, Object> sessionNameToken) throws Exception {

        System.out.println("Removing user | {sessionName, token}=" + sessionNameToken);

        // BODY에서 파라미터를 검색함
        String sessionName = (String) sessionNameToken.get("sessionName");
        String token = (String) sessionNameToken.get("token");

        // 세션이 존재하는 경우
        if (this.mapSessions.get(sessionName) != null && this.mapSessionNamesTokens.get(sessionName) != null) {

            // 토큰이 존재하는 경우
            if (this.mapSessionNamesTokens.get(sessionName).remove(token) != null) {
                // 사용자가 세션을 나감
                if (this.mapSessionNamesTokens.get(sessionName).isEmpty()) {
                    // 마지막 사용자가 나감: 세션을 제거해야 함
                    this.mapSessions.remove(sessionName);
                }
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                // 토큰이 유효하지 않았음
                System.out.println("Problems in the app server: the TOKEN wasn't valid");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } else {
            // 세션이 존재하지 않음
            System.out.println("Problems in the app server: the SESSION does not exist");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 세션을 닫는 엔드포인트
    @RequestMapping(value = "/close-session", method = RequestMethod.DELETE)
    public ResponseEntity<JsonObject> closeSession(@RequestBody Map<String, Object> sessionName) throws Exception {

        System.out.println("Closing session | {sessionName}=" + sessionName);

        // BODY에서 파라미터를 검색함
        String session = (String) sessionName.get("sessionName");

        // 세션이 존재하는 경우
        if (this.mapSessions.get(session) != null && this.mapSessionNamesTokens.get(session) != null) {
            Session s = this.mapSessions.get(session);
            s.close();
            this.mapSessions.remove(session);
            this.mapSessionNamesTokens.remove(session);
            this.sessionRecordings.remove(s.getSessionId());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            // 세션이 존재하지 않음
            System.out.println("Problems in the app server: the SESSION does not exist");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 세션 정보를 가져오는 엔드포인트
    @RequestMapping(value = "/fetch-info", method = RequestMethod.POST)
    public ResponseEntity<JsonObject> fetchInfo(@RequestBody Map<String, Object> sessionName) {
        try {
            System.out.println("Fetching session info | {sessionName}=" + sessionName);

            // BODY에서 파라미터를 검색함
            String session = (String) sessionName.get("sessionName");

            // 세션이 존재하는 경우
            if (this.mapSessions.get(session) != null && this.mapSessionNamesTokens.get(session) != null) {
                Session s = this.mapSessions.get(session);
                boolean changed = s.fetch();
                System.out.println("Any change: " + changed);
                return new ResponseEntity<>(this.sessionToJson(s), HttpStatus.OK);
            } else {
                // 세션이 존재하지 않음
                System.out.println("Problems in the app server: the SESSION does not exist");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            e.printStackTrace();
            return getErrorResponse(e);
        }
    }

    // 모든 세션 정보를 가져오는 엔드포인트
    @RequestMapping(value = "/fetch-all", method = RequestMethod.GET)
    public ResponseEntity<?> fetchAll() {
        try {
            System.out.println("Fetching all session info");
            boolean changed = this.openvidu.fetch();
            System.out.println("Any change: " + changed);
            JsonArray jsonArray = new JsonArray();
            for (Session s : this.openvidu.getActiveSessions()) {
                jsonArray.add(this.sessionToJson(s));
            }
            return new ResponseEntity<>(jsonArray, HttpStatus.OK);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            e.printStackTrace();
            return getErrorResponse(e);
        }
    }

    // 사용자를 강제로 연결 해제하는 엔드포인트
    @RequestMapping(value = "/force-disconnect", method = RequestMethod.DELETE)
    public ResponseEntity<JsonObject> forceDisconnect(@RequestBody Map<String, Object> params) {
        try {
            // BODY에서 파라미터를 검색함
            String session = (String) params.get("sessionName");
            String connectionId = (String) params.get("connectionId");

            // 세션이 존재하는 경우
            if (this.mapSessions.get(session) != null && this.mapSessionNamesTokens.get(session) != null) {
                Session s = this.mapSessions.get(session);
                s.forceDisconnect(connectionId);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                // 세션이 존재하지 않음
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            e.printStackTrace();
            return getErrorResponse(e);
        }
    }

    // 스트림을 강제로 언게제하는 엔드포인트
    @RequestMapping(value = "/force-unpublish", method = RequestMethod.DELETE)
    public ResponseEntity<JsonObject> forceUnpublish(@RequestBody Map<String, Object> params) {
        try {
            // BODY에서 파라미터를 검색함
            String session = (String) params.get("sessionName");
            String streamId = (String) params.get("streamId");

            // 세션이 존재하는 경우
            if (this.mapSessions.get(session) != null && this.mapSessionNamesTokens.get(session) != null) {
                Session s = this.mapSessions.get(session);
                s.forceUnpublish(streamId);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                // 세션이 존재하지 않음
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            e.printStackTrace();
            return getErrorResponse(e);
        }
    }

    // 예외 처리를 위한 메소드: 예외 정보를 JSON 형태로 반환함
    private ResponseEntity<JsonObject> getErrorResponse(Exception e) {
        JsonObject json = new JsonObject();
        json.addProperty("cause", e.getCause().toString());
        json.addProperty("error", e.getMessage());
        json.addProperty("exception", e.getClass().getCanonicalName());
        return new ResponseEntity<>(json, HttpStatus.INTERNAL_SERVER_ERROR);
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
        json.add("defaultRecordingProperties",
                gson.toJsonTree(session.getProperties().defaultRecordingProperties()).getAsJsonObject());
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

    /*******************/
    /** Recording API **/
    /*******************/

    // OpenVidu 서버와 상호 작용하는 녹화 관련 RESTful API를 제공하는 컨트롤러 메소드들

    // 녹화를 시작하는 엔드포인트
    @RequestMapping(value = "/recording/start", method = RequestMethod.POST)
    public ResponseEntity<?> startRecording(@RequestBody Map<String, Object> params) {
        String sessionId = (String) params.get("session");
        Recording.OutputMode outputMode = Recording.OutputMode.valueOf((String) params.get("outputMode"));
        boolean hasAudio = (boolean) params.get("hasAudio");
        boolean hasVideo = (boolean) params.get("hasVideo");

        // RecordingProperties 객체를 빌드하여 녹화 설정을 생성함
        RecordingProperties properties = new RecordingProperties.Builder().outputMode(outputMode).hasAudio(hasAudio)
                .hasVideo(hasVideo).build();

        System.out.println("Starting recording for session " + sessionId + " with properties {outputMode=" + outputMode
                + ", hasAudio=" + hasAudio + ", hasVideo=" + hasVideo + "}");

        try {
            // OpenVidu 서버에서 녹화 시작
            Recording recording = this.openvidu.startRecording(sessionId, properties);
            this.sessionRecordings.put(sessionId, true);
            return new ResponseEntity<>(recording, HttpStatus.OK);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 녹화를 중지하는 엔드포인트
    @RequestMapping(value = "/recording/stop", method = RequestMethod.POST)
    public ResponseEntity<?> stopRecording(@RequestBody Map<String, Object> params) {
        String recordingId = (String) params.get("recording");

        System.out.println("Stoping recording | {recordingId}=" + recordingId);

        try {
            // OpenVidu 서버에서 녹화 중지
            Recording recording = this.openvidu.stopRecording(recordingId);
            this.sessionRecordings.remove(recording.getSessionId());
            return new ResponseEntity<>(recording, HttpStatus.OK);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 녹화를 삭제하는 엔드포인트
    @RequestMapping(value = "/recording/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteRecording(@RequestBody Map<String, Object> params) {
        String recordingId = (String) params.get("recording");

        System.out.println("Deleting recording | {recordingId}=" + recordingId);

        try {
            // OpenVidu 서버에서 녹화 삭제
            this.openvidu.deleteRecording(recordingId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 특정 녹화 정보를 가져오는 엔드포인트
    @RequestMapping(value = "/recording/get/{recordingId}", method = RequestMethod.GET)
    public ResponseEntity<?> getRecording(@PathVariable(value = "recordingId") String recordingId) {

        System.out.println("Getting recording | {recordingId}=" + recordingId);

        try {
            // OpenVidu 서버에서 특정 녹화 정보를 가져옴
            Recording recording = this.openvidu.getRecording(recordingId);
            return new ResponseEntity<>(recording, HttpStatus.OK);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 모든 녹화 정보를 가져오는 엔드포인트
    @RequestMapping(value = "/recording/list", method = RequestMethod.GET)
    public ResponseEntity<?> listRecordings() {

        System.out.println("Listing recordings");

        try {
            // OpenVidu 서버에서 모든 녹화 정보를 가져옴
            List<Recording> recordings = this.openvidu.listRecordings();

            return new ResponseEntity<>(recordings, HttpStatus.OK);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}