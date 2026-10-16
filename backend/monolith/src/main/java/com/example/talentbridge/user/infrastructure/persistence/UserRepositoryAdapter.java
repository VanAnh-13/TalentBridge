package com.example.talentbridge.user.infrastructure.persistence;

import com.example.talentbridge.company.infrastructure.persistence.CompanyJpaEntity;
import com.example.talentbridge.access.infrastructure.persistence.RoleJpaEntity;
import com.example.talentbridge.user.domain.User;
import com.example.talentbridge.user.domain.UserAccount;
import com.example.talentbridge.user.domain.UserDetails;
import com.example.talentbridge.user.domain.UserRepository;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;
import com.example.talentbridge.shared.infrastructure.persistence.FilterSpecifications;
import com.example.talentbridge.shared.infrastructure.persistence.PagingMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpa;
    private final EntityManager entityManager;
    private final FilterSpecifications filters;

    @Override
    public UserDetails save(User user) {
        UserJpaEntity entity;
        if (user.getId() == null) {
            entity = new UserJpaEntity();
            entity.setEmail(user.getEmail());
        } else {
            entity = jpa.findById(user.getId()).orElseThrow(() -> new NotFoundException("User not found"));
        }

        entity.setName(user.getName());
        entity.setPassword(user.getPasswordHash());
        entity.setDob(user.getDob());
        entity.setAddress(user.getAddress());
        entity.setGender(user.getGender());
        entity.setLogoUrl(user.getLogoUrl());
        entity.setCompany(user.getCompanyId() == null ? null : entityManager.getReference(CompanyJpaEntity.class, user.getCompanyId()));
        entity.setRole(user.getRoleId() == null ? null : entityManager.getReference(RoleJpaEntity.class, user.getRoleId()));

        return toDetails(jpa.saveAndFlush(entity));
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpa.findById(id).map(UserRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpa.findByEmail(email).map(UserRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<UserDetails> findDetailsById(Long id) {
        return jpa.findById(id).map(UserRepositoryAdapter::toDetails);
    }

    @Override
    public Optional<UserDetails> findDetailsByEmail(String email) {
        return jpa.findByEmail(email).map(UserRepositoryAdapter::toDetails);
    }

    @Override
    public Optional<UserAccount> findAccountByEmail(String email) {
        return jpa.findByEmail(email).map(e -> {
            RoleJpaEntity role = e.getRole();
            List<String> permissions = role == null || role.getPermissions() == null
                    ? List.of()
                    : role.getPermissions().stream().map(p -> p.getMethod() + " " + p.getApiPath()).toList();
            return new UserAccount(toDomain(e), roleSummary(role), permissions);
        });
    }

    @Override
    public PageResult<UserDetails> search(FilterExpression filter, PageQuery page) {
        return PagingMapper.toPageResult(
                jpa.findAll(filters.<UserJpaEntity>toSpecification(filter), PagingMapper.toPageable(page)),
                UserRepositoryAdapter::toDetails);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email);
    }

    @Override
    public List<User> findByCompanyId(Long companyId) {
        return jpa.findByCompanyId(companyId).stream().map(UserRepositoryAdapter::toDomain).toList();
    }

    @Override
    public List<User> findByRoleId(Long roleId) {
        return jpa.findByRoleId(roleId).stream().map(UserRepositoryAdapter::toDomain).toList();
    }

    @Override
    public void delete(User user) {
        UserJpaEntity entity = jpa.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        entity.setRole(null);
        entity.setCompany(null);
        jpa.delete(entity);
        jpa.flush();
    }

    static User toDomain(UserJpaEntity e) {
        return User.restore(
                e.getId(), e.getEmail(), e.getName(), e.getPassword(), e.getDob(), e.getAddress(), e.getGender(),
                e.getLogoUrl(),
                e.getCompany() == null ? null : e.getCompany().getId(),
                e.getRole() == null ? null : e.getRole().getId(),
                e.getCreatedAt(), e.getUpdatedAt());
    }

    static UserDetails.RoleSummary roleSummary(RoleJpaEntity role) {
        return role == null ? null : new UserDetails.RoleSummary(role.getId(), role.getName(), role.getDescription());
    }

    static UserDetails toDetails(UserJpaEntity e) {
        UserDetails.CompanySummary company = null;
        if (e.getCompany() != null)
            company = new UserDetails.CompanySummary(
                    e.getCompany().getId(), e.getCompany().getName(), e.getCompany().getAddress(),
                    e.getCompany().getCompanyLogo() == null ? "" : e.getCompany().getCompanyLogo().getLogoUrl());

        return new UserDetails(toDomain(e), company, roleSummary(e.getRole()));
    }
}
