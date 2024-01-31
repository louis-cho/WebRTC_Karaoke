package com.ssafy.server.chat.repository;

import com.ssafy.server.chat.model.ChatRoomDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomDTO, Long> {
}