package com.ssafy.server.friends.repository;

import com.ssafy.server.friends.model.Friends;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendsRepository extends JpaRepository<Friends, Long> {
    Page<Friends> findFriendsByFromUserPkOrToUserPkAndStatus(long fromUser, long toUser, char status, Pageable pageable);

    Page<Friends> findFriendsByToUserPkAndStatus(long toUser, char status, Pageable pageable);

    Page<Friends> findFriendsByFromUserPkAndStatus(long fromUser, char status, Pageable pageable);

    boolean existsByFromUserPkAndToUserPkAndStatus(long fromUser, long toUser, char status);

}
