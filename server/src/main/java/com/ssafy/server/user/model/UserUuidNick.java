package com.ssafy.server.user.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserUuidNick {
    private UUID userUuid;
    private String nickname;
}
