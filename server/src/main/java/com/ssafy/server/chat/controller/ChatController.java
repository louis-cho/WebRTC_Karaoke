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
    // /pub/chat.message.{roomId} 로 요청하면 브로커를 통해 처리
    // /exchange/chat.exchange/room.{roomId} 를 구독한 클라이언트에 메시지가 전송된다.
    @MessageMapping("chat.enter.{chatRoomId}")
    public void enterUser(@Payload Chat chat, @DestinationVariable String chatRoomId) throws IOException {
        chat.setTime(String.valueOf(LocalDateTime.now()));
        chat.setMessage(chat.getSender() + " 님 " + chat.getRoomId()  +"방 입장!!");
        chatService.saveToRedis(chat, false);
        rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "room." + chatRoomId, chat);
    }

    // redis에 있는 채팅 내역 조회 후, 반환 (아직 db에 저장이 안된 데이터)
    @GetMapping("/room/{chatRoomId}/newMsg")
    public ResponseEntity<List<Object>> loadNewMsg(@PathVariable String chatRoomId) {
        return ResponseEntity.ok(chatService.loadFromRedis(chatRoomId,false, false));
    }

    //redis에 있는 해당 채팅방의 "모든" 채팅 내역 조회 후, 반환
    @GetMapping("/room/{chatRoomId}/oldMsg")
    public ResponseEntity<List<Object>> loadOldMsg( @PathVariable String chatRoomId,
                                                    @RequestParam int page,
                                                    @RequestParam int size) throws JsonProcessingException {
        // Redis에서 해당 채팅방의 전체 데이터를 가져오기
        List<Object> res = chatService.loadFromRedis(chatRoomId, true, false);

        if (res.isEmpty()) {
            // Redis에 데이터가 없으면 JPA에서 데이터를 가져와서 Redis에 저장
            List<Chat> chatList = chatService.loadFromJPA(chatRoomId);
            for (Chat chat : chatList) {
                chatService.saveToRedis(chat, true);
            }

            // 전체 데이터를 Redis에 저장한 후, 페이지네이션을 위해 일부만 가져와서 반환
            res = paginate(Collections.singletonList(chatList), page, size);
        } else {
            // Redis에 데이터가 있으면 해당 페이지의 데이터만 가져와서 반환
            res = paginate(res, page, size);
        }

        return ResponseEntity.ok(res);
    }

    @MessageMapping("chat.message.{chatRoomId}")
    public void sendMessage(@Payload Chat chat, @DestinationVariable String chatRoomId) throws JsonProcessingException {
        chat.setTime(String.valueOf(LocalDateTime.now()));
        chat.setMessage(chat.getMessage());
        chatService.saveToRedis(chat, false);
        rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "room." + chatRoomId, chat);
    }

    //기본적으로 chat.queue가 exchange에 바인딩 되어 있기 때문에 모든 메시지 처리
    @RabbitListener(queues = CHAT_QUEUE_NAME)
    public void receive(Chat chat){
        System.out.println("received : " + chat.getMessage());
    }

    //redis로 페이지네이션 처리
    private List<Object> paginate(List<Object> dataList, int page, int size) {
        int totalSize = dataList.size();
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, totalSize);

        if (startIndex >= totalSize) {
            return Collections.emptyList(); // 페이지 범위를 벗어나면 빈 리스트 반환
        }

        return dataList.subList(startIndex, endIndex);
    }
}