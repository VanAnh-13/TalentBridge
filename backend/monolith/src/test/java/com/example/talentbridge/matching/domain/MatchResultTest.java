package com.example.talentbridge.matching.domain;

import com.example.talentbridge.shared.domain.exception.InvalidRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchResultTest {

    private static MatchResult assess(int score, List<String> matched, List<String> missing, String summary) {
        return MatchResult.assess(1L, 2L, 3L, score, matched, missing, summary, "m", Instant.EPOCH);
    }

    @Test @DisplayName("scores outside 0-100 are clamped")
    void clamps() {
        assertEquals(100, assess(240, List.of(), List.of(), "s").getScore());
        assertEquals(0, assess(-5, List.of(), List.of(), "s").getScore());
        assertEquals(73, assess(73, List.of(), List.of(), "s").getScore());
    }

    @Test @DisplayName("skill lists are trimmed, deduplicated and bounded; null means empty")
    void cleansSkills() {
        MatchResult r = assess(50, Arrays.asList(" Java ", "Java", "", null, "SQL"), null, "s");
        assertEquals(List.of("Java", "SQL"), r.getMatchedSkills());
        assertTrue(r.getMissingSkills().isEmpty());
        List<String> many = java.util.stream.IntStream.range(0, 100).mapToObj(i -> "skill" + i).toList();
        assertEquals(30, assess(50, many, List.of(), "s").getMatchedSkills().size());
    }

    @Test @DisplayName("an over-long summary is shortened; a missing one is empty")
    void summary() {
        assertEquals(MatchResult.MAX_SUMMARY, assess(50, List.of(), List.of(), "x".repeat(5000)).getSummary().length());
        assertEquals("", assess(50, List.of(), List.of(), null).getSummary());
    }

    @Test @DisplayName("a result needs an application, a job and a candidate")
    void needsIds() {
        assertThrows(InvalidRequestException.class,
                () -> MatchResult.assess(null, 2L, 3L, 1, List.of(), List.of(), "", "m", Instant.EPOCH));
        assertThrows(InvalidRequestException.class,
                () -> MatchResult.assess(1L, null, 3L, 1, List.of(), List.of(), "", "m", Instant.EPOCH));
    }
}
