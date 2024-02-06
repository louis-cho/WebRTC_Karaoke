package com.ssafy.server.user.repository;

import com.ssafy.server.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByNickname(String nickname);

    User findByUserPk(int userPk);

}

