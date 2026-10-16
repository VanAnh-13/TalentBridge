package com.example.talentbridge.matching.application;

import com.example.talentbridge.matching.application.port.Accounts;
import com.example.talentbridge.matching.application.port.Applications;
import com.example.talentbridge.matching.application.port.Jobs;
import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.shared.domain.exception.ForbiddenException;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Who is calling, and whether they may look at the matches of an application or of a job. */
@Component
@RequiredArgsConstructor
class MatchAccess {

    private final Accounts accounts;
    private final Applications applications;
    private final Jobs jobs;
    private final CurrentUser currentUser;

    Accounts.Account caller() {
        return accounts.findByEmail(currentUser.email())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    /** The application, provided the caller is an admin or works for the company that received it. */
    Applications.Application applicationFor(Long resumeId) {
        Accounts.Account me = caller();
        Applications.Application application = applications.find(resumeId)
                .orElseThrow(() -> new NotFoundException("Application not found"));
        requireCompany(me, application.companyId());
        return application;
    }

    /** The job, provided the caller is an admin or works for the company that owns it. */
    Jobs.Job jobFor(Long jobId) {
        Accounts.Account me = caller();
        Jobs.Job job = jobs.find(jobId).orElseThrow(() -> new NotFoundException("Job not found"));
        requireCompany(me, job.companyId());
        return job;
    }

    private static void requireCompany(Accounts.Account me, Long companyId) {
        if (me.admin()) return;
        if (me.companyId() == null || !me.companyId().equals(companyId))
            throw new ForbiddenException("Access denied");
    }
}
