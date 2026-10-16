package com.example.talentbridge.job.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.talentbridge.job.domain.Level;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobRequest {

    @NotBlank(message = "Job title is required")
    private String name;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Salary is required")
    @PositiveOrZero(message = "Salary must be greater than or equal to 0")
    private Double salary;

    @NotNull(message = "Number of openings is required")
    @Positive(message = "Number of openings must be greater than 0")
    private Integer quantity;

    @NotNull(message = "Level is required")
    private Level level;

    @NotBlank(message = "Job description is required")
    private String description;

    @NotNull(message = "Start date is required")
    private Instant startDate;

    @NotNull(message = "End date is required")
    private Instant endDate;

    @NotNull(message = "Status is required")
    private Boolean active;

    @NotNull(message = "Company is required")
    @Valid
    private CompanyId company;

    @NotNull(message = "Skills are required")
    @Size(min = 1, message = "At least one skill is required")
    @Valid
    private List<SkillId> skills;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompanyId {
        @NotNull(message = "Company id is required")
        private Long id;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SkillId {
        @NotNull(message = "Skill id is required")
        private Long id;
    }

    public com.example.talentbridge.job.application.JobData toData() {
        return new com.example.talentbridge.job.application.JobData(
                name, location, salary, quantity, level, description, startDate, endDate, active,
                company == null ? null : company.getId(),
                skills == null ? null : skills.stream().map(SkillId::getId).toList());
    }
}
