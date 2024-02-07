package com.ssafy.server.song.model;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Component
public class ReserveModel {
    private final Map<String, ArrayDeque<String>> mapSongReserveDeq;

    public ReserveModel() {
        mapSongReserveDeq = new ConcurrentHashMap<>();
    }

}
