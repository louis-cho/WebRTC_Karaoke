package com.ssafy.server.friends.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "friends")
@Table(name ="friends")
public class Friends {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="FRIEND_ID", updatable = false)
    private Integer friendId;

    private Integer fromUserPk;
    private Integer toUserPk;
    public Friends() {}
    public Friends(Integer from, Integer to){
        this.fromUserPk = from;
        this.toUserPk = to;
    }

    private char status;
}
