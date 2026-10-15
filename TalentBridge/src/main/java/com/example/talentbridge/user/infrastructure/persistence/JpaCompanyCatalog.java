package com.example.talentbridge.user.infrastructure.persistence;

import com.example.talentbridge.company.infrastructure.persistence.CompanyJpaEntity;
import com.example.talentbridge.user.application.port.CompanyCatalog;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("userCompanyCatalog")
@RequiredArgsConstructor
public class JpaCompanyCatalog implements CompanyCatalog {

    private final EntityManager entityManager;

    @Override
    public boolean exists(Long companyId) {
        return entityManager.find(CompanyJpaEntity.class, companyId) != null;
    }
}
