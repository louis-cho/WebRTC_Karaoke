package com.ssafy.server.notification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationResponseDto {
    private Integer notificationId;
    private String message;
    private String info;
    private String type;
}
