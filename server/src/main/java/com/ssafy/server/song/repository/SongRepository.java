package com.ssafy.server.song.repository;

import com.ssafy.server.song.model.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Integer> {

}
