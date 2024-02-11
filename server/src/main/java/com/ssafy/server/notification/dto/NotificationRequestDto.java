package com.ssafy.server.notification.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequestDto {
    private Integer notificationId;
    private Integer toUser;
    private Integer fromUser;
    private String info;
    private String type;
    private Character status;

}
