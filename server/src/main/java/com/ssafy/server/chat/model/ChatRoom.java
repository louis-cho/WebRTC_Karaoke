package com.ssafy.server.chat.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ROOM_PK", updatable = false)
    private long roomPk;
//    private String roomId; // 채팅방 UUID 아이디
    private String roomName; // 채팅방 제목
    private long userCount; // 채팅방 유저 수

    public ChatRoom create(String roomName){
        ChatRoom chatRoom = new ChatRoom();
//        chatRoom.roomId = UUID.randomUUID().toString();
        chatRoom.roomName = roomName;
        return chatRoom;
    }

}