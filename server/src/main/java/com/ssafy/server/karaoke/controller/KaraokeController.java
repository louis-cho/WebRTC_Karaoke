package com.ssafy.server.karaoke.controller;

import com.ssafy.server.karaoke.model.KaraokeSong;
import com.ssafy.server.karaoke.service.KaraokeSongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/karaoke")
public class KaraokeController {

    private final KaraokeSongService karaokeSongService;

    @Autowired
    public KaraokeController(KaraokeSongService karaokeSongService) {
        this.karaokeSongService = karaokeSongService;
    }

    @GetMapping("/songs")
    public ResponseEntity<List<KaraokeSong>> getAllSongs() {
        List<KaraokeSong> songs = karaokeSongService.getAllSongs();
        return ResponseEntity.ok(songs);
    }

    @GetMapping("/songs/{id}")
    public ResponseEntity<KaraokeSong> getSongById(@PathVariable Long id) {
        return karaokeSongService.getSongById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/songs")
    public ResponseEntity<KaraokeSong> createSong(@RequestBody KaraokeSong song) {
        KaraokeSong createdSong = karaokeSongService.saveSong(song);
        return ResponseEntity.ok(createdSong);
    }

    @DeleteMapping("/songs/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
        karaokeSongService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }

    // 기타 필요한 엔드포인트들...

}
