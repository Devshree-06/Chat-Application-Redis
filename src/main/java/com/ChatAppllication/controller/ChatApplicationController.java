package com.ChatAppllication.controller;

import com.ChatAppllication.model.ChatMessage;
import com.ChatAppllication.model.ChatRoom;
import com.ChatAppllication.model.Response.ChatRoomRes;
import com.ChatAppllication.service.ChatMessageService;
import com.ChatAppllication.service.ChatRoomService;
import com.ChatAppllication.service.JoinChatRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatapp/chatrooms")
@Slf4j
public class ChatApplicationController {

    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private JoinChatRoomService joinChatRoomService;
    @Autowired
    private ChatMessageService chatMessageService;

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

    @PostMapping("/{roomId}/messages")
    public Mono<ChatRoomRes> sendMessages(@PathVariable String roomId,
                                          @RequestBody Map<String,String> request){

        String message = request.get("message");
        String participant = request.get("participant");

        return chatMessageService.sendMessage(roomId,participant,message);
    }

    @GetMapping("/{roomId}/messages")
    public Mono<List<ChatMessage>> getMessages(@PathVariable String roomId,
                                               @RequestParam(defaultValue = "10") long limit) {
        return chatMessageService.getMessageHistory(roomId, limit);
    }

    @DeleteMapping("/{roomId}")
    public Mono<ChatRoomRes> deleteChatRoom(@PathVariable String roomId){
        return chatMessageService.deleteChatRoom(roomId);
    }

}
