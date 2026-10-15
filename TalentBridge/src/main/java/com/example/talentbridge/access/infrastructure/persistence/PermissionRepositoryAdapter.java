package com.example.talentbridge.access.infrastructure.persistence;

import com.example.talentbridge.access.domain.Permission;
import com.example.talentbridge.access.domain.PermissionRepository;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;
import com.example.talentbridge.shared.infrastructure.persistence.FilterSpecifications;
import com.example.talentbridge.shared.infrastructure.persistence.PagingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PermissionRepositoryAdapter implements PermissionRepository {

    private final PermissionJpaRepository jpa;
    private final RoleJpaRepository roleJpa;
    private final FilterSpecifications filters;

    @Override
    public Permission save(Permission permission) {
        PermissionJpaEntity entity = permission.getId() == null
                ? new PermissionJpaEntity()
                : jpa.findById(permission.getId()).orElseThrow(() -> new NotFoundException("Không tìm thấy quyền hạn này"));

        entity.setName(permission.getName());
        entity.setApiPath(permission.getApiPath());
        entity.setMethod(permission.getMethod());
        entity.setModule(permission.getModule());
        return toDomain(jpa.saveAndFlush(entity));
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return jpa.findById(id).map(PermissionRepositoryAdapter::toDomain);
    }

    @Override
    public List<Permission> findAllById(Collection<Long> ids) {
        return jpa.findAllById(ids).stream().map(PermissionRepositoryAdapter::toDomain).toList();
    }

    @Override
    public PageResult<Permission> search(FilterExpression filter, PageQuery page) {
        return PagingMapper.toPageResult(
                jpa.findAll(filters.<PermissionJpaEntity>toSpecification(filter), PagingMapper.toPageable(page)),
                PermissionRepositoryAdapter::toDomain);
    }

    @Override
    public List<Permission> findAll(FilterExpression filter) {
        return jpa.findAll(filters.<PermissionJpaEntity>toSpecification(filter)).stream()
                .map(PermissionRepositoryAdapter::toDomain).toList();
    }

    @Override
    public boolean existsByApiPathAndMethod(String apiPath, String method) {
        return jpa.existsByApiPathAndMethod(apiPath, method);
    }

    @Override
    public boolean existsByApiPathAndMethodAndIdNot(String apiPath, String method, Long id) {
        return jpa.existsByApiPathAndMethodAndIdNot(apiPath, method, id);
    }

    @Override
    public void delete(Permission permission) {
        PermissionJpaEntity entity = jpa.findById(permission.getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy quyền hạn này"));

        if (entity.getRoles() != null)
            entity.getRoles().forEach(role -> {
                role.getPermissions().remove(entity);
                roleJpa.saveAndFlush(role);
            });

        jpa.delete(entity);
    }

    static Permission toDomain(PermissionJpaEntity e) {
        return Permission.restore(e.getId(), e.getName(), e.getApiPath(), e.getMethod(), e.getModule(),
                e.getCreatedAt(), e.getUpdatedAt());
    }
}
