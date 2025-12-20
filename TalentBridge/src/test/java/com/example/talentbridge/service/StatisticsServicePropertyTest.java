package com.example.talentbridge.service;

import net.jqwik.api.*;
import net.jqwik.api.constraints.LongRange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Property-based tests for StatisticsService
 * Feature: admin-dashboard
 */
class StatisticsServicePropertyTest {

    private final StatisticsService statisticsService;

    StatisticsServicePropertyTest() {
        // Create a minimal instance for testing the calculation method
        // The calculateGrowthPercentage method is public and doesn't depend on repositories
        this.statisticsService = new StatisticsService(null, null, null, null, null);
    }

    /**
     * Feature: admin-dashboard, Property 3: Growth Percentage Calculation
     * For any current month count (C) and previous month count (P), 
     * the growth percentage SHALL equal ((C - P) / P) * 100 when P > 0.
     * Validates: Requirements 1.5, 5.4
     */
    @Property(tries = 100)
    void growthPercentageShouldBeCorrectWhenPreviousIsPositive(
            @ForAll @LongRange(min = 0, max = 1_000_000) long current,
            @ForAll @LongRange(min = 1, max = 1_000_000) long previous
    ) {
        double result = statisticsService.calculateGrowthPercentage(current, previous);
        double expected = ((double) (current - previous) / previous) * 100;
        
        assertThat(result)
                .as("Growth percentage for current=%d, previous=%d should be ((C-P)/P)*100", current, previous)
                .isCloseTo(expected, within(0.0001));
    }

    /**
     * Feature: admin-dashboard, Property 3: Growth Percentage Calculation
     * When previous month count is 0 and current is positive, growth should be 100%.
     * Validates: Requirements 1.5, 5.4
     */
    @Property(tries = 100)
    void growthPercentageShouldBe100WhenPreviousIsZeroAndCurrentIsPositive(
            @ForAll @LongRange(min = 1, max = 1_000_000) long current
    ) {
        double result = statisticsService.calculateGrowthPercentage(current, 0);
        
        assertThat(result)
                .as("Growth percentage should be 100%% when previous=0 and current=%d", current)
                .isEqualTo(100.0);
    }

    /**
     * Feature: admin-dashboard, Property 3: Growth Percentage Calculation
     * When both current and previous are 0, growth should be 0%.
     * Validates: Requirements 1.5, 5.4
     */
    @Property(tries = 1)
    void growthPercentageShouldBeZeroWhenBothAreZero() {
        double result = statisticsService.calculateGrowthPercentage(0, 0);
        
        assertThat(result)
                .as("Growth percentage should be 0%% when both current and previous are 0")
                .isEqualTo(0.0);
    }

    /**
     * Feature: admin-dashboard, Property 3: Growth Percentage Calculation
     * When current equals previous (and both positive), growth should be 0%.
     * Validates: Requirements 1.5, 5.4
     */
    @Property(tries = 100)
    void growthPercentageShouldBeZeroWhenCurrentEqualsPrevious(
            @ForAll @LongRange(min = 1, max = 1_000_000) long value
    ) {
        double result = statisticsService.calculateGrowthPercentage(value, value);
        
        assertThat(result)
                .as("Growth percentage should be 0%% when current equals previous (%d)", value)
                .isCloseTo(0.0, within(0.0001));
    }

    /**
     * Feature: admin-dashboard, Property 3: Growth Percentage Calculation
     * When current is double the previous, growth should be 100%.
     * Validates: Requirements 1.5, 5.4
     */
    @Property(tries = 100)
    void growthPercentageShouldBe100WhenCurrentIsDoublePrevious(
            @ForAll @LongRange(min = 1, max = 500_000) long previous
    ) {
        long current = previous * 2;
        double result = statisticsService.calculateGrowthPercentage(current, previous);
        
        assertThat(result)
                .as("Growth percentage should be 100%% when current is double previous")
                .isCloseTo(100.0, within(0.0001));
    }

    /**
     * Feature: admin-dashboard, Property 3: Growth Percentage Calculation
     * When current is half the previous, growth should be -50%.
     * Validates: Requirements 1.5, 5.4
     */
    @Property(tries = 100)
    void growthPercentageShouldBeNegative50WhenCurrentIsHalfPrevious(
            @ForAll @LongRange(min = 2, max = 1_000_000) long previous
    ) {
        // Ensure previous is even for exact half calculation
        long adjustedPrevious = previous - (previous % 2);
        if (adjustedPrevious < 2) adjustedPrevious = 2;
        
        long current = adjustedPrevious / 2;
        double result = statisticsService.calculateGrowthPercentage(current, adjustedPrevious);
        
        assertThat(result)
                .as("Growth percentage should be -50%% when current is half previous")
                .isCloseTo(-50.0, within(0.0001));
    }

    /**
     * Feature: admin-dashboard, Property 3: Growth Percentage Calculation
     * Growth percentage should be negative when current < previous.
     * Validates: Requirements 1.5, 5.4
     */
    @Property(tries = 100)
    void growthPercentageShouldBeNegativeWhenCurrentLessThanPrevious(
            @ForAll @LongRange(min = 0, max = 999_999) long current,
            @ForAll @LongRange(min = 1, max = 1_000_000) long previous
    ) {
        Assume.that(current < previous);
        
        double result = statisticsService.calculateGrowthPercentage(current, previous);
        
        assertThat(result)
                .as("Growth percentage should be negative when current (%d) < previous (%d)", current, previous)
                .isNegative();
    }

    /**
     * Feature: admin-dashboard, Property 3: Growth Percentage Calculation
     * Growth percentage should be positive when current > previous.
     * Validates: Requirements 1.5, 5.4
     */
    @Property(tries = 100)
    void growthPercentageShouldBePositiveWhenCurrentGreaterThanPrevious(
            @ForAll @LongRange(min = 1, max = 1_000_000) long current,
            @ForAll @LongRange(min = 1, max = 999_999) long previous
    ) {
        Assume.that(current > previous);
        
        double result = statisticsService.calculateGrowthPercentage(current, previous);
        
        assertThat(result)
                .as("Growth percentage should be positive when current (%d) > previous (%d)", current, previous)
                .isPositive();
    }
}
