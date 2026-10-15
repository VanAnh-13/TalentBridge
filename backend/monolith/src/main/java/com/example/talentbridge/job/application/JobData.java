package com.example.talentbridge.job.application;

import com.example.talentbridge.job.domain.Level;

import java.time.Instant;
import java.util.List;

/** The editable fields of a job as sent by a client. */
public record JobData(String name, String location, Double salary, Integer quantity, Level level,
                      String description, Instant startDate, Instant endDate, Boolean active,
                      Long companyId, List<Long> skillIds) {
}
