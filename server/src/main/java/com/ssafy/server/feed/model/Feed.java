package com.ssafy.server.feed.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * feed 모델 클래스
 */
@Getter
@Setter
@ToString
@Entity(name = "feed")
@Table(name = "feed")
public class Feed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id")
    private Integer feedId;
    @Column(name = "user_pk")
    private Integer userPk;
    @Column(name = "song_id")
    private Integer songId;
    @Column(name = "title")
    private String title;
    @Column(name = "content")
    private String content;
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    @Column(name = "video_url")
    private String videoUrl;
    @Column(name = "video_length")
    private Integer videoLength;
    @Column(name = "status")
    private Character status;
    @Column(name = "total_point")
    private Integer totalPoint;
    @Column(name = "time")
    private String time;

    // getters, setters, constructors
}
