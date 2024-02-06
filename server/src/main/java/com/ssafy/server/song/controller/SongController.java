package com.ssafy.server.song.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.ssafy.server.song.model.entity.Song;
import com.ssafy.server.song.model.entity.SongInfo;
import com.ssafy.server.song.service.SongService;
import com.ssafy.server.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/song")
public class SongController {

    @Autowired
    private final SongService songService;

    @Autowired
    private final UserService userService;

    public SongController(SongService songService, UserService userService) {
        this.songService = songService;
        this.userService = userService;
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
    public ResponseEntity<?> createSingLog(@RequestBody JsonNode jsonNode) {
        int songId = -1, userPk = -1;
        UUID userUUID = null;
        try {
            songId = Integer.parseInt(jsonNode.get("songId").asText());

            if(jsonNode.get("uuid") != null) {
                userUUID = UUID.fromString(jsonNode.get("uuid").asText());
                userPk = userService.getUserPk(userUUID);
            }
            else {
                userPk = Integer.parseInt(jsonNode.get("userPk").asText());
            }

        } catch(Exception e) {
//            return;
        }
        return null;
    }

}
