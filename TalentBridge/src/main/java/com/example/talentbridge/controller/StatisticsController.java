package com.example.talentbridge.controller;

import com.example.talentbridge.annotation.ApiMessage;
import com.example.talentbridge.dto.response.dashboard.DashboardStatisticsDto;
import com.example.talentbridge.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Statistics")
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    @ApiMessage(value = "Get dashboard statistics")
    @PreAuthorize("hasAuthority('GET /statistics')")
    @Operation(
            summary = "Get dashboard statistics",
            description = "Required permission: <b>GET /statistics</b>"
    )
    public ResponseEntity<DashboardStatisticsDto> getDashboardStatistics() {
        return ResponseEntity.ok(statisticsService.getDashboardStatistics());
    }
}
