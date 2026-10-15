package com.example.talentbridge.skill.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillJpaRepository extends
        JpaRepository<SkillJpaEntity, Long>,
        JpaSpecificationExecutor<SkillJpaEntity> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
