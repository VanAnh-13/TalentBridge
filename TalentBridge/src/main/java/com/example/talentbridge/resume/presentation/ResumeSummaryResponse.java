package com.example.talentbridge.resume.presentation;

import com.example.talentbridge.resume.domain.ResumeDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeSummaryResponse {

    private Long id;
    private String email;
    private String jobName;
    private String companyName;
    private String createdAt;
    private String updatedAt;

    public static ResumeSummaryResponse from(ResumeDetails d) {
        return new ResumeSummaryResponse(
                d.resume().getId(),
                d.user() == null ? null : d.user().email(),
                d.job() == null ? null : d.job().name(),
                d.company() == null ? null : d.company().name(),
                d.resume().getCreatedAt() == null ? null : d.resume().getCreatedAt().toString(),
                d.resume().getUpdatedAt() == null ? null : d.resume().getUpdatedAt().toString());
    }
}
