package com.ssafy.server.song.service;

import com.ssafy.server.song.model.entity.Song;
import com.ssafy.server.song.model.entity.SongInfo;

public interface SongService {

    Song getSongById(int songId);

    SongInfo getSongInfoById(int songId);
}
