package com.ssafy.server.friends.controller;

import com.ssafy.server.friends.model.Friends;
import com.ssafy.server.friends.service.FriendsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendsController {

    private final FriendsService friendsService;

    // 친구 목록 조회
    @GetMapping("/{userId}/list")
    public Page<Friends> getFriendsList(@PathVariable long userId, Pageable pageable) {
        return friendsService.getFriendsList(userId, pageable);
    }

    // 친구 신청
    @PostMapping("/request")
    public void requestFriend(@RequestParam long fromUser, @RequestParam long toUser) {
        friendsService.requestFriend(fromUser, toUser);
    }

    // 친구 신청 수락
    @PostMapping("/accept")
    public void acceptFriendRequest(@RequestParam long requestId) {
        friendsService.acceptFriendRequest(requestId);
    }

    // 친구 삭제
    @PostMapping("/delete")
    public void deleteFriend(@RequestParam long friendId) {
        friendsService.deleteFriend(friendId);
    }

    // 자신에게 온 친구 요청 목록 조회
    @GetMapping("/incoming-requests")
    public Page<Friends> getIncomingRequests(@RequestParam long toUser, Pageable pageable) {
        return friendsService.getIncomingRequests(toUser, pageable);
    }

    // 내가 보낸 친구 요청 목록 조회
    @GetMapping("/outgoing-requests")
    public Page<Friends> getOutgoingRequests(@RequestParam long fromUser, Pageable pageable) {
        return friendsService.getOutgoingRequests(fromUser, pageable);
    }
}
