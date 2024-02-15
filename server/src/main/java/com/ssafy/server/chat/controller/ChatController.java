package com.ssafy.server.chat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssafy.server.chat.model.Chat;
import com.ssafy.server.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    private final RabbitTemplate rabbitTemplate;
    private final static String CHAT_EXCHANGE_NAME = "chat.exchange";
    private final static String CHAT_QUEUE_NAME = "chat.queue";
    // /pub/chat.message.{roomId} 요청, 브로커를 통해 처리
    // /exchange/chat.exchange/room.{roomId}를 구독한 클라이언트에 메시지 전송
    @MessageMapping("chat.typing.{chatRoomId}")
    public void enterUser(@Payload Chat chat, @DestinationVariable String chatRoomId) throws IOException {
        rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "room." + chatRoomId, chat);
    }

    @GetMapping("/room/{chatRoomId}/newMsg")
    public ResponseEntity<List<Object>> loadNewMsg(@PathVariable String chatRoomId) {
        return ResponseEntity.ok(chatService.loadFromRedis(chatRoomId,false, false));
    }

    @GetMapping("/room/{chatRoomId}/oldMsg")
    public ResponseEntity<List<Object>> loadOldMsg( @PathVariable String chatRoomId,
                                                    @RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "10") int size) throws JsonProcessingException {

        List<Object> res = chatService.loadFromRedis(chatRoomId, true, false);

        if (res.isEmpty()) {
            List<Chat> chatList = chatService.loadFromJPA(chatRoomId);
            for (Chat chat : chatList) {
                chatService.saveToRedis(chat, true);
            }

            res = chatService.loadFromRedis(chatRoomId, true, false);
        }

        int maxPage = (res.size() + size - 1) / size;

        if (page > maxPage) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        page = Math.min(page, maxPage);

        int startIndex = (maxPage - page) * size;
        int endIndex = Math.min(startIndex + size, res.size());
        List<Object> paginatedRes = res.subList(startIndex, endIndex);
        return ResponseEntity.ok(paginatedRes);
    }

    @MessageMapping("chat.message.{chatRoomId}")
    public void sendMessage(@Payload Chat chat, @DestinationVariable String chatRoomId) throws JsonProcessingException {
        chat.setTime(String.valueOf(LocalDateTime.now()));
        chat.setMessage(chat.getMessage());
        if(!chat.getType().toString().equals("TYPE")) {
            chatService.saveToRedis(chat, false);
        }
        else{
        }
        rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "room." + chatRoomId, chat);
    }

    @RabbitListener(queues = CHAT_QUEUE_NAME)
    public void receive(Chat chat){}
}