package com.ssafy.server.karaoke.service;

import com.ssafy.server.feed.model.Feed;
import com.ssafy.server.karaoke.model.PerfectScore;
import com.ssafy.server.karaoke.model.Song;
import com.ssafy.server.karaoke.model.SongInfo;
import com.ssafy.server.karaoke.repository.PerfectScoreRepository;
import com.ssafy.server.karaoke.repository.SongInfoRepository;
import com.ssafy.server.karaoke.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SongServiceImpl implements SongService{

    @Autowired
    private SongRepository songRepository;
    @Autowired
    private PerfectScoreRepository perfectScoreRepository;
    @Autowired
    private SongInfoRepository songInfoRepository;

    @Override
    public Song getSongById(int songId) {
        Optional<Song> optionalSong = songRepository.findById(songId);
        return optionalSong.orElse(null);
    }

    @Override
    public PerfectScore getPerfectScoreById(int songId) {
        Optional<PerfectScore> optionalScore = perfectScoreRepository.findById(songId);
        return optionalScore.orElse(null);
    }

    @Override
    public SongInfo getSongInfoById(int songId) {
        Optional<SongInfo> optionalInfo= songInfoRepository.findById(songId);
        return optionalInfo.orElse(null);
    }

}
