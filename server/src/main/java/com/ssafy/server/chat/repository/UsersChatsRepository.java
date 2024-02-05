package com.ssafy.server.chat.repository;

import com.ssafy.server.chat.model.UsersChats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsersChatsRepository extends JpaRepository<UsersChats, Long> {
    List<UsersChats> findByUserPkAndStatus(long userPk, char status);
    List<UsersChats> findByRoomPkAndStatus(long roomPk, char status);
    Optional<UsersChats> findByUserPkAndRoomPk(long userPk, long roomPk);
}