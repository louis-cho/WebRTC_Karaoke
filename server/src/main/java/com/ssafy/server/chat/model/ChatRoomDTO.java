package com.ssafy.server.chat.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class ChatRoomDTO {

    private String roomId;
    private String name;

    public static ChatRoomDTO create(String name){
        ChatRoomDTO room = new ChatRoomDTO();

        room.roomId = UUID.randomUUID().toString();
        room.name = name;
        return room;
    }

    @Override
    public String toString() {
        return "ChatRoomDTO{" +
                "roomId='" + roomId + '\'' +
                ", name='" + name + '\'' +
//                ", sessions=" + sessions +
                '}';
    }
}