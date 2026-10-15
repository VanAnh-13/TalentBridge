package com.example.talentbridge.resume.presentation;

import com.example.talentbridge.resume.application.ResumeView;
import com.example.talentbridge.resume.domain.ResumeDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeDisplayResponse {

    private Long id;
    private String status;
    private String pdfUrl;
    private User user;
    private Job job;
    private Company company;
    private String createdAt;
    private String updatedAt;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {
        private Long id;
        private String email;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Job {
        private Long id;
        private String name;
        private String location;
        private List<String> skills;
        private String level;
        private String description;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Company {
        private Long id;
        private String name;
        private String logoUrl;
    }

    public static ResumeDisplayResponse from(ResumeView view) {
        ResumeDetails d = view.details();
        return new ResumeDisplayResponse(
                d.resume().getId(),
                d.resume().getStatus().toString(),
                view.pdfUrl(),
                d.user() == null ? null : new User(d.user().id(), d.user().email()),
                d.job() == null ? null : new Job(d.job().id(), d.job().name(), d.job().location(),
                        d.job().skills(), d.job().level(), d.job().description()),
                d.company() == null ? null : new Company(d.company().id(), d.company().name(), d.company().logoUrl()),
                d.resume().getCreatedAt() == null ? null : d.resume().getCreatedAt().toString(),
                d.resume().getUpdatedAt() == null ? null : d.resume().getUpdatedAt().toString());
    }
}
