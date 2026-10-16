package com.example.talentbridge.interview.application;

import com.example.talentbridge.interview.domain.Interview;
import com.example.talentbridge.interview.domain.InterviewStatus;
import com.example.talentbridge.shared.domain.exception.BusinessRuleViolationException;
import com.example.talentbridge.shared.domain.exception.ForbiddenException;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.interview.application.port.Accounts;
import com.example.talentbridge.interview.application.port.Applications;
import com.example.talentbridge.interview.domain.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/** The iCalendar (.ics) file of a confirmed interview, for either party to add to their calendar. */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InterviewCalendar {

    private static final DateTimeFormatter UTC = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneOffset.UTC);

    private final InterviewRepository interviews;
    private final InterviewContext context;
    private final Clock clock;

    public String icsOf(Long id) {
        Interview interview = interviews.findById(id).orElseThrow(() -> new NotFoundException("Interview not found"));
        Accounts.Account me = context.caller();
        boolean candidate = me.id().equals(interview.getCandidateId());
        boolean company = me.companyId() != null && me.companyId().equals(interview.getCompanyId());
        if (!candidate && !company)
            throw new ForbiddenException("Access denied");
        if (interview.getStatus() != InterviewStatus.CONFIRMED)
            throw new BusinessRuleViolationException("Only a confirmed interview has a calendar entry");

        Applications.Application application = context.application(interview.getResumeId());
        return render(interview, "Interview: " + application.jobName() + " - " + application.companyName(),
                "Interview for " + application.jobName() + " at " + application.companyName()
                        + (interview.getNote() == null ? "" : "\n" + interview.getNote()));
    }

    String render(Interview interview, String summary, String description) {
        String location = interview.getLocation();
        return String.join("\r\n",
                "BEGIN:VCALENDAR",
                "VERSION:2.0",
                "PRODID:-//TalentBridge//Interview//VI",
                "CALSCALE:GREGORIAN",
                "METHOD:PUBLISH",
                "BEGIN:VEVENT",
                "UID:interview-" + interview.getId() + "@talentbridge",
                "DTSTAMP:" + UTC.format(Instant.now(clock)),
                "DTSTART:" + UTC.format(interview.getConfirmedSlot()),
                "DTEND:" + UTC.format(interview.endsAt()),
                "SUMMARY:" + escape(summary),
                "LOCATION:" + escape(location),
                "DESCRIPTION:" + escape(description),
                "END:VEVENT",
                "END:VCALENDAR") + "\r\n";
    }

    /** RFC 5545 text escaping. */
    static String escape(String text) {
        return text.replace("\\", "\\\\").replace(";", "\\;").replace(",", "\\,")
                .replace("\r\n", "\\n").replace("\n", "\\n");
    }
}
