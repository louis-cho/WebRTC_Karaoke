package com.ssafy.server.chat.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UsersChatsRes {
    private long roomPk;
    private char status; // 1 : enter 0 : exit
    private String time;
    private UUID userUuid;
    public UsersChatsRes(UsersChats usersChats){
        this.roomPk = usersChats.getRoomPk();
        this.status = usersChats.getStatus();
        this.time = usersChats.getTime();
        this.userUuid = usersChats.getUserUuid();
    }
}
