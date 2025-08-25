package com.ChatAppllication.redis;

import com.ChatAppllication.model.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper mapper;

    public RedisSubscriber(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(pattern);
            String json = new String(message.getBody());
            // Convert back to ChatMessage for logging
            ChatMessage chatMessage = mapper.readValue(json, ChatMessage.class);
            log.info("RedisSubscriber received message on channel [" + channel + "]: " + chatMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
