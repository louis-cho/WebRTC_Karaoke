package com.ssafy.server.user.repository;

import com.ssafy.server.user.model.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthRepository extends JpaRepository<UserAuth, Integer> {
    // 추가적인 사용자 정의 쿼리 메서드가 필요하다면 여기에 추가할 수 있습니다.
}
