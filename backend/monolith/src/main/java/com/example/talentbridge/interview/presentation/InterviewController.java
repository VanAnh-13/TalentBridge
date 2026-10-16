package com.example.talentbridge.interview.presentation;

import com.example.talentbridge.interview.application.*;
import com.example.talentbridge.shared.presentation.ApiMessage;
import com.example.talentbridge.shared.presentation.PageRequests;
import com.example.talentbridge.shared.presentation.PageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Interview")
@RestController
@RequestMapping("/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final ScheduleInterview scheduleInterview;
    private final RespondToInterview respondToInterview;
    private final ChangeInterview changeInterview;
    private final ListInterviews listInterviews;
    private final InterviewCalendar interviewCalendar;

    @PostMapping
    @ApiMessage("Propose an interview")
    @PreAuthorize("hasAuthority('POST /interviews')")
    @Operation(summary = "A recruiter proposes interview time slots", description = "Required permission: <b>POST /interviews</b>")
    public ResponseEntity<InterviewResponse> schedule(@Valid @RequestBody InterviewRequest request) {
        if (request.getResumeId() == null)
            throw new com.example.talentbridge.shared.domain.exception.InvalidRequestException("Application is required");
        return ResponseEntity.status(HttpStatus.CREATED).body(InterviewResponse.from(scheduleInterview.execute(
                request.getResumeId(), request.getMode(), request.getLocation(), request.getNote(),
                request.getDurationMinutes(), request.getSlots())));
    }

    @PutMapping("/{id}/reschedule")
    @ApiMessage("Reschedule an interview")
    @PreAuthorize("hasAuthority('PUT /interviews/{id}/reschedule')")
    @Operation(summary = "A recruiter proposes new slots", description = "Required permission: <b>PUT /interviews/{id}/reschedule</b>")
    public ResponseEntity<InterviewResponse> reschedule(@PathVariable Long id, @Valid @RequestBody InterviewRequest request) {
        return ResponseEntity.ok(InterviewResponse.from(changeInterview.reschedule(id, request.getMode(),
                request.getLocation(), request.getNote(), request.getDurationMinutes(), request.getSlots())));
    }

    @PutMapping("/{id}/cancel")
    @ApiMessage("Cancel an interview")
    @PreAuthorize("hasAuthority('PUT /interviews/{id}/cancel')")
    @Operation(summary = "A recruiter cancels the interview", description = "Required permission: <b>PUT /interviews/{id}/cancel</b>")
    public ResponseEntity<InterviewResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(InterviewResponse.from(changeInterview.cancel(id)));
    }

    @PutMapping("/{id}/confirm")
    @ApiMessage("Confirm an interview slot")
    @PreAuthorize("hasAuthority('PUT /interviews/{id}/confirm')")
    @Operation(summary = "The candidate picks a slot", description = "Required permission: <b>PUT /interviews/{id}/confirm</b>")
    public ResponseEntity<InterviewResponse> confirm(@PathVariable Long id, @Valid @RequestBody ConfirmRequest request) {
        return ResponseEntity.ok(InterviewResponse.from(respondToInterview.confirm(id, request.getSlot())));
    }

    @PutMapping("/{id}/decline")
    @ApiMessage("Decline an interview")
    @PreAuthorize("hasAuthority('PUT /interviews/{id}/decline')")
    @Operation(summary = "The candidate declines", description = "Required permission: <b>PUT /interviews/{id}/decline</b>")
    public ResponseEntity<InterviewResponse> decline(@PathVariable Long id) {
        return ResponseEntity.ok(InterviewResponse.from(respondToInterview.decline(id)));
    }

    @GetMapping("/me")
    @ApiMessage("Get my interviews")
    @PreAuthorize("hasAuthority('GET /interviews/me')")
    @Operation(summary = "Interviews of the signed-in candidate", description = "Required permission: <b>GET /interviews/me</b>")
    public ResponseEntity<PageResponseDto<InterviewResponse>> mine(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(PageResponseDto.from(
                listInterviews.asCandidate(PageRequests.from(pageable)).map(InterviewResponse::from)));
    }

    @GetMapping("/company")
    @ApiMessage("Get my company's interviews")
    @PreAuthorize("hasAuthority('GET /interviews/company')")
    @Operation(summary = "Interviews of the recruiter's company", description = "Required permission: <b>GET /interviews/company</b>")
    public ResponseEntity<PageResponseDto<InterviewResponse>> ofCompany(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(PageResponseDto.from(
                listInterviews.forMyCompany(PageRequests.from(pageable)).map(InterviewResponse::from)));
    }

    @GetMapping("/{id}/calendar.ics")
    @PreAuthorize("hasAuthority('GET /interviews/{id}/calendar.ics')")
    @Operation(summary = "Calendar file of a confirmed interview", description = "Required permission: <b>GET /interviews/{id}/calendar.ics</b>")
    public ResponseEntity<String> calendar(@PathVariable Long id) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/calendar; charset=UTF-8")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"interview-" + id + ".ics\"")
                .body(interviewCalendar.icsOf(id));
    }
}
