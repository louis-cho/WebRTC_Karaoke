package com.ssafy.server.chat.service;

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.server.chat.model.ChatRoom;
import com.ssafy.server.chat.model.UsersChats;
import com.ssafy.server.chat.repository.ChatRoomRepository;
import com.ssafy.server.chat.repository.UsersChatsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private UsersChatsRepository usersChatsRepository;

    //참여중인 채팅방 목록
    public List<UsersChats> findAllRoomByUserId(long userId){
        return usersChatsRepository.findByUserPkAndStatus(userId, '1');
    }

    // roomName으로 채팅방 만들기
    public ChatRoom createChatRoom(String roomName, long host, List<Long> guests){
        ChatRoom chatRoom = new ChatRoom().create(roomName, guests.size());
        chatRoom.setRoomPk(chatRoomRepository.save(chatRoom).getRoomPk());
        UsersChats hostChats = new UsersChats(host, chatRoom.getRoomPk());
        usersChatsRepository.save(hostChats);
        inviteUser(guests, chatRoom.getRoomPk());
        return chatRoom;
    }

    //roomId에 userId로 유저 초대하기
    public void inviteUser(List<Long> guests, long roomId){
        for(Long guest : guests){
            Optional<UsersChats> existingChat = usersChatsRepository.findByUserPkAndRoomPk(guest, roomId);
            if (existingChat.isPresent()) {
                UsersChats guestChats = existingChat.get();
                guestChats.setStatus('1');
                usersChatsRepository.save(guestChats);
            }
            else {
                UsersChats guestChats = new UsersChats(guest, roomId);
                usersChatsRepository.save(guestChats);
            }
        }
    }

    //채팅방에서 나가기
    public void exitRoom(long userId, long roomId){
        Optional<UsersChats> existingChat = usersChatsRepository.findByUserPkAndRoomPk(userId, roomId);
        if(existingChat.isPresent()){
            UsersChats guestChats = existingChat.get();
            guestChats.setStatus('0');
            usersChatsRepository.save(guestChats);
        }
    }

    //roomId로 참여자 목록 구하기
    public List<UsersChats> getUserList(long roomId){
        return usersChatsRepository.findByRoomPkAndStatus(roomId, '1');
    }
}