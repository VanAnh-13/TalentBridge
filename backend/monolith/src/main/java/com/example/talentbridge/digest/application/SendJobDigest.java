package com.example.talentbridge.digest.application;

import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.digest.application.port.DigestMailer;
import com.example.talentbridge.digest.application.port.JobRecommendations;
import com.example.talentbridge.digest.application.port.Subscribers;
import com.example.talentbridge.digest.domain.DigestJob;
import com.example.talentbridge.digest.domain.JobDigest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/** Tells subscribers about the newest jobs that match the skills they follow. */
@Slf4j
@Service
@RequiredArgsConstructor
public class SendJobDigest {

    static final int JOBS_PER_DIGEST = 3;

    /** How a bulk run went. */
    public record Outcome(int sent, int failed) {
    }

    private final Subscribers subscribers;
    private final JobRecommendations recommendations;
    private final DigestMailer mailer;

    /** Sends one digest on request. */
    public void toSubscriber(String email) {
        Subscribers.Subscription subscription = subscribers.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        deliver(subscription);
    }

    /** The daily run: a failure for one subscriber never stops the others. */
    public Outcome toAllSubscribers() {
        int sent = 0, failed = 0;
        for (Subscribers.Subscription subscription : subscribers.all()) {
            try {
                deliver(subscription);
                sent++;
            } catch (Exception ex) {
                failed++;
                log.warn("Could not send the job digest to a subscriber: {}", ex.getMessage());
            }
        }
        log.info("Sent job mail to {} users, failed {}", sent, failed);
        return new Outcome(sent, failed);
    }

    private void deliver(Subscribers.Subscription subscription) {
        List<DigestJob> jobs = recommendations.latestFor(subscription.skillNames(), JOBS_PER_DIGEST);
        mailer.send(new JobDigest(subscription.email(), jobs));
    }
}
