package com.example.talentbridge.matching.presentation;

import com.example.talentbridge.matching.application.MatchOfApplication;
import com.example.talentbridge.matching.application.MatchingStatus;
import com.example.talentbridge.matching.application.RankCandidates;
import com.example.talentbridge.matching.application.RecommendJobsFor;
import com.example.talentbridge.matching.presentation.MatchResponses.*;
import com.example.talentbridge.shared.presentation.ApiMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Matching")
@RestController
@RequestMapping("/matching")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchOfApplication matchOfApplication;
    private final RankCandidates rankCandidates;
    private final RecommendJobsFor recommendJobsFor;
    private final MatchingStatus status;

    @GetMapping("/status")
    @ApiMessage("Get the AI matching status")
    @PreAuthorize("hasAuthority('GET /matching/status')")
    @Operation(summary = "Whether AI scoring is switched on", description = "Required permission: <b>GET /matching/status</b>")
    public ResponseEntity<StatusResponse> status() {
        return ResponseEntity.ok(StatusResponse.from(status.execute()));
    }

    @GetMapping("/resumes/{resumeId}")
    @ApiMessage("Get the AI assessment of an application")
    @PreAuthorize("hasAuthority('GET /matching/resumes/{resumeId}')")
    @Operation(summary = "AI assessment of an application", description = "Required permission: <b>GET /matching/resumes/{resumeId}</b>")
    public ResponseEntity<MatchLookupResponse> ofResume(@PathVariable Long resumeId) {
        MatchResponse match = matchOfApplication.find(resumeId).map(MatchResponse::from).orElse(null);
        return ResponseEntity.ok(new MatchLookupResponse(match != null, match));
    }

    @PostMapping("/resumes/{resumeId}/recompute")
    @ApiMessage("Ask for a new AI assessment")
    @PreAuthorize("hasAuthority('POST /matching/resumes/{resumeId}/recompute')")
    @Operation(summary = "Queue a new AI assessment of an application", description = "Required permission: <b>POST /matching/resumes/{resumeId}/recompute</b>")
    public ResponseEntity<Map<String, Boolean>> recompute(@PathVariable Long resumeId) {
        matchOfApplication.recompute(resumeId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("queued", true));
    }

    @GetMapping("/jobs/{jobId}/candidates")
    @ApiMessage("Rank the applicants of a job")
    @PreAuthorize("hasAuthority('GET /matching/jobs/{jobId}/candidates')")
    @Operation(summary = "Applicants of a job, best AI match first", description = "Required permission: <b>GET /matching/jobs/{jobId}/candidates</b>")
    public ResponseEntity<List<RankedCandidateResponse>> candidates(@PathVariable Long jobId) {
        return ResponseEntity.ok(rankCandidates.execute(jobId).stream().map(RankedCandidateResponse::from).toList());
    }

    @GetMapping("/me/recommendations")
    @ApiMessage("Get recommended jobs")
    @PreAuthorize("hasAuthority('GET /matching/me/recommendations')")
    @Operation(summary = "Jobs that fit the skills I follow", description = "Required permission: <b>GET /matching/me/recommendations</b>")
    public ResponseEntity<List<RecommendationResponse>> recommendations(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendJobsFor.execute(Math.min(Math.max(limit, 1), 30)).stream()
                .map(RecommendationResponse::from).toList());
    }
}
