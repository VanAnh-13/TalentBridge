package com.example.talentbridge.auth.presentation;

import com.example.talentbridge.auth.application.PasswordReset.TokenCheck;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TokenCheckResponse {

    private boolean valid;
    private String message;
    private String errorCode;

    public static TokenCheckResponse from(TokenCheck c) {
        return new TokenCheckResponse(c.valid(), c.message(), c.errorCode());
    }
}
