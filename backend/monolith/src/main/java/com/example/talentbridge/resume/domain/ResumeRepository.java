package com.example.talentbridge.resume.domain;

import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository {

    ResumeDetails save(Resume resume);

    Optional<ResumeDetails> findDetailsById(Long id);

    Optional<ResumeDetails> findDetailsByUserEmailAndJobId(String email, Long jobId);

    Optional<ResumeDetails> findDetailsByUserEmailAndId(String email, Long id);

    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    PageResult<ResumeDetails> search(FilterExpression filter, PageQuery page);

    PageResult<ResumeDetails> searchByUserEmail(String email, FilterExpression filter, PageQuery page);

    PageResult<ResumeDetails> searchByCompany(Long companyId, FilterExpression filter, PageQuery page);

    List<Resume> findByJobId(Long jobId);

    List<Resume> findByUserId(Long userId);

    /** Removes the resume (its file is the caller's concern). */
    void delete(Resume resume);
}
