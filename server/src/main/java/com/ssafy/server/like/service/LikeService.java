package com.ssafy.server.like.service;

public interface LikeService {
    void save(int feedId, int userPk);
    int findAllByFeedId(int feedId);
    void delete(int feedId, int userPk);

    void saveToMySQL ();
}
