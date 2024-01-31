package com.ssafy.server.feed.rank.document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;


@Document(indexName = "feed_stats")
@Getter
@Setter
@ToString
public class FeedStatsDocument {

    @Id
    private int feedId;

    private int likes;

    private int views;

    private double score;

    // 생성자, getter, setter 등 필요한 메서드 추가

    public void init() {
        likes = 0;
        views = 0;
        score = 0.0;
    }

    public int increaseLike() {
        likes += 1;
        return likes;
    }

    public int decreaseLike() {
        likes -= 1;
        return likes;
    }

    public int increaseView() {
        views += 1;
        return views;
    }

    public int decreaseView() {
        views -= 1;
        return views;
    }


}