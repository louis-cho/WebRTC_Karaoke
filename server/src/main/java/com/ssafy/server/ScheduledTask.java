package com.ssafy.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssafy.server.chat.model.Chat;
import com.ssafy.server.chat.service.ChatService;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduledTask {

    @Autowired
    private ChatService chatService;

    @Scheduled(fixedRate = 5000) // 1 sec 마다 실행 (단위: 밀리초)
    public void updateData() throws JsonProcessingException {
        System.out.println("스케쥴 호출");

//        userService.updateData();   // Redis to MySQL
        List<Chat> list = chatService.loadFromJPA("4");
        for(Chat chat : list) {
            chatService.saveToRedis(chat, true);
        }
//        chatService.loadFromRedis("4", true, false);
        
//        chatService.saveToJPA(chatService.loadFromRedis("4", true, true));
//        userService.clearCache(); // Redis 캐시 초기화
//        userService.getAllUsers(); // MySQL 데이터 갱신 및 Redis에 저장
        System.out.println("Data updated at: " + System.currentTimeMillis());
    }
}