package com.ssafy.server.chat.service;

import java.time.LocalDateTime;
import java.util.*;

import com.ssafy.server.chat.model.ChatRoom;
import com.ssafy.server.chat.model.UsersChats;
import com.ssafy.server.chat.repository.ChatRoomRepository;
import com.ssafy.server.chat.repository.UsersChatsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private UsersChatsRepository usersChatsRepository;

    //참여중인 채팅방 목록
    public Page<UsersChats> findAllRoomByUserId(long userId, Pageable pageable){
        return usersChatsRepository.findByUserPkAndStatus(userId, '1', pageable);
    }

    // roomName으로 채팅방 만들기
    public ChatRoom createChatRoom(String roomName, long host, List<Long> guests){
        ChatRoom chatRoom = new ChatRoom().create(roomName);
        chatRoom.setRoomPk(chatRoomRepository.save(chatRoom).getRoomPk());
        UsersChats hostChats = new UsersChats(host, chatRoom.getRoomPk(), String.valueOf(LocalDateTime.now()));
        usersChatsRepository.save(hostChats);
        inviteUser(guests, chatRoom.getRoomPk());
        return chatRoom;
    }

    //roomId에 userId로 유저 초대하기
    public void inviteUser(List<Long> guests, long roomId){
        String localTime = String.valueOf(LocalDateTime.now());
        for(Long guest : guests){
            Optional<UsersChats> existingChat = usersChatsRepository.findByUserPkAndRoomPk(guest, roomId);
            if (existingChat.isPresent()) {
                UsersChats guestChats = existingChat.get();
                guestChats.setStatus('1');
                usersChatsRepository.save(guestChats);
            }
            else {
                UsersChats guestChats = new UsersChats(guest, roomId, localTime);
                usersChatsRepository.save(guestChats);
            }
        }
    }
    
    //입장
    public void enterOutInfo(long roomId, long userId){
        Optional<UsersChats> existingChat = usersChatsRepository.findByUserPkAndRoomPk(userId, roomId);
        if(existingChat.isPresent()){
            UsersChats usersChats = existingChat.get();
            usersChats.setTime(String.valueOf(LocalDateTime.now()));
            usersChatsRepository.save(usersChats);
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

    //roomId로 방 정보 찾기
    public Optional<ChatRoom> getRoomInfo(long roomId) {
        return chatRoomRepository.findById(roomId);
    }
}