package com.example.talentbridge.access.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionJpaRepository extends
        JpaRepository<PermissionJpaEntity, Long>,
        JpaSpecificationExecutor<PermissionJpaEntity> {

    boolean existsByApiPathAndMethod(String apiPath, String method);

    boolean existsByApiPathAndMethodAndIdNot(String apiPath, String method, Long id);
}
