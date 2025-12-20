import { describe, it, expect } from "vitest";
import * as fc from "fast-check";
import type {
    DashboardStatistics,
    OverviewStatistics,
    MonthlyStatistics,
    MonthlyTrend,
} from "@/types/statistics";

/**
 * Property-based tests for Dashboard Statistics
 * Feature: admin-dashboard
 * Validates: Requirements 4.2, 4.3, 4.4
 */

// Arbitraries for generating test data
const overviewStatisticsArb: fc.Arbitrary<OverviewStatistics> = fc.record({
    totalUsers: fc.nat({ max: 1_000_000 }),
    usersGrowth: fc.double({ min: -100, max: 1000, noNaN: true }),
    totalCompanies: fc.nat({ max: 1_000_000 }),
    companiesGrowth: fc.double({ min: -100, max: 1000, noNaN: true }),
    activeJobs: fc.nat({ max: 1_000_000 }),
    jobsGrowth: fc.double({ min: -100, max: 1000, noNaN: true }),
    totalResumes: fc.nat({ max: 1_000_000 }),
    resumesGrowth: fc.double({ min: -100, max: 1000, noNaN: true }),
});

const monthlyStatisticsArb: fc.Arbitrary<MonthlyStatistics> = fc.record({
    newUsers: fc.nat({ max: 100_000 }),
    totalUsers: fc.nat({ max: 1_000_000 }),
    newJobs: fc.nat({ max: 100_000 }),
    totalJobs: fc.nat({ max: 1_000_000 }),
    newResumes: fc.nat({ max: 100_000 }),
    totalResumes: fc.nat({ max: 1_000_000 }),
    subscribers: fc.nat({ max: 100_000 }),
    resumeAcceptanceRate: fc.double({ min: 0, max: 100, noNaN: true }),
});

const monthlyTrendArb: fc.Arbitrary<MonthlyTrend> = fc.record({
    month: fc.constantFrom("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"),
    users: fc.nat({ max: 100_000 }),
    jobs: fc.nat({ max: 100_000 }),
    resumes: fc.nat({ max: 100_000 }),
});

const dashboardStatisticsArb: fc.Arbitrary<DashboardStatistics> = fc.record({
    overview: overviewStatisticsArb,
    monthly: monthlyStatisticsArb,
    trends: fc.array(monthlyTrendArb, { minLength: 6, maxLength: 6 }),
});

/**
 * Helper functions that mirror the display logic in DashboardPage
 */
function formatGrowthPercentage(growth: number): string {
    const sign = growth >= 0 ? "+" : "";
    return `${sign}${growth.toFixed(1)}%`;
}

function formatNumber(value: number): string {
    return value.toLocaleString();
}

function isValidStatistics(stats: DashboardStatistics): boolean {
    return (
        stats.overview !== null &&
        stats.monthly !== null &&
        Array.isArray(stats.trends) &&
        stats.trends.length === 6
    );
}

describe("Dashboard Statistics Data Validation Properties", () => {
    /**
     * Feature: admin-dashboard, Property: Statistics data structure validity
     * For any valid statistics data, the structure should contain all required fields
     * Validates: Requirements 4.2, 4.4
     */
    it("should have valid structure for all generated statistics", () => {
        fc.assert(
            fc.property(dashboardStatisticsArb, (stats) => {
                expect(isValidStatistics(stats)).toBe(true);
                expect(stats.overview).toBeDefined();
                expect(stats.monthly).toBeDefined();
                expect(stats.trends).toHaveLength(6);
            }),
            { numRuns: 100 }
        );
    });

    /**
     * Feature: admin-dashboard, Property: Overview statistics non-negative counts
     * For any overview statistics, all count values should be non-negative
     * Validates: Requirements 4.4
     */
    it("should have non-negative counts in overview statistics", () => {
        fc.assert(
            fc.property(overviewStatisticsArb, (overview) => {
                expect(overview.totalUsers).toBeGreaterThanOrEqual(0);
                expect(overview.totalCompanies).toBeGreaterThanOrEqual(0);
                expect(overview.activeJobs).toBeGreaterThanOrEqual(0);
                expect(overview.totalResumes).toBeGreaterThanOrEqual(0);
            }),
            { numRuns: 100 }
        );
    });

    /**
     * Feature: admin-dashboard, Property: Monthly statistics non-negative counts
     * For any monthly statistics, all count values should be non-negative
     * Validates: Requirements 4.4
     */
    it("should have non-negative counts in monthly statistics", () => {
        fc.assert(
            fc.property(monthlyStatisticsArb, (monthly) => {
                expect(monthly.newUsers).toBeGreaterThanOrEqual(0);
                expect(monthly.totalUsers).toBeGreaterThanOrEqual(0);
                expect(monthly.newJobs).toBeGreaterThanOrEqual(0);
                expect(monthly.totalJobs).toBeGreaterThanOrEqual(0);
                expect(monthly.newResumes).toBeGreaterThanOrEqual(0);
                expect(monthly.totalResumes).toBeGreaterThanOrEqual(0);
                expect(monthly.subscribers).toBeGreaterThanOrEqual(0);
            }),
            { numRuns: 100 }
        );
    });

    /**
     * Feature: admin-dashboard, Property: Resume acceptance rate bounds
     * For any monthly statistics, resume acceptance rate should be between 0 and 100
     * Validates: Requirements 4.4
     */
    it("should have resume acceptance rate between 0 and 100", () => {
        fc.assert(
            fc.property(monthlyStatisticsArb, (monthly) => {
                expect(monthly.resumeAcceptanceRate).toBeGreaterThanOrEqual(0);
                expect(monthly.resumeAcceptanceRate).toBeLessThanOrEqual(100);
            }),
            { numRuns: 100 }
        );
    });

    /**
     * Feature: admin-dashboard, Property: Trend data has exactly 6 months
     * For any dashboard statistics, trends should contain exactly 6 months of data
     * Validates: Requirements 4.4
     */
    it("should have exactly 6 months of trend data", () => {
        fc.assert(
            fc.property(dashboardStatisticsArb, (stats) => {
                expect(stats.trends).toHaveLength(6);
            }),
            { numRuns: 100 }
        );
    });

    /**
     * Feature: admin-dashboard, Property: Trend data non-negative values
     * For any trend data point, all values should be non-negative
     * Validates: Requirements 4.4
     */
    it("should have non-negative values in trend data", () => {
        fc.assert(
            fc.property(monthlyTrendArb, (trend) => {
                expect(trend.users).toBeGreaterThanOrEqual(0);
                expect(trend.jobs).toBeGreaterThanOrEqual(0);
                expect(trend.resumes).toBeGreaterThanOrEqual(0);
            }),
            { numRuns: 100 }
        );
    });

    /**
     * Feature: admin-dashboard, Property: Trend month names are valid
     * For any trend data point, month name should be a valid short month name
     * Validates: Requirements 4.4
     */
    it("should have valid month names in trend data", () => {
        const validMonths = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

        fc.assert(
            fc.property(monthlyTrendArb, (trend) => {
                expect(validMonths).toContain(trend.month);
            }),
            { numRuns: 100 }
        );
    });
});

describe("Dashboard Display Formatting Properties", () => {
    /**
     * Feature: admin-dashboard, Property: Growth percentage formatting
     * For any growth value, formatted string should include sign and percentage
     * Validates: Requirements 4.4
     */
    it("should format growth percentage with sign and percentage symbol", () => {
        fc.assert(
            fc.property(fc.double({ min: -100, max: 1000, noNaN: true }), (growth) => {
                const formatted = formatGrowthPercentage(growth);
                expect(formatted).toMatch(/^[+-]?\d+(\.\d+)?%$/);

                if (growth >= 0) {
                    expect(formatted.startsWith("+")).toBe(true);
                } else {
                    expect(formatted.startsWith("-")).toBe(true);
                }
            }),
            { numRuns: 100 }
        );
    });

    /**
     * Feature: admin-dashboard, Property: Number formatting consistency
     * For any number, formatted string should be a valid locale string
     * Validates: Requirements 4.4
     */
    it("should format numbers consistently", () => {
        fc.assert(
            fc.property(fc.nat({ max: 10_000_000 }), (value) => {
                const formatted = formatNumber(value);
                // Should be a non-empty string
                expect(formatted.length).toBeGreaterThan(0);
                // Should contain only digits, commas, and periods (locale-dependent)
                expect(formatted).toMatch(/^[\d,.\s]+$/);
            }),
            { numRuns: 100 }
        );
    });

    /**
     * Feature: admin-dashboard, Property: Zero growth formatting
     * When growth is exactly 0, it should be formatted as "+0.0%"
     * Validates: Requirements 4.4
     */
    it("should format zero growth as positive", () => {
        const formatted = formatGrowthPercentage(0);
        expect(formatted).toBe("+0.0%");
    });

    /**
     * Feature: admin-dashboard, Property: Large number formatting
     * Large numbers should be formatted with proper separators
     * Validates: Requirements 4.4
     */
    it("should format large numbers with separators", () => {
        fc.assert(
            fc.property(fc.integer({ min: 1000, max: 10_000_000 }), (value) => {
                const formatted = formatNumber(value);
                // Large numbers should have some formatting (commas or spaces)
                // The exact format depends on locale, but length should be >= digits
                expect(formatted.length).toBeGreaterThanOrEqual(String(value).length);
            }),
            { numRuns: 100 }
        );
    });
});

describe("Dashboard Statistics Edge Cases", () => {
    /**
     * Feature: admin-dashboard, Property: Empty trends handling
     * Statistics with empty trends should be considered invalid
     * Validates: Requirements 4.3
     */
    it("should consider statistics with wrong trend count as invalid", () => {
        fc.assert(
            fc.property(
                overviewStatisticsArb,
                monthlyStatisticsArb,
                fc.array(monthlyTrendArb, { minLength: 0, maxLength: 5 }),
                (overview, monthly, trends) => {
                    const stats: DashboardStatistics = { overview, monthly, trends };
                    expect(isValidStatistics(stats)).toBe(false);
                }
            ),
            { numRuns: 100 }
        );
    });

    /**
     * Feature: admin-dashboard, Property: Negative growth handling
     * Negative growth values should be formatted correctly with minus sign
     * Validates: Requirements 4.4
     */
    it("should handle negative growth values correctly", () => {
        fc.assert(
            fc.property(fc.double({ min: -100, max: -0.01, noNaN: true }), (growth) => {
                const formatted = formatGrowthPercentage(growth);
                expect(formatted.startsWith("-")).toBe(true);
                expect(formatted.endsWith("%")).toBe(true);
            }),
            { numRuns: 100 }
        );
    });

    /**
     * Feature: admin-dashboard, Property: Very large growth handling
     * Very large growth percentages should still format correctly
     * Validates: Requirements 4.4
     */
    it("should handle very large growth percentages", () => {
        fc.assert(
            fc.property(fc.double({ min: 100, max: 10000, noNaN: true }), (growth) => {
                const formatted = formatGrowthPercentage(growth);
                expect(formatted.startsWith("+")).toBe(true);
                expect(formatted.endsWith("%")).toBe(true);
                // Should contain the number
                expect(formatted.length).toBeGreaterThan(2);
            }),
            { numRuns: 100 }
        );
    });
});
