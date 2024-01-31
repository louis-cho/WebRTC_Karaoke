package com.ssafy.server.karaoke.service;

import com.ssafy.server.karaoke.model.PerfectScore;
import com.ssafy.server.karaoke.model.Song;
import com.ssafy.server.karaoke.model.SongInfo;

public interface SongService {

    Song getSongById(int songId);

    PerfectScore getPerfectScoreById(int songId);

    SongInfo getSongInfoById(int songId);
}
