package com.ssafy.server.song.model.entity;

import javax.persistence.*;

@Entity
public class SongInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int songId;
    private int length;
    private String genre;
    private String mmlData;
    private String songUrl;
    private String albumCover;

    @OneToOne
    @JoinColumn(name = "songId")
    private Song song;
}
