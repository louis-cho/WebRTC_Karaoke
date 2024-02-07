package com.ssafy.server.song.model;

import lombok.Getter;

@Getter
public class ReserveModel {
    private final String userName;
    private final int songId;

    public ReserveModel(String userName, int songId) {
        this.userName = userName;
        this.songId = songId;
    }

}
