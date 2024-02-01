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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

    public void saveToJPA(Chat chat){
        chatRepository.save(chat);
    }

    public void loadFromJPA(String roomId) throws JsonProcessingException {
        List<Chat> list = chatRepository.findChatByRoomId(roomId);
//        for(Chat chat : list) System.out.println(chat.toString());
        String key = String.format(OLD_CHAT_KEY, roomId);
        for(Chat chat : list) {
            String chatJson = objectMapper.writeValueAsString(chat);
            redisTemplate.opsForList().leftPush(key, chatJson);
        }
    }
    public void saveToRedis(Chat chat) throws JsonProcessingException {
        String chatJson = objectMapper.writeValueAsString(chat);
        String key = String.format(CHAT_KEY, chat.getRoomId());
        redisTemplate.opsForList().leftPush(key, chatJson);
    }

    public void loadFromRedis(String roomId) throws IOException {
        String key = String.format(CHAT_KEY, roomId);
        List<Object> chatJsonList = redisTemplate.opsForList().range(key, 0, -1);

        //데이터 저장 Test
        List<Chat> chatList = new ArrayList<>();
        assert chatJsonList != null;
        for (Object chatJson : chatJsonList) {
//            System.out.println(chatJson.toString());
            Chat chat = objectMapper.readValue(chatJson.toString(), Chat.class);
            System.out.println(chat);
            saveToJPA(chat);
//            Chat chat = objectMapper.readValue((JsonParser) chatJson, Chat.class);
//            System.out.println(chat);
//            chatList.add(chat);
        }
    }
}
