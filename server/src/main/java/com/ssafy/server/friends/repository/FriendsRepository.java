package com.ssafy.server.friends.repository;

import com.ssafy.server.friends.model.Friends;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FriendsRepository extends JpaRepository<Friends, Long> {
    @Query("SELECT f FROM Friends f WHERE (f.fromUserPk = :userId OR f.toUserPk = :userId) AND f.status = :status")
    Page<Friends> findFriendsByUserAndStatus(@Param("userId") long userId, @Param("status") char status, Pageable pageable);

    @Query("SELECT f FROM Friends f WHERE f.toUserPk = :toUser AND f.status = :status")
    Page<Friends> findFriendsToUserAndStatus(@Param("toUser") long toUser, @Param("status") char status, Pageable pageable);

    @Query("SELECT f FROM Friends f WHERE f.fromUserPk = :fromUser AND f.status = :status")
    Page<Friends> findFriendsFromUserAndStatus(@Param("fromUser") long fromUser, @Param("status") char status, Pageable pageable);

    boolean existsByFromUserPkAndToUserPkAndStatus(long fromUser, long toUser, char status);

}
