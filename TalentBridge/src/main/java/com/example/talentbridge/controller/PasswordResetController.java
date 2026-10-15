package com.example.talentbridge.controller;

import com.example.talentbridge.shared.presentation.ApiMessage;
import com.example.talentbridge.dto.request.auth.ForgotPasswordRequest;
import com.example.talentbridge.dto.request.auth.ResetPasswordRequest;
import com.example.talentbridge.dto.response.auth.TokenValidationResponse;
import com.example.talentbridge.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Password Reset")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    @ApiMessage(value = "Yêu cầu đặt lại mật khẩu đã được gửi")
    @Operation(summary = "Request password reset email")
    @SecurityRequirements()
    public ResponseEntity<Void> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        passwordResetService.requestPasswordReset(request.getEmail());
        // Always return success to prevent email enumeration
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reset-password/verify")
    @ApiMessage(value = "Xác thực token đặt lại mật khẩu")
    @Operation(summary = "Verify password reset token")
    @SecurityRequirements()
    public ResponseEntity<TokenValidationResponse> verifyResetToken(
            @RequestParam String token
    ) {
        TokenValidationResponse response = passwordResetService.validateResetToken(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    @ApiMessage(value = "Đặt lại mật khẩu thành công")
    @Operation(summary = "Reset password with token")
    @SecurityRequirements()
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        passwordResetService.resetPassword(
                request.getToken(),
                request.getPassword(),
                request.getConfirmPassword()
        );
        return ResponseEntity.ok().build();
    }
}
