package com.example.talentbridge.company.application;

import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;
import com.example.talentbridge.company.application.port.CompanyJobs;
import com.example.talentbridge.company.domain.Company;
import com.example.talentbridge.company.domain.CompanyRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchCompanies {

    private final CompanyRepository companies;
    private final CompanyJobs jobs;

    /** A company together with how many jobs it has posted. */
    public record CompanyWithJobCount(Company company, long jobsCount) {
    }

    public PageResult<Company> execute(FilterExpression filter, PageQuery page) {
        return companies.search(filter, page);
    }

    public PageResult<CompanyWithJobCount> executeWithJobCount(FilterExpression filter, PageQuery page) {
        return companies.search(filter, page)
                .map(company -> new CompanyWithJobCount(company, jobs.countJobs(company.getId())));
    }
}
