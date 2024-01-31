package com.ssafy.server.karaoke.controller;

import com.ssafy.server.karaoke.model.OpenViduModel;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Recording;
import io.openvidu.java.client.RecordingProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/karaoke/recording")
public class RecordingController {
    private final OpenViduModel openViduModel;

    @Autowired
    public RecordingController(@Value("${OPENVIDU_URL}") String openviduUrl, @Value("${OPENVIDU_SECRET}") String openviduSecret, OpenViduModel openViduModel) {
        this.openViduModel = openViduModel;
    }

    // OpenVidu 서버와 상호 작용하는 녹화 관련 RESTful API를 제공하는 컨트롤러 메소드들

    // 녹화를 시작하는 엔드포인트
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public ResponseEntity<?> startRecording(@RequestBody Map<String, Object> params) {
        String sessionId = (String) params.get("session");
        Recording.OutputMode outputMode = Recording.OutputMode.valueOf((String) params.get("outputMode"));
        boolean hasAudio = (boolean) params.get("hasAudio");
        boolean hasVideo = (boolean) params.get("hasVideo");

        // RecordingProperties 객체를 빌드하여 녹화 설정을 생성함
        RecordingProperties properties = new RecordingProperties.Builder().outputMode(outputMode).hasAudio(hasAudio).hasVideo(hasVideo).build();

        System.out.println("Starting recording for session " + sessionId + " with properties {outputMode=" + outputMode + ", hasAudio=" + hasAudio + ", hasVideo=" + hasVideo + "}");

        try {
            // OpenVidu 서버에서 녹화 시작
            Recording recording = openViduModel.getOpenvidu().startRecording(sessionId, properties);
            openViduModel.getSessionRecordings().put(sessionId, true);
            return new ResponseEntity<>(recording, HttpStatus.OK);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 녹화를 중지하는 엔드포인트
    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    public ResponseEntity<?> stopRecording(@RequestBody Map<String, Object> params) {
        String recordingId = (String) params.get("recording");

        System.out.println("Stoping recording | {recordingId}=" + recordingId);

        try {
            // OpenVidu 서버에서 녹화 중지
            Recording recording = openViduModel.getOpenvidu().stopRecording(recordingId);
            openViduModel.getSessionRecordings().remove(recording.getSessionId());
            return new ResponseEntity<>(recording, HttpStatus.OK);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 녹화를 삭제하는 엔드포인트
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntity<?> deleteRecording(@RequestBody Map<String, Object> params) {
        String recordingId = (String) params.get("recording");

        System.out.println("Deleting recording | {recordingId}=" + recordingId);

        try {
            // OpenVidu 서버에서 녹화 삭제
            openViduModel.getOpenvidu().deleteRecording(recordingId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 특정 녹화 정보를 가져오는 엔드포인트
    @RequestMapping(value = "/get/{recordingId}", method = RequestMethod.GET)
    public ResponseEntity<?> getRecording(@PathVariable(value = "recordingId") String recordingId) {

        System.out.println("Getting recording | {recordingId}=" + recordingId);

        try {
            // OpenVidu 서버에서 특정 녹화 정보를 가져옴
            Recording recording = openViduModel.getOpenvidu().getRecording(recordingId);
            return new ResponseEntity<>(recording, HttpStatus.OK);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 모든 녹화 정보를 가져오는 엔드포인트
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<?> listRecordings() {

        System.out.println("Listing recordings");

        try {
            // OpenVidu 서버에서 모든 녹화 정보를 가져옴
            List<Recording> recordings = openViduModel.getOpenvidu().listRecordings();

            return new ResponseEntity<>(recordings, HttpStatus.OK);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
