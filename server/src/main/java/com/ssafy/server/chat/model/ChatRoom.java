package com.ssafy.server.chat.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "chat_room")
@Table(name ="chat_room")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ROOM_PK", updatable = false)
    private long roomPk;
    private String roomName; // 채팅방 제목

    public ChatRoom create(String roomName){
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomName = roomName;
        return chatRoom;
    }

}