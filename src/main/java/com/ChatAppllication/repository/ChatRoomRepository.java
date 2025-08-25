package com.ChatAppllication.repository;

import com.ChatAppllication.model.ChatRoom;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class ChatRoomRepository {

    private final ReactiveRedisTemplate<String, ChatRoom> redisTemplate;
    private final ReactiveValueOperations<String,ChatRoom> valueOps;

    public ChatRoomRepository(ReactiveRedisTemplate<String, ChatRoom> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOps = redisTemplate.opsForValue();
    }

    public Mono<ChatRoom> saveChatRoom(ChatRoom room){
        return valueOps.set("chatroom:" + room.getId(),room)
                .thenReturn(room)
                .onErrorMap(ex -> new RuntimeException("Redis save failed: " + ex.getMessage()));
    }

    public Mono<ChatRoom> findById(String id) {
        return valueOps.get("chatroom:" + id)
                .switchIfEmpty(Mono.error(new RuntimeException("Chat room not found")));

    }

    public Mono<Boolean> deleteById(String id) {
        return redisTemplate.delete("chatroom:" + id).map(count -> count > 0)
                .onErrorMap(ex -> new RuntimeException("Redis delete failed: " + ex.getMessage()));

    }
}
