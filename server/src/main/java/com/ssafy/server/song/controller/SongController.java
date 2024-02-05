package com.ssafy.server.song.controller;

import com.ssafy.server.song.model.entity.Song;
import com.ssafy.server.song.model.entity.SongInfo;
import com.ssafy.server.song.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/song")
public class SongController {

    @Autowired
    private SongService songService;

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

}
