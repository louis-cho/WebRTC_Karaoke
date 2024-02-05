package com.ssafy.server.chat.controller;

import java.util.*;

import com.ssafy.server.chat.model.ChatRoom;
import com.ssafy.server.chat.model.UsersChats;
import com.ssafy.server.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chatroom")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 채팅 리스트 화면
    @GetMapping("/list/{userId}")
    public List<UsersChats> chatRoomList(@PathVariable long userId){
        return chatRoomService.findAllRoomByUserId(userId);
    }

    // 채팅방 생성
    @PostMapping("/create")
    public ChatRoom createRoom(@RequestParam String name, @RequestParam long host, @RequestParam List<Long> guests) {
        return chatRoomService.createChatRoom(name, host, guests);
    }

    // 채팅방 참여 유저 리스트 반환
    @GetMapping("/list/users/{roomId}")
    public List<UsersChats> userList(@PathVariable long roomId) {
        return chatRoomService.getUserList(roomId);
    }

    //유저 초대
    @PostMapping("/invite/{roomId}")
    public void inviteUser(@PathVariable long roomId, @RequestParam List<Long> guests){
        chatRoomService.inviteUser(guests, roomId);
    }

    //방 나가기
    @PostMapping("/exit/{roomId}")
    public void exitRoom(@PathVariable long roomId, @RequestParam long userId){
        chatRoomService.exitRoom(userId, roomId);
    }
}