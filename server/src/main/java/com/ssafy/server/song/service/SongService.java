package com.ssafy.server.song.service;

import com.ssafy.server.song.model.entity.SingLog;
import com.ssafy.server.song.model.entity.Song;
import com.ssafy.server.song.model.entity.SongInfo;

import java.util.List;

public interface SongService {

    Song getSongById(int songId);

    SongInfo getSongInfoById(int songId);

    void createSongLog(SingLog singlog);

    List<Song> getSongList();
}
