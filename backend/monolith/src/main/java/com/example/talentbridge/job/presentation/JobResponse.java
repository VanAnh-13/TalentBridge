package com.example.talentbridge.job.presentation;

import com.example.talentbridge.job.domain.Job;
import com.example.talentbridge.job.domain.JobDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobResponse {

    private Long id;
    private String name;
    private String location;
    private Double salary;
    private Integer quantity;
    private String level;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private Boolean active;
    private CompanyDto company;
    private List<SkillDto> skills;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompanyDto {
        private Long id;
        private String name;
        private String logoUrl;
        private String address;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SkillDto {
        private Long id;
        private String name;
    }

    public static JobResponse from(JobDetails details) {
        Job job = details.job();
        CompanyDto company = details.company() == null ? null : new CompanyDto(
                details.company().id(), details.company().name(), details.company().logoUrl(), details.company().address());

        return new JobResponse(
                job.getId(), job.getName(), job.getLocation(), job.getSalary(), job.getQuantity(),
                job.getLevel().toString(), job.getDescription(), job.getStartDate(), job.getEndDate(), job.getActive(),
                company,
                details.skills().stream().map(s -> new SkillDto(s.id(), s.name())).toList());
    }

    /** The public per-company listing leaves out the description and the company. */
    public static JobResponse summaryFrom(JobDetails details) {
        JobResponse res = from(details);
        res.setDescription(null);
        res.setCompany(null);
        return res;
    }
}
