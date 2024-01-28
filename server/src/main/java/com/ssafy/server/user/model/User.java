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

    private UUID userKey;
    private String nickname;
    private char role;
    private String profileImgUrl;
    private String introduction;

    // Constructors, getters, setters, etc.

    public User() {
        // Default constructor required by JPA
    }

    public User(UUID userKey, String nickname, char role, String profileImgUrl, String introduction) {
        this.userKey = userKey;
        this.nickname = nickname;
        this.role = role;
        this.profileImgUrl = profileImgUrl;
        this.introduction = introduction;
    }
}
