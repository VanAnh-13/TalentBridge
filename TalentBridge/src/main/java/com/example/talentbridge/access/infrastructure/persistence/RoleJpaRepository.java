package com.example.talentbridge.access.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleJpaRepository extends
        JpaRepository<RoleJpaEntity, Long>,
        JpaSpecificationExecutor<RoleJpaEntity> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    Optional<RoleJpaEntity> findByName(String name);
}
