package com.ssafy.server.friends.controller;

import com.ssafy.server.feed.model.FeedResponseFactory;
import com.ssafy.server.friends.model.Friends;
import com.ssafy.server.friends.model.dto.FriendResponseDto;
import com.ssafy.server.friends.service.FriendsService;
import com.ssafy.server.user.model.User;
import com.ssafy.server.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Api(tags = "Friends API")
@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendsController {

    private final FriendsService friendsService;
    private final UserService userService;

    @Operation(summary = "친구 목록 API", description = "userPK를 기반으로 친구 목록을 조회(페이지네이션 적용)")
    @GetMapping("/list")
    public ResponseEntity<List<FriendResponseDto>> getFriendsList(@RequestParam int page,
                                                                  @RequestParam int size) throws Exception{
        Integer userPk = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserPk();
        Pageable pageable = PageRequest.of(page, size);
        Page<Friends> friends = friendsService.getFriendsList(userPk, pageable);

        List<FriendResponseDto> friendsResponseDtoList = friends.getContent()
                .stream()
                .map(friend -> {
                    FriendResponseDto friendResponseDto = new FriendResponseDto();
                    friendResponseDto.setFriendId((friend.getFriendId()));
                    UUID fromUserkey = userService.getUUIDByUserPk(friend.getFromUserPk());
                    UUID toUserkey = userService.getUUIDByUserPk(friend.getToUserPk());
                    friendResponseDto.setFromUserKey(fromUserkey);
                    friendResponseDto.setToUserKey(toUserkey);
                    try {
                        friendResponseDto.setFromUserNickname(userService.getUserNickname(friend.getFromUserPk()));
                        friendResponseDto.setToUserNickname(userService.getUserNickname(friend.getToUserPk()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    friendResponseDto.setStatus(friend.getStatus());
                    return friendResponseDto;
                })
                .filter(Objects::nonNull) // null 값 필터링
                .collect(Collectors.toList());
        return new ResponseEntity<>(friendsResponseDtoList, HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getFriendsCount(){
        Integer userPk = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserPk();
        Integer friendsCount = friendsService.getFriendsCount(userPk);
        if(friendsCount == null) friendsCount = 0;

        return new ResponseEntity<>(friendsCount, HttpStatus.OK);
    }

    @Operation(summary = "친구 신청 API", description = "userPK를 기반으로 친구 신청")
    @GetMapping("/request/{toUserKey}")
    public void requestFriend(@PathVariable String toUserKey) {
        Integer fromUser = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserPk();
        Integer toUser = userService.getUserPk(UUID.fromString(toUserKey));
        if (fromUser == toUser)
            throw new RuntimeException("나는 나 자신의 영원한 친구 입니다.");
        friendsService.requestFriend(fromUser, toUser);
    }

    @Operation(summary = "친구 수락 API", description = "friendId(friendPK)로 친구 수락 처리")
    @PostMapping("/accept")
    public void acceptFriendRequest(@RequestParam Integer friendId) {
        friendsService.acceptFriendRequest(friendId);
    }

    @Operation(summary = "친구 삭제/취소 API", description = "friendId(friendPK)로 친구 삭제 처리")
    @PostMapping("/delete")
    public void deleteFriend(@RequestParam Integer friendId) {
        friendsService.deleteFriend(friendId);
    }

    @Operation(summary = "친구 요청 목록 API", description = "나에게 온 친구 요청 목록 확인")
    @GetMapping("/incoming-requests")
    public ResponseEntity<List<FriendResponseDto>> getIncomingRequests(@RequestParam int page,
                                             @RequestParam int size) {
        Integer toUser = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserPk();
        Pageable pageable = PageRequest.of(page, size);
        Page<Friends> friends = friendsService.getIncomingRequests(toUser, pageable);
        List<FriendResponseDto> friendsResponseDtoList = friends.getContent()
                .stream()
                .map(friend -> {
                    FriendResponseDto friendResponseDto = new FriendResponseDto();
                    friendResponseDto.setFriendId((friend.getFriendId()));
                    friendResponseDto.setFromUserKey(userService.getUUIDByUserPk(friend.getFromUserPk()));
                    friendResponseDto.setToUserKey(userService.getUUIDByUserPk(friend.getToUserPk()));
                    try {
                        friendResponseDto.setFromUserNickname(userService.getUserNickname(friend.getFromUserPk()));
                        friendResponseDto.setToUserNickname(userService.getUserNickname(friend.getToUserPk()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    friendResponseDto.setStatus(friend.getStatus());
                    return friendResponseDto;
                })
                .filter(Objects::nonNull) // null 값 필터링
                .collect(Collectors.toList());
        System.out.println("반환 친구리스트 수 "+ friendsResponseDtoList.size());
        return new ResponseEntity<>(friendsResponseDtoList, HttpStatus.OK);
    }

    @Operation(summary = "보낸 친구 요청 목록 API", description = "내가 보낸 친구 요청 목록 확인")
    @GetMapping("/outgoing-requests")
    public Page<Friends> getOutgoingRequests(@RequestParam Integer fromUser,
                                             @RequestParam int page,
                                             @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return friendsService.getOutgoingRequests(fromUser, pageable);
    }
}
