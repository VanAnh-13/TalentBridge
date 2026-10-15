package com.example.talentbridge.auth.presentation;

import com.example.talentbridge.auth.application.PublishSigningKeys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

/** Public: the JSON Web Key Set that services use to verify access tokens (not wrapped in ApiResponse). */
@Tag(name = "Auth")
@RestController
@RequiredArgsConstructor
public class JwksController {

    private final PublishSigningKeys publishSigningKeys;

    @GetMapping("/.well-known/jwks.json")
    @Operation(summary = "Public keys that verify access tokens (JWKS)")
    public ResponseEntity<Map<String, Object>> jwks() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(5)).cachePublic())
                .body(publishSigningKeys.execute());
    }
}
