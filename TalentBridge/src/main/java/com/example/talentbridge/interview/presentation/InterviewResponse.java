package com.example.talentbridge.interview.presentation;

import com.example.talentbridge.interview.application.ListInterviews.InterviewView;
import com.example.talentbridge.interview.domain.Interview;

import java.time.Instant;
import java.util.List;

public record InterviewResponse(Long id, Long resumeId, String candidateEmail, String jobName, String companyName,
                                String mode, String location, String note, int durationMinutes,
                                List<Instant> proposedSlots, Instant confirmedSlot, String status, Instant createdAt) {

    public static InterviewResponse from(Interview i) {
        return new InterviewResponse(i.getId(), i.getResumeId(), null, null, null, i.getMode().name(), i.getLocation(),
                i.getNote(), i.getDurationMinutes(), i.getProposedSlots(), i.getConfirmedSlot(), i.getStatus().name(),
                i.getCreatedAt());
    }

    public static InterviewResponse from(InterviewView v) {
        Interview i = v.interview();
        return new InterviewResponse(i.getId(), i.getResumeId(), v.candidateEmail(), v.jobName(), v.companyName(),
                i.getMode().name(), i.getLocation(), i.getNote(), i.getDurationMinutes(), i.getProposedSlots(),
                i.getConfirmedSlot(), i.getStatus().name(), i.getCreatedAt());
    }
}
