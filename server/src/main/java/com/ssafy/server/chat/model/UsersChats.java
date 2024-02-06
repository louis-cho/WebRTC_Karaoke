package com.ssafy.server.chat.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "users_chats")
public class UsersChats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="USERS_CHATS_ID", updatable = false)
    private long usersChatsId;

    private long userPk;
    private long roomPk;
    public UsersChats() {}
    public UsersChats(long up, long rp){
        this.userPk = up;
        this.roomPk = rp;
        this.status = '1';
    }
    private char status; // 1 : enter 0 : exit
}
