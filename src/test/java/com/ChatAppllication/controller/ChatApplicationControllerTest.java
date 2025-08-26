package com.ChatAppllication.controller;



import com.ChatAppllication.service.ChatRoomService;
import com.ChatAppllication.service.JoinChatRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ChatApplicationControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ChatApplicationController chatApplicationController;

    @Mock
    private ChatRoomService chatRoomService;

    @Mock
    private JoinChatRoomService joinChatRoomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(chatApplicationController).build();
    }

    @Test
    void testCreateChatRoom() throws Exception {
        // Mock the service to return a Mono with a value
        com.ChatAppllication.model.Response.ChatRoomRes response = new com.ChatAppllication.model.Response.ChatRoomRes("success", "123", "Room created");
        when(chatRoomService.createChatRoom(anyString()))
                .thenReturn(Mono.just(response));

        mockMvc.perform(post("/api/chatapp/chatrooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roomName\":\"TestRoom\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.roomId").value("123"))
                .andExpect(jsonPath("$.message").value("Room created"));
    }

    @Test
    void testJoinChatRoom() throws Exception {
        // Mock the service to return a Mono with a value
        com.ChatAppllication.model.Response.ChatRoomRes response = new com.ChatAppllication.model.Response.ChatRoomRes("success", "123", "Joined");
        when(joinChatRoomService.joinChatRoom(anyString(), anyString()))
                .thenReturn(Mono.just(response));



        mockMvc.perform(post("/api/chatapp/chatrooms/123/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"participant\":\"Devshree\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.roomId").value("123"))
                .andExpect(jsonPath("$.message").value("Joined"));
    }
}
