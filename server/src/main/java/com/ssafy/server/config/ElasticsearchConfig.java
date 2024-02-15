package com.ssafy.server.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

/**
 * Elasticsearch 설정 관리 클래스
 */
@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.host}")
    private String elasticsearchHost;

    @Value("${elasticsearch.port}")
    private int elasticsearchPort;

    /**
     * RestHighLevelClient를 생성하여 반환한다.
     * @return RestHightLevelClient
     */
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost(elasticsearchHost, elasticsearchPort, "http"))
        );
    }

    /**
     * ElasticsearchRestTemplate을 생성하여 반환한다.
     * @return ElasticsearchRestTemplate
     */
    @Bean
    public ElasticsearchRestTemplate elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(elasticsearchClient());
    }
}
