package com.example.talentbridge.chat.presentation;

import com.example.talentbridge.chat.application.OpenConversation;
import com.example.talentbridge.chat.application.ReadChats;
import com.example.talentbridge.chat.application.SendMessage;
import com.example.talentbridge.chat.presentation.ChatResponses.ConversationResponse;
import com.example.talentbridge.chat.presentation.ChatResponses.MessageResponse;
import com.example.talentbridge.shared.presentation.ApiMessage;
import com.example.talentbridge.shared.presentation.PageRequests;
import com.example.talentbridge.shared.presentation.PageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Chat")
@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {

    private final OpenConversation openConversation;
    private final SendMessage sendMessage;
    private final ReadChats readChats;

    @PostMapping("/resumes/{resumeId}")
    @ApiMessage("Open the conversation about an application")
    @PreAuthorize("hasAuthority('POST /chats/resumes/{resumeId}')")
    @Operation(summary = "Open (or create) the conversation about an application", description = "Required permission: <b>POST /chats/resumes/{resumeId}</b>")
    public ResponseEntity<ConversationResponse> open(@PathVariable Long resumeId) {
        return ResponseEntity.ok(ConversationResponse.of(openConversation.forApplication(resumeId)));
    }

    @GetMapping
    @ApiMessage("Get my conversations")
    @PreAuthorize("hasAuthority('GET /chats')")
    @Operation(summary = "My conversations, most recent first", description = "Required permission: <b>GET /chats</b>")
    public ResponseEntity<PageResponseDto<ConversationResponse>> conversations(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(PageResponseDto.from(
                readChats.conversations(PageRequests.from(pageable)).map(ConversationResponse::from)));
    }

    @GetMapping("/unread-count")
    @ApiMessage("Count unread chat messages")
    @PreAuthorize("hasAuthority('GET /chats/unread-count')")
    @Operation(summary = "Unread messages over all my conversations", description = "Required permission: <b>GET /chats/unread-count</b>")
    public ResponseEntity<Map<String, Long>> unread() {
        return ResponseEntity.ok(Map.of("count", readChats.unreadCount()));
    }

    @GetMapping("/{id}/messages")
    @ApiMessage("Get the messages of a conversation")
    @PreAuthorize("hasAuthority('GET /chats/{id}/messages')")
    @Operation(summary = "Messages of a conversation, newest first", description = "Required permission: <b>GET /chats/{id}/messages</b>")
    public ResponseEntity<PageResponseDto<MessageResponse>> messages(@PathVariable Long id, @PageableDefault(size = 30) Pageable pageable) {
        return ResponseEntity.ok(PageResponseDto.from(
                readChats.messages(id, PageRequests.from(pageable)).map(MessageResponse::from)));
    }

    @PostMapping("/{id}/messages")
    @ApiMessage("Send a message")
    @PreAuthorize("hasAuthority('POST /chats/{id}/messages')")
    @Operation(summary = "Send a message", description = "Required permission: <b>POST /chats/{id}/messages</b>")
    public ResponseEntity<MessageResponse> send(@PathVariable Long id, @Valid @RequestBody ChatRequests.SendMessage request) {
        var message = sendMessage.execute(id, request.getText());
        return ResponseEntity.status(HttpStatus.CREATED).body(MessageResponse.from(message, null));
    }

    @PostMapping("/{id}/read")
    @ApiMessage("Mark a conversation as read")
    @PreAuthorize("hasAuthority('POST /chats/{id}/read')")
    @Operation(summary = "Mark the conversation as read", description = "Required permission: <b>POST /chats/{id}/read</b>")
    public ResponseEntity<Void> read(@PathVariable Long id) {
        readChats.markRead(id);
        return ResponseEntity.noContent().build();
    }
}
