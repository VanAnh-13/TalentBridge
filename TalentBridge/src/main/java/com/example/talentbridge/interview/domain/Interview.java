package com.example.talentbridge.interview.domain;

import com.example.talentbridge.shared.domain.exception.BusinessRuleViolationException;
import com.example.talentbridge.shared.domain.exception.InvalidRequestException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * An interview between a company and a candidate about one application. The recruiter proposes up to
 * {@value #MAX_SLOTS} time slots, the candidate confirms one or declines; the recruiter may reschedule or cancel.
 */
public class Interview {

    public static final int MAX_SLOTS = 5;
    public static final int MIN_DURATION_MINUTES = 15;
    public static final int MAX_DURATION_MINUTES = 480;

    private final Long id;
    private final Long resumeId;
    private final Long companyId;
    private final Long recruiterId;
    private final Long candidateId;
    private InterviewMode mode;
    private String location;
    private String note;
    private int durationMinutes;
    private List<Instant> proposedSlots;
    private Instant confirmedSlot;
    private InterviewStatus status;
    private boolean reminded24h;
    private boolean reminded1h;
    private final Instant createdAt;

    private Interview(Long id, Long resumeId, Long companyId, Long recruiterId, Long candidateId, InterviewMode mode,
                      String location, String note, int durationMinutes, List<Instant> proposedSlots,
                      Instant confirmedSlot, InterviewStatus status, boolean reminded24h, boolean reminded1h,
                      Instant createdAt) {
        this.id = id;
        this.resumeId = resumeId;
        this.companyId = companyId;
        this.recruiterId = recruiterId;
        this.candidateId = candidateId;
        this.mode = mode;
        this.location = location;
        this.note = note;
        this.durationMinutes = durationMinutes;
        this.proposedSlots = proposedSlots;
        this.confirmedSlot = confirmedSlot;
        this.status = status;
        this.reminded24h = reminded24h;
        this.reminded1h = reminded1h;
        this.createdAt = createdAt;
    }

    public static Interview propose(Long resumeId, Long companyId, Long recruiterId, Long candidateId,
                                    InterviewMode mode, String location, String note, int durationMinutes,
                                    List<Instant> slots, Instant now) {
        Interview interview = new Interview(null, resumeId, companyId, recruiterId, candidateId, mode, null, null,
                durationMinutes, List.of(), null, InterviewStatus.PROPOSED, false, false, now);
        interview.define(mode, location, note, durationMinutes);
        interview.proposedSlots = validSlots(slots, now);
        return interview;
    }

    public static Interview restore(Long id, Long resumeId, Long companyId, Long recruiterId, Long candidateId,
                                    InterviewMode mode, String location, String note, int durationMinutes,
                                    List<Instant> proposedSlots, Instant confirmedSlot, InterviewStatus status,
                                    boolean reminded24h, boolean reminded1h, Instant createdAt) {
        return new Interview(id, resumeId, companyId, recruiterId, candidateId, mode, location, note, durationMinutes,
                List.copyOf(proposedSlots), confirmedSlot, status, reminded24h, reminded1h, createdAt);
    }

    // ------------------------------------------------------------------ behaviour

    /** The candidate picks one of the proposed slots. */
    public void confirm(Instant slot, Instant now) {
        if (status != InterviewStatus.PROPOSED)
            throw new BusinessRuleViolationException("Buổi phỏng vấn không còn chờ xác nhận");
        if (slot == null || !proposedSlots.contains(slot))
            throw new InvalidRequestException("Khung giờ không nằm trong các lựa chọn của nhà tuyển dụng");
        if (!slot.isAfter(now))
            throw new BusinessRuleViolationException("Khung giờ này đã qua");
        confirmedSlot = slot;
        status = InterviewStatus.CONFIRMED;
        reminded24h = false;
        reminded1h = false;
    }

    /** The candidate turns the interview down, before or after confirming. */
    public void decline() {
        if (!isActive())
            throw new BusinessRuleViolationException("Buổi phỏng vấn đã kết thúc");
        confirmedSlot = null;
        status = InterviewStatus.DECLINED;
    }

    /** The recruiter calls it off. */
    public void cancel() {
        if (!isActive())
            throw new BusinessRuleViolationException("Buổi phỏng vấn đã kết thúc");
        status = InterviewStatus.CANCELLED;
    }

    /** The recruiter offers new slots (and possibly a new place); the candidate has to answer again. */
    public void reschedule(InterviewMode newMode, String newLocation, String newNote, int newDurationMinutes,
                           List<Instant> slots, Instant now) {
        if (status == InterviewStatus.CANCELLED)
            throw new BusinessRuleViolationException("Buổi phỏng vấn đã bị hủy");
        define(newMode, newLocation, newNote, newDurationMinutes);
        proposedSlots = validSlots(slots, now);
        confirmedSlot = null;
        status = InterviewStatus.PROPOSED;
        reminded24h = false;
        reminded1h = false;
    }

    public boolean isActive() {
        return status == InterviewStatus.PROPOSED || status == InterviewStatus.CONFIRMED;
    }

    public Instant endsAt() {
        return confirmedSlot == null ? null : confirmedSlot.plus(Duration.ofMinutes(durationMinutes));
    }

    public void markReminded24h() { reminded24h = true; }
    public void markReminded1h() { reminded1h = true; }

    // ------------------------------------------------------------------ invariants

    private void define(InterviewMode newMode, String newLocation, String newNote, int newDurationMinutes) {
        if (newMode == null)
            throw new InvalidRequestException("Hình thức phỏng vấn không được để trống");
        if (newLocation == null || newLocation.isBlank())
            throw new InvalidRequestException(newMode == InterviewMode.ONLINE
                    ? "Liên kết phòng họp không được để trống" : "Địa điểm phỏng vấn không được để trống");
        if (newLocation.length() > 500)
            throw new InvalidRequestException("Địa điểm/liên kết tối đa 500 ký tự");
        if (newNote != null && newNote.length() > 1000)
            throw new InvalidRequestException("Ghi chú tối đa 1000 ký tự");
        if (newDurationMinutes < MIN_DURATION_MINUTES || newDurationMinutes > MAX_DURATION_MINUTES)
            throw new InvalidRequestException("Thời lượng phải từ " + MIN_DURATION_MINUTES + " đến "
                    + MAX_DURATION_MINUTES + " phút");
        this.mode = newMode;
        this.location = newLocation.trim();
        this.note = newNote == null || newNote.isBlank() ? null : newNote.trim();
        this.durationMinutes = newDurationMinutes;
    }

    private static List<Instant> validSlots(List<Instant> slots, Instant now) {
        if (slots == null || slots.isEmpty())
            throw new InvalidRequestException("Phải đề xuất ít nhất một khung giờ");
        List<Instant> distinct = slots.stream().distinct().sorted().toList();
        if (distinct.size() > MAX_SLOTS)
            throw new InvalidRequestException("Chỉ được đề xuất tối đa " + MAX_SLOTS + " khung giờ");
        if (distinct.stream().anyMatch(slot -> !slot.isAfter(now)))
            throw new InvalidRequestException("Các khung giờ phải ở trong tương lai");
        return distinct;
    }

    public Long getId() { return id; }
    public Long getResumeId() { return resumeId; }
    public Long getCompanyId() { return companyId; }
    public Long getRecruiterId() { return recruiterId; }
    public Long getCandidateId() { return candidateId; }
    public InterviewMode getMode() { return mode; }
    public String getLocation() { return location; }
    public String getNote() { return note; }
    public int getDurationMinutes() { return durationMinutes; }
    public List<Instant> getProposedSlots() { return proposedSlots; }
    public Instant getConfirmedSlot() { return confirmedSlot; }
    public InterviewStatus getStatus() { return status; }
    public boolean isReminded24h() { return reminded24h; }
    public boolean isReminded1h() { return reminded1h; }
    public Instant getCreatedAt() { return createdAt; }
}
