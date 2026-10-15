package com.example.talentbridge.controller;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.talentbridge.shared.presentation.ApiMessage;
import com.example.talentbridge.dto.request.resume.ResumeRequestDto;
import com.example.talentbridge.dto.request.resume.UpdateResumeStatusRequestDto;
import com.example.talentbridge.shared.presentation.PageResponseDto;
import com.example.talentbridge.dto.response.resume.ResumeForDisplayResponseDto;
import com.example.talentbridge.model.Resume;
import com.example.talentbridge.service.ResumeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    private final ResumeService resumeService;

    @PostMapping
    @ApiMessage(value = "Create Resume")
    @PreAuthorize("hasAuthority('POST /resumes')")
    @Operation(
            summary = "Create Resume",
            description = "Required permission: <b>POST /resumes</b>"
    )
    public ResponseEntity<?> saveResume(
            @Valid @RequestPart("resume") ResumeRequestDto resumeRequestDto,
            @RequestPart(value = "pdfFile") MultipartFile pdfFile
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resumeService.saveResume(resumeRequestDto, pdfFile));
    }

    @GetMapping
    @ApiMessage(value = "Get resume list")
    @PreAuthorize("hasAuthority('GET /resumes')")
    @Operation(
            summary = "Get resume list",
            description = "Required permission: <b>GET /resumes</b>"
    )
    public ResponseEntity<?> findAllResumes(
            @Filter Specification<Resume> spec,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        Page<ResumeForDisplayResponseDto> page = resumeService.findAllResumes(spec, pageable);

        PageResponseDto<ResumeForDisplayResponseDto> res = new PageResponseDto<>(
                page.getContent(),
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return ResponseEntity.ok(res);
    }

    @GetMapping("/company")
    @ApiMessage(value = "Get resume list for current user's company")
    @PreAuthorize("hasAuthority('GET /resumes/company')")
    @Operation(
            summary = "Get resume list for current user's company",
            description = "Required permission: <b>GET /resumes/company</b>"
    )
    public ResponseEntity<?> findAllResumesForRecruiterCompany(
            @Filter Specification<Resume> spec,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        Page<ResumeForDisplayResponseDto> page = resumeService.findAllResumesForRecruiterCompany(spec, pageable);

        PageResponseDto<ResumeForDisplayResponseDto> res = new PageResponseDto<>(
                page.getContent(),
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return ResponseEntity.ok(res);
    }

    @GetMapping("/me")
    @ApiMessage(value = "Get resume for user")
    @PreAuthorize("hasAuthority('GET /resumes/me')")
    @Operation(
            summary = "Get resume for current user",
            description = "Required permission: <b>GET /resumes/me</b>"
    )
    public ResponseEntity<?> findSelfResumes(
            @Filter Specification<Resume> spec,
            @PageableDefault(size = 5) Pageable pageable) {
        Page<ResumeForDisplayResponseDto> page = resumeService.findSelfResumes(spec, pageable);

        PageResponseDto<ResumeForDisplayResponseDto> res = new PageResponseDto<>(
                page.getContent(),
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/me/jobs/{jobId}")
    @ApiMessage(value = "Delete resume by job id for current user")
    @PreAuthorize("hasAuthority('DELETE /resumes/me/jobs/{jobId}')")
    @Operation(
            summary = "Delete resume by job id for current user",
            description = "Required permission: <b>DELETE /resumes/me/jobs/{jobId}</b>"
    )
    public ResponseEntity<?> removeSelfResumeByJobId(
            @PathVariable Long jobId
    ) {
        return ResponseEntity.ok(resumeService.removeSelfResumeByJobId(jobId));
    }

    @PutMapping("/me/file/{id}")
    @ApiMessage(value = "Update resume file")
    @PreAuthorize("hasAuthority('PUT /resumes/me/file/{id}')")
    @Operation(
            summary = "Update resume file",
            description = "Required permission: <b>PUT /resumes/me/file/{id}</b>"
    )
    public ResponseEntity<?> updateSelfResumeFile(
            @PathVariable Long id,
            @RequestPart("pdfFile") MultipartFile pdfFile) {
        return ResponseEntity.ok(resumeService.updateSelfResumeFile(id, pdfFile));
    }

    @GetMapping("/file/{id}")
    @ApiMessage(value = "Get resume file")
    @PreAuthorize("hasAuthority('GET /resumes/file/{id}')")
    @Operation(
            summary = "Get resume file",
            description = "Required permission: <b>GET /resumes/file/{id}</b>"
    )
    public ResponseEntity<?> getResumeFileUrl(@PathVariable Long id) {
        return ResponseEntity.ok(resumeService.getResumeFileUrl(id));
    }

    @PutMapping("/status")
    @ApiMessage("Update resume status")
    @PreAuthorize("hasAuthority('PUT /resumes/status')")
    @Operation(
            summary = "Update resume status",
            description = "Required permission: <b>PUT /resumes/status</b>"
    )
    public ResponseEntity<?> updateResumeStatus(
            @RequestBody UpdateResumeStatusRequestDto updateResumeStatusRequestDto) {
        return ResponseEntity.ok(resumeService.updateResumeStatus(updateResumeStatusRequestDto));
    }

    @PutMapping("/company/status")
    @ApiMessage("Update resume status for current user's company")
    @PreAuthorize("hasAuthority('PUT /resumes/company/status')")
    @Operation(
            summary = "Update resume status for current user's company",
            description = "Required permission: <b>PUT /resumes/company/status</b>"
    )
    public ResponseEntity<?> updateResumeStatusForRecruiterCompany(
            @RequestBody UpdateResumeStatusRequestDto updateResumeStatusRequestDto) {
        return ResponseEntity.ok(resumeService.updateResumeStatusForRecruiterCompany(updateResumeStatusRequestDto));
    }


}
