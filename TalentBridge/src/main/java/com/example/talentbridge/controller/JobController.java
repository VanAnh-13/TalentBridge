package com.example.talentbridge.controller;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.talentbridge.shared.presentation.ApiMessage;
import com.example.talentbridge.dto.request.job.JobRequestDto;
import com.example.talentbridge.shared.presentation.PageResponseDto;
import com.example.talentbridge.dto.response.job.JobResponseDto;
import com.example.talentbridge.model.Job;
import com.example.talentbridge.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Job")
@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    @ApiMessage(value = "Create Job")
    @PreAuthorize("hasAuthority('POST /jobs')")
    @Operation(
            summary = "Create Job",
            description = "Required permission: <b>POST /jobs</b>"
    )
    public ResponseEntity<?> saveJob(@Valid @RequestBody JobRequestDto jobRequestDto) {
        return ResponseEntity.ok(jobService.saveJob(jobRequestDto, false));
    }

    @PostMapping("/company")
    @ApiMessage(value = "Create Job for current user's company")
    @PreAuthorize("hasAuthority('POST /jobs/company')")
    @Operation(
            summary = "Create Job for current user's company",
            description = "Required permission: <b>POST /jobs/company</b>"
    )
    public ResponseEntity<?> saveJobForRecruiterPage(@Valid @RequestBody JobRequestDto jobRequestDto) {
        return ResponseEntity.ok(jobService.saveJob(jobRequestDto, true));
    }

    @GetMapping
    @ApiMessage(value = "Get Job list")
    @PreAuthorize("hasAuthority('GET /jobs') OR isAnonymous()")
    @Operation(
            summary = "Get Job list",
            description = "Required permission: <b>GET /jobs</b>"
    )
    @SecurityRequirements()
    public ResponseEntity<?> findAllJobs(
            @Filter Specification<Job> spec,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        Page<JobResponseDto> page = jobService.findAllJobs(spec, pageable);

        PageResponseDto<JobResponseDto> res = new PageResponseDto<>(
                page.getContent(),
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    @ApiMessage(value = "Get Job by id")
    @PreAuthorize("hasAuthority('GET /jobs/{id}') OR isAnonymous()")
    @Operation(
            summary = "Get Job by id",
            description = "Required permission: <b>GET /jobs/{id}</b>"
    )
    @SecurityRequirements()
    public ResponseEntity<?> findJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.findJobById(id));
    }

    @GetMapping("/companies/{id}")
    @ApiMessage(value = "Get Job by Company")
    @PreAuthorize("hasAuthority('GET /jobs/companies/{id}') OR isAnonymous()")
    @Operation(
            summary = "Get Job by Company",
            description = "Required permission: <b>GET /jobs/companies/{id}</b>"
    )
    @SecurityRequirements()
    public ResponseEntity<?> findJobByCompanyId(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.findJobByCompanyId(id));
    }

    @GetMapping("/company")
    @ApiMessage(value = "Get Job list for current user's company")
    @PreAuthorize("hasAuthority('GET /jobs/company')")
    @Operation(
            summary = "Get Job list for current user's company",
            description = "Required permission: <b>GET /jobs/company</b>"
    )
    public ResponseEntity<?> findAllJobsForRecruiterCompany(
            @Filter Specification<Job> spec,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        Page<JobResponseDto> page = jobService.findAllJobsForRecruiterCompany(spec, pageable);

        PageResponseDto<JobResponseDto> res = new PageResponseDto<>(
                page.getContent(),
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return ResponseEntity.ok(res);
    }

    @PutMapping("/{id}")
    @ApiMessage(value = "Update Job by id")
    @PreAuthorize("hasAuthority('PUT /jobs/{id}')")
    @Operation(
            summary = "Update Job by id",
            description = "Required permission: <b>PUT /jobs/{id}</b>"
    )
    public ResponseEntity<?> updateJobById(
            @PathVariable Long id,
            @Valid @RequestBody JobRequestDto jobRequestDto
    ) {
        return ResponseEntity.ok(jobService.updateJobById(id, jobRequestDto, false));
    }

    @PutMapping("/company/{id}")
    @ApiMessage(value = "Update Job by id for current user's company")
    @PreAuthorize("hasAuthority('PUT /jobs/company/{id}')")
    @Operation(
            summary = "Update Job by id for current user's company",
            description = "Required permission: <b>PUT /jobs/company/{id}</b>"
    )
    public ResponseEntity<?> updateJobByIdForRecruiterCompany(
            @PathVariable Long id,
            @Valid @RequestBody JobRequestDto jobRequestDto
    ) {
        return ResponseEntity.ok(jobService.updateJobById(id, jobRequestDto, true));
    }


    @DeleteMapping("/{id}")
    @ApiMessage(value = "Delete Job by id")
    @PreAuthorize("hasAuthority('DELETE /jobs/{id}')")
    @Operation(
            summary = "Delete Job by id",
            description = "Required permission: <b>DELETE jobs/{id}</b>"
    )
    public ResponseEntity<?> deleteJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.deleteJobById(id));
    }

    @DeleteMapping("/company/{id}")
    @ApiMessage(value = "Delete Job by id for current user's company")
    @PreAuthorize("hasAuthority('DELETE /jobs/company/{id}')")
    @Operation(
            summary = "Delete Job by id for current user's company",
            description = "Required permission: <b>DELETE jobs/company/{id}</b>"
    )
    public ResponseEntity<?> deleteJobByIdForRecruiterCompany(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.deleteJobByIdForRecruiterCompany(id));
    }


}
