package com.ssafy.server.friends.repository;

import com.ssafy.server.friends.model.Friends;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FriendsRepository extends JpaRepository<Friends, Integer> {
    @Query("SELECT f FROM friends f WHERE (f.fromUserPk = :userId OR f.toUserPk = :userId) AND f.status = :status")
    Page<Friends> findFriendsByUserAndStatus(@Param("userId") Integer userId, @Param("status") char status, Pageable pageable);

    @Query("SELECT f FROM friends f WHERE f.toUserPk = :toUser AND f.status = :status")
    Page<Friends> findFriendsToUserAndStatus(@Param("toUser") Integer toUser, @Param("status") char status, Pageable pageable);

    @Query("SELECT f FROM friends f WHERE f.fromUserPk = :fromUser AND f.status = :status")
    Page<Friends> findFriendsFromUserAndStatus(@Param("fromUser") Integer fromUser, @Param("status") char status, Pageable pageable);

    boolean existsByFromUserPkAndToUserPkAndStatus(Integer fromUser, Integer toUser, char status);

    Friends findByFromUserPkAndToUserPkAndStatus(Integer fromUser, Integer toUser, char status);

    @Query("SELECT COUNT(f) FROM friends f WHERE f.fromUserPk = :inputValue1 OR f.toUserPk = :inputValue1 AND f.status not in ('0')")
    Integer countByFromUserOrToUserAndStatusNotZero(@Param("inputValue1")Integer inputValue1);;
}
