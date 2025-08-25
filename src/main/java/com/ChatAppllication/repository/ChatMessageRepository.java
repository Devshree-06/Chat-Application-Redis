package com.ChatAppllication.repository;

import com.ChatAppllication.Utils.CommonMethods;
import com.ChatAppllication.model.ChatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;


@Repository
public class ChatMessageRepository {


    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public ChatMessageRepository(ReactiveStringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }


    public Mono<Long> saveMessage(String roomId,ChatMessage message){
        String json;

        try{
            json = objectMapper.writeValueAsString(message);
        }
        catch (JsonProcessingException e){
            return Mono.error(e);
        }

        return redisTemplate.opsForList().rightPush(CommonMethods.Key(roomId),json);
    }

    public Mono<List<ChatMessage>> getMessages(String roomId,long limit){
        return redisTemplate.opsForList().range(CommonMethods.Key(roomId),0,Math.max(0,limit-1))
                .flatMap(js-> Mono.fromCallable(()-> objectMapper.readValue(js,ChatMessage.class)))
                .collectList();
    }
}
