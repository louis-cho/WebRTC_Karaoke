package com.ssafy.server.user.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@Entity(name = "user_auth")
@Table(name = "user_auth")
public class UserAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_pk")
    private int userPk;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_password")
    private String userPassword;

    @Column(name = "status")
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
