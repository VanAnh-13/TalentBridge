package com.example.talentbridge.skill.infrastructure.persistence;

import com.example.talentbridge.model.Job;
import com.example.talentbridge.subscriber.infrastructure.persistence.SubscriberJpaEntity;
import com.example.talentbridge.shared.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "skills")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class SkillJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "skills")
    @ToString.Exclude
    private List<Job> jobs;

    @ManyToMany(mappedBy = "skills", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<SubscriberJpaEntity> subscribers;
}
