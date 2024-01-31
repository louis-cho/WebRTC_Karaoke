package com.ssafy.server.cache.event;

// RabbitMQListener.java
import com.ssafy.server.cache.elastic.ElasticSearchService;
import com.ssafy.server.cache.redis.RedisService;
import com.ssafy.server.like.model.Likes;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LikeEventSubscriber {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    ElasticSearchService elasticSearchService;

    @Autowired
    RedisService redisService;

    // RabbitMQ 큐인 like_events를 감지하여 처리
    @RabbitListener(queues = "like_events")
    public void handleLikeEvent(Likes like) {
        try {
            // 엘라스틱서치 업데이트
            elasticSearchService.updateLikeStatistics(like);

            redisService.saveLike(like);
            // Redis 업데이트
            redisService.updateLikeStatistics(like);
        } catch (Exception e) {
            // 예외 발생 시 DLQ에 메시지 보관
            handleException(e, like);
        }
    }

    // 한 번에 안된 녀석들 핸들링
    @RabbitListener(queues = "like_events_dlq")
    public void handleLikeEventInDLQ(Likes like) {
        try {
            // 엘라스틱서치 업데이트
            elasticSearchService.updateLikeStatistics(like);

            // Redis 업데이트
            redisService.updateLikeStatistics(like);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void handleException(Exception e, Likes like) {
        // Dead Letter Queue(DLQ)에 저장 등
        storeMessageInDLQ(like);
    }

    private void storeMessageInDLQ(Likes like) {
        rabbitTemplate.convertAndSend("like_events_dlq", like);
    }
}
