package com.ssafy.server.notification.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequestDto {
    private Integer notificationId;
    private String toUserKey;
    private String info;
    private String type;
    private Character status;

    private Integer toUser; //백에서 삽입
    private Integer fromUser; //백에서 삽입

}
