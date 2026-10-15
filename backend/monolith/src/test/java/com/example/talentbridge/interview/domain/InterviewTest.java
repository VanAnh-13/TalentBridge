package com.example.talentbridge.interview.domain;

import com.example.talentbridge.shared.domain.exception.BusinessRuleViolationException;
import com.example.talentbridge.shared.domain.exception.InvalidRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InterviewTest {

    private static final Instant NOW = Instant.parse("2030-01-01T09:00:00Z");
    private static final Instant TOMORROW = NOW.plus(Duration.ofDays(1));
    private static final Instant IN_TWO_DAYS = NOW.plus(Duration.ofDays(2));

    private Interview proposed() {
        return Interview.propose(1L, 2L, 3L, 4L, InterviewMode.ONLINE, "https://meet.example/abc", " bring CV ", 45,
                List.of(IN_TWO_DAYS, TOMORROW, TOMORROW), NOW);
    }

    @Test
    @DisplayName("proposing sorts and de-duplicates the slots, trims text and starts as PROPOSED")
    void propose() {
        Interview i = proposed();
        assertEquals(InterviewStatus.PROPOSED, i.getStatus());
        assertEquals(List.of(TOMORROW, IN_TWO_DAYS), i.getProposedSlots());
        assertEquals("bring CV", i.getNote());
        assertTrue(i.isActive());
        assertNull(i.getConfirmedSlot());
    }

    @Test
    @DisplayName("slots, duration, place and mode are validated")
    void validation() {
        assertThrows(InvalidRequestException.class, () -> Interview.propose(1L, 2L, 3L, 4L, InterviewMode.ONLINE, "x", null, 60, List.of(), NOW));
        assertThrows(InvalidRequestException.class, () -> Interview.propose(1L, 2L, 3L, 4L, InterviewMode.ONLINE, "x", null, 60, List.of(NOW.minusSeconds(1)), NOW));
        assertThrows(InvalidRequestException.class, () -> Interview.propose(1L, 2L, 3L, 4L, InterviewMode.ONLINE, "x", null, 60, List.of(NOW), NOW), "now is not in the future");
        assertThrows(InvalidRequestException.class, () -> Interview.propose(1L, 2L, 3L, 4L, InterviewMode.ONLINE, "x", null, 5, List.of(TOMORROW), NOW));
        assertThrows(InvalidRequestException.class, () -> Interview.propose(1L, 2L, 3L, 4L, InterviewMode.ONLINE, "x", null, 600, List.of(TOMORROW), NOW));
        assertThrows(InvalidRequestException.class, () -> Interview.propose(1L, 2L, 3L, 4L, InterviewMode.ONSITE, " ", null, 60, List.of(TOMORROW), NOW));
        assertThrows(InvalidRequestException.class, () -> Interview.propose(1L, 2L, 3L, 4L, null, "x", null, 60, List.of(TOMORROW), NOW));
        List<Instant> six = java.util.stream.IntStream.rangeClosed(1, 6).mapToObj(d -> NOW.plus(Duration.ofDays(d))).toList();
        assertThrows(InvalidRequestException.class, () -> Interview.propose(1L, 2L, 3L, 4L, InterviewMode.ONLINE, "x", null, 60, six, NOW));
    }

    @Test
    @DisplayName("the candidate confirms one of the proposed slots, once")
    void confirm() {
        Interview i = proposed();
        assertThrows(InvalidRequestException.class, () -> i.confirm(NOW.plus(Duration.ofDays(3)), NOW), "not offered");
        assertThrows(InvalidRequestException.class, () -> i.confirm(null, NOW));

        i.confirm(TOMORROW, NOW);
        assertEquals(InterviewStatus.CONFIRMED, i.getStatus());
        assertEquals(TOMORROW, i.getConfirmedSlot());
        assertEquals(TOMORROW.plus(Duration.ofMinutes(45)), i.endsAt());
        assertThrows(BusinessRuleViolationException.class, () -> i.confirm(IN_TWO_DAYS, NOW), "already answered");
    }

    @Test
    @DisplayName("a slot that has passed by the time the candidate answers is refused")
    void confirmTooLate() {
        Interview i = proposed();
        assertThrows(BusinessRuleViolationException.class, () -> i.confirm(TOMORROW, TOMORROW.plusSeconds(1)));
    }

    @Test
    @DisplayName("decline works before and after confirming, not after the end")
    void decline() {
        Interview before = proposed();
        before.decline();
        assertEquals(InterviewStatus.DECLINED, before.getStatus());
        assertFalse(before.isActive());
        assertThrows(BusinessRuleViolationException.class, before::decline);

        Interview after = proposed();
        after.confirm(TOMORROW, NOW);
        after.decline();
        assertEquals(InterviewStatus.DECLINED, after.getStatus());
        assertNull(after.getConfirmedSlot());
    }

    @Test
    @DisplayName("cancel ends it for good; nothing can be rescheduled afterwards")
    void cancel() {
        Interview i = proposed();
        i.cancel();
        assertEquals(InterviewStatus.CANCELLED, i.getStatus());
        assertThrows(BusinessRuleViolationException.class, i::cancel);
        assertThrows(BusinessRuleViolationException.class, () -> i.reschedule(InterviewMode.ONLINE, "x", null, 60, List.of(TOMORROW), NOW));
    }

    @Test
    @DisplayName("rescheduling re-opens a declined or confirmed interview with fresh slots and reminders")
    void reschedule() {
        Interview i = proposed();
        i.confirm(TOMORROW, NOW);
        i.markReminded24h();
        i.markReminded1h();

        i.reschedule(InterviewMode.ONSITE, "12 Nguyen Hue", null, 30, List.of(IN_TWO_DAYS), NOW);
        assertEquals(InterviewStatus.PROPOSED, i.getStatus());
        assertEquals(InterviewMode.ONSITE, i.getMode());
        assertEquals(List.of(IN_TWO_DAYS), i.getProposedSlots());
        assertNull(i.getConfirmedSlot());
        assertFalse(i.isReminded24h());
        assertFalse(i.isReminded1h());

        i.decline();
        i.reschedule(InterviewMode.ONSITE, "12 Nguyen Hue", null, 30, List.of(IN_TWO_DAYS), NOW);
        assertEquals(InterviewStatus.PROPOSED, i.getStatus());
    }
}
