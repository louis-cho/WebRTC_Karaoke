package com.ssafy.server.song.service;

import com.ssafy.server.song.model.entity.SingLog;
import com.ssafy.server.song.model.entity.Song;
import com.ssafy.server.song.model.entity.SongInfo;
import com.ssafy.server.song.repository.SingLogRepository;
import com.ssafy.server.song.repository.SongInfoRepository;
import com.ssafy.server.song.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SongServiceImpl implements SongService {

    @Autowired
    private SongRepository songRepository;
    @Autowired
    private SongInfoRepository songInfoRepository;
    @Autowired
    SingLogRepository singLogRepository;

    @Override
    public List<Song> getSongList() {
        return songRepository.findAll();
    }

    @Override
    public Song getSongById(int songId) {
        Optional<Song> optionalSong = songRepository.findById(songId);
        return optionalSong.orElse(null);
    }

    @Override
    public SongInfo getSongInfoById(int songId) {
        Optional<SongInfo> optionalInfo = songInfoRepository.findById(songId);
        return optionalInfo.orElse(null);
    }

    @Override
    public void createSongLog(SingLog singlog) {
        singLogRepository.save(singlog);
    }


}
