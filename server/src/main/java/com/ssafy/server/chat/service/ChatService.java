package com.ssafy.server.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.server.chat.model.Chat;
import com.ssafy.server.chat.repository.ChatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
        List<Chat> list = new ArrayList<>();
        for (Object chatJson : chatJsonList) {
            Chat chat = objectMapper.readValue(chatJson.toString(), Chat.class);
            list.add(chat);
        }
        chatRepository.saveAll(list);
    }

    //해당 방에 있는 모든 데이터 로딩
    public List<Chat> loadFromJPA(String roomId){
        return chatRepository.findByRoomIdOrderByTimeDesc(roomId);
    }
    public void saveToRedis(Chat chat, Boolean flag) throws JsonProcessingException {
        String chatJson = objectMapper.writeValueAsString(chat);
        String key = String.format(CHAT_KEY, chat.getRoomId());
        if(flag) key = String.format(OLD_CHAT_KEY, chat.getRoomId());
        redisTemplate.opsForList().leftPush(key, chatJson);
    }

    public List<Object> loadFromRedis(String roomId, Boolean flag, Boolean delete) {
        String key = String.format(CHAT_KEY, roomId);
        if(flag) key = String.format(OLD_CHAT_KEY, roomId);
        List<Object> list = redisTemplate.opsForList().range(key, 0, -1);
        if(delete) redisTemplate.delete(key);
        return list;
    }

    public Set<String> getRedisKeys(){
        return redisTemplate.keys("*");
    }

    public void deleteKeyInRedis(String key){
        redisTemplate.delete(key);
    }
}
