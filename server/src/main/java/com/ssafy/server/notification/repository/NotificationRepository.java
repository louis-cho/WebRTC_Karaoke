package com.ssafy.server.notification.repository;

import com.ssafy.server.notification.dto.NotificationResponseDto;
import com.ssafy.server.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    Integer countByStatusAndToUser(char status, Integer ToUser);
    List<Notification> findAllByStatusAndToUserOrderByCreatedAtDesc(char status, Integer toUser);
}
