package com.ssafy.server.karaoke.model;

import javax.persistence.*;

@Entity
public class PerfectScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int songId;
    @OneToOne
    @JoinColumn(name = "songId")
    private Song song;
}
