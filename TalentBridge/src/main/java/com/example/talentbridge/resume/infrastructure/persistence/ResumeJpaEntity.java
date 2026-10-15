package com.example.talentbridge.resume.infrastructure.persistence;

import com.example.talentbridge.job.infrastructure.persistence.JobJpaEntity;
import com.example.talentbridge.user.infrastructure.persistence.UserJpaEntity;
import com.example.talentbridge.resume.domain.ResumeStatus;
import com.example.talentbridge.shared.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "resumes")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class ResumeJpaEntity extends BaseEntity {

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
    private UserJpaEntity user;

    @ManyToOne
    @JoinColumn(name = "job_id")
    @ToString.Exclude
    private JobJpaEntity job;

    private Long version;

    public ResumeJpaEntity(String email, ResumeStatus status, Long version) {
        this.email = email;
        this.status = status;
        this.version = version;
    }
}
