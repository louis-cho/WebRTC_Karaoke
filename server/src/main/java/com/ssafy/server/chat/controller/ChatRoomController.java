package com.ssafy.server.chat.controller;

import java.util.*;

import com.ssafy.server.chat.model.ChatRoomDTO;
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
    @GetMapping("/")
//    public ResponseEntity<ResultResponse> goChatRoom(){
    public String goChatRoom(){
        List<ChatRoomDTO> chatRooms = chatRoomService.findAllRoom();
//        return ResponseEntity.ok(ResultResponse.of(CREATE_POST_SUCCESS,chatRooms));
        return "ok " + chatRooms.toString();
    }

    // 채팅방 생성
    @PostMapping("/room")
    public String createRoom(@RequestParam String name) {
//    public ResponseEntity<ResultResponse> createRoom(@RequestParam String name) {
        ChatRoomDTO room = chatRoomService.createChatRoom(name);
        return "ok roomId " + room.getRoomId();
//        return ResponseEntity.ok(ResultResponse.of(CREATE_POST_SUCCESS,room.getRoomId()));
    }

    // 채팅에 참여한 유저 리스트 반환
    @GetMapping("/userlist")
    public ArrayList<String> userList(String roomId) {

        return chatRoomService.getUserList(roomId);
    }
}