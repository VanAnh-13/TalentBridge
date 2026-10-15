package com.example.talentbridge.subscriber.presentation;

import com.example.talentbridge.shared.presentation.ApiMessage;
import com.example.talentbridge.subscriber.application.CreateSelfSubscriber;
import com.example.talentbridge.subscriber.application.DeleteSelfSubscriber;
import com.example.talentbridge.subscriber.application.GetSelfSubscriber;
import com.example.talentbridge.subscriber.application.UpdateSelfSubscriber;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Subscriber")
@RestController
@RequestMapping("/subscribers")
@RequiredArgsConstructor
public class SubscriberController {

    private final CreateSelfSubscriber createSelfSubscriber;
    private final GetSelfSubscriber getSelfSubscriber;
    private final UpdateSelfSubscriber updateSelfSubscriber;
    private final DeleteSelfSubscriber deleteSelfSubscriber;

    @PostMapping("/me")
    @ApiMessage(value = "Create subscriber for current user")
    @PreAuthorize("hasAuthority('POST /subscribers/me')")
    @Operation(
            summary = "Create subscriber for current user",
            description = "Required permission: <b>POST /subscribers/me</b>"
    )
    public ResponseEntity<SubscriberResponse> saveSelfSubscriber(@Valid @RequestBody SubscriberRequest request) {
        return ResponseEntity.ok(SubscriberResponse.from(createSelfSubscriber.execute(request.skillIds())));
    }

    @GetMapping("/me")
    @ApiMessage(value = "Get subscriber for current user")
    @PreAuthorize("hasAuthority('GET /subscribers/me')")
    @Operation(
            summary = "Get subscriber for current user",
            description = "Required permission: <b>GET /subscribers/me</b>"
    )
    public ResponseEntity<SubscriberResponse> findSelfSubscriber() {
        // 200 with an empty body when the caller has not subscribed (existing API behaviour)
        return ResponseEntity.ok(getSelfSubscriber.execute().map(SubscriberResponse::from).orElse(null));
    }

    @PutMapping("/me")
    @ApiMessage(value = "Update subscriber for current user")
    @PreAuthorize("hasAuthority('PUT /subscribers/me')")
    @Operation(
            summary = "Update subscriber for current user",
            description = "Required permission: <b>PUT /subscribers/me</b>"
    )
    public ResponseEntity<SubscriberResponse> updateSelfSubscriber(@Valid @RequestBody SubscriberRequest request) {
        return ResponseEntity.ok(SubscriberResponse.from(updateSelfSubscriber.execute(request.skillIds())));
    }

    @DeleteMapping("/me")
    @ApiMessage(value = "Delete subscriber for current user")
    @PreAuthorize("hasAuthority('DELETE /subscribers/me')")
    @Operation(
            summary = "Delete subscriber for current user",
            description = "Required permission: <b>DELETE /subscribers/me</b>"
    )
    public ResponseEntity<Void> deleteSelfSubscriber() {
        deleteSelfSubscriber.execute();
        return ResponseEntity.noContent().build();
    }
}
