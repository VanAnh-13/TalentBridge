package com.example.talentbridge.controller;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.talentbridge.annotation.ApiMessage;
import com.example.talentbridge.dto.request.company.DefaultCompanyRequestDto;
import com.example.talentbridge.dto.request.user.RecruiterRequestDto;
import com.example.talentbridge.dto.response.PageResponseDto;
import com.example.talentbridge.dto.response.ApiResponse;
import com.example.talentbridge.dto.response.company.DefaultCompanyExtendedResponseDto;
import com.example.talentbridge.dto.response.company.DefaultCompanyResponseDto;
import com.example.talentbridge.model.Company;
import com.example.talentbridge.service.CompanyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Company")
@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    @ApiMessage(value = "Create Company")
    @PreAuthorize("hasAuthority('POST /companies')")
    @Operation(
            summary = "Create Company",
            description = "Required permission: <b>POST /companies</b>"
    )
    public ResponseEntity<?> saveCompany(
            @Valid @RequestPart("company") DefaultCompanyRequestDto defaultCompanyRequestDto,
            @RequestPart(value = "logoFile", required = false) MultipartFile logoFile
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(companyService.saveCompany(defaultCompanyRequestDto, logoFile, false));
    }

    @PostMapping("/me")
    @ApiMessage(value = "Create Company for current user")
    @PreAuthorize("hasAuthority('POST /companies/me')")
    @Operation(
            summary = "Create Company for current user",
            description = "Required permission: <b>POST /companies/me</b>"
    )
    public ResponseEntity<?> saveSelfCompany(
            @Valid @RequestPart("company") DefaultCompanyRequestDto defaultCompanyRequestDto,
            @RequestPart(value = "logoFile", required = false) MultipartFile logoFile
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(companyService.saveCompany(defaultCompanyRequestDto, logoFile, true));
    }

    @GetMapping
    @ApiMessage(value = "Get Company list")
    @PreAuthorize("permitAll()")
    @Operation(
            summary = "Get Company list",
            description = "Public endpoint - no permission required"
    )
    @SecurityRequirements()
    public ResponseEntity<?> findAllCompanies(
            @Filter Specification<Company> spec,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        Page<DefaultCompanyResponseDto> page = companyService.findAllCompanies(spec, pageable);

        PageResponseDto<DefaultCompanyResponseDto> res = new PageResponseDto<>(
                page.getContent(),
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return ResponseEntity.ok(res);
    }

    @GetMapping("/with-jobs-count")
    @ApiMessage(value = "Get Company list with job count")
    @PreAuthorize("permitAll()")
    @Operation(
            summary = "Get Company list with job count",
            description = "Public endpoint - no permission required"
    )
    @SecurityRequirements()
    public ResponseEntity<?> findAllCompaniesWithJobsCount(
            @Filter Specification<Company> spec,
            @PageableDefault(size = 9) Pageable pageable
    ) {
        Page<DefaultCompanyExtendedResponseDto> page = companyService.findAllCompaniesWithJobsCount(spec, pageable);

        PageResponseDto<DefaultCompanyExtendedResponseDto> res = new PageResponseDto<>(
                page.getContent(),
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    @ApiMessage(value = "Get Company by id")
    @PreAuthorize("permitAll()")
    @Operation(
            summary = "Get Company by id",
            description = "Public endpoint - no permission required"
    )
    @SecurityRequirements()
    public ResponseEntity<DefaultCompanyResponseDto> findCompanyById(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.findCompanyById(id));
    }

    @GetMapping("/me")
    @ApiMessage(value = "Get Company for current user")
    @PreAuthorize("hasAuthority('GET /companies/me')")
    @Operation(
            summary = "Get Company for current user",
            description = "Required permission: <b>GET /companies/me</b>"
    )
    public ResponseEntity<?> findSelfCompany() {
        return ResponseEntity.ok(
                companyService.findSelfCompany()
        );
    }

    @GetMapping("/me/users")
    @ApiMessage(value = "Get recruiter user list for current user")
    @PreAuthorize("hasAuthority('GET /companies/me/users')")
    @Operation(
            summary = "Get recruiter user list for current user",
            description = "Required permission: <b>GET /companies/me/users</b>"
    )
    public ResponseEntity<?> findAllRecruitersBySelfCompany() {
        return ResponseEntity.ok(
                companyService.findAllRecruitersBySelfCompany()
        );
    }

    @PutMapping(value = "/{id}")
    @ApiMessage(value = "Update Company by id")
    @PreAuthorize("hasAuthority('PUT /companies/{id}')")
    @Operation(
            summary = "Update Company by id",
            description = "Required permission: <b>PUT /companies/{id}</b>"
    )
    public ResponseEntity<?> updateCompanyById(
            @Valid @RequestPart("company") DefaultCompanyRequestDto defaultCompanyRequestDto,
            @RequestPart(value = "logoFile", required = false) MultipartFile logoFile,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(companyService.updateCompany(defaultCompanyRequestDto, id, logoFile, false));
    }

    @PutMapping(value = "/me")
    @ApiMessage(value = "Update Company for current user")
    @PreAuthorize("hasAuthority('PUT /companies/me')")
    @Operation(
            summary = "Update Company for current user",
            description = "Required permission: <b>PUT /companies/me</b>"
    )
    public ResponseEntity<?> updateSelfCompany(
            @Valid @RequestPart("company") DefaultCompanyRequestDto defaultCompanyRequestDto,
            @RequestPart(value = "logoFile", required = false) MultipartFile logoFile
    ) {
        return ResponseEntity.ok(companyService.updateCompany(
                defaultCompanyRequestDto, null, logoFile, true
        ));
    }

    @DeleteMapping("/{id}")
    @ApiMessage(value = "Delete company by id")
    @PreAuthorize("hasAuthority('DELETE /companies/{id}')")
    @Operation(
            summary = "Delete company by id",
            description = "Required permission: <b>DELETE /companies/{id}</b>"
    )
    public ResponseEntity<?> deleteCompanyById(@PathVariable Long id) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Delete company",
                        companyService.deleteCompanyById(id)
                )
        );
    }


    @PostMapping("/me/users")
    @ApiMessage(value = "Add another user to current user's company")
    @PreAuthorize("hasAuthority('POST /companies/me/users')")
    @Operation(
            summary = "Add another user to current user's company",
            description = "Required permission: <b>POST /companies/me/users</b>"
    )
    public ResponseEntity<Void> addMemberToCompany(
            @Valid @RequestBody RecruiterRequestDto recruiterRequestDto
    ) {
        companyService.addMemberToCompany(recruiterRequestDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me/users")
    @ApiMessage(value = "Remove another user from current user's company")
    @PreAuthorize("hasAuthority('PUT /companies/me/users')")
    @Operation(
            summary = "Remove another user from current user's company",
            description = "Required permission: <b>PUT /companies/me/users</b>"
    )
    public ResponseEntity<Void> removeMemberFromCompany(
            @Valid @RequestBody RecruiterRequestDto recruiterRequestDto
    ) {
        companyService.removeMemberFromCompany(recruiterRequestDto);
        return ResponseEntity.ok().build();
    }

}
