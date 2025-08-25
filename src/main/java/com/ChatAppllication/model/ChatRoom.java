package com.ChatAppllication.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash("ChatRoom")
@Builder
public class ChatRoom {

    @Id
    private String id;
    private String chatRoomName;
}
