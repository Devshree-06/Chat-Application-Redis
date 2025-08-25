package com.ChatAppllication.controller;

import com.ChatAppllication.model.ChatRoom;
import com.ChatAppllication.model.Response.ChatRoomRes;
import com.ChatAppllication.service.ChatRoomService;
import com.ChatAppllication.service.JoinChatRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/chatapp/chatrooms")
@Slf4j
public class ChatApplicationController {

    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private JoinChatRoomService joinChatRoomService;

    @PostMapping
    public Mono<ChatRoomRes> createChatRoom(@RequestBody ChatRoom request){
        return chatRoomService.createChatRoom(request.getChatRoomName());
    }

    @PostMapping("/{roomId}/join")
    public Mono<ChatRoomRes> joinChatRoom(@PathVariable String roomId,
                                          @RequestBody Map<String, String> request){
        String participant = request.get("participant");
        return joinChatRoomService.joinChatRoom(roomId,participant);
    }
}
