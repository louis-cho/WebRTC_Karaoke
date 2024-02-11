package com.ssafy.server.notification.service;

import com.ssafy.server.notification.dto.NotificationRequestDto;
import com.ssafy.server.notification.dto.NotificationResponseDto;
import com.ssafy.server.notification.entity.Notification;
import com.ssafy.server.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{

    private final NotificationRepository notificationRepository;

    @Override
    public Notification makeNotification(NotificationRequestDto notificationDto) {
        Notification notification = new Notification();
        notification.setToUser(notificationDto.getToUser());
        notification.setFromUser(notificationDto.getFromUser());
        notification.setInfo(notificationDto.getInfo());
        notification.setType(notificationDto.getType());
        notification.setStatus(notificationDto.getStatus());

        StringBuilder sb = new StringBuilder();
        if("friend".equals(notification.getType())){
            sb.append(notification.getFromUser()).append("님이 친구요청을 보냈습니다.");
        }
        else if("like".equals(notification.getType())){
            sb.append(notification.getFromUser()).append("님이 게시글").append(notification.getInfo()).append("번에 좋아요를 눌렀습니다.");
        }
        else if("comment".equals(notification.getType())){
            sb.append(notification.getFromUser()).append("님이 게시글").append(notification.getInfo()).append("번에 댓글을 달았습니다.");
        }
        else if("karaoke".equals(notification.getType())){
            sb.append(notification.getFromUser()).append("님이 노래방으로 초대했습니다.");
        }
        else{
            System.out.println("잘못된 구독 생성 요청입니다.");
        }
        notification.setMessage(sb.toString());

        return notificationRepository.save(notification);
    }

    @Override
    public Integer countUnReadNotifications(int toUser) {
        return notificationRepository.countByStatusAndToUser('0',toUser);
    }

    @Override
    public List<NotificationResponseDto> getNotificationList(Integer toUser) {
        return notificationRepository.findAllByStatusAndToUserOrderByCreatedAtDesc('0', toUser).stream()
                .map(notification -> {
                    NotificationResponseDto dto = new NotificationResponseDto();
                    dto.setNotificationId(notification.getNotificationId());
                    dto.setMessage(notification.getMessage());
                    dto.setInfo(notification.getInfo());
                    dto.setType(notification.getType());
                    // 필요한 경우 더 많은 필드를 설정
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public NotificationResponseDto getNotification(Integer notificationId) {
        NotificationResponseDto dto = new NotificationResponseDto();
        Notification notification = notificationRepository.findById(notificationId).orElse(null);;
        if(notification == null)
            return null;
        dto.setNotificationId(notification.getNotificationId());
        dto.setMessage(notification.getMessage());
        dto.setInfo(notification.getInfo());
        dto.setType(notification.getType());
        return dto;
    }

    @Override
    public void readNotification(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if(notification == null){
            System.out.println("잘못된 알림 클릭");
        }
        else{
            notification.setStatus('1');
            notificationRepository.save(notification);
        }
    }

    @Override
    public void readAllNotifications(List<Integer> notificationIds) {
        List<Notification> notificationList = notificationRepository.findAllById(notificationIds);
        for(Notification notification : notificationList){
            notification.setStatus('1');
        }

        notificationRepository.saveAll(notificationList);
    }
}
