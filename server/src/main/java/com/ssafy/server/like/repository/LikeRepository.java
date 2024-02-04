package com.ssafy.server.like.repository;

import com.ssafy.server.syncdata.LikeSyncData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<LikeSyncData, Integer> {

    List<LikeSyncData> findByUserPkAndFeedId(int userPk, int feedId);
    List<LikeSyncData> findByFeedId(int feedId);
}
