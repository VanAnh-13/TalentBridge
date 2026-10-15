package com.example.talentbridge.model;

import com.example.talentbridge.job.infrastructure.persistence.JobJpaEntity;

import jakarta.persistence.*;
import lombok.*;
import com.example.talentbridge.shared.infrastructure.persistence.BaseEntity;
import com.example.talentbridge.model.constant.ResumeStatus;

@Entity
@Table(name = "resumes")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Resume extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String email;

    private String fileKey;

    @Enumerated(EnumType.STRING)
    private ResumeStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @ManyToOne
    @JoinColumn(name = "job_id")
    @ToString.Exclude
    private JobJpaEntity job;

    private Long version;

    public Resume(String email, ResumeStatus status, Long version) {
        this.email = email;
        this.status = status;
        this.version = version;
    }
}
