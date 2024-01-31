package com.ssafy.server.like.service;

import com.ssafy.server.like.model.Like;
import com.ssafy.server.like.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;

    @Autowired
    public LikeServiceImpl(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    @Override
    public Like createLike(Like newLike) {
        // 데이터베이스에 새로운 좋아요 생성
        return likeRepository.save(newLike);
    }

    @Override
    public Like getLikeById(int likeId) {
        // likeId에 해당하는 좋아요 조회
        Optional<Like> optionalLike = likeRepository.findById(likeId);
        return optionalLike.orElse(null);
    }

    @Override
    public Like updateLike(int likeId, Like updatedLike) {
        // likeId에 해당하는 좋아요 업데이트
        Optional<Like> optionalExistingLike = likeRepository.findById(likeId);

        if (optionalExistingLike.isPresent()) {
            Like existingLike = optionalExistingLike.get();
            existingLike.setUserPk(updatedLike.getUserPk());
            existingLike.setFeedId(updatedLike.getFeedId());
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
}

