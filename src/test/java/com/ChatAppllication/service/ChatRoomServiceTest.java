package com.ChatAppllication.service;


import com.ChatAppllication.model.ChatRoom;
import com.ChatAppllication.model.Response.ChatRoomRes;
import com.ChatAppllication.repository.ChatRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ChatRoomServiceTest {
    @Mock
    private ChatRoomRepository chatRoomRepository;

    private ChatRoomService chatRoomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        chatRoomService = new ChatRoomService();
        chatRoomService.chatRoomRepository = chatRoomRepository;
    }

    @Test
    void testCreateChatRoomSuccess() {
        ChatRoom room = ChatRoom.builder().id(UUID.randomUUID().toString()).chatRoomName("TestRoom").build();
        when(chatRoomRepository.saveChatRoom(any(ChatRoom.class))).thenReturn(Mono.just(room));

        Mono<ChatRoomRes> result = chatRoomService.createChatRoom("TestRoom");

        StepVerifier.create(result)
                .expectNextMatches(res -> res.getStatus().equals("Success") && res.getRoomId().equals(room.getId()))
                .verifyComplete();
    }

    @Test
    void testCreateChatRoomFailure() {
        when(chatRoomRepository.saveChatRoom(any(ChatRoom.class)))
                .thenReturn(Mono.error(new RuntimeException("DB Error")));

        Mono<ChatRoomRes> result = chatRoomService.createChatRoom("TestRoom");

        StepVerifier.create(result)
                .expectNextMatches(res -> res.getStatus().equals("failure") && res.getMessage().contains("DB Error"))
                .verifyComplete();
    }
}
