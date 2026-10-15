package com.example.talentbridge.notification.presentation;

import com.example.talentbridge.notification.application.MyNotifications;
import com.example.talentbridge.shared.presentation.ApiMessage;
import com.example.talentbridge.shared.presentation.PageRequests;
import com.example.talentbridge.shared.presentation.PageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Notification")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final MyNotifications myNotifications;

    @GetMapping
    @ApiMessage("Get my notifications")
    @PreAuthorize("hasAuthority('GET /notifications')")
    @Operation(summary = "My notifications, newest first", description = "Required permission: <b>GET /notifications</b>")
    public ResponseEntity<PageResponseDto<NotificationResponse>> list(
            @RequestParam(value = "unread", defaultValue = "false") boolean unreadOnly,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(PageResponseDto.from(
                myNotifications.list(unreadOnly, PageRequests.from(pageable)).map(NotificationResponse::from)));
    }

    @GetMapping("/unread-count")
    @ApiMessage("Count my unread notifications")
    @PreAuthorize("hasAuthority('GET /notifications/unread-count')")
    @Operation(summary = "Unread notification count", description = "Required permission: <b>GET /notifications/unread-count</b>")
    public ResponseEntity<Map<String, Long>> unreadCount() {
        return ResponseEntity.ok(Map.of("count", myNotifications.unreadCount()));
    }

    @PatchMapping("/{id}/read")
    @ApiMessage("Mark a notification as read")
    @PreAuthorize("hasAuthority('PATCH /notifications/{id}/read')")
    @Operation(summary = "Mark one notification as read", description = "Required permission: <b>PATCH /notifications/{id}/read</b>")
    public ResponseEntity<Void> markRead(@PathVariable Long id) {
        myNotifications.markRead(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/read-all")
    @ApiMessage("Mark all notifications as read")
    @PreAuthorize("hasAuthority('POST /notifications/read-all')")
    @Operation(summary = "Mark every notification as read", description = "Required permission: <b>POST /notifications/read-all</b>")
    public ResponseEntity<Map<String, Integer>> markAllRead() {
        return ResponseEntity.ok(Map.of("updated", myNotifications.markAllRead()));
    }
}
