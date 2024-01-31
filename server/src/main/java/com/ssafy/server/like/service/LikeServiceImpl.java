package com.ssafy.server.like.service;

import com.ssafy.server.cache.event.LikeEventPublisher;
import com.ssafy.server.feed.rank.model.FeedStats;
import com.ssafy.server.like.document.LikesDocument;
import com.ssafy.server.like.model.Likes;
import com.ssafy.server.like.repository.LikeDocumentRepository;
import com.ssafy.server.like.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private LikeDocumentRepository likeDocumentRepository;

    @Autowired
    LikeEventPublisher likeEventPublisher;

    @Autowired
    private RedisTemplate<String, FeedStats> likeRedis; // Redis Template 추가

    private String prefix = "Like_";


    @Override
    public void createLike(Likes newLike) {
       likeEventPublisher.publishLikeEvent(newLike);
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

            // elastic search 에서도 가져오기
            LikesDocument existingLikesDocument = likeDocumentRepository.findById(updatedLike.getLikeId()).orElse(null);
            existingLikesDocument.setStatus(updatedLike.getStatus());
            likeDocumentRepository.save(existingLikesDocument);

            return likeRepository.save(existingLike);
        }

        return null;
    }

    @Override
    public boolean deleteLike(int likeId) {
        // likeId에 해당하는 좋아요 삭제
        if (likeRepository.existsById(likeId)) {
            likeRepository.deleteById(likeId);
            LikesDocument invalidate = likeDocumentRepository.findById(likeId).orElse(null);
            invalidate.setStatus('X');
            likeDocumentRepository.save(invalidate);

            return true;
        }
        return false;
    }

    private static boolean incrementLike(FeedStats feedStats) {
        int likeSum = feedStats.getLikes();

        if(likeSum < 0)
            return false;

        likeSum += 1;
        feedStats.setLikes(likeSum);

        return true;
    }

    private static boolean decrementLike(FeedStats feedStats) {
        int likeSum = feedStats.getLikes();

        if(likeSum > 0) {
            likeSum -= 1;
            feedStats.setLikes(likeSum);
        }

        return likeSum >= 0;
    }
}

