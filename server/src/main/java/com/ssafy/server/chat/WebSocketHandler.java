package com.ssafy.server.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper;

    private static final ConcurrentHashMap<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<String, WebSocketSession>();

    private static final ConcurrentHashMap<Long, Set<WebSocketSession>> CHATROOMMAP = new ConcurrentHashMap<Long, Set<WebSocketSession>>();

    public WebSocketHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // A 방법
        // redis로 부터 지금 들어온 session id에 해당하는 유저가 어떤 채팅방에 포함되어야 할 지 알아온다.
        // 알아낸 채팅방 아이디를 갖고
        // 이 유저를 CLIENTS 데이터에 PUT하자
        // CLIENTS.put(session.getId(), session);

        System.out.println("연결된 상태임");
        CLIENTS.put(session.getId(), session);

        // B 방법
        /*
        url -> /chat/{채팅방 아이디}
        채팅방이 있다? => 그럼 그냥 넣어주기
        없으면 => 만들어서 넣기
         */
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        CLIENTS.remove(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        ChatMessageDto chatMessageDto = mapper.readValue(payload, ChatMessageDto.class);

        Long chatRoomId = chatMessageDto.getChatRoomId();
        if (!CHATROOMMAP.containsKey(chatRoomId)) {
            CHATROOMMAP.put(chatRoomId, new HashSet<>());
        }

        Set<WebSocketSession> chatRoomSession = CHATROOMMAP.get(chatRoomId);

        if (chatMessageDto.getMessageType().equals(ChatMessageDto.MessageType.ENTER)) {
            System.out.println(chatMessageDto.getSenderId() + "님 입장-!");
            chatRoomSession.add(session);
        }

        if (chatRoomSession.size() >= 3) {
            removeClosedSession(chatRoomSession);
        }
        sendMessageToChatRoom(chatMessageDto, chatRoomSession);

//        String id = session.getId();  //메시지를 보낸 아이디
//        CLIENTS.entrySet().forEach( arg->{
//            if(!arg.getKey().equals(id)) {  //같은 아이디가 아니면 메시지를 전달합니다.
//                try {
//                    arg.getValue().sendMessage(message);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    private void removeClosedSession(Set<WebSocketSession> chatRoomSession) {
        chatRoomSession.removeIf(sess -> !CLIENTS.contains(sess));
    }

    private void sendMessageToChatRoom(ChatMessageDto chatMessageDto, Set<WebSocketSession> chatRoomSession) {
        System.out.println(chatRoomSession.toString() + "chatting 뿌리기");
        chatRoomSession.parallelStream().forEach(sess -> sendMessage(sess, chatMessageDto));
    }

    public <T> void sendMessage(WebSocketSession session, T message) {
        try {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
        } catch (IOException e) {}
    }
}