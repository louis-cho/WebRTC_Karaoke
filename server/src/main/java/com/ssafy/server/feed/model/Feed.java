package com.ssafy.server.feed.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity(name = "feed")
@Table(name = "feed")
public class Feed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id")
    private int feedId;
    @Column(name = "user_pk")
    private int userPk;
    @Column(name = "song_id")
    private int songId;
    @Column(name = "content")
    private String content;
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    @Column(name = "video_url")
    private String videoUrl;
    @Column(name = "video_length")
    private int videoLength;
    @Column(name = "status")
    private char status;
    @Column(name = "total_point")
    private int totalPoint;

    // getters, setters, constructors
}
