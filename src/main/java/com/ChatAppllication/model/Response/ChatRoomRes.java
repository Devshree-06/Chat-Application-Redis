package com.ChatAppllication.model.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomRes {

    private String status;
    private String roomId;
    private String message;

    public ChatRoomRes(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
