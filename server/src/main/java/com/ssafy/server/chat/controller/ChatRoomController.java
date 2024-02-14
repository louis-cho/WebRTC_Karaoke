package com.ssafy.server.chat.controller;

import com.ssafy.server.chat.model.ChatRoom;
import com.ssafy.server.chat.model.UsersChats;
import com.ssafy.server.chat.model.UsersChatsRes;
import com.ssafy.server.chat.service.ChatRoomService;
import com.ssafy.server.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/chatroom")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final UserService userService;

    @GetMapping("/test")
    public String test(){
        return "test";
    }
    // 채팅 리스트 화면
    @GetMapping("/list/{userUuid}")
    public Page<UsersChats> chatRoomList(@PathVariable String userUuid,
                                         @RequestParam(name="page", defaultValue = "0") int page,
                                         @RequestParam(name="size", defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        long pk = userService.getUserPk(UUID.fromString(userUuid));
        System.out.println(chatRoomService.findAllRoomByUserId(pk, pageable));
        return chatRoomService.findAllRoomByUserId(pk, pageable);
    }

    @GetMapping("/info/{roomId}")
    public Optional<ChatRoom> getRoomInfo(@PathVariable long roomId) {return chatRoomService.getRoomInfo(roomId);}

    // 채팅방 생성
    @PostMapping("/create")
    public ChatRoom createRoom(@RequestParam String name, @RequestParam String host, @RequestParam List<String> guests) {
        int hostPk = userService.getUserPk(UUID.fromString(host));
        return chatRoomService.createChatRoom(name, hostPk, guests);
    }

    // 채팅방 참여 유저 리스트 반환
    @GetMapping("/list/users/{roomId}")
    public List<UsersChatsRes> userList(@PathVariable long roomId) {
        return chatRoomService.getUserList(roomId);
    }

    //유저 초대
    @PostMapping("/invite/{roomId}")
    public void inviteUser(@PathVariable long roomId, @RequestParam List<String> guests){
        chatRoomService.inviteUser(guests, roomId);
    }

    //채팅방 입, 퇴장
    @PostMapping("/eoInfo/{roomId}")
    public void enterOutInfo(@PathVariable long roomId, @RequestParam long userId){
        chatRoomService.enterOutInfo(roomId, userId);
    }

    //방 나가기
    @PostMapping("/exit/{roomId}")
    public void exitRoom(@PathVariable long roomId, @RequestParam long userId){
        chatRoomService.exitRoom(userId, roomId);
    }
}