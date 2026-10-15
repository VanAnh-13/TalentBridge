package com.example.talentbridge.company.domain;

import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;

import java.util.Optional;

public interface CompanyRepository {

    Company save(Company company);

    Optional<Company> findById(Long id);

    Optional<Company> findByOwnerId(Long ownerId);

    PageResult<Company> search(FilterExpression filter, PageQuery page);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    /** Removes the company together with its logo row. */
    void delete(Company company);
}
