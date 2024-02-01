package com.ssafy.server.chat.repository;

import com.ssafy.server.chat.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByRoomIdOrderByTime(String roomId);
}
