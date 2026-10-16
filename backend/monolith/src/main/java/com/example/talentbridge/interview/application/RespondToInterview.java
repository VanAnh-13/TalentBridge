package com.example.talentbridge.interview.application;

import com.example.talentbridge.interview.application.port.Accounts;
import com.example.talentbridge.interview.application.port.Notifier;
import com.example.talentbridge.interview.domain.Interview;
import com.example.talentbridge.interview.domain.InterviewRepository;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

/** The candidate answers an invitation. */
@Service
@Transactional
@RequiredArgsConstructor
public class RespondToInterview {

    private final InterviewRepository interviews;
    private final InterviewContext context;
    private final Notifier notifier;
    private final Clock clock;

    public Interview confirm(Long id, Instant slot) {
        Interview interview = load(id);
        Accounts.Account me = context.callerAsCandidateOf(interview);

        interview.confirm(slot, Instant.now(clock));
        Interview saved = interviews.save(interview);

        notifier.notify(saved.getRecruiterId(), "INTERVIEW_CONFIRMED", "Interview confirmed by the candidate",
                me.name() + " confirmed the interview at " + InterviewContext.when(slot) + " for "
                        + context.application(saved.getResumeId()).jobName(), "/recruiter/interviews");
        return saved;
    }

    public Interview decline(Long id) {
        Interview interview = load(id);
        Accounts.Account me = context.callerAsCandidateOf(interview);

        interview.decline();
        Interview saved = interviews.save(interview);

        notifier.notify(saved.getRecruiterId(), "INTERVIEW_DECLINED", "Interview declined by the candidate",
                me.name() + " declined the interview for "
                        + context.application(saved.getResumeId()).jobName(), "/recruiter/interviews");
        return saved;
    }

    private Interview load(Long id) {
        return interviews.findById(id).orElseThrow(() -> new NotFoundException("Interview not found"));
    }
}
