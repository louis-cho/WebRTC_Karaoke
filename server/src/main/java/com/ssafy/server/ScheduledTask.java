package com.ssafy.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssafy.server.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ScheduledTask {

    @Autowired
    private ChatService chatService;

    @Scheduled(fixedRate = 600000) // 1 sec 마다 실행 (단위: 밀리초)
    public void updateData() throws JsonProcessingException {

        //일정 주기마다 redis에 있는 신규 채팅 데이터를 MySQL에 저장 / 그 후 redis Cache 삭제
        Set<String> keySets = chatService.getRedisKeys();
        for(String keyName : keySets){
            if(keyName.startsWith("chat")) {
                String keyNumStr = keyName.replaceAll("[^0-9]", "");
                chatService.saveToJPA(chatService.loadFromRedis(keyNumStr, false, true));
            }
            else if(keyName.startsWith("oldChat")){
                chatService.deleteKeyInRedis(keyName);
            }
        }
    }
}