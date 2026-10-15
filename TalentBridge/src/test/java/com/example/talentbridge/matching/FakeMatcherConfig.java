package com.example.talentbridge.matching;

import com.example.talentbridge.matching.application.port.CvMatcher;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** A judge that reads {@code score=NN} from the CV text; "boom" in the CV makes it fail. Counts its calls. */
@TestConfiguration
public class FakeMatcherConfig {

    public static final AtomicInteger CALLS = new AtomicInteger();
    private static final Pattern SCORE = Pattern.compile("score=(\\d+)");

    @Bean
    @Primary
    CvMatcher fakeMatcher() {
        return new CvMatcher() {
            @Override public boolean available() { return true; }
            @Override public String model() { return "fake-model"; }

            @Override
            public Verdict assess(Posting job, byte[] cvPdf) {
                CALLS.incrementAndGet();
                String text = new String(cvPdf, StandardCharsets.UTF_8);
                if (text.contains("boom")) throw new com.example.talentbridge.shared.domain.exception.ServiceUnavailableException("down");
                Matcher m = SCORE.matcher(text);
                int score = m.find() ? Integer.parseInt(m.group(1)) : 50;
                List<String> skills = job.requiredSkills();
                int half = skills.size() / 2;
                return new Verdict(score, skills.subList(0, half), skills.subList(half, skills.size()),
                        "Fake summary for " + job.title(), "fake-model");
            }
        };
    }
}
