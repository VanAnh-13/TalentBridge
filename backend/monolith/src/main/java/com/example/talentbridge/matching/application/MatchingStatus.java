package com.example.talentbridge.matching.application;

import com.example.talentbridge.matching.application.port.CvMatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Whether AI scoring is switched on, and with which model. */
@Service
@RequiredArgsConstructor
public class MatchingStatus {

    public record Status(boolean enabled, String model) {
    }

    private final CvMatcher matcher;

    public Status execute() {
        return new Status(matcher.available(), matcher.available() ? matcher.model() : null);
    }
}
