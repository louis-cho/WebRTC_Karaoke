package com.ssafy.server.user.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class UserPkMapping {

    @Id
    private String userId;

    private int userPk;
}
