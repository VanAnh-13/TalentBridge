package com.example.talentbridge.resume.presentation;

import com.example.talentbridge.resume.domain.ResumeStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateResumeStatusRequest {

    @NotNull(message = "Resume id is required")
    private Long id;

    @NotNull(message = "Resume status is required")
    private ResumeStatus status;
}
