//package com.ssafy.server;
//
//import com.ssafy.server.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//public class ScheduledTask {
//
//    @Autowired
//    private UserService userService;
//
//    @Scheduled(fixedRate = 1000) // 1 sec 마다 실행 (단위: 밀리초)
//    public void updateData() {
//        System.out.println("스케쥴 호출");
//        userService.updateData();   // Redis to MySQL
//        userService.clearCache(); // Redis 캐시 초기화
//        userService.getAllUsers(); // MySQL 데이터 갱신 및 Redis에 저장
//        System.out.println("Data updated at: " + System.currentTimeMillis());
//    }
//}