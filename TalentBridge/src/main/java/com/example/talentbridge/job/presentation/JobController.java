package com.example.talentbridge.job.presentation;

import com.example.talentbridge.job.application.CreateJob;
import com.example.talentbridge.job.application.DeleteJob;
import com.example.talentbridge.job.application.GetJob;
import com.example.talentbridge.job.application.SearchJobs;
import com.example.talentbridge.job.application.UpdateJob;
import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.presentation.ApiMessage;
import com.example.talentbridge.shared.presentation.PageRequests;
import com.example.talentbridge.shared.presentation.PageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Job")
@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final CreateJob createJob;
    private final UpdateJob updateJob;
    private final DeleteJob deleteJob;
    private final GetJob getJob;
    private final SearchJobs searchJobs;

    @PostMapping
    @ApiMessage(value = "Create Job")
    @PreAuthorize("hasAuthority('POST /jobs')")
    @Operation(
            summary = "Create Job",
            description = "Required permission: <b>POST /jobs</b>"
    )
    public ResponseEntity<JobResponse> saveJob(@Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(JobResponse.from(createJob.execute(request.toData())));
    }

    @PostMapping("/company")
    @ApiMessage(value = "Create Job for current user's company")
    @PreAuthorize("hasAuthority('POST /jobs/company')")
    @Operation(
            summary = "Create Job for current user's company",
            description = "Required permission: <b>POST /jobs/company</b>"
    )
    public ResponseEntity<JobResponse> saveJobForRecruiterPage(@Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(JobResponse.from(createJob.executeForCurrentUser(request.toData())));
    }

    @GetMapping
    @ApiMessage(value = "Get Job list")
    @PreAuthorize("hasAuthority('GET /jobs') OR isAnonymous()")
    @Operation(
            summary = "Get Job list",
            description = "Required permission: <b>GET /jobs</b>"
    )
    @SecurityRequirements()
    public ResponseEntity<PageResponseDto<JobResponse>> findAllJobs(
            @RequestParam(value = "filter", required = false) String filter,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return ResponseEntity.ok(PageResponseDto.from(
                searchJobs.execute(FilterExpression.of(filter), PageRequests.from(pageable)).map(JobResponse::from)));
    }

    @GetMapping("/{id}")
    @ApiMessage(value = "Get Job by id")
    @PreAuthorize("hasAuthority('GET /jobs/{id}') OR isAnonymous()")
    @Operation(
            summary = "Get Job by id",
            description = "Required permission: <b>GET /jobs/{id}</b>"
    )
    @SecurityRequirements()
    public ResponseEntity<JobResponse> findJobById(@PathVariable Long id) {
        return ResponseEntity.ok(JobResponse.from(getJob.execute(id)));
    }

    @GetMapping("/companies/{id}")
    @ApiMessage(value = "Get Job by Company")
    @PreAuthorize("hasAuthority('GET /jobs/companies/{id}') OR isAnonymous()")
    @Operation(
            summary = "Get Job by Company",
            description = "Required permission: <b>GET /jobs/companies/{id}</b>"
    )
    @SecurityRequirements()
    public ResponseEntity<List<JobResponse>> findJobByCompanyId(@PathVariable Long id) {
        return ResponseEntity.ok(searchJobs.ofCompany(id).stream().map(JobResponse::summaryFrom).toList());
    }

    @GetMapping("/company")
    @ApiMessage(value = "Get Job list for current user's company")
    @PreAuthorize("hasAuthority('GET /jobs/company')")
    @Operation(
            summary = "Get Job list for current user's company",
            description = "Required permission: <b>GET /jobs/company</b>"
    )
    public ResponseEntity<PageResponseDto<JobResponse>> findAllJobsForRecruiterCompany(
            @RequestParam(value = "filter", required = false) String filter,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return ResponseEntity.ok(PageResponseDto.from(
                searchJobs.executeForCurrentUser(FilterExpression.of(filter), PageRequests.from(pageable))
                        .map(JobResponse::from)));
    }

    @PutMapping("/{id}")
    @ApiMessage(value = "Update Job by id")
    @PreAuthorize("hasAuthority('PUT /jobs/{id}')")
    @Operation(
            summary = "Update Job by id",
            description = "Required permission: <b>PUT /jobs/{id}</b>"
    )
    public ResponseEntity<JobResponse> updateJobById(
            @PathVariable Long id,
            @Valid @RequestBody JobRequest request
    ) {
        return ResponseEntity.ok(JobResponse.from(updateJob.execute(id, request.toData())));
    }

    @PutMapping("/company/{id}")
    @ApiMessage(value = "Update Job by id for current user's company")
    @PreAuthorize("hasAuthority('PUT /jobs/company/{id}')")
    @Operation(
            summary = "Update Job by id for current user's company",
            description = "Required permission: <b>PUT /jobs/company/{id}</b>"
    )
    public ResponseEntity<JobResponse> updateJobByIdForRecruiterCompany(
            @PathVariable Long id,
            @Valid @RequestBody JobRequest request
    ) {
        return ResponseEntity.ok(JobResponse.from(updateJob.executeForCurrentUser(id, request.toData())));
    }

    @DeleteMapping("/{id}")
    @ApiMessage(value = "Delete Job by id")
    @PreAuthorize("hasAuthority('DELETE /jobs/{id}')")
    @Operation(
            summary = "Delete Job by id",
            description = "Required permission: <b>DELETE /jobs/{id}</b>"
    )
    public ResponseEntity<JobResponse> deleteJobById(@PathVariable Long id) {
        return ResponseEntity.ok(JobResponse.from(deleteJob.execute(id)));
    }

    @DeleteMapping("/company/{id}")
    @ApiMessage(value = "Delete Job by id for current user's company")
    @PreAuthorize("hasAuthority('DELETE /jobs/company/{id}')")
    @Operation(
            summary = "Delete Job by id for current user's company",
            description = "Required permission: <b>DELETE /jobs/company/{id}</b>"
    )
    public ResponseEntity<JobResponse> deleteJobByIdForRecruiterCompany(@PathVariable Long id) {
        return ResponseEntity.ok(JobResponse.from(deleteJob.executeForCurrentUser(id)));
    }
}
