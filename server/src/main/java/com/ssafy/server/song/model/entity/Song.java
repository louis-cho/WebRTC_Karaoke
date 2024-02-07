package com.ssafy.server.song.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity(name = "song")
@Getter
@Setter
@ToString
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "song_id")
    private int songId;
    @Column(name = "singer")
    private String singer;
    @Column(name = "title")
    private String title;
}
