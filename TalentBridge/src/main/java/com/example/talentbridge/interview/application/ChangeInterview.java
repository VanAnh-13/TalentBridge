package com.example.talentbridge.interview.application;

import com.example.talentbridge.interview.application.port.Notifier;
import com.example.talentbridge.interview.domain.Interview;
import com.example.talentbridge.interview.domain.InterviewMode;
import com.example.talentbridge.interview.domain.InterviewRepository;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

/** A recruiter of the company moves or calls off an interview. */
@Service
@Transactional
@RequiredArgsConstructor
public class ChangeInterview {

    private final InterviewRepository interviews;
    private final InterviewContext context;
    private final Notifier notifier;
    private final Clock clock;

    public Interview reschedule(Long id, InterviewMode mode, String location, String note, int durationMinutes,
                                List<Instant> slots) {
        Interview interview = load(id);
        context.callerAsCompanyMemberOf(interview);

        interview.reschedule(mode, location, note, durationMinutes, slots, Instant.now(clock));
        Interview saved = interviews.save(interview);

        notifier.notify(saved.getCandidateId(), "INTERVIEW_RESCHEDULED", "Lịch phỏng vấn thay đổi",
                "Nhà tuyển dụng đề xuất khung giờ mới cho vị trí "
                        + context.application(saved.getResumeId()).jobName() + ". Vui lòng chọn lại.",
                "/user/interviews");
        return saved;
    }

    public Interview cancel(Long id) {
        Interview interview = load(id);
        context.callerAsCompanyMemberOf(interview);

        interview.cancel();
        Interview saved = interviews.save(interview);

        notifier.notify(saved.getCandidateId(), "INTERVIEW_CANCELLED", "Buổi phỏng vấn đã bị hủy",
                "Buổi phỏng vấn vị trí " + context.application(saved.getResumeId()).jobName() + " đã bị hủy.",
                "/user/interviews");
        return saved;
    }

    private Interview load(Long id) {
        return interviews.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy buổi phỏng vấn"));
    }
}
