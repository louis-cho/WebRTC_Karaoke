package com.ssafy.server.user.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class UserAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userPk;

    private String userId;
    private String userPassword;
    private char status;

    // Constructors, getters, setters, etc.

    public UserAuth() {
        // Default constructor required by JPA
    }

    public UserAuth(String userId, String userPassword) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.status = 'O';
    }

    public UserAuth(String userId, String userPassword, char status) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.status = status;
    }
}
