package com.ssafy.server.point.repository;

import com.ssafy.server.point.model.entity.PointMemo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointMemoRepository extends JpaRepository<PointMemo, Integer> {

    // userPK를 이용하여 생성시간 기준으로 정렬된 PointMemo를 최대 2개 반환
    List<PointMemo> findTop2ByUserPKOrderByLastTimeDesc(Integer userPK);
}
