package com.ssafy.server.friends.service;

import com.ssafy.server.friends.model.Friends;
import com.ssafy.server.friends.repository.FriendsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendsService {
    private final FriendsRepository friendsRepository;

    // 친구 목록 조회
    public Page<Friends> getFriendsList(long userId, Pageable pageable) {
        return friendsRepository.findFriendsByUserAndStatus(userId,'2', pageable);
    }

    // 친구 신청
    public void requestFriend(long fromUser, long toUser) {
        // 이미 친구인지 또는 이미 친구 신청 대기 중인지 확인
        if (isAlreadyFriend(fromUser, toUser) || isFriendRequestPending(fromUser, toUser)) {
            throw new RuntimeException("이미 친구이거나, 친구 요청중인 상태인데요..?");
        }

        Friends friends = new Friends(fromUser, toUser);
        friends.setStatus('1'); // 친구 요청 상태
        friendsRepository.save(friends);
    }

    // 친구 신청 수락
    public void acceptFriendRequest(long friendId) {
        Optional<Friends> optionalFriends = friendsRepository.findById(friendId);
        if (optionalFriends.isPresent()) {
            Friends friends = optionalFriends.get();
            friends.setStatus('2'); // 친구 상태로 변경
            friendsRepository.save(friends);
        } else {
            throw new RuntimeException("Friend not found");
        }
    }

    // 친구 삭제 & 요청 취소
    public void deleteFriend(long friendId) {
        Optional<Friends> optionalFriends = friendsRepository.findById(friendId);
        if (optionalFriends.isPresent()) {
            Friends friends = optionalFriends.get();
            friends.setStatus('0'); // 친구 삭제 상태로 변경
            friendsRepository.save(friends);
        } else {
            throw new RuntimeException("Friend not found");
        }
    }

    // 자신에게 온 친구 요청 목록 조회
    public Page<Friends> getIncomingRequests(long toUser, Pageable pageable) {
        return friendsRepository.findFriendsToUserAndStatus(toUser, '1', pageable);
    }

    // 내가 보낸 친구 요청 목록 조회
    public Page<Friends> getOutgoingRequests(long fromUser, Pageable pageable) {
        return friendsRepository.findFriendsFromUserAndStatus(fromUser, '1', pageable);
    }

    // 이미 친구인지 확인
    private boolean isAlreadyFriend(long fromUser, long toUser) {
        return friendsRepository.existsByFromUserPkAndToUserPkAndStatus(fromUser, toUser, '2') ||
                friendsRepository.existsByFromUserPkAndToUserPkAndStatus(toUser, fromUser, '2');
    }

    // 이미 친구 신청 대기 중인지 확인
    private boolean isFriendRequestPending(long fromUser, long toUser) {
        return friendsRepository.existsByFromUserPkAndToUserPkAndStatus(fromUser, toUser, '1') ||
                friendsRepository.existsByFromUserPkAndToUserPkAndStatus(toUser, fromUser, '1');
    }
}
