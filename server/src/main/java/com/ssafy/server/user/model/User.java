package com.ssafy.server.user.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Getter
@Setter
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userPk;

    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID userKey;

    private String nickname;
    private char role;
    private String profileImgUrl;
    private String introduction;

    // Constructors, getters, setters, etc.

    public User() {
        // Default constructor required by JPA
    }

    public User(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "[User] >> " + this.userPk + "," + this.userKey + "," + this.nickname +"\n";
    }
}
