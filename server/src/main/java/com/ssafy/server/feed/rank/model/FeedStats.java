package com.ssafy.server.feed.rank.model;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@ToString
@Entity(name = "feed_stats")
public class FeedStats {
    @Id
    @Column(name = "feed_id")
    private int feedId; // 게시글 ID
    @Column(name = "like_count")
    private int likeCount;
    @Column(name = "view_count")
    private int viewCount;
    @Column(name = "score")
    private double score;
}
