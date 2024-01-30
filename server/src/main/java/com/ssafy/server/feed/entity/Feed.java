package com.ssafy.server.feed.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString
public class Feed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int feedId;

    private int userPk;
    private int songId;
    private String content;
    private String thumbnailUrl;
    private String videoUrl;
    private int videoLength;
    private char status;
    private int totalPoint;

    // getters, setters, constructors
}
