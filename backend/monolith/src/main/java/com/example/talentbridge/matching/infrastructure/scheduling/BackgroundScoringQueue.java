package com.example.talentbridge.matching.infrastructure.scheduling;

import com.example.talentbridge.matching.application.ScoreApplication;
import com.example.talentbridge.matching.application.port.ScoringQueue;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Scores applications on a small worker pool, after the submitting transaction committed (the CV must be visible).
 * With {@code app.matching.async=false} the work runs on the calling thread, which keeps tests deterministic.
 */
@Slf4j
@Component
public class BackgroundScoringQueue implements ScoringQueue {

    private final ScoreApplication score;
    private final boolean async;
    private final ExecutorService workers = new ThreadPoolExecutor(2, 2, 0, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(200), runnable -> {
        Thread thread = new Thread(runnable, "cv-scoring");
        thread.setDaemon(true);
        return thread;
    });

    public BackgroundScoringQueue(ScoreApplication score, @Value("${app.matching.async:true}") boolean async) {
        this.score = score;
        this.async = async;
    }

    @Override
    public void enqueue(Long resumeId) {
        if (TransactionSynchronizationManager.isSynchronizationActive())
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    run(resumeId);
                }
            });
        else
            run(resumeId);
    }

    private void run(Long resumeId) {
        if (!async) {
            score.execute(resumeId);
            return;
        }
        try {
            workers.execute(() -> score.execute(resumeId));
        } catch (RejectedExecutionException ex) {
            log.warn("Scoring queue is full; application {} will not be scored now", resumeId);
        }
    }

    @PreDestroy
    void stop() {
        workers.shutdownNow();
    }
}
