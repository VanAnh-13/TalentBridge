package com.example.talentbridge.auth.presentation;

import com.example.talentbridge.auth.application.CurrentAccount;
import com.example.talentbridge.auth.application.Login;
import com.example.talentbridge.auth.application.Logout;
import com.example.talentbridge.auth.application.ManageSessions;
import com.example.talentbridge.auth.application.RefreshSession;
import com.example.talentbridge.auth.application.RegisterAccount;
import com.example.talentbridge.auth.application.AuthSession;
import com.example.talentbridge.shared.presentation.ApiMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final RegisterAccount registerAccount;
    private final Login login;
    private final Logout logout;
    private final RefreshSession refreshSession;
    private final CurrentAccount currentAccount;
    private final ManageSessions manageSessions;
    private final RefreshCookies cookies;

    @PostMapping("/register")
    @ApiMessage(value = "Registration successful")
    @Operation(summary = "User registration")
    @SecurityRequirements()
    public ResponseEntity<SessionUserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(SessionUserResponse.from(registerAccount.execute(
                request.getName(), request.getEmail(), request.getPassword(), request.getDob(),
                request.getAddress(), request.getGender(), request.isRecruiter())));
    }

    @PostMapping("/login")
    @ApiMessage(value = "Login successful")
    @Operation(summary = "User login")
    @SecurityRequirements()
    public ResponseEntity<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return withRefreshCookie(login.execute(
                request.getEmail(), request.getPassword(), request.getSessionMetaRequest().toDevice()));
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout")
    @SecurityRequirements()
    public ResponseEntity<Void> logout(
            @CookieValue(value = RefreshCookies.NAME, required = false) String refreshToken
    ) {
        logout.execute(refreshToken);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookies.clear().toString()).build();
    }

    @GetMapping("/me")
    @ApiMessage(value = "Returns current user session information")
    @Operation(summary = "Get current user session information")
    public ResponseEntity<SessionUserResponse> getCurrentUser() {
        return ResponseEntity.ok(SessionUserResponse.from(currentAccount.session()));
    }

    @GetMapping("/me/details")
    @ApiMessage(value = "Returns current user detailed information")
    @Operation(summary = "Get current user detailed information")
    public ResponseEntity<AccountDetailsResponse> getCurrentUserDetails() {
        return ResponseEntity.ok(AccountDetailsResponse.from(currentAccount.details()));
    }

    @PostMapping("/refresh-token")
    @ApiMessage(value = "Get refresh token")
    @Operation(summary = "Reissue new access token and refresh token")
    public ResponseEntity<AuthTokenResponse> refreshToken(
            @CookieValue(value = RefreshCookies.NAME) String refreshToken,
            @RequestBody SessionMetaRequest sessionMetaRequest
    ) {
        return withRefreshCookie(refreshSession.execute(refreshToken, sessionMetaRequest.toDevice()));
    }

    @GetMapping("/sessions")
    @ApiMessage(value = "Get sessions")
    @Operation(summary = "Get all sessions of current user")
    public ResponseEntity<List<SessionResponse>> getAllSelfSessionMetas(
            @CookieValue(value = RefreshCookies.NAME) String refreshToken
    ) {
        return ResponseEntity.ok(manageSessions.list(refreshToken).stream().map(SessionResponse::from).toList());
    }

    @DeleteMapping("/sessions/{sessionId}")
    @ApiMessage(value = "Delete session")
    @Operation(summary = "Delete user session by session id")
    public ResponseEntity<Void> removeSelfSession(@PathVariable String sessionId) {
        manageSessions.revoke(sessionId);
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<AuthTokenResponse> withRefreshCookie(AuthSession session) {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookies.issue(session.refreshToken()).toString())
                .body(AuthTokenResponse.from(session));
    }
}
