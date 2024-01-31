package com.ssafy.server.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;

@Configuration
public class ElasticsearchConfig {

    @Bean
    public ElasticsearchRestTemplate elasticsearchRestTemplate(RestHighLevelClient elasticsearchClient,
                                                               ElasticsearchConverter converter) {
        return new ElasticsearchRestTemplate(elasticsearchClient, converter);
    }
}
