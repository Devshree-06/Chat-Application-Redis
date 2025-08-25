package com.ChatAppllication.config;

import com.ChatAppllication.model.ChatMessage;
import com.ChatAppllication.model.ChatRoom;
import com.ChatAppllication.redis.RedisSubscriber;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.*;


@Configuration
@Slf4j
public class RedisConfig {


    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
                                                        RedisSubscriber subscriber,
                                                        ChannelTopic topic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(subscriber, new PatternTopic("chatroom*"));
        log.info("RedisMessageListenerContainer initialized and subscriber registered for topic: " + topic.getTopic());

        return container;
    }


    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic("chatroom");
    }

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        return new LettuceConnectionFactory(); // default localhost:6379
    }

    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Support Instant
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO format
        return mapper;
    }

    @Bean
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(ReactiveRedisConnectionFactory factory) {
        return new ReactiveStringRedisTemplate(factory);
    }


    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory, ObjectMapper mapper) {
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(mapper);

        RedisSerializationContext<String, Object> context = RedisSerializationContext
                .<String, Object>newSerializationContext(new StringRedisSerializer())
                .key(new StringRedisSerializer())
                .value(serializer)
                .hashKey(new StringRedisSerializer())
                .hashValue(serializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public ReactiveRedisTemplate<String, ChatRoom> reactiveChatRoomTemplate(
            ReactiveRedisConnectionFactory factory, ObjectMapper mapper) {

        Jackson2JsonRedisSerializer<ChatRoom> serializer = new Jackson2JsonRedisSerializer<>(ChatRoom.class);
        serializer.setObjectMapper(mapper); // enables Instant serialization

        RedisSerializationContext.SerializationPair<ChatRoom> valuePair =
                RedisSerializationContext.SerializationPair.fromSerializer(serializer);

        RedisSerializationContext<String, ChatRoom> context =
                RedisSerializationContext.<String, ChatRoom>newSerializationContext(new StringRedisSerializer())
                        .key(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                        .value(valuePair)
                        .hashKey(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                        .hashValue(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                        .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }


    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper mapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key serializers
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value serializer using custom ObjectMapper
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(mapper);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }


}
