package com.ssafy.server.karaoke.repository;

import com.ssafy.server.karaoke.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Integer> {

}
