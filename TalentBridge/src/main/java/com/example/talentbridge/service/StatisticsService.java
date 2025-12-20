package com.example.talentbridge.service;

import com.example.talentbridge.dto.response.dashboard.DashboardStatisticsDto;
import com.example.talentbridge.dto.response.dashboard.MonthlyStatisticsDto;
import com.example.talentbridge.dto.response.dashboard.MonthlyTrendDto;
import com.example.talentbridge.dto.response.dashboard.OverviewStatisticsDto;
import com.example.talentbridge.model.constant.ResumeStatus;
import com.example.talentbridge.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final SubscriberRepository subscriberRepository;

    public DashboardStatisticsDto getDashboardStatistics() {
        OverviewStatisticsDto overview = calculateOverviewStatistics();
        MonthlyStatisticsDto monthly = calculateMonthlyStatistics();
        List<MonthlyTrendDto> trends = calculateTrends(6);

        return new DashboardStatisticsDto(overview, monthly, trends);
    }

    private OverviewStatisticsDto calculateOverviewStatistics() {
        // Get current month boundaries
        YearMonth currentMonth = YearMonth.now();
        Instant currentMonthStart = currentMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant currentMonthEnd = currentMonth.atEndOfMonth().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();

        // Get previous month boundaries
        YearMonth previousMonth = currentMonth.minusMonths(1);
        Instant previousMonthStart = previousMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant previousMonthEnd = previousMonth.atEndOfMonth().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();

        // Total counts
        long totalUsers = userRepository.count();
        long totalCompanies = companyRepository.count();
        long activeJobs = jobRepository.countByActiveTrue();
        long totalResumes = resumeRepository.count();

        // Current month counts
        long usersThisMonth = userRepository.countByCreatedAtBetween(currentMonthStart, currentMonthEnd);
        long companiesThisMonth = companyRepository.countByCreatedAtBetween(currentMonthStart, currentMonthEnd);
        long jobsThisMonth = jobRepository.countByCreatedAtBetween(currentMonthStart, currentMonthEnd);
        long resumesThisMonth = resumeRepository.countByCreatedAtBetween(currentMonthStart, currentMonthEnd);

        // Previous month counts
        long usersPreviousMonth = userRepository.countByCreatedAtBetween(previousMonthStart, previousMonthEnd);
        long companiesPreviousMonth = companyRepository.countByCreatedAtBetween(previousMonthStart, previousMonthEnd);
        long jobsPreviousMonth = jobRepository.countByCreatedAtBetween(previousMonthStart, previousMonthEnd);
        long resumesPreviousMonth = resumeRepository.countByCreatedAtBetween(previousMonthStart, previousMonthEnd);

        // Calculate growth percentages
        double usersGrowth = calculateGrowthPercentage(usersThisMonth, usersPreviousMonth);
        double companiesGrowth = calculateGrowthPercentage(companiesThisMonth, companiesPreviousMonth);
        double jobsGrowth = calculateGrowthPercentage(jobsThisMonth, jobsPreviousMonth);
        double resumesGrowth = calculateGrowthPercentage(resumesThisMonth, resumesPreviousMonth);

        return new OverviewStatisticsDto(
                totalUsers, usersGrowth,
                totalCompanies, companiesGrowth,
                activeJobs, jobsGrowth,
                totalResumes, resumesGrowth
        );
    }

    private MonthlyStatisticsDto calculateMonthlyStatistics() {
        // Get current month boundaries
        YearMonth currentMonth = YearMonth.now();
        Instant currentMonthStart = currentMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant currentMonthEnd = currentMonth.atEndOfMonth().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();

        // Monthly counts
        long newUsers = userRepository.countByCreatedAtBetween(currentMonthStart, currentMonthEnd);
        long newJobs = jobRepository.countByCreatedAtBetween(currentMonthStart, currentMonthEnd);
        long newResumes = resumeRepository.countByCreatedAtBetween(currentMonthStart, currentMonthEnd);

        // Total counts
        long totalUsers = userRepository.count();
        long totalJobs = jobRepository.count();
        long totalResumes = resumeRepository.count();
        long subscribers = subscriberRepository.count();

        // Calculate resume acceptance rate
        long acceptedResumes = resumeRepository.countByStatus(ResumeStatus.APPROVED);
        double resumeAcceptanceRate = totalResumes > 0 ? (double) acceptedResumes / totalResumes * 100 : 0;

        return new MonthlyStatisticsDto(
                newUsers, totalUsers,
                newJobs, totalJobs,
                newResumes, totalResumes,
                subscribers, resumeAcceptanceRate
        );
    }

    private List<MonthlyTrendDto> calculateTrends(int months) {
        List<MonthlyTrendDto> trends = new ArrayList<>();
        YearMonth currentMonth = YearMonth.now();

        for (int i = months - 1; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);
            Instant monthStart = month.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
            Instant monthEnd = month.atEndOfMonth().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();

            long users = userRepository.countByCreatedAtBetween(monthStart, monthEnd);
            long jobs = jobRepository.countByCreatedAtBetween(monthStart, monthEnd);
            long resumes = resumeRepository.countByCreatedAtBetween(monthStart, monthEnd);

            String monthName = month.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault());

            trends.add(new MonthlyTrendDto(monthName, users, jobs, resumes));
        }

        return trends;
    }

    public double calculateGrowthPercentage(long current, long previous) {
        if (previous == 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        return ((double) (current - previous) / previous) * 100;
    }
}
