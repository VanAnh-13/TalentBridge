package com.example.talentbridge.notification.infrastructure.mail;

import com.example.talentbridge.notification.domain.DigestJob;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/** The model the Thymeleaf template renders (property names are part of the template). */
@Data
@AllArgsConstructor
public class DigestJobView {

    private Long id;
    private String name;
    private Double salary;
    private List<SkillView> skills;
    private CompanyView company;
    private String applyUrl;

    @Data
    @AllArgsConstructor
    public static class SkillView {
        private Long id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    public static class CompanyView {
        private Long id;
        private String name;
        private String location;
    }

    public static DigestJobView of(DigestJob job, String frontendUrl) {
        return new DigestJobView(
                job.id(), job.name(), job.salary(),
                job.skills().stream().map(s -> new SkillView(s.id(), s.name())).toList(),
                job.company() == null ? null : new CompanyView(job.company().id(), job.company().name(), job.company().address()),
                frontendUrl + "/jobs/" + job.id());
    }
}
