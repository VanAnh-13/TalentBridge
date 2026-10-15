package com.example.talentbridge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.talentbridge.shared.presentation.ApiMessage;
import com.example.talentbridge.dto.request.auth.UserLoginRequestDto;
import com.example.talentbridge.dto.request.auth.SessionMetaRequest;
import com.example.talentbridge.dto.request.auth.UserRegisterRequestDto;
import com.example.talentbridge.dto.response.auth.AuthResult;
import com.example.talentbridge.dto.response.auth.AuthTokenResponseDto;
import com.example.talentbridge.dto.response.auth.SessionMetaResponse;
import com.example.talentbridge.dto.response.user.UserDetailsResponseDto;
import com.example.talentbridge.dto.response.user.UserSessionResponseDto;
import com.example.talentbridge.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Auth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ApiMessage(value = "Registration successful")
    @Operation(summary = "User registration")
    @SecurityRequirements()
    public ResponseEntity<UserSessionResponseDto> register(
            @Valid @RequestBody UserRegisterRequestDto userRegisterRequestDto
    ) {
        return ResponseEntity.ok(authService.handleRegister(userRegisterRequestDto));
    }

    @PostMapping("/login")
    @ApiMessage(value = "Login successful")
    @Operation(summary = "User login")
    @SecurityRequirements()
    public ResponseEntity<AuthTokenResponseDto> login(
            @Valid @RequestBody UserLoginRequestDto userLoginRequestDto
    ) {
        AuthResult authResult = authService.handleLogin(userLoginRequestDto);

        AuthTokenResponseDto authTokenResponseDto = authResult.getAuthTokenResponseDto();
        ResponseCookie responseCookie = authResult.getResponseCookie();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(authTokenResponseDto);
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout")
    @SecurityRequirements()
    public ResponseEntity<Void> logout(
            @CookieValue(value = "refresh_token", required = false) String refreshToken
    ) {
        ResponseCookie responseCookie = authService.handleLogout(refreshToken);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .build();
    }

    @GetMapping("/me")
    @ApiMessage(value = "Returns current user session information")
    @Operation(summary = "Get current user session information")
    public ResponseEntity<UserSessionResponseDto> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    @GetMapping("/me/details")
    @ApiMessage(value = "Returns current user detailed information")
    @Operation(summary = "Get current user detailed information")
    public ResponseEntity<UserDetailsResponseDto> getCurrentUserDetails() {
        return ResponseEntity.ok(authService.getCurrentUserDetails());
    }

    @PostMapping("/refresh-token")
    @ApiMessage(value = "Get refresh token")
    @Operation(summary = "Reissue new access token and refresh token")
    public ResponseEntity<AuthTokenResponseDto> refreshToken(
            @CookieValue(value = "refresh_token") String refreshToken,
            @RequestBody SessionMetaRequest sessionMetaRequest
    ) {
        AuthResult authResult = authService.handleRefresh(refreshToken, sessionMetaRequest);

        AuthTokenResponseDto authTokenResponseDto = authResult.getAuthTokenResponseDto();
        ResponseCookie responseCookie = authResult.getResponseCookie();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(authTokenResponseDto);
    }

    @GetMapping("/sessions")
    @ApiMessage(value = "Get sessions")
    @Operation(summary = "Get all sessions of current user")
    public ResponseEntity<List<SessionMetaResponse>> getAllSelfSessionMetas(@CookieValue(value = "refresh_token") String refreshToken) {
        return ResponseEntity.ok(authService.getAllSelfSessionMetas(refreshToken));
    }

    @DeleteMapping("/sessions/{sessionId}")
    @ApiMessage(value = "Delete session")
    @Operation(summary = "Delete user session by session id")
    public ResponseEntity<Void> removeSelfSession(@PathVariable String sessionId) {
        authService.removeSelfSession(sessionId);

        return ResponseEntity.ok().build();
    }


}
