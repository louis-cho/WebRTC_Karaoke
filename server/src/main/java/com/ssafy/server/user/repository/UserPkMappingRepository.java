package com.ssafy.server.user.repository;

import com.ssafy.server.user.model.UserPkMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPkMappingRepository extends JpaRepository<UserPkMapping, String> {


}
