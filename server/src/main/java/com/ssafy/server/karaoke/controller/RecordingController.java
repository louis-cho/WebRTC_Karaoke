package com.ssafy.server.karaoke.controller;

import com.ssafy.server.karaoke.model.OpenViduModel;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Recording;
import io.openvidu.java.client.RecordingProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/karaoke/recording")
public class RecordingController {
    private final OpenViduModel openViduModel;

    @Autowired
    public RecordingController(OpenViduModel openViduModel) {
        this.openViduModel = openViduModel;
    }

    // OpenVidu 서버와 상호 작용하는 녹화 관련 RESTful API를 제공하는 컨트롤러 메소드들

    // 녹화를 시작하는 엔드포인트
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public ResponseEntity<?> startRecording(@RequestBody Map<String, Object> params) {
        String sessionName = (String) params.get("sessionName");
        Recording.OutputMode outputMode = Recording.OutputMode.valueOf("COMPOSED");
        boolean hasAudio = true;
        boolean hasVideo = true;

        // RecordingProperties 객체를 빌드하여 녹화 설정을 생성함
        RecordingProperties properties = new RecordingProperties.Builder().outputMode(outputMode).hasAudio(hasAudio).hasVideo(hasVideo).build();

        try {
            // OpenVidu 서버에서 녹화 시작
            Recording recording = openViduModel.getOpenvidu().startRecording(sessionName, properties);
            openViduModel.getSessionRecordings().put(sessionName, true);
            return new ResponseEntity<>(recording, HttpStatus.OK);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            return new ResponseEntity<>("녹화 시작 중 오류 발생", HttpStatus.BAD_REQUEST);
        }
    }

    // 녹화를 중지하는 엔드포인트
    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    public ResponseEntity<?> stopRecording(@RequestBody Map<String, Object> params) {
        String recordingId = (String) params.get("recordingId");

        try {
            // OpenVidu 서버에서 녹화 중지
            Recording recording = openViduModel.getOpenvidu().stopRecording(recordingId);
            openViduModel.getSessionRecordings().remove(recording.getSessionId());
            return new ResponseEntity<>(recording, HttpStatus.OK);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            return new ResponseEntity<>("녹화 종료 중 오류 발생", HttpStatus.BAD_REQUEST);
        }
    }

    // 녹화를 삭제하는 엔드포인트
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntity<?> deleteRecording(@RequestBody Map<String, Object> params) {
        String recordingId = (String) params.get("recordingId");

        try {
            // OpenVidu 서버에서 녹화 삭제
            openViduModel.getOpenvidu().deleteRecording(recordingId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            return new ResponseEntity<>("녹화 삭제 중 오류 발생", HttpStatus.BAD_REQUEST);
        }
    }
}
