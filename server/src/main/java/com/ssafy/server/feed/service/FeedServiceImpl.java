package com.ssafy.server.feed.service;

import com.ssafy.server.feed.model.Feed;
import com.ssafy.server.feed.model.FeedResponse;
import com.ssafy.server.feed.repository.FeedRepository;
import com.ssafy.server.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FeedServiceImpl implements FeedService {

    @Autowired
    private final FeedRepository feedRepository;

    @Autowired
    private UserService userService;

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
    public Feed updateFeed(int feedId, FeedResponse updatedFeed) {
        // 데이터베이스에서 기존 피드 조회 후 업데이트
        Optional<Feed> optionalExistingFeed = feedRepository.findById(feedId);

        Integer userPk = userService.getUserPk(updatedFeed.getUserUUID());

        if (optionalExistingFeed.isPresent()) {
            Feed existingFeed = optionalExistingFeed.get();
            existingFeed.setUserPk(userPk);
            existingFeed.setSongId(updatedFeed.getSongId());
            existingFeed.setTitle(updatedFeed.getTitle());
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

    @Override
    public List<FeedResponse> getFeedByUserPk(int userPk) {
         List<Feed> list =  feedRepository.findByUserPk(userPk).get();
         List<FeedResponse> res = new ArrayList<>();
         for(Feed feed : list){
             FeedResponse tmp = new FeedResponse();
             tmp.setFeedId(feed.getFeedId());
             tmp.setUserUUID(userService.getUUIDByUserPk(userPk));
             tmp.setTime(feed.getTime());
             tmp.setStatus(feed.getStatus());
             tmp.setTitle(feed.getTitle());
             tmp.setContent(feed.getContent());
             tmp.setSongId(feed.getSongId());
             tmp.setThumbnailUrl(feed.getThumbnailUrl());
             tmp.setTotalPoint(feed.getTotalPoint());
             tmp.setVideoLength(feed.getVideoLength());
             tmp.setVideoUrl(feed.getVideoUrl());
             res.add(tmp);
         }
         return res;
    }
}
