package com.example.talentbridge.matching.application;

import com.example.talentbridge.matching.application.port.Jobs;
import com.example.talentbridge.matching.application.port.Subscriptions;
import com.example.talentbridge.shared.application.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Jobs for the calling candidate, from the skills they follow: the more of a job's required skills they have,
 * the higher it ranks. This is a transparent overlap, not an AI judgement (that needs a CV and a job together).
 */
@Service
@RequiredArgsConstructor
public class RecommendJobsFor {

    public record Recommendation(Jobs.Job job, int fitPercent, List<String> sharedSkills) {
    }

    private static final int POOL = 50;

    private final Subscriptions subscriptions;
    private final Jobs jobs;
    private final CurrentUser currentUser;

    public List<Recommendation> execute(int limit) {
        List<String> mine = subscriptions.skillsOf(currentUser.email());
        if (mine.isEmpty()) return List.of();
        Set<String> mineLower = new HashSet<>();
        mine.forEach(s -> mineLower.add(s.toLowerCase(Locale.ROOT)));

        return jobs.latestRequiring(mine, POOL).stream()
                .map(job -> {
                    List<String> shared = job.skills().stream()
                            .filter(s -> mineLower.contains(s.toLowerCase(Locale.ROOT))).toList();
                    int fit = job.skills().isEmpty() ? 0 : Math.round(100f * shared.size() / job.skills().size());
                    return new Recommendation(job, fit, shared);
                })
                .sorted(Comparator.comparingInt(Recommendation::fitPercent).reversed()
                        .thenComparing(r -> r.job().id(), Comparator.reverseOrder()))
                .limit(Math.max(1, limit))
                .toList();
    }
}
