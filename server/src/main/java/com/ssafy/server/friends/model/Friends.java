package com.ssafy.server.friends.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Friends {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="FRIEND_ID", updatable = false)
    private long friendId;

    private long fromUserPk;
    private long toUserPk;
    public Friends() {}
    public Friends(long from, long to){
        this.fromUserPk = from;
        this.toUserPk = to;
    }

    private char status;
}
