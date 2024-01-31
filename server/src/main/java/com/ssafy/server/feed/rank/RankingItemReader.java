package com.ssafy.server.feed.rank;

import com.ssafy.server.feed.rank.model.Ranking;
import com.ssafy.server.feed.rank.service.ElasticsearchService;
import com.ssafy.server.feed.rank.util.RankingCalculator;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RankingItemReader implements ItemReader<List<Ranking>> {

    private final ElasticsearchService elasticsearchService;
    private final RankingCalculator rankingCalculator;

    @Autowired
    public RankingItemReader(ElasticsearchService elasticsearchService, RankingCalculator rankingCalculator) {
        this.elasticsearchService = elasticsearchService;
        this.rankingCalculator = rankingCalculator;
    }

    @Override
    public List<Ranking> read() throws Exception {
        List<Map<String, Object>> likeData = elasticsearchService.searchLikeData();
        List<Map<String, Object>> hitData = elasticsearchService.searchHitData();
        return rankingCalculator.calculateRankings(likeData, hitData);
    }
}
