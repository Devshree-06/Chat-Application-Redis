package com.ChatAppllication.service;

import com.ChatAppllication.model.ChatMessage;
import com.ChatAppllication.model.ChatRoom;
import com.ChatAppllication.model.Response.ChatRoomRes;
import com.ChatAppllication.redis.RedisPublisher;
import com.ChatAppllication.repository.ChatMessageRepository;
import com.ChatAppllication.repository.ChatRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.listener.ChannelTopic;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ChatMessageServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private RedisPublisher redisPublisher;

    private ChatMessageService chatMessageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        chatMessageService = new ChatMessageService();
        chatMessageService.chatMessageRepository = chatMessageRepository;
        chatMessageService.chatRoomRepository = chatRoomRepository;
        chatMessageService.redisPublisher = redisPublisher;
    }

    @Test
    void testSendMessageSuccess() {
        ChatMessage msg = ChatMessage.builder()
                .roomId("1")
                .participant("Alice")
                .message("Hello")
                .timeStamp(Instant.now())
                .build();

        ChatRoom room = ChatRoom.builder()
                .id("1")
                .chatRoomName("Room1")
                .build();

        when(chatRoomRepository.findById("1")).thenReturn(Mono.just(room));
        when(chatMessageRepository.saveMessage(eq("1"), any(ChatMessage.class))).thenReturn(Mono.just(1l));

        Mono<ChatRoomRes> result = chatMessageService.sendMessage("1", "Alice", "Hello");

        StepVerifier.create(result)
                .expectNextMatches(res -> res.getStatus().equals("Success") &&
                        res.getMessage().contains("Message sent"))
                .verifyComplete();

        verify(redisPublisher, times(1)).publish(any(ChannelTopic.class), any(ChatMessage.class));
    }

    @Test
    void testSendMessageChatRoomNotFound() {
        when(chatRoomRepository.findById("1")).thenReturn(Mono.empty());

        Mono<ChatRoomRes> result = chatMessageService.sendMessage("1", "Bob", "Hi");

        StepVerifier.create(result)
                .expectNextMatches(res -> res.getStatus().equals("Fail") &&
                        res.getMessage().contains("Chat Room not found"))
                .verifyComplete();

        verify(redisPublisher, never()).publish(any(), any());
    }

    @Test
    void testSendMessageRedisPublishFails() {
        ChatMessage msg = ChatMessage.builder()
                .roomId("1")
                .participant("Charlie")
                .message("Test")
                .timeStamp(Instant.now())
                .build();

        ChatRoom room = ChatRoom.builder()
                .id("1")
                .chatRoomName("Room1")
                .build();

        when(chatRoomRepository.findById("1")).thenReturn(Mono.just(room));
        when(chatMessageRepository.saveMessage(eq("1"), any(ChatMessage.class))).thenReturn(Mono.just(1l));

        doThrow(new RuntimeException("Redis down")).when(redisPublisher).publish(any(), any());

        Mono<ChatRoomRes> result = chatMessageService.sendMessage("1", "Charlie", "Test");

        StepVerifier.create(result)
                .expectNextMatches(res -> res.getStatus().equals("Success")) // still success, just logs warning
                .verifyComplete();

        verify(redisPublisher, times(1)).publish(any(ChannelTopic.class), any(ChatMessage.class));
    }

    @Test
    void testGetMessageHistory() {
        ChatMessage msg1 = ChatMessage.builder().roomId("1").message("Hi").participant("A").timeStamp(Instant.now()).build();
        ChatMessage msg2 = ChatMessage.builder().roomId("1").message("Hello").participant("B").timeStamp(Instant.now()).build();

        when(chatMessageRepository.getMessages("1", 2L)).thenReturn(Mono.just(Arrays.asList(msg1, msg2)));

        StepVerifier.create(chatMessageService.getMessageHistory("1", 2))
                .expectNextMatches(list -> list.size() == 2 && list.get(0).getMessage().equals("Hi"))
                .verifyComplete();
    }

    @Test
    void testDeleteChatRoomSuccess() {
        when(chatRoomRepository.deleteById("1")).thenReturn(Mono.just(true));

        StepVerifier.create(chatMessageService.deleteChatRoom("1"))
                .expectNextMatches(res -> res.getStatus().equals("Success"))
                .verifyComplete();
    }

    @Test
    void testDeleteChatRoomNotFound() {
        when(chatRoomRepository.deleteById("1")).thenReturn(Mono.just(false));

        StepVerifier.create(chatMessageService.deleteChatRoom("1"))
                .expectNextMatches(res -> res.getStatus().equals("Fail") &&
                        res.getMessage().contains("not found"))
                .verifyComplete();
    }

    @Test
    void testDeleteChatRoomError() {
        when(chatRoomRepository.deleteById("1")).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(chatMessageService.deleteChatRoom("1"))
                .expectNextMatches(res -> res.getStatus().equals("Fail") &&
                        res.getMessage().contains("DB error"))
                .verifyComplete();
    }
}
