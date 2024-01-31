package com.ssafy.server.chat.repository;

import com.ssafy.server.chat.model.UsersChats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersChatsRepository extends JpaRepository<UsersChats, Long> {
}