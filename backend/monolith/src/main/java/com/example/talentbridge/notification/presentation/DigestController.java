package com.example.talentbridge.notification.presentation;

import com.example.talentbridge.notification.application.SendJobDigest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Email")
@RestController
@RequiredArgsConstructor
@ConditionalOnExpression("'${spring.mail.host:}' != ''")
public class DigestController {

    private final SendJobDigest sendJobDigest;

    @PostMapping("/emails/job-recommendation/{email}")
    @PreAuthorize("hasAuthority('POST /emails/job-recommendation/{email}')")
    @Operation(
            summary = "Send the job recommendation email to a subscriber",
            description = "Required permission: <b>POST /emails/job-recommendation/{email}</b>"
    )
    public ResponseEntity<String> sendJobMail(@PathVariable String email) {
        try {
            sendJobDigest.toSubscriber(email);
            return ResponseEntity.ok("Email sent successfully!");
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Email sending failed: " + ex.getMessage());
        }
    }
}
