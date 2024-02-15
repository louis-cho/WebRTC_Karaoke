package com.ssafy.server.friends.model.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class FriendResponseDto {

    private Integer friendId;
    private UUID fromUserKey;
    private UUID toUserKey;
    private String fromUserNickname;
    private String toUserNickname;
    private char status;
}
