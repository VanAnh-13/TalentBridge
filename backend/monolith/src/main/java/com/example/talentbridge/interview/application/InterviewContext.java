package com.example.talentbridge.interview.application;

import com.example.talentbridge.interview.application.port.Accounts;
import com.example.talentbridge.interview.application.port.Applications;
import com.example.talentbridge.interview.domain.Interview;
import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.shared.domain.exception.ForbiddenException;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.Instant;

/** Who is calling, which application an interview is about, and the wording shared by the use cases. */
@Component
@RequiredArgsConstructor
class InterviewContext {

    private static final DateTimeFormatter WHEN = DateTimeFormatter.ofPattern("HH:mm 'ngày' dd/MM/yyyy")
            .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));

    private final Accounts accounts;
    private final Applications applications;
    private final CurrentUser currentUser;

    Accounts.Account caller() {
        return accounts.findByEmail(currentUser.email())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
    }

    Applications.Application application(Long resumeId) {
        return applications.find(resumeId).orElseThrow(() -> new NotFoundException("Không tìm thấy hồ sơ ứng tuyển"));
    }

    /** The candidate of the interview, nobody else. */
    Accounts.Account callerAsCandidateOf(Interview interview) {
        Accounts.Account me = caller();
        if (!me.id().equals(interview.getCandidateId()))
            throw new ForbiddenException("Không có quyền truy cập");
        return me;
    }

    /** A member of the company that runs the interview. */
    Accounts.Account callerAsCompanyMemberOf(Interview interview) {
        Accounts.Account me = caller();
        if (me.companyId() == null || !me.companyId().equals(interview.getCompanyId()))
            throw new ForbiddenException("Không có quyền truy cập");
        return me;
    }

    static String when(Instant instant) {
        return WHEN.format(instant);
    }
}
