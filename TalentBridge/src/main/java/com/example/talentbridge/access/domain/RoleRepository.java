package com.example.talentbridge.access.domain;

import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;

import java.util.Optional;

public interface RoleRepository {

    Role save(Role role);

    Optional<Role> findById(Long id);

    PageResult<Role> search(FilterExpression filter, PageQuery page);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    void delete(Role role);
}
