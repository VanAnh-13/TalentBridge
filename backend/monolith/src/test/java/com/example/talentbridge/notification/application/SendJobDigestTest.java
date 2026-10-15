package com.example.talentbridge.notification.application;

import com.example.talentbridge.notification.application.port.DigestMailer;
import com.example.talentbridge.notification.application.port.JobRecommendations;
import com.example.talentbridge.notification.application.port.Subscribers;
import com.example.talentbridge.notification.domain.DigestJob;
import com.example.talentbridge.notification.domain.JobDigest;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/** The use case against in-memory fakes of its three ports: no Spring, no database, no SMTP. */
class SendJobDigestTest {

    private final List<Subscribers.Subscription> subscriptions = new ArrayList<>();
    private final List<JobDigest> sent = new ArrayList<>();
    private final List<String> failFor = new ArrayList<>();
    private int lastLimit;

    private final Subscribers subscribers = new Subscribers() {
        @Override public List<Subscription> all() { return subscriptions; }
        @Override public Optional<Subscription> findByEmail(String email) {
            return subscriptions.stream().filter(s -> s.email().equals(email)).findFirst();
        }
    };

    private final JobRecommendations recommendations = (skills, limit) -> {
        lastLimit = limit;
        return skills.stream().map(s -> new DigestJob(1L, "Job for " + s, 1000.0,
                new DigestJob.Company(1L, "Acme", "Hanoi"), List.of())).toList();
    };

    private final DigestMailer mailer = digest -> {
        if (failFor.contains(digest.recipient())) throw new IllegalStateException("smtp down");
        sent.add(digest);
    };

    private final SendJobDigest sendJobDigest = new SendJobDigest(subscribers, recommendations, mailer);

    @Test
    @DisplayName("sends the digest built from the subscriber's skills")
    void sendsToOneSubscriber() {
        subscriptions.add(new Subscribers.Subscription("a@x.com", List.of("Java", "SQL")));

        sendJobDigest.toSubscriber("a@x.com");

        assertEquals(1, sent.size());
        assertEquals("a@x.com", sent.get(0).recipient());
        assertEquals(2, sent.get(0).jobs().size());
        assertEquals(3, lastLimit);
    }

    @Test
    @DisplayName("an unknown subscriber is a NotFoundException and nothing is sent")
    void unknownSubscriber() {
        assertThrows(NotFoundException.class, () -> sendJobDigest.toSubscriber("nobody@x.com"));
        assertTrue(sent.isEmpty());
    }

    @Test
    @DisplayName("a failure for one subscriber does not stop the others")
    void bulkRunSurvivesFailures() {
        subscriptions.add(new Subscribers.Subscription("a@x.com", List.of("Java")));
        subscriptions.add(new Subscribers.Subscription("b@x.com", List.of("Java")));
        subscriptions.add(new Subscribers.Subscription("c@x.com", List.of("Java")));
        failFor.add("b@x.com");

        SendJobDigest.Outcome outcome = sendJobDigest.toAllSubscribers();

        assertEquals(2, outcome.sent());
        assertEquals(1, outcome.failed());
        assertEquals(List.of("a@x.com", "c@x.com"), sent.stream().map(JobDigest::recipient).toList());
    }
}
