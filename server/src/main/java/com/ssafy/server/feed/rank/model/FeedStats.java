package com.ssafy.server.feed.rank.model;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString
public class FeedStats {
    @Id
    private int feedId; // 게시글 ID
    private int likes;
    private int views;
    private double score;
}
