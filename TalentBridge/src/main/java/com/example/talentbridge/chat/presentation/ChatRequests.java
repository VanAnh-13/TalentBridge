package com.example.talentbridge.chat.presentation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public final class ChatRequests {

    private ChatRequests() {
    }

    @Data
    public static class SendMessage {
        @NotBlank(message = "Tin nhắn không được để trống")
        @Size(max = 2000, message = "Tin nhắn tối đa 2000 ký tự")
        private String text;
    }
}
