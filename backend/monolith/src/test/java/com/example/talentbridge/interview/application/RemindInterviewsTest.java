package com.example.talentbridge.interview.application;

import com.example.talentbridge.interview.application.port.Applications;
import com.example.talentbridge.interview.application.port.Notifier;
import com.example.talentbridge.interview.domain.Interview;
import com.example.talentbridge.interview.domain.InterviewMode;
import com.example.talentbridge.interview.domain.InterviewRepository;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The reminder rules against in-memory fakes and a fixed clock. */
class RemindInterviewsTest {

    private static final Instant START = Instant.parse("2030-03-10T09:00:00Z");

    /** Just enough of a repository for the reminder query. */
    static class FakeRepository implements InterviewRepository {
        final List<Interview> all = new ArrayList<>();
        @Override public Interview save(Interview interview) { return interview; }
        @Override public Optional<Interview> findById(Long id) { return Optional.empty(); }
        @Override public boolean existsActiveForResume(Long resumeId) { return false; }
        @Override public PageResult<Interview> findByCandidate(Long candidateId, PageQuery page) { return null; }
        @Override public PageResult<Interview> findByCompany(Long companyId, PageQuery page) { return null; }
        @Override public List<Interview> findConfirmedStartingBetween(Instant from, Instant to) {
            return all.stream().filter(i -> i.getConfirmedSlot().isAfter(from) && !i.getConfirmedSlot().isAfter(to)).toList();
        }
    }

    private final FakeRepository repository = new FakeRepository();
    private final List<String> sent = new ArrayList<>();
    private final Notifier notifier = (userId, type, title, body, link) -> sent.add(userId + ":" + body);
    private final Applications applications = id -> Optional.of(new Applications.Application(id, 4L, "c@x.com", "Java Dev", 2L, "Acme"));

    private RemindInterviews at(Instant now) {
        return new RemindInterviews(repository, applications, notifier, Clock.fixed(now, ZoneOffset.UTC));
    }

    private Interview confirmedFor(Instant slot) {
        Instant created = slot.minus(Duration.ofDays(10));
        Interview i = Interview.propose(1L, 2L, 3L, 4L, InterviewMode.ONLINE, "https://meet", null, 60, List.of(slot), created);
        i.confirm(slot, created);
        repository.all.add(i);
        return i;
    }

    @Test
    @DisplayName("nothing to do more than a day ahead")
    void tooEarly() {
        confirmedFor(START);
        assertEquals(0, at(START.minus(Duration.ofHours(25))).run());
        assertTrue(sent.isEmpty());
    }

    @Test
    @DisplayName("24h reminder goes to both sides, once")
    void dayBefore() {
        Interview i = confirmedFor(START);
        RemindInterviews job = at(START.minus(Duration.ofHours(23)));

        assertEquals(1, job.run());
        assertEquals(2, sent.size());
        assertTrue(sent.get(0).startsWith("4:") && sent.get(1).startsWith("3:"), "candidate then recruiter");
        assertTrue(sent.get(0).contains("Java Dev") && sent.get(0).contains("in 24 hours"));
        assertTrue(i.isReminded24h());
        assertEquals(0, job.run(), "idempotent");
        assertEquals(2, sent.size());
    }

    @Test
    @DisplayName("1h reminder follows later; each reminder is sent once")
    void hourBefore() {
        Interview i = confirmedFor(START);
        assertEquals(1, at(START.minus(Duration.ofHours(23))).run());
        assertEquals(1, at(START.minus(Duration.ofMinutes(50))).run());
        assertEquals(4, sent.size());
        assertTrue(sent.get(2).contains("in 1 hour"));
        assertTrue(i.isReminded1h());
        assertEquals(0, at(START.minus(Duration.ofMinutes(30))).run());
    }

    @Test
    @DisplayName("confirmed within the last hour: only the 1h reminder, never both")
    void lateConfirmation() {
        confirmedFor(START);
        assertEquals(1, at(START.minus(Duration.ofMinutes(40))).run());
        assertEquals(2, sent.size());
        assertEquals(0, at(START.minus(Duration.ofMinutes(20))).run());
    }

    @Test
    @DisplayName("interviews that already started are left alone")
    void past() {
        confirmedFor(START);
        assertEquals(0, at(START.plusSeconds(1)).run());
    }
}
