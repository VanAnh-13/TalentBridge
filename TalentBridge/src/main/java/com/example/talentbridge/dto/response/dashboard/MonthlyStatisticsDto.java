package com.example.talentbridge.dto.response.dashboard;

public record MonthlyStatisticsDto(
    long newUsers,
    long totalUsers,
    long newJobs,
    long totalJobs,
    long newResumes,
    long totalResumes,
    long subscribers,
    double resumeAcceptanceRate
) {}
