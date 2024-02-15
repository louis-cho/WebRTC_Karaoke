package com.ssafy.server.chat.model;

import lombok.*;
import javax.persistence.*;

@Getter
@Setter
@Entity(name = "chat")
@Table(name ="chat")
@ToString
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="MESSAGE_ID", updatable = false)
    private long messageID;

    private String sender; // 송신 유저 PK
    private String roomId; // 채팅방 번호 PK

    public enum MessageType{
        ENTER, TALK, LEAVE, MEDIA, TYPE;
    }

    @Enumerated(EnumType.STRING)
    private MessageType type; // 메시지 타입

    @Column(name = "message", nullable = false, columnDefinition = "VARCHAR(1024) CHARACTER SET UTF8")
    private String message; // 메시지
    private String time; // 채팅 발송 시간
}