package com.example.talentbridge.dto.response.dashboard;

public record OverviewStatisticsDto(
    long totalUsers,
    double usersGrowth,
    long totalCompanies,
    double companiesGrowth,
    long activeJobs,
    double jobsGrowth,
    long totalResumes,
    double resumesGrowth
) {}
