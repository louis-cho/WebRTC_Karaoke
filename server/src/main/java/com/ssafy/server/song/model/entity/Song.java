package com.ssafy.server.song.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int songId;
    private String singer;
    private String title;
    private String test;

    @OneToOne(mappedBy = "song")
    private SongInfo songInfo;
}
