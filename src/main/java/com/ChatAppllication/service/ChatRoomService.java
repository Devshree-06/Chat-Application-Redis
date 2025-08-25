package com.ChatAppllication.service;

import com.ChatAppllication.model.ChatRoom;
import com.ChatAppllication.model.Response.ChatRoomRes;
import com.ChatAppllication.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ChatRoomService {

    @Autowired
    ChatRoomRepository chatRoomRepository;

    public Mono<ChatRoomRes> createChatRoom(String name){
        ChatRoom room = ChatRoom.builder()
                .id(UUID.randomUUID().toString())
                .chatRoomName(name)
                .build();

        return chatRoomRepository.saveChatRoom(room)
                .map(createdRoom->{
                    ChatRoomRes res = new ChatRoomRes();
                    res.setRoomId(createdRoom.getId());
                    res.setStatus("Success");
                    res.setMessage("Chat Room '"  + createdRoom.getChatRoomName()+"' created successfully.");
                    return res;
                }).onErrorResume(ex -> {
                    ChatRoomRes errorRes = new ChatRoomRes();
                    errorRes.setStatus("failure");
                    errorRes.setRoomId(null);
                    errorRes.setMessage("Failed to create chat room: " + ex.getMessage());
                    return Mono.just(errorRes);
                });
    }
}
