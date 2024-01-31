package com.ssafy.server.chat.service;

import com.ssafy.server.chat.model.ChatDTO;
import com.ssafy.server.chat.repository.ChatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;

    public void saveToJPA(ChatDTO chatDTO){
        chatRepository.save(chatDTO);
    }
}
