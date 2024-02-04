package com.ssafy.server.chat.service;

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.server.chat.model.ChatRoom;
import com.ssafy.server.chat.model.UsersChats;
import com.ssafy.server.chat.repository.ChatRoomRepository;
import com.ssafy.server.chat.repository.UsersChatsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private UsersChatsRepository usersChatsRepository;

    // 전체 채팅방 조회
    public List<ChatRoom> findAllRoom(){
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        return chatRooms;
    }

    // roomID 기준으로 채팅방 찾기
//    public ChatRoomDTO findRoomById(String roomId){
//        return chatRoomMap.get(roomId);
//    }

    // roomName 로 채팅방 만들기
    public ChatRoom createChatRoom(String roomName, long host, long guest){
        ChatRoom chatRoom = new ChatRoom().create(roomName);
        chatRoom.setRoomPk(chatRoomRepository.save(chatRoom).getRoomPk());
        UsersChats hostChats = new UsersChats(host, chatRoom.getRoomPk());
        UsersChats guestChats = new UsersChats(guest, chatRoom.getRoomPk());
        usersChatsRepository.save(hostChats);
        usersChatsRepository.save(guestChats);
        return chatRoom;
    }
}