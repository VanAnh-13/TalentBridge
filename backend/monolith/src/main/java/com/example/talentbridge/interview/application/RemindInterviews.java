package com.example.talentbridge.interview.application;

import com.example.talentbridge.interview.application.port.Applications;
import com.example.talentbridge.interview.application.port.Notifier;
import com.example.talentbridge.interview.domain.Interview;
import com.example.talentbridge.interview.domain.InterviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/** Reminds both sides 24 hours and 1 hour before a confirmed interview. Idempotent: each reminder goes out once. */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RemindInterviews {

    private final InterviewRepository interviews;
    private final Applications applications;
    private final Notifier notifier;
    private final Clock clock;
    private final InterviewTimeFormat timeFormat;

    /** @return how many reminders were sent */
    public int run() {
        Instant now = Instant.now(clock);
        int sent = 0;
        for (Interview interview : interviews.findConfirmedStartingBetween(now, now.plus(Duration.ofHours(24)))) {
            boolean withinHour = !interview.getConfirmedSlot().isAfter(now.plus(Duration.ofHours(1)));
            if (withinHour && !interview.isReminded1h()) {
                remind(interview, "1 hour");
                interview.markReminded24h();
                interview.markReminded1h();
                interviews.save(interview);
                sent++;
            } else if (!withinHour && !interview.isReminded24h()) {
                remind(interview, "24 hours");
                interview.markReminded24h();
                interviews.save(interview);
                sent++;
            }
        }
        if (sent > 0) log.info("Sent {} interview reminders", sent);
        return sent;
    }

    private void remind(Interview interview, String lead) {
        String job = applications.find(interview.getResumeId()).map(Applications.Application::jobName).orElse("");
        String text = "The interview for " + job + " starts in " + lead + " ("
                + timeFormat.format(interview.getConfirmedSlot()) + ").";
        notifier.notify(interview.getCandidateId(), "INTERVIEW_REMINDER", "Interview reminder", text, "/user/interviews");
        notifier.notify(interview.getRecruiterId(), "INTERVIEW_REMINDER", "Interview reminder", text, "/recruiter/interviews");
    }
}
