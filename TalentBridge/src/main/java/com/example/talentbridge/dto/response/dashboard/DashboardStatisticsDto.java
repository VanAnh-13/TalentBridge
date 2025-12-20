package com.example.talentbridge.dto.response.dashboard;

import java.util.List;

public record DashboardStatisticsDto(
    OverviewStatisticsDto overview,
    MonthlyStatisticsDto monthly,
    List<MonthlyTrendDto> trends
) {}
