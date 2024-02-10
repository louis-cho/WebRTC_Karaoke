package com.ssafy.server.notification.service;

import com.ssafy.server.notification.dto.NotificationRequestDto;
import com.ssafy.server.notification.dto.NotificationResponseDto;
import com.ssafy.server.notification.entity.Notification;

import java.util.List;

public interface NotificationService {
    //    makeService
    Notification makeNotification(NotificationRequestDto notificationDto);

    Integer countUnReadNotifications(int toUser);

    List<NotificationResponseDto> getNotificationList(Integer toUser);

    NotificationResponseDto getNotification(Integer notificationId);

    void readNotification(Integer notificationId);

    void readAllNotifications(List<Integer> notificationIds);
}
