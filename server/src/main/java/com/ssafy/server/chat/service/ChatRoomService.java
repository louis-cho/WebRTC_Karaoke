package com.ssafy.server.chat.service;

import java.util.*;
import com.ssafy.server.chat.model.ChatRoomDTO;
import com.ssafy.server.chat.repository.ChatRoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    // 전체 채팅방 조회
    public List<ChatRoomDTO> findAllRoom(){
//        List chatRooms = new ArrayList<>(chatRoomMap.values());
//        Collections.reverse(chatRooms);
        List<ChatRoomDTO> chatRooms = chatRoomRepository.findAll();
        return chatRooms;
    }

    // roomID 기준으로 채팅방 찾기
//    public ChatRoomDTO findRoomById(String roomId){
//        return chatRoomMap.get(roomId);
//    }

    // roomName 로 채팅방 만들기
    public ChatRoomDTO createChatRoom(String roomName){
        ChatRoomDTO chatRoom = new ChatRoomDTO().create(roomName);
//        chatRoomMap.put(chatRoom.getRoomId(), chatRoom);
        chatRoomRepository.save(chatRoom);
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