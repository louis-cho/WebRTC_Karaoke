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
//    private long userCount; // 채팅방 유저 수

    public ChatRoom create(String roomName){
//    public ChatRoom create(String roomName, int cnt){
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomName = roomName;
//        chatRoom.userCount = cnt+1; // 채팅방 생성 시, 최소 인원 2이상
        return chatRoom;
    }

}