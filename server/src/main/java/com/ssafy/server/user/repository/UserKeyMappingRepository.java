package com.ssafy.server.user.repository;

import com.ssafy.server.user.model.UserKeyMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserKeyMappingRepository extends JpaRepository<UserKeyMapping, UUID> {

    Optional<UserKeyMapping> findByUserPk(int userPk);

    Optional<UserKeyMapping> findByUuid(UUID uuid);

}
