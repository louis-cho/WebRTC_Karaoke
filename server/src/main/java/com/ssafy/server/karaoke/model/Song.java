package com.ssafy.server.karaoke.model;

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
    @OneToOne(mappedBy = "song")
    private PerfectScore perfectScore;
    @OneToOne(mappedBy = "song")
    private SongInfo songInfo;
}
