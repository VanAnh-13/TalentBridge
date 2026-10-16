package com.example.talentbridge.interview.application;

import com.example.talentbridge.interview.application.port.Accounts;
import com.example.talentbridge.interview.application.port.Applications;
import com.example.talentbridge.interview.domain.Interview;
import com.example.talentbridge.interview.domain.InterviewRepository;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Interviews with the data a screen needs about their application. */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ListInterviews {

    /** An interview plus who and what it is about. */
    public record InterviewView(Interview interview, String candidateEmail, String jobName, String companyName) {
    }

    private final InterviewRepository interviews;
    private final InterviewContext context;

    public PageResult<InterviewView> asCandidate(PageQuery page) {
        return interviews.findByCandidate(context.caller().id(), page).map(this::view);
    }

    public PageResult<InterviewView> forMyCompany(PageQuery page) {
        Accounts.Account me = context.caller();
        if (me.companyId() == null)
            throw new NotFoundException("The user's company was not found");
        return interviews.findByCompany(me.companyId(), page).map(this::view);
    }

    private InterviewView view(Interview interview) {
        Applications.Application application = context.application(interview.getResumeId());
        return new InterviewView(interview, application.candidateEmail(), application.jobName(), application.companyName());
    }
}
