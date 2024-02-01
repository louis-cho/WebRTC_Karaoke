package com.ssafy.server.like.service;

import com.ssafy.server.like.model.Likes;
import com.ssafy.server.like.repository.LikeRepository;
import com.ssafy.server.syncdata.LikeSyncData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private LikeRepository likeRepository;


    @Override
    public void createLike(Likes newLike) {

    }

    @Override
    public Likes getLikeById(int likeId) {
        // likeId에 해당하는 좋아요 조회
        Optional<Likes> optionalLike = likeRepository.findById(likeId);
        return optionalLike.orElse(null);
    }

    @Override
    public Likes updateLike(Likes updatedLike) {
        // likeId에 해당하는 좋아요 업데이트
        Optional<Likes> optionalExistingLike = likeRepository.findById(updatedLike.getLikeId());

        if (optionalExistingLike.isPresent()) {
            Likes existingLike = optionalExistingLike.get();
            existingLike.setStatus(updatedLike.getStatus());

            return likeRepository.save(existingLike);
        }

        return null;
    }

    @Override
    public boolean deleteLike(int likeId) {
        // likeId에 해당하는 좋아요 삭제
        if (likeRepository.existsById(likeId)) {
            likeRepository.deleteById(likeId);

            return true;
        }
        return false;
    }

    @Override
    public void syncToDB(Integer likeId, LikeSyncData likeSyncData) {

        Likes likes = new Likes();
        likes.setLikeId(likeSyncData.getLikeId());
        likes.setFeedId(likeSyncData.getFeedId());
        likes.setUserPk(likeSyncData.getUserPk());
        likes.setStatus(likeSyncData.getStatus());

        Likes fetched = likeRepository.findById(likeId).orElse(null);
        if(fetched == null)
            likeRepository.save(likes);
        else {
            fetched.setStatus(likes.getStatus());
            likeRepository.save(fetched);
        }
    }

}

