package com.example.talentbridge.resume.presentation;

import com.example.talentbridge.resume.domain.Resume;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeCreatedResponse {

    private Long id;
    private Instant createdAt;
    private String createdBy;

    public static ResumeCreatedResponse from(Resume resume) {
        return new ResumeCreatedResponse(resume.getId(), resume.getCreatedAt(), resume.getCreatedBy());
    }
}
