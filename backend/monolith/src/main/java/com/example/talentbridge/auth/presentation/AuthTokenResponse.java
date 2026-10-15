package com.example.talentbridge.auth.presentation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.example.talentbridge.auth.application.AuthSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonPropertyOrder({"user", "accessToken"})
public class AuthTokenResponse {

    @JsonProperty("user")
    private SessionUserResponse user;

    private String accessToken;

    public static AuthTokenResponse from(AuthSession session) {
        return new AuthTokenResponse(SessionUserResponse.from(session.user()), session.accessToken());
    }
}
