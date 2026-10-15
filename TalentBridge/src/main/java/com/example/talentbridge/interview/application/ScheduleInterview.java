package com.example.talentbridge.interview.application;

import com.example.talentbridge.interview.application.port.Accounts;
import com.example.talentbridge.interview.application.port.Applications;
import com.example.talentbridge.interview.application.port.Notifier;
import com.example.talentbridge.interview.domain.Interview;
import com.example.talentbridge.interview.domain.InterviewMode;
import com.example.talentbridge.interview.domain.InterviewRepository;
import com.example.talentbridge.shared.domain.exception.BusinessRuleViolationException;
import com.example.talentbridge.shared.domain.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleInterview {

    private final InterviewRepository interviews;
    private final InterviewContext context;
    private final Notifier notifier;
    private final Clock clock;

    /** A recruiter of the company that received the application proposes time slots to the candidate. */
    public Interview execute(Long resumeId, InterviewMode mode, String location, String note, int durationMinutes,
                             List<Instant> slots) {
        Accounts.Account recruiter = context.caller();
        Applications.Application application = context.application(resumeId);

        if (recruiter.companyId() == null || !recruiter.companyId().equals(application.companyId()))
            throw new ForbiddenException("Không có quyền truy cập");
        if (interviews.existsActiveForResume(resumeId))
            throw new BusinessRuleViolationException(
                    "Hồ sơ này đã có một buổi phỏng vấn đang diễn ra. Hãy dời lịch hoặc hủy buổi đó");

        Interview saved = interviews.save(Interview.propose(resumeId, application.companyId(), recruiter.id(),
                application.candidateId(), mode, location, note, durationMinutes, slots, Instant.now(clock)));

        notifier.notify(saved.getCandidateId(), "INTERVIEW_PROPOSED", "Lời mời phỏng vấn",
                application.companyName() + " mời bạn phỏng vấn vị trí " + application.jobName()
                        + ". Hãy chọn một khung giờ phù hợp.", "/user/interviews");
        return saved;
    }
}
