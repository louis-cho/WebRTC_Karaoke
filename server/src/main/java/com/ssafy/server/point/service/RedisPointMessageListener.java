package com.ssafy.server.point.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

@Service
public class RedisPointMessageListener extends KeyExpirationEventMessageListener {

    private final RedisTemplate<String, Object> redisTemplate;

    private final PointServiceImpl pointService;

    public RedisPointMessageListener(RedisMessageListenerContainer listenerContainer, RedisTemplate<String, Object> redisTemplate, PointServiceImpl pointService) {
        super(listenerContainer);
        this.redisTemplate = redisTemplate;
        this.pointService = pointService;
    }
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = new String(message.getBody());
        // 만료된 키를 기반으로 DB에 저장하는 로직을 수행
        String expiredValue = (String) redisTemplate.opsForValue().get(expiredKey);
        Integer userPK = Integer.parseInt(expiredKey.replace("point_", "").replace("\"",""));
        System.out.println(userPK);
       pointService.refreshMemo(userPK);
    }

    private void saveToDatabase(String key) {
        // 만료된 키를 기반으로 DB에 저장하는 로직을 구현
        System.out.println("Key '" + key + "' expired. Saving to the database...");
    }
}
