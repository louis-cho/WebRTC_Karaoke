package com.ssafy.server.chat.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.server.chat.model.Chat;
import com.ssafy.server.chat.repository.ChatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String CHAT_KEY = "chat:%s";
    private static final String OLD_CHAT_KEY = "oldChat:%s";

    public void saveToJPA(List<Object> chatJsonList) throws JsonProcessingException {
        assert chatJsonList != null;
        Collections.reverse(chatJsonList);
        for (Object chatJson : chatJsonList) {
            Chat chat = objectMapper.readValue(chatJson.toString(), Chat.class);
            chatRepository.save(chat);
        }
    }

    public List<Chat> loadFromJPA(String roomId){
        return chatRepository.findByRoomIdOrderByTime(roomId);
    }
    public void saveToRedis(Chat chat, Boolean flag) throws JsonProcessingException {
        String chatJson = objectMapper.writeValueAsString(chat);
        String key = String.format(CHAT_KEY, chat.getRoomId());
        if(flag) key = String.format(OLD_CHAT_KEY, chat.getRoomId());
        redisTemplate.opsForList().leftPush(key, chatJson);
    }

    public List<Object> loadFromRedis(String roomId, Boolean flag) {
        String key = String.format(CHAT_KEY, roomId);
        if(flag) key = String.format(OLD_CHAT_KEY, roomId);
        return redisTemplate.opsForList().range(key, 0, -1);
    }
}
