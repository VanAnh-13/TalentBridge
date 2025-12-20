package com.example.talentbridge.dto.response.dashboard;

public record MonthlyTrendDto(
    String month,
    long users,
    long jobs,
    long resumes
) {}
