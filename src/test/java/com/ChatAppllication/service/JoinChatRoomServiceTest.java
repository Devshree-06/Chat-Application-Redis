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

import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class JoinChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    private JoinChatRoomService joinChatRoomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        joinChatRoomService = new JoinChatRoomService();
        joinChatRoomService.chatRoomRepository = chatRoomRepository;
    }

    @Test
    void testJoinChatRoomSuccess() {
        ChatRoom room = ChatRoom.builder().id("1").chatRoomName("Room1").participants(new HashSet<>()).build();
        when(chatRoomRepository.findById("1")).thenReturn(Mono.just(room));
        when(chatRoomRepository.saveChatRoom(any(ChatRoom.class))).thenReturn(Mono.just(room));

        Mono<ChatRoomRes> result = joinChatRoomService.joinChatRoom("1", "Alice");

        StepVerifier.create(result)
                .expectNextMatches(res -> res.getStatus().equals("Success") && res.getMessage().contains("Alice"))
                .verifyComplete();
    }

    @Test
    void testJoinChatRoomEmptyParticipant() {
        Mono<ChatRoomRes> result = joinChatRoomService.joinChatRoom("1", "");

        StepVerifier.create(result)
                .expectNextMatches(res -> res.getStatus().equals("fail") && res.getMessage().contains("cannot be empty"))
                .verifyComplete();
    }

    @Test
    void testJoinChatRoomNotFound() {
        when(chatRoomRepository.findById("1")).thenReturn(Mono.empty());

        Mono<ChatRoomRes> result = joinChatRoomService.joinChatRoom("1", "Bob");

        StepVerifier.create(result)
                .expectNextMatches(res -> res.getStatus().equals("Fail") && res.getMessage().contains("Not Found"))
                .verifyComplete();
    }
}
