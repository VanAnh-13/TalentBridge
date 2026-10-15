package com.example.talentbridge.job.infrastructure.persistence;

import com.example.talentbridge.company.infrastructure.persistence.CompanyJpaEntity;
import com.example.talentbridge.job.domain.Level;
import com.example.talentbridge.resume.infrastructure.persistence.ResumeJpaEntity;
import com.example.talentbridge.skill.infrastructure.persistence.SkillJpaEntity;
import com.example.talentbridge.shared.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "jobs")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class JobJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private String location;

    private Double salary;

    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private Level level;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private Instant startDate;

    private Instant endDate;

    private Boolean active = true;

    @ManyToOne
    @JoinColumn(name = "company_id")
    @ToString.Exclude
    private CompanyJpaEntity company;

    @ManyToMany
    @JoinTable(
            name = "job_skill",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @ToString.Exclude
    private List<SkillJpaEntity> skills;

    @OneToMany(mappedBy = "job")
    @ToString.Exclude
    private List<ResumeJpaEntity> resumes;

    public JobJpaEntity(String name, String location, Double salary, Integer quantity, Level level,
                        String description, Instant startDate, Instant endDate, Boolean active) {
        this.name = name;
        this.location = location;
        this.salary = salary;
        this.quantity = quantity;
        this.level = level;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
    }
}
