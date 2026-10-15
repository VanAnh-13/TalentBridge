package com.example.talentbridge.job.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobJpaRepository extends
        JpaRepository<JobJpaEntity, Long>,
        JpaSpecificationExecutor<JobJpaEntity> {

    List<JobJpaEntity> findByCompanyId(Long id);

    Long countByCompanyId(Long id);

    boolean existsByCompanyIdAndActiveTrue(Long companyId);

    List<JobJpaEntity> findDistinctBySkills_NameInOrderByCreatedAtDesc(List<String> skillNames, org.springframework.data.domain.Pageable pageable);
}
