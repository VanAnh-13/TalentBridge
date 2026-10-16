package com.example.talentbridge.company.infrastructure.persistence;

import com.example.talentbridge.company.domain.Company;
import com.example.talentbridge.company.domain.CompanyRepository;
import com.example.talentbridge.user.infrastructure.persistence.UserJpaEntity;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;
import com.example.talentbridge.shared.infrastructure.persistence.FilterSpecifications;
import com.example.talentbridge.shared.infrastructure.persistence.PagingMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CompanyRepositoryAdapter implements CompanyRepository {

    private final CompanyJpaRepository jpa;
    private final EntityManager entityManager;
    private final FilterSpecifications filters;

    @Override
    public Company save(Company company) {
        CompanyJpaEntity entity = company.getId() == null
                ? new CompanyJpaEntity()
                : jpa.findById(company.getId()).orElseThrow(() -> new NotFoundException("Company not found"));

        entity.setName(company.getName());
        entity.setDescription(company.getDescription());
        entity.setAddress(company.getAddress());
        entity.setOwner(company.getOwnerId() == null ? null : entityManager.getReference(UserJpaEntity.class, company.getOwnerId()));

        if (company.getLogoUrl() != null) {
            CompanyLogoJpaEntity logo = entity.getCompanyLogo();
            if (logo == null) {
                logo = new CompanyLogoJpaEntity();
                logo.setCompany(entity);
                entity.setCompanyLogo(logo);
            }
            logo.setLogoUrl(company.getLogoUrl());
        }

        return toDomain(jpa.saveAndFlush(entity));
    }

    @Override
    public Optional<Company> findById(Long id) {
        return jpa.findById(id).map(CompanyRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<Company> findByOwnerId(Long ownerId) {
        return jpa.findByOwnerId(ownerId).map(CompanyRepositoryAdapter::toDomain);
    }

    @Override
    public PageResult<Company> search(FilterExpression filter, PageQuery page) {
        return PagingMapper.toPageResult(
                jpa.findAll(filters.<CompanyJpaEntity>toSpecification(filter), PagingMapper.toPageable(page)),
                CompanyRepositoryAdapter::toDomain);
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return jpa.existsByNameIgnoreCase(name);
    }

    @Override
    public boolean existsByNameIgnoreCaseAndIdNot(String name, Long id) {
        return jpa.existsByNameIgnoreCaseAndIdNot(name, id);
    }

    @Override
    public void delete(Company company) {
        CompanyJpaEntity entity = jpa.findById(company.getId())
                .orElseThrow(() -> new NotFoundException("Company not found"));
        // the owner column must not point at a user while the company row goes away
        entity.setOwner(null);
        jpa.delete(entity);          // cascades to the logo row
        jpa.flush();
    }

    static Company toDomain(CompanyJpaEntity e) {
        return Company.restore(
                e.getId(), e.getName(), e.getDescription(), e.getAddress(),
                e.getCompanyLogo() == null ? null : e.getCompanyLogo().getLogoUrl(),
                e.getOwner() == null ? null : e.getOwner().getId(),
                e.getCreatedAt(), e.getUpdatedAt());
    }
}
