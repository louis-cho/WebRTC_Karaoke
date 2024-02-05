package com.ssafy.server.friends.controller;

import com.ssafy.server.friends.model.Friends;
import com.ssafy.server.friends.service.FriendsService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Friends API")
@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendsController {

    private final FriendsService friendsService;

    @Operation(summary = "친구 목록 API", description = "userPK를 기반으로 친구 목록을 조회(페이지네이션 적용)")
    @GetMapping("/{userId}/list")
    public Page<Friends> getFriendsList(@PathVariable long userId, Pageable pageable) {
        return friendsService.getFriendsList(userId, pageable);
    }

    @Operation(summary = "친구 신청 API", description = "userPK를 기반으로 친구 신청")
    @PostMapping("/request")
    public void requestFriend(@RequestParam long fromUser, @RequestParam long toUser) {
        if (fromUser == toUser)
            throw new RuntimeException("나는 나 자신의 영원한 친구 입니다.");
        friendsService.requestFriend(fromUser, toUser);
    }

    @Operation(summary = "친구 수락 API", description = "friendId(friendPK)로 친구 수락 처리")
    @PostMapping("/accept")
    public void acceptFriendRequest(@RequestParam long friendId) {
        friendsService.acceptFriendRequest(friendId);
    }

    @Operation(summary = "친구 삭제/취소 API", description = "friendId(friendPK)로 친구 삭제 처리")
    @PostMapping("/delete")
    public void deleteFriend(@RequestParam long friendId) {
        friendsService.deleteFriend(friendId);
    }

    @Operation(summary = "친구 요청 목록 API", description = "나에게 온 친구 요청 목록 확인")
    @GetMapping("/incoming-requests")
    public Page<Friends> getIncomingRequests(@RequestParam long toUser, Pageable pageable) {
        return friendsService.getIncomingRequests(toUser, pageable);
    }

    @Operation(summary = "보낸 친구 요청 목록 API", description = "내가 보낸 친구 요청 목록 확인")
    @GetMapping("/outgoing-requests")
    public Page<Friends> getOutgoingRequests(@RequestParam long fromUser, Pageable pageable) {
        return friendsService.getOutgoingRequests(fromUser, pageable);
    }
}
