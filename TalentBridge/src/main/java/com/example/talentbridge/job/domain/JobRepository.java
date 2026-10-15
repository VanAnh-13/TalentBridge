package com.example.talentbridge.job.domain;

import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;

import java.util.List;
import java.util.Optional;

public interface JobRepository {

    /** Persists the job and returns it with its company / skill display data. */
    JobDetails save(Job job);

    Optional<Job> findById(Long id);

    Optional<JobDetails> findDetailsById(Long id);

    PageResult<JobDetails> search(FilterExpression filter, PageQuery page);

    PageResult<JobDetails> searchByCompany(Long companyId, FilterExpression filter, PageQuery page);

    List<JobDetails> findByCompany(Long companyId);

    /** The newest jobs that require at least one of the named skills. */
    List<JobDetails> findLatestBySkillNames(List<String> skillNames, int limit);

    long countByCompany(Long companyId);

    boolean existsActiveByCompany(Long companyId);

    List<Long> findIdsByCompany(Long companyId);

    /** Removes the job and its links to skills. */
    void delete(Job job);
}
