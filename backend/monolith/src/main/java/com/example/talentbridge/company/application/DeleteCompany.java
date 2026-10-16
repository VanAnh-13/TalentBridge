package com.example.talentbridge.company.application;

import com.example.talentbridge.shared.domain.exception.BusinessRuleViolationException;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.shared.application.FileStorage;
import com.example.talentbridge.company.application.port.MemberDirectory;
import com.example.talentbridge.company.application.port.CompanyJobs;
import com.example.talentbridge.company.domain.Company;
import com.example.talentbridge.company.domain.CompanyRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteCompany {

    private final CompanyRepository companies;
    private final MemberDirectory members;
    private final CompanyJobs jobs;
    private final FileStorage storage;

    public Company execute(Long id) {
        Company company = companies.findById(id)
                .orElseThrow(() -> new NotFoundException("Company not found"));

        // Must run before any side effect below (members are detached, jobs deleted).
        if (jobs.hasActiveJobs(company.getId()))
            throw new BusinessRuleViolationException(
                    "The company has active jobs: close them before deleting the company");

        members.removeAllFromCompany(company.getId());

        if (company.getLogoUrl() != null)
            storage.deleteFileByUrl(company.getLogoUrl());

        jobs.deleteAllJobs(company.getId());
        companies.delete(company);
        return company;
    }
}
