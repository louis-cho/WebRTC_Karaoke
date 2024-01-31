package com.ssafy.server.feed.rank.util;

import com.ssafy.server.feed.rank.model.Ranking;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class RankingCalculator {

    public List<Ranking> calculateRankings(List<Map<String, Object>> likeData, List<Map<String, Object>> hitData) {
        // Implement logic to calculate rankings based on likeData and hitData
        // Return a list of Ranking objects
        return new ArrayList<>();
    }
}
