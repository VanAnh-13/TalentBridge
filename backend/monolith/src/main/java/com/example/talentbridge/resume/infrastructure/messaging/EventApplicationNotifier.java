package com.example.talentbridge.resume.infrastructure.messaging;

import com.example.talentbridge.resume.application.port.ApplicationNotifier;
import com.example.talentbridge.resume.domain.ResumeDetails;
import com.example.talentbridge.shared.infrastructure.messaging.Outbox;
import com.example.talentbridge.user.application.UserDirectory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Adapter: notifications belong to the notification service, which hears about them through the outbox
 * (event NotificationRequested). A failure is logged, never propagated.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventApplicationNotifier implements ApplicationNotifier {

    private final Outbox outbox;
    private final UserDirectory users;

    @Override
    public void submitted(ResumeDetails application) {
        try {
            if (application.companyId() == null) return;
            String job = application.job() == null ? "" : application.job().name();
            for (UserDirectory.UserSummary member : users.findByCompanyId(application.companyId()))
                outbox.requestNotification(member.id(), "APPLICATION_SUBMITTED", "Có ứng viên mới ứng tuyển",
                        application.user().email() + " vừa ứng tuyển vị trí " + job, "/recruiter/resumes", false);
        } catch (RuntimeException ex) {
            log.warn("Could not notify the company about application {}: {}", application.resume().getId(), ex.getMessage());
        }
    }

    @Override
    public void statusChanged(ResumeDetails application) {
        try {
            String job = application.job() == null ? "" : application.job().name();
            outbox.requestNotification(application.user().id(), "APPLICATION_STATUS_CHANGED", "Hồ sơ ứng tuyển được cập nhật",
                    "Hồ sơ của bạn cho vị trí " + job + " chuyển sang trạng thái " + application.resume().getStatus(),
                    "/user/resumes", false);
        } catch (RuntimeException ex) {
            log.warn("Could not notify the candidate about application {}: {}", application.resume().getId(), ex.getMessage());
        }
    }
}
