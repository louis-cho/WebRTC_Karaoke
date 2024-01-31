package com.ssafy.server.chat.service;

import java.util.*;
import com.ssafy.server.chat.model.ChatRoom;
import com.ssafy.server.chat.model.UsersChats;
import com.ssafy.server.chat.repository.ChatRoomRepository;
import com.ssafy.server.chat.repository.UsersChatsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    // 채팅방 인원+1
    public void plusUserCnt(String roomId){
//        ChatRoomDTO room = chatRoomMap.get(roomId);
//        room.setUserCount(room.getUserCount()+1);
    }

    // 채팅방 인원-1
    public void minusUserCnt(String roomId){
//        ChatRoomDTO room = chatRoomMap.get(roomId);
//        room.setUserCount(room.getUserCount()-1);
    }

    // 채팅방 유저 리스트에 유저 추가
    public String addUser(String roomId, String userName){
//        ChatRoomDTO room = chatRoomMap.get(roomId);
        String userUUID = UUID.randomUUID().toString();

//        room.getUserList().put(userUUID, userName);

        return userUUID;
    }

    // 채팅방 유저 리스트 삭제
    public void delUser(String roomId, String userUUID){
//        ChatRoomDTO room = chatRoomMap.get(roomId);
//        room.getUserList().remove(userUUID);
    }

    // 채팅방 userName 조회
    public String getUserName(String roomId, String userUUID){
//        ChatRoomDTO room = chatRoomMap.get(roomId);
//        return room.getUserList().get(userUUID);
        return "dummy";
    }

    // 채팅방 전체 userlist 조회
    public ArrayList<String> getUserList(String roomId){
        ArrayList<String> list = new ArrayList<>();
//        ChatRoomDTO room = chatRoomMap.get(roomId);

//        room.getUserList().forEach((key, value) -> list.add(value));
        return list;
    }
}