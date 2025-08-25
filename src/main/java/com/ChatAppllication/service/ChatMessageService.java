package com.ChatAppllication.service;

import com.ChatAppllication.model.ChatMessage;
import com.ChatAppllication.model.Response.ChatRoomRes;
import com.ChatAppllication.redis.RedisPublisher;
import com.ChatAppllication.repository.ChatMessageRepository;
import com.ChatAppllication.repository.ChatRoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
public class ChatMessageService {

    @Autowired
    ChatMessageRepository chatMessageRepository;
    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    RedisPublisher redisPublisher;


    public Mono<ChatRoomRes> sendMessage(String roomId, String participant, String message){
        ChatMessage message1 = ChatMessage.builder()
                .roomId(roomId)
                .participant(participant)
                .message(message)
                .timeStamp(Instant.now())
                .build();

        return chatRoomRepository.findById(roomId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Chat Room not found")))
                .then(chatMessageRepository.saveMessage(roomId,message1))
                .doOnNext(s->{
                    try{
                        log.info("Sending message: " + message1);

                        redisPublisher.publish(new ChannelTopic("chatroom:" + roomId),message1);
                    }
                    catch (Exception e){
                        log.warn("Redis publish failed: {}",e.getMessage());
                    }

                })
                .thenReturn(new ChatRoomRes("Success","Message sent Successfully."))
                .onErrorResume(ex-> Mono.just(new ChatRoomRes("Fail","Failed to send message: "+ex.getMessage())));

    }

    public Mono<List<ChatMessage>> getMessageHistory(String roomId,long limit){
        return chatMessageRepository.getMessages(roomId,limit);

    }

    public Mono<ChatRoomRes> deleteChatRoom(String roomId) {
        return chatRoomRepository.deleteById(roomId)
                .map(deleted -> {
                    if (deleted) {
                        return new ChatRoomRes("Success", "Chat room deleted successfully.");
                    } else {
                        return new ChatRoomRes("Fail", "Chat room not found.");
                    }
                })
                .onErrorResume(ex -> Mono.just(new ChatRoomRes("Fail", "Error deleting chat room: " + ex.getMessage())));
    }

}
