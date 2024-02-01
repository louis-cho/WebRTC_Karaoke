package com.ssafy.server.chat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.server.chat.model.Chat;
import com.ssafy.server.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatService chatService;

    private final RabbitTemplate rabbitTemplate;
    private final static String CHAT_EXCHANGE_NAME = "chat.exchange";
    private final static String CHAT_QUEUE_NAME = "chat.queue";
    // /pub/chat.message.{roomId} 로 요청하면 브로커를 통해 처리
    // /exchange/chat.exchange/room.{roomId} 를 구독한 클라이언트에 메시지가 전송된다.
    @MessageMapping("chat.enter.{chatRoomId}")
    public void enterUser(@Payload Chat chat, @DestinationVariable String chatRoomId) throws IOException {
        chat.setTime(String.valueOf(LocalDateTime.now()));
        chat.setMessage(chat.getSender() + " 님 " + chat.getRoomId()  +" 입장!!");
        chatService.loadFromJPA("4");
//        chatService.loadFromRedis(chat.getRoomId());
//        chatService.saveToRedis(chat);
//        chatService.saveToJPA(chat);
        rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "room." + chatRoomId, chat);
    }

    @MessageMapping("chat.message.{chatRoomId}")
    public void sendMessage(@Payload Chat chat, @DestinationVariable String chatRoomId) throws JsonProcessingException {
        chat.setTime(String.valueOf(LocalDateTime.now()));
        chat.setMessage(chat.getMessage());
        chatService.saveToRedis(chat);
//        chatService.saveToJPA(chat);
        rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "room." + chatRoomId, chat);

    }

    //기본적으로 chat.queue가 exchange에 바인딩 되어있기 때문에 모든 메시지 처리
    @RabbitListener(queues = CHAT_QUEUE_NAME)
    public void receive(Chat chat){
        System.out.println("received : " + chat.getMessage());
    }
}