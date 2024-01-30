package com.ssafy.server.karaoke.service;

import com.ssafy.server.karaoke.model.KaraokeSong;
import com.ssafy.server.karaoke.repository.KaraokeSongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KaraokeSongService {

    private final KaraokeSongRepository karaokeSongRepository;

    @Autowired
    public KaraokeSongService(KaraokeSongRepository karaokeSongRepository) {
        this.karaokeSongRepository = karaokeSongRepository;
    }

    public List<KaraokeSong> getAllSongs() {
        return karaokeSongRepository.findAll();
    }

    public Optional<KaraokeSong> getSongById(Long id) {
        return karaokeSongRepository.findById(id);
    }

    public KaraokeSong saveSong(KaraokeSong song) {
        return karaokeSongRepository.save(song);
    }

    public void deleteSong(Long id) {
        karaokeSongRepository.deleteById(id);
    }

    // 기타 비즈니스 로직 메서드들...

}
