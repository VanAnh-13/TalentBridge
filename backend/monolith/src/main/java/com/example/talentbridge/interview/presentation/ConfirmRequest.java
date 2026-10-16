package com.example.talentbridge.interview.presentation;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class ConfirmRequest {

    @NotNull(message = "Choose a time slot")
    private Instant slot;
}
