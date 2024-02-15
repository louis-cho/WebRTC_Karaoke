package com.ssafy.server.chat.service;

import java.time.LocalDateTime;
import java.util.*;

import com.ssafy.server.chat.model.ChatRoom;
import com.ssafy.server.chat.model.UsersChats;
import com.ssafy.server.chat.model.UsersChatsRes;
import com.ssafy.server.chat.repository.ChatRoomRepository;
import com.ssafy.server.chat.repository.UsersChatsRepository;
import com.ssafy.server.user.service.UserService;
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
    @Autowired
    private UserService userService;

    //참여중인 채팅방 목록
    public Page<UsersChats> findAllRoomByUserId(long userId, Pageable pageable){
        return usersChatsRepository.findByUserPkAndStatus(userId, '1', pageable);
    }

    // roomName으로 채팅방 만들기
    public ChatRoom createChatRoom(String roomName, long host, List<String> guests){
        ChatRoom chatRoom = new ChatRoom().create(roomName);
        chatRoom.setRoomPk(chatRoomRepository.save(chatRoom).getRoomPk());
        UsersChats hostChats = new UsersChats(host, chatRoom.getRoomPk(), String.valueOf(LocalDateTime.now()));
        usersChatsRepository.save(hostChats);
        inviteUser(guests, chatRoom.getRoomPk());
        return chatRoom;
    }

    //roomId에 userId로 유저 초대하기
    public void inviteUser(List<String> guests, long roomId){
        String localTime = String.valueOf(LocalDateTime.now());
        for(String guest : guests){
            long pk = userService.getUserPk(UUID.fromString(guest));
            Optional<UsersChats> existingChat = usersChatsRepository.findByUserPkAndRoomPk(pk, roomId);
            if (existingChat.isPresent()) {
                UsersChats guestChats = existingChat.get();
                guestChats.setStatus('1');
                usersChatsRepository.save(guestChats);
            }
            else {
                UsersChats guestChats = new UsersChats(pk, roomId, localTime);
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
    public List<UsersChatsRes> getUserList(long roomId){
        List<UsersChats> tmpList = usersChatsRepository.findByRoomPkAndStatus(roomId, '1');
        List<UsersChatsRes> resList = new ArrayList<>();
        for(UsersChats uc : tmpList) {
            uc.setUserUuid(userService.getUUIDByUserPk((int) uc.getUserPk()));
            UsersChatsRes tmp = new UsersChatsRes(uc);
            resList.add(tmp);
        }
        return resList;
    }

    //roomId로 방 정보 찾기
    public Optional<ChatRoom> getRoomInfo(long roomId) {
        return chatRoomRepository.findById(roomId);
    }
}