package com.example.talentbridge.user.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserJpaRepository extends
        JpaRepository<UserJpaEntity, Long>,
        JpaSpecificationExecutor<UserJpaEntity> {

    boolean existsByEmail(String email);

    Optional<UserJpaEntity> findByEmail(String email);

    List<UserJpaEntity> findByCompanyId(Long companyId);

    List<UserJpaEntity> findByRoleId(Long roleId);
}
