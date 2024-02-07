package com.ssafy.server.song.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.ssafy.server.song.model.ReserveModel;
import com.ssafy.server.song.model.entity.SingLog;
import com.ssafy.server.song.model.entity.Song;
import com.ssafy.server.song.model.entity.SongInfo;
import com.ssafy.server.song.service.SongService;
import com.ssafy.server.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/song")
public class SongController {

    @Autowired
    private final SongService songService;

    @Autowired
    private final UserService userService;

    private Queue<ReserveModel> reserveList;

    public SongController(SongService songService, UserService userService) {
        this.songService = songService;
        this.userService = userService;
        this.reserveList = new ArrayDeque<>();
    }

    @GetMapping("/list")
    public ResponseEntity<?> getSongList() {
        ArrayList<Song> list = (ArrayList<Song>) songService.getSongList();

        return ResponseEntity.ok(list);
    }

    @PostMapping("/reserve")
    public ResponseEntity<?> reserveSong(@RequestBody Map<String, Object> params) {
        System.out.println("Reserve Song : " + params);

        String userName = (String) params.get("userName");
        int songId = (Integer) params.get("songId");

        ReserveModel reserveModel = new ReserveModel(userName, songId);
        reserveList.offer(reserveModel);

        return ResponseEntity.ok(reserveList);
    }

    @GetMapping("/{songId}")
    public ResponseEntity<Song> getSongById(@PathVariable int songId) {
        Song song = songService.getSongById(songId);

        return ResponseEntity.ok((song));
    }

    @GetMapping("/songInfo/{songId}")
    public ResponseEntity<SongInfo> getSongInfoById(@PathVariable int songId) {
        SongInfo songInfo = songService.getSongInfoById(songId);

        return ResponseEntity.ok((songInfo));
    }

    @PostMapping("/createLog")
    public void createSongLog(@RequestBody JsonNode jsonNode) {
        SingLog singlog = new SingLog();
        int songId = -1, userPk = -1, singScore = -1;
        String singMode = "", singStatus = "";
        UUID userUUID = null;
        try {
            songId = Integer.parseInt(jsonNode.get("songId").asText());
            if (jsonNode.get("uuid") != null) {
                userUUID = UUID.fromString(jsonNode.get("uuid").asText());
                userPk = userService.getUserPk(userUUID);
            } else {
                userPk = Integer.parseInt(jsonNode.get("userPk").asText());
            }
            singMode = jsonNode.get("singMode").asText();
            singStatus = jsonNode.get("singStatus").asText();

            if (jsonNode.get("songId").asText() != null) {
                singScore = Integer.parseInt(jsonNode.get("singScore").asText());
            }
        } catch (Exception e) {
            return;
        }
        singlog.setSongId(songId);
        singlog.setUserPk(userPk);
        singlog.setSingMode(singMode);
        singlog.setSingStatus(singStatus);
        singlog.setSingScore(singScore);

        songService.createSongLog(singlog);
    }

}
