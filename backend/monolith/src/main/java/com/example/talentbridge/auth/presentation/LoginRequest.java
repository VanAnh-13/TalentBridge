package com.example.talentbridge.auth.presentation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequest {

    @NotBlank(message = "User email is required")
    @Email(
            message = "Invalid email format",
            regexp = "^[\\w\\-.]+@([\\w\\-]+\\.)+[\\w\\-]{2,4}$"
    )
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "Session details are required")
    private SessionMetaRequest sessionMetaRequest;
}
