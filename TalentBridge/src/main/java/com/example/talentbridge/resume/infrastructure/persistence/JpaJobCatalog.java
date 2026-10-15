package com.example.talentbridge.resume.infrastructure.persistence;

import com.example.talentbridge.job.infrastructure.persistence.JobJpaEntity;
import com.example.talentbridge.resume.application.port.JobCatalog;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaJobCatalog implements JobCatalog {

    private final EntityManager entityManager;

    @Override
    public boolean exists(Long jobId) {
        return entityManager.find(JobJpaEntity.class, jobId) != null;
    }
}
