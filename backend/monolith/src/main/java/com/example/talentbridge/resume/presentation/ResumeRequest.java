package com.example.talentbridge.resume.presentation;

import com.example.talentbridge.resume.domain.ResumeStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResumeRequest {

    @NotBlank(message = "User email is required")
    @Email(
            message = "Invalid email format",
            regexp = "^[\\w\\-.]+@([\\w\\-]+\\.)+[\\w\\-]{2,4}$"
    )
    private String email;

    /** Accepted for compatibility; the server decides the initial status (always PENDING). */
    @NotNull(message = "Resume status is required")
    private ResumeStatus status;

    @NotNull(message = "User is required")
    private User user;

    @NotNull(message = "Job is required")
    private Job job;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class User {
        private Long id;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Job {
        private Long id;
    }
}
