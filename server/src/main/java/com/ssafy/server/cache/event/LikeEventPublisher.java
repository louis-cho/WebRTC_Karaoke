package com.ssafy.server.cache.event;

import com.ssafy.server.like.model.Likes;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeEventPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void publishLikeEvent(Likes like) {
        try {
            // RabbitMQ를 통해 Elastic Search와 Redis에 저장할 like 데이터 전달
            rabbitTemplate.convertAndSend("like_events", like);

            // RabbitMQ에 데이터
        } catch (Exception e) {
            // 예외 발생 시 DLQ에 메시지 보관
            handleException(e, like);
        }
    }

    private void handleException(Exception e, Likes like) {
        // Dead Letter Queue에 저장
        storeMessageInDLQ(like.toString());
    }

    private void storeMessageInDLQ(String message) {
        rabbitTemplate.convertAndSend("like_events_dlq", message);
    }
}
