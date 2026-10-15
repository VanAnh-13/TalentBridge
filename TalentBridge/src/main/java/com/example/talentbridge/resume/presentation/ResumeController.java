package com.example.talentbridge.resume.presentation;

import com.example.talentbridge.resume.application.ChangeResumeStatus;
import com.example.talentbridge.resume.application.GetResumeFileLink;
import com.example.talentbridge.resume.application.ReplaceResumeFile;
import com.example.talentbridge.resume.application.SearchResumes;
import com.example.talentbridge.resume.application.SubmitResume;
import com.example.talentbridge.resume.application.WithdrawResume;
import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.presentation.ApiMessage;
import com.example.talentbridge.shared.presentation.FileUploads;
import com.example.talentbridge.shared.presentation.PageRequests;
import com.example.talentbridge.shared.presentation.PageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Resume")
@RestController
@RequestMapping("/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final SubmitResume submitResume;
    private final SearchResumes searchResumes;
    private final WithdrawResume withdrawResume;
    private final ReplaceResumeFile replaceResumeFile;
    private final GetResumeFileLink getResumeFileLink;
    private final ChangeResumeStatus changeResumeStatus;

    @PostMapping
    @ApiMessage(value = "Create Resume")
    @PreAuthorize("hasAuthority('POST /resumes')")
    @Operation(
            summary = "Create Resume",
            description = "Required permission: <b>POST /resumes</b>"
    )
    public ResponseEntity<ResumeCreatedResponse> saveResume(
            @Valid @RequestPart("resume") ResumeRequest request,
            @RequestPart(value = "pdfFile") MultipartFile pdfFile
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ResumeCreatedResponse.from(
                submitResume.execute(request.getEmail(), request.getUser().getId(), request.getJob().getId(),
                        FileUploads.from(pdfFile)).resume()));
    }

    @GetMapping
    @ApiMessage(value = "Get resume list")
    @PreAuthorize("hasAuthority('GET /resumes')")
    @Operation(
            summary = "Get resume list",
            description = "Required permission: <b>GET /resumes</b>"
    )
    public ResponseEntity<PageResponseDto<ResumeDisplayResponse>> findAllResumes(
            @RequestParam(value = "filter", required = false) String filter,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return ResponseEntity.ok(PageResponseDto.from(
                searchResumes.execute(FilterExpression.of(filter), PageRequests.from(pageable))
                        .map(ResumeDisplayResponse::from)));
    }

    @GetMapping("/company")
    @ApiMessage(value = "Get resume list for current user's company")
    @PreAuthorize("hasAuthority('GET /resumes/company')")
    @Operation(
            summary = "Get resume list for current user's company",
            description = "Required permission: <b>GET /resumes/company</b>"
    )
    public ResponseEntity<PageResponseDto<ResumeDisplayResponse>> findAllResumesForRecruiterCompany(
            @RequestParam(value = "filter", required = false) String filter,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return ResponseEntity.ok(PageResponseDto.from(
                searchResumes.executeForCurrentUserCompany(FilterExpression.of(filter), PageRequests.from(pageable))
                        .map(ResumeDisplayResponse::from)));
    }

    @GetMapping("/me")
    @ApiMessage(value = "Get resume for user")
    @PreAuthorize("hasAuthority('GET /resumes/me')")
    @Operation(
            summary = "Get resume for current user",
            description = "Required permission: <b>GET /resumes/me</b>"
    )
    public ResponseEntity<PageResponseDto<ResumeDisplayResponse>> findSelfResumes(
            @RequestParam(value = "filter", required = false) String filter,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return ResponseEntity.ok(PageResponseDto.from(
                searchResumes.executeForCurrentUser(FilterExpression.of(filter), PageRequests.from(pageable))
                        .map(ResumeDisplayResponse::from)));
    }

    @DeleteMapping("/me/jobs/{jobId}")
    @ApiMessage(value = "Delete resume by job id for current user")
    @PreAuthorize("hasAuthority('DELETE /resumes/me/jobs/{jobId}')")
    @Operation(
            summary = "Delete resume by job id for current user",
            description = "Required permission: <b>DELETE /resumes/me/jobs/{jobId}</b>"
    )
    public ResponseEntity<ResumeSummaryResponse> removeSelfResumeByJobId(@PathVariable Long jobId) {
        return ResponseEntity.ok(ResumeSummaryResponse.from(withdrawResume.execute(jobId)));
    }

    @PutMapping("/me/file/{id}")
    @ApiMessage(value = "Update resume file")
    @PreAuthorize("hasAuthority('PUT /resumes/me/file/{id}')")
    @Operation(
            summary = "Update resume file",
            description = "Required permission: <b>PUT /resumes/me/file/{id}</b>"
    )
    public ResponseEntity<ResumeSummaryResponse> updateSelfResumeFile(
            @PathVariable Long id,
            @RequestPart("pdfFile") MultipartFile pdfFile
    ) {
        return ResponseEntity.ok(ResumeSummaryResponse.from(
                replaceResumeFile.execute(id, FileUploads.from(pdfFile))));
    }

    @GetMapping("/file/{id}")
    @ApiMessage(value = "Get resume file")
    @PreAuthorize("hasAuthority('GET /resumes/file/{id}')")
    @Operation(
            summary = "Get resume file",
            description = "Required permission: <b>GET /resumes/file/{id}</b>"
    )
    public ResponseEntity<ResumeFileResponse> getResumeFileUrl(@PathVariable Long id) {
        return ResponseEntity.ok(new ResumeFileResponse(getResumeFileLink.execute(id)));
    }

    @PutMapping("/status")
    @ApiMessage("Update resume status")
    @PreAuthorize("hasAuthority('PUT /resumes/status')")
    @Operation(
            summary = "Update resume status",
            description = "Required permission: <b>PUT /resumes/status</b>"
    )
    public ResponseEntity<ResumeSummaryResponse> updateResumeStatus(@Valid @RequestBody UpdateResumeStatusRequest request) {
        return ResponseEntity.ok(ResumeSummaryResponse.from(
                changeResumeStatus.execute(request.getId(), request.getStatus())));
    }

    @PutMapping("/company/status")
    @ApiMessage("Update resume status for current user's company")
    @PreAuthorize("hasAuthority('PUT /resumes/company/status')")
    @Operation(
            summary = "Update resume status for current user's company",
            description = "Required permission: <b>PUT /resumes/company/status</b>"
    )
    public ResponseEntity<ResumeSummaryResponse> updateResumeStatusForRecruiterCompany(
            @Valid @RequestBody UpdateResumeStatusRequest request
    ) {
        return ResponseEntity.ok(ResumeSummaryResponse.from(
                changeResumeStatus.executeForCurrentUserCompany(request.getId(), request.getStatus())));
    }
}
