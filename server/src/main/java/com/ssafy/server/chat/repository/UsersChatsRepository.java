package com.ssafy.server.chat.repository;

import com.ssafy.server.chat.model.UsersChats;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UsersChatsRepository extends JpaRepository<UsersChats, Long> {
    @Query("SELECT uc FROM users_chats uc WHERE uc.userPk = :userPk AND uc.status = :status")
    Page<UsersChats> findByUserPkAndStatus(@Param("userPk") long userPk, @Param("status") char status, Pageable pageable);
    List<UsersChats> findByRoomPkAndStatus(long roomPk, char status);
    Optional<UsersChats> findByUserPkAndRoomPk(long userPk, long roomPk);
}