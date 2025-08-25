package com.ChatAppllication.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisPublisher {

    private final RedisTemplate<String,Object> redisTemplate;

    public RedisPublisher(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void publish(ChannelTopic topic, Object message) {
        log.info("Publishing message to topic [" + topic.getTopic() + "]: " + message);
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
