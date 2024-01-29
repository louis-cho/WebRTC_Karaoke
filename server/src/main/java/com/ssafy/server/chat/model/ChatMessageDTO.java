package com.ssafy.server.chat.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDTO {
    public enum MessageType {
        ENTER, TALK, MEDIA, LEAVE
    }
    private MessageType type;
    private String roomId;
    private String writer;
    private String message;
}