package com.ssafy.server.config;

import ch.qos.logback.classic.pattern.MessageConverter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RabbitMQConfig {
    @Bean
    public SimpleMessageConverter converter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("com.ssafy.server.*", "java.util.*"));
        return converter;
    }

    @Bean
    public Queue likeEventsQueue() {
        return new Queue("like_events");
    }

    @Bean
    public Queue likeEventsDLQ() {
        return new Queue("like_events_dlq");
    }

    @Bean
    public DirectExchange likeEventsExchange() {
        return new DirectExchange("like_events_exchange");
    }

    @Bean
    public Binding likeEventsBinding(Queue likeEventsQueue, DirectExchange likeEventsExchange) {
        return BindingBuilder.bind(likeEventsQueue).to(likeEventsExchange).with("like_events");
    }

    @Bean
    public Binding likeEventsDLQBinding(Queue likeEventsDLQ, DirectExchange likeEventsExchange) {
        return BindingBuilder.bind(likeEventsDLQ).to(likeEventsExchange).with("like_events_dlq");
    }
}