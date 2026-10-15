package com.example.talentbridge.interview.presentation;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class ConfirmRequest {

    @NotNull(message = "Phải chọn một khung giờ")
    private Instant slot;
}
