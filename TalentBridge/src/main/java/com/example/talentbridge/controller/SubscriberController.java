package com.example.talentbridge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.talentbridge.shared.presentation.ApiMessage;
import com.example.talentbridge.dto.request.subscriber.DefaultSubscriberRequestDto;
import com.example.talentbridge.dto.response.subscriber.DefaultSubscriberResponseDto;
import com.example.talentbridge.service.SubscriberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Subscriber")
@RestController
@RequestMapping("/subscribers")
@RequiredArgsConstructor
public class SubscriberController {

    private final SubscriberService subscriberService;

    @PostMapping("/me")
    @ApiMessage(value = "Create subscriber for current user")
    @PreAuthorize("hasAuthority('POST /subscribers/me')")
    @Operation(
            summary = "Create subscriber for current user",
            description = "Required permission: <b>POST /subscribers/me</b>"
    )
    public ResponseEntity<DefaultSubscriberResponseDto> saveSelfSubscriber(
            @Valid @RequestBody DefaultSubscriberRequestDto defaultSubscriberRequestDto
    ) {
        return ResponseEntity.ok(subscriberService.saveSelfSubscriber(defaultSubscriberRequestDto));
    }

    @GetMapping("/me")
    @ApiMessage(value = "Get subscriber for current user")
    @PreAuthorize("hasAuthority('GET /subscribers/me')")
    @Operation(
            summary = "Get subscriber for current user",
            description = "Required permission: <b>GET /subscribers/me</b>"
    )
    public ResponseEntity<DefaultSubscriberResponseDto> findSelfSubscriber() {
        return ResponseEntity.ok(subscriberService.findSelfSubscriber());
    }

    @PutMapping("/me")
    @ApiMessage(value = "Update subscriber for current user")
    @PreAuthorize("hasAuthority('PUT /subscribers/me')")
    @Operation(
            summary = "Update subscriber for current user",
            description = "Required permission: <b>PUT /subscribers/me</b>"
    )
    public ResponseEntity<DefaultSubscriberResponseDto> updateSelfSubscriber(
            @Valid @RequestBody DefaultSubscriberRequestDto defaultSubscriberRequestDto
    ) {
        return ResponseEntity.ok(subscriberService.updateSelfSubscriber(defaultSubscriberRequestDto));
    }

    @DeleteMapping("/me")
    @ApiMessage(value = "Delete subscriber for current user")
    @PreAuthorize("hasAuthority('DELETE /subscribers/me')")
    @Operation(
            summary = "Delete subscriber for current user",
            description = "Required permission: <b>DELETE /subscribers/me</b>"
    )
    public ResponseEntity<Void> deleteSelfSubscriber() {
        subscriberService.deleteSelfSubscriber();
        return ResponseEntity.noContent().build();
    }

}
