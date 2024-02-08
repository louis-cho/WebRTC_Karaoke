package com.ssafy.server.chat.repository;

import com.ssafy.server.chat.model.Chat;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
//    @Query("SELECT c FROM chat c WHERE c.roomId = :roomId AND DATE(c.time) = (SELECT MAX(DATE(c2.time)) FROM chat c2 WHERE c2.roomId = :roomId)")
//    List<Chat> findLatestChatsByRoomId(@Param("roomId") String roomId);
    List<Chat> findByRoomIdOrderByTimeDesc(@Param("roomId") String roomId);
}
