package com.example.talentbridge.job.application;

import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;
import com.example.talentbridge.job.domain.JobDetails;
import com.example.talentbridge.job.domain.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchJobs {

    private final JobRepository jobs;
    private final RecruiterScope recruiterScope;

    public PageResult<JobDetails> execute(FilterExpression filter, PageQuery page) {
        return jobs.search(filter, page);
    }

    /** The jobs of the caller's company. */
    public PageResult<JobDetails> executeForCurrentUser(FilterExpression filter, PageQuery page) {
        return jobs.searchByCompany(recruiterScope.companyId(), filter, page);
    }

    public List<JobDetails> ofCompany(Long companyId) {
        return jobs.findByCompany(companyId);
    }
}
