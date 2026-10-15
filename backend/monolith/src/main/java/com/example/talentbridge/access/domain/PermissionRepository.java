package com.example.talentbridge.access.domain;

import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PermissionRepository {

    Permission save(Permission permission);

    Optional<Permission> findById(Long id);

    /** The existing permissions among {@code ids}; unknown ids are ignored. */
    List<Permission> findAllById(Collection<Long> ids);

    PageResult<Permission> search(FilterExpression filter, PageQuery page);

    List<Permission> findAll(FilterExpression filter);

    boolean existsByApiPathAndMethod(String apiPath, String method);

    boolean existsByApiPathAndMethodAndIdNot(String apiPath, String method, Long id);

    /** Deletes the permission and withdraws it from every role that held it. */
    void delete(Permission permission);
}
