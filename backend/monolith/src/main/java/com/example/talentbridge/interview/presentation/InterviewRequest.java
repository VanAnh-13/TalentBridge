package com.example.talentbridge.interview.presentation;

import com.example.talentbridge.interview.domain.InterviewMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/** Proposes (resumeId set) or reschedules an interview. */
@Data
public class InterviewRequest {

    /** Only used when scheduling. */
    private Long resumeId;

    @NotNull(message = "Interview mode is required")
    private InterviewMode mode;

    @NotNull(message = "A location or meeting link is required")
    @Size(max = 500, message = "The location/link can have at most 500 characters")
    private String location;

    @Size(max = 1000, message = "The note can have at most 1000 characters")
    private String note;

    private int durationMinutes = 60;

    @NotEmpty(message = "Propose at least one time slot")
    private List<Instant> slots;
}
