package com.example.talentbridge.chat.presentation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public final class ChatRequests {

    private ChatRequests() {
    }

    @Data
    public static class SendMessage {
        @NotBlank(message = "Message text is required")
        @Size(max = 2000, message = "A message can have at most 2000 characters")
        private String text;
    }
}
