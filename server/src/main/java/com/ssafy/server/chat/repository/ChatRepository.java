package com.ssafy.server.chat.repository;

import com.ssafy.server.chat.model.ChatDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<ChatDTO, Long> {
}
