package com.example.talentbridge.company.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyJpaRepository extends
        JpaRepository<CompanyJpaEntity, Long>,
        JpaSpecificationExecutor<CompanyJpaEntity> {

    CompanyJpaEntity findByOwnerEmail(String email);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
