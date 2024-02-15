package com.ssafy.server.config;

import com.ssafy.server.hit.model.Hit;
import com.ssafy.server.like.model.Like;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 설정 클래스 파일
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * object redis template을 생성하여 반환한다.
     * @param redisConnectionFactory redis 서버 연결 관리 인터페이스
     * @return redis template
     */
    @Bean
    public RedisTemplate<String, Object> RedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setDefaultSerializer(new StringRedisSerializer());
        return template;
    }


    /**
     * 좋아요 관련 redis template을 생성하여 반환한다.
     * @param connectionFactory redis 서버 연결 관리 인터페이스
     * @return redis template
     */
    @Bean
    public RedisTemplate<String, Like> likeRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Like> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setDefaultSerializer(new JdkSerializationRedisSerializer());
        return template;
    }

    /**
     * 조회수 관련 redis template을 생성하여 반환한다.
     * @param connectionFactory redis 서버 연결 관리 인터페이스
     * @return redis template
     */
    @Bean
    public RedisTemplate<String, Hit> hitRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Hit> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setDefaultSerializer(new JdkSerializationRedisSerializer());
        return template;
    }

    @Bean
    public RedisMessageListenerContainer RedisMessageListener(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        return container;
    }
}