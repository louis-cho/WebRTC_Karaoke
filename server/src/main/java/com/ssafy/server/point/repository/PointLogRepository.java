package com.ssafy.server.point.repository;

import com.ssafy.server.point.model.entity.PointLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PointLogRepository extends JpaRepository<PointLog, Integer> {

    @Query("SELECT COALESCE(SUM(CASE WHEN p.fromUser = :userPK THEN -p.amount " +
            "                         WHEN p.toUser = :userPK THEN p.amount " +
            "                         ELSE 0 END), 0) AS result " +
            "FROM PointLog p " +
            "WHERE (p.fromUser = :userPK OR p.toUser = :userPK) AND p.createdAt >= :startAt AND p.createdAt <= :endAt")
    Integer calculatePoint(@Param("userPK") Integer userPK, @Param("startAt") LocalDateTime startAt, @Param("endAt") LocalDateTime endAt);


}
