package com.ssafy.server.controller;

import com.ssafy.server.model.User;
import com.ssafy.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/testa")
    public String createRandomUserInCache() {
        userService.saveRandomUsersToRedis();
        // redis to mysql => scheduledtask 10 sec
        return "Redis to MySQL";
    }

    @RequestMapping("/testb")
    public String MySQL2Redis() {
        userService.clearCache();   // redis clear
        List<User> users = userService.getAllUsers();   // load from mysql

        String result = "";

        for(int i=0; i<Math.min(3, users.size()); i++) {
            result += users.get(i) + "\n";
        }
        return result;
    }
}