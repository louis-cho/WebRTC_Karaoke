package com.ssafy.server.feed.model;

import com.ssafy.server.like.model.Like;
import com.ssafy.server.syncdata.LikeSyncData;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "feed")
    private List<LikeSyncData> likes = new ArrayList<>();

    // getters, setters, constructors
}
