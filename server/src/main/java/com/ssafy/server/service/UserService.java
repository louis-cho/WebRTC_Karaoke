package com.ssafy.server.service;

import com.ssafy.server.model.User;
import com.ssafy.server.repository.UserRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// UserService.java
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static String getComplexRandomMail() {

        return RandomString.make(12);
    }

    public void saveRandomUsersToRedis() {

        redisTemplate.delete("users");
        System.out.println("[user deleted]");

        List<User> randomUsers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String username = getComplexRandomMail();
            String email = getComplexRandomMail() + "@gmail.com";

            User user = new User(username, email);
            randomUsers.add(user);
        }

        // Save to Redis
        redisTemplate.opsForValue().set("users", randomUsers);
        System.out.println("[user saved]");
    }

    // Redis 캐시를 지우지 않고 내용을 MySQL에 적어주는 메서드
    public void updateData() {
        // Redis 캐시에서 데이터를 불러옴
        List<User> cachedUsers = (List<User>) redisTemplate.opsForValue().get("users");

        if (cachedUsers != null) {
            // MySQL에 데이터 저장
            userRepository.saveAll(cachedUsers);
            System.out.println("Data saved to MySQL");
        } else {
            System.out.println("No data found in Redis cache");
        }
    }

    @CacheEvict(value = "users", allEntries = true)
    public void clearCache() {
        // Clear the cache when updating MySQL data
        System.out.println("redis cleared!!");
    }

    // 사용자 목록을 가져오는 메서드
    public List<User> getAllUsers() {
        // 먼저 Redis 캐시에서 데이터를 조회
        List<User> cachedUsers = (List<User>) redisTemplate.opsForValue().get("users");

        if (cachedUsers != null) {
            // 캐시에 데이터가 있으면 캐시에서 반환
            System.out.println("Fetching from Redis Cache");
            return cachedUsers;
        } else {
            // 캐시에 데이터가 없으면 MySQL에서 조회 후 Redis에 저장
            List<User> users = userRepository.findAll();
            redisTemplate.opsForValue().set("users", users);
            System.out.println("Fetching from MySQL and storing in Redis");
            return users;
        }
    }
}