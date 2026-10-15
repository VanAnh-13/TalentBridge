package com.example.talentbridge.job.infrastructure.persistence;

import com.example.talentbridge.company.infrastructure.persistence.CompanyJpaEntity;
import com.example.talentbridge.job.application.port.CompanyDirectory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaCompanyDirectory implements CompanyDirectory {

    private final EntityManager entityManager;

    @Override
    public boolean exists(Long companyId) {
        return entityManager.find(CompanyJpaEntity.class, companyId) != null;
    }
}
