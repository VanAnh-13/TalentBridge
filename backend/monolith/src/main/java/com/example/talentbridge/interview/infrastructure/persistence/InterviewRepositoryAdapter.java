package com.example.talentbridge.interview.infrastructure.persistence;

import com.example.talentbridge.interview.domain.Interview;
import com.example.talentbridge.interview.domain.InterviewMode;
import com.example.talentbridge.interview.domain.InterviewRepository;
import com.example.talentbridge.interview.domain.InterviewStatus;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;
import com.example.talentbridge.shared.infrastructure.persistence.PagingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InterviewRepositoryAdapter implements InterviewRepository {

    private static final Sort NEWEST_FIRST = Sort.by(Sort.Direction.DESC, "createdAt", "id");

    private final InterviewJpaRepository jpa;

    @Override
    public Interview save(Interview i) {
        InterviewJpaEntity e = i.getId() == null
                ? new InterviewJpaEntity()
                : jpa.findById(i.getId()).orElseThrow(() -> new NotFoundException("Interview not found"));
        if (i.getId() == null) {
            e.setResumeId(i.getResumeId());
            e.setCompanyId(i.getCompanyId());
            e.setRecruiterId(i.getRecruiterId());
            e.setCandidateId(i.getCandidateId());
            e.setCreatedAt(i.getCreatedAt());
        }
        e.setMode(i.getMode().name());
        e.setLocation(i.getLocation());
        e.setNote(i.getNote());
        e.setDurationMinutes(i.getDurationMinutes());
        e.getProposedSlots().clear();
        e.getProposedSlots().addAll(i.getProposedSlots());
        e.setConfirmedSlot(i.getConfirmedSlot());
        e.setStatus(i.getStatus().name());
        e.setReminded24h(i.isReminded24h());
        e.setReminded1h(i.isReminded1h());
        return toDomain(jpa.saveAndFlush(e));
    }

    @Override
    public Optional<Interview> findById(Long id) {
        return jpa.findById(id).map(InterviewRepositoryAdapter::toDomain);
    }

    @Override
    public boolean existsActiveForResume(Long resumeId) {
        return jpa.existsByResumeIdAndStatusIn(resumeId,
                List.of(InterviewStatus.PROPOSED.name(), InterviewStatus.CONFIRMED.name()));
    }

    @Override
    public PageResult<Interview> findByCandidate(Long candidateId, PageQuery page) {
        return PagingMapper.toPageResult(
                jpa.findByCandidateId(candidateId, PageRequest.of(page.page(), page.size(), NEWEST_FIRST)),
                InterviewRepositoryAdapter::toDomain);
    }

    @Override
    public PageResult<Interview> findByCompany(Long companyId, PageQuery page) {
        return PagingMapper.toPageResult(
                jpa.findByCompanyId(companyId, PageRequest.of(page.page(), page.size(), NEWEST_FIRST)),
                InterviewRepositoryAdapter::toDomain);
    }

    @Override
    public List<Interview> findConfirmedStartingBetween(Instant from, Instant to) {
        return jpa.findConfirmedStartingBetween(from, to).stream().map(InterviewRepositoryAdapter::toDomain).toList();
    }

    private static Interview toDomain(InterviewJpaEntity e) {
        return Interview.restore(e.getId(), e.getResumeId(), e.getCompanyId(), e.getRecruiterId(), e.getCandidateId(),
                InterviewMode.valueOf(e.getMode()), e.getLocation(), e.getNote(), e.getDurationMinutes(),
                new ArrayList<>(e.getProposedSlots()), e.getConfirmedSlot(), InterviewStatus.valueOf(e.getStatus()),
                e.isReminded24h(), e.isReminded1h(), e.getCreatedAt());
    }
}
