package com.ssafy.server.karaoke.repository;

import com.ssafy.server.karaoke.model.KaraokeSong;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KaraokeSongRepository extends JpaRepository<KaraokeSong, Long> {

    // 필요한 추가적인 쿼리 메서드들...

}
