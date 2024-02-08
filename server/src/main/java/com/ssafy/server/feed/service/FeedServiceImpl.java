package com.ssafy.server.feed.service;

import com.ssafy.server.feed.model.Feed;
import com.ssafy.server.feed.repository.FeedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class FeedServiceImpl implements FeedService {

    @Autowired
    private final FeedRepository feedRepository;

    @Autowired
    public FeedServiceImpl(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    @Override
    public Page<Feed> getAllFeedList(Pageable pageable) {
        return feedRepository.findAll(pageable);
    }

    @Override
    public Page<Feed> sortRecentFeedList(Pageable pageable) {
        return feedRepository.findAllByOrderByTimeDesc(pageable);
    }

    @Override
    public Page<Feed> sortOldFeedList(Pageable pageable) {
        return feedRepository.findAllByOrderByTimeAsc(pageable);
    }

    @Override
    public Feed getFeedById(int feedId) {
        Optional<Feed> optionalFeed = feedRepository.findById(feedId);
        return optionalFeed.orElse(null);
    }

    @Override
    public Feed createFeed(Feed newFeed) {
        newFeed.setTime(String.valueOf(LocalDateTime.now()));
        // 데이터베이스에 새로운 피드 생성
        return feedRepository.save(newFeed);
    }

    @Override
    public Feed updateFeed(int feedId, Feed updatedFeed) {
        // 데이터베이스에서 기존 피드 조회 후 업데이트
        Optional<Feed> optionalExistingFeed = feedRepository.findById(feedId);

        if (optionalExistingFeed.isPresent()) {
            Feed existingFeed = optionalExistingFeed.get();
            existingFeed.setUserPk(updatedFeed.getUserPk());
            existingFeed.setSongId(updatedFeed.getSongId());
            existingFeed.setContent(updatedFeed.getContent());
            existingFeed.setThumbnailUrl(updatedFeed.getThumbnailUrl());
            existingFeed.setVideoUrl(updatedFeed.getVideoUrl());
            existingFeed.setVideoLength(updatedFeed.getVideoLength());
            existingFeed.setStatus(updatedFeed.getStatus());
            existingFeed.setTotalPoint(updatedFeed.getTotalPoint());

            return feedRepository.save(existingFeed);
        }

        return null;
    }

    @Override
    public boolean deleteFeed(int feedId) {
        // 데이터베이스에서 피드 삭제
        if (feedRepository.existsById(feedId)) {
            feedRepository.deleteById(feedId);
            return true;
        }
        return false;
    }
}
