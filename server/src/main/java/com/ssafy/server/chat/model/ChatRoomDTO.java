package com.ssafy.server.chat.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.UUID;

@Getter
@Setter
@Entity
public class ChatRoomDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="DM_ROOM_PK", updatable = false)
    private long roomPk;
    private String roomId; // 채팅방 UUID 아이디
    private String roomName; // 채팅방 제목
    private long userCount; // 채팅방 유저 수
//    private HashMap<String, String> userList = new HashMap<String, String>();

    //기본 생성자 (JPA)
    public ChatRoomDTO(){}

    public ChatRoomDTO create(String roomName){
        ChatRoomDTO chatRoom = new ChatRoomDTO();
        chatRoom.roomId = UUID.randomUUID().toString();
        chatRoom.roomName = roomName;

        return chatRoom;
    }

}