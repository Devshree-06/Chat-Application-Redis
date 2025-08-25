package com.ChatAppllication.controller;

import com.ChatAppllication.model.ChatRoom;
import com.ChatAppllication.model.Response.ChatRoomRes;
import com.ChatAppllication.service.ChatRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/chatapp")
@Slf4j
public class ChatApplicationController {

    @Autowired
    private ChatRoomService chatRoomService;

    @PostMapping("/chatrooms")
    public Mono<ChatRoomRes> createChatRoom(@RequestBody ChatRoom request){
        return chatRoomService.createChatRoom(request.getChatRoomName());
    }
}
