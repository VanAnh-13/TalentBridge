package com.example.talentbridge.company.presentation;

import com.example.talentbridge.company.application.AddCompanyMember;
import com.example.talentbridge.company.application.CreateCompany;
import com.example.talentbridge.company.application.DeleteCompany;
import com.example.talentbridge.company.application.GetCompany;
import com.example.talentbridge.company.application.ListCompanyMembers;
import com.example.talentbridge.company.application.RemoveCompanyMember;
import com.example.talentbridge.company.application.SearchCompanies;
import com.example.talentbridge.company.application.UpdateCompany;
import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.presentation.ApiMessage;
import com.example.talentbridge.shared.presentation.ApiResponse;
import com.example.talentbridge.shared.presentation.FileUploads;
import com.example.talentbridge.shared.presentation.PageRequests;
import com.example.talentbridge.shared.presentation.PageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
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

import java.util.List;

@Tag(name = "Company")
@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CreateCompany createCompany;
    private final UpdateCompany updateCompany;
    private final GetCompany getCompany;
    private final SearchCompanies searchCompanies;
    private final DeleteCompany deleteCompany;
    private final ListCompanyMembers listCompanyMembers;
    private final AddCompanyMember addCompanyMember;
    private final RemoveCompanyMember removeCompanyMember;

    @PostMapping
    @ApiMessage(value = "Create Company")
    @PreAuthorize("hasAuthority('POST /companies')")
    @Operation(
            summary = "Create Company",
            description = "Required permission: <b>POST /companies</b>"
    )
    public ResponseEntity<CompanyResponse> saveCompany(
            @Valid @RequestPart("company") CompanyRequest request,
            @RequestPart(value = "logoFile", required = false) MultipartFile logoFile
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CompanyResponse.from(createCompany.execute(
                request.getName(), request.getDescription(), request.getAddress(), FileUploads.from(logoFile))));
    }

    @PostMapping("/me")
    @ApiMessage(value = "Create Company for current user")
    @PreAuthorize("hasAuthority('POST /companies/me')")
    @Operation(
            summary = "Create Company for current user",
            description = "Required permission: <b>POST /companies/me</b>"
    )
    public ResponseEntity<CompanyResponse> saveSelfCompany(
            @Valid @RequestPart("company") CompanyRequest request,
            @RequestPart(value = "logoFile", required = false) MultipartFile logoFile
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CompanyResponse.from(createCompany.executeForCurrentUser(
                request.getName(), request.getDescription(), request.getAddress(), FileUploads.from(logoFile))));
    }

    @GetMapping
    @ApiMessage(value = "Get Company list")
    @PreAuthorize("hasAuthority('GET /companies') OR isAnonymous()")
    @Operation(
            summary = "Get Company list",
            description = "Required permission: <b>GET /companies</b>"
    )
    @SecurityRequirements()
    public ResponseEntity<PageResponseDto<CompanyResponse>> findAllCompanies(
            @RequestParam(value = "filter", required = false) String filter,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return ResponseEntity.ok(PageResponseDto.from(
                searchCompanies.execute(FilterExpression.of(filter), PageRequests.from(pageable)).map(CompanyResponse::from)));
    }

    @GetMapping("/with-jobs-count")
    @ApiMessage(value = "Get Company list with job count")
    @PreAuthorize("hasAuthority('GET /companies/with-jobs-count') OR isAnonymous()")
    @Operation(
            summary = "Get Company list with job count",
            description = "Required permission: <b>GET /companies/with-jobs-count</b>"
    )
    @SecurityRequirements()
    public ResponseEntity<PageResponseDto<CompanyWithJobsCountResponse>> findAllCompaniesWithJobsCount(
            @RequestParam(value = "filter", required = false) String filter,
            @PageableDefault(size = 9) Pageable pageable
    ) {
        return ResponseEntity.ok(PageResponseDto.from(
                searchCompanies.executeWithJobCount(FilterExpression.of(filter), PageRequests.from(pageable))
                        .map(CompanyWithJobsCountResponse::from)));
    }

    @GetMapping("/{id}")
    @ApiMessage(value = "Get Company by id")
    @PreAuthorize("hasAuthority('GET /companies/{id}') OR isAnonymous()")
    @Operation(
            summary = "Get Company by id",
            description = "Required permission: <b>GET /companies/{id}</b>"
    )
    @SecurityRequirements()
    public ResponseEntity<CompanyResponse> findCompanyById(@PathVariable Long id) {
        return ResponseEntity.ok(CompanyResponse.from(getCompany.execute(id)));
    }

    @GetMapping("/me")
    @ApiMessage(value = "Get Company for current user")
    @PreAuthorize("hasAuthority('GET /companies/me')")
    @Operation(
            summary = "Get Company for current user",
            description = "Required permission: <b>GET /companies/me</b>"
    )
    public ResponseEntity<CompanyResponse> findSelfCompany() {
        return ResponseEntity.ok(CompanyResponse.from(getCompany.executeForCurrentUser()));
    }

    @GetMapping("/me/users")
    @ApiMessage(value = "Get recruiter user list for current user")
    @PreAuthorize("hasAuthority('GET /companies/me/users')")
    @Operation(
            summary = "Get recruiter user list for current user",
            description = "Required permission: <b>GET /companies/me/users</b>"
    )
    public ResponseEntity<List<MemberResponse>> findAllRecruitersBySelfCompany() {
        return ResponseEntity.ok(listCompanyMembers.execute().stream().map(MemberResponse::from).toList());
    }

    @PutMapping("/{id}")
    @ApiMessage(value = "Update Company by id")
    @PreAuthorize("hasAuthority('PUT /companies/{id}')")
    @Operation(
            summary = "Update Company by id",
            description = "Required permission: <b>PUT /companies/{id}</b>"
    )
    public ResponseEntity<CompanyResponse> updateCompanyById(
            @Valid @RequestPart("company") CompanyRequest request,
            @RequestPart(value = "logoFile", required = false) MultipartFile logoFile,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(CompanyResponse.from(updateCompany.execute(
                id, request.getName(), request.getDescription(), request.getAddress(), FileUploads.from(logoFile))));
    }

    @PutMapping("/me")
    @ApiMessage(value = "Update Company for current user")
    @PreAuthorize("hasAuthority('PUT /companies/me')")
    @Operation(
            summary = "Update Company for current user",
            description = "Required permission: <b>PUT /companies/me</b>"
    )
    public ResponseEntity<CompanyResponse> updateSelfCompany(
            @Valid @RequestPart("company") CompanyRequest request,
            @RequestPart(value = "logoFile", required = false) MultipartFile logoFile
    ) {
        return ResponseEntity.ok(CompanyResponse.from(updateCompany.executeForCurrentUser(
                request.getName(), request.getDescription(), request.getAddress(), FileUploads.from(logoFile))));
    }

    @DeleteMapping("/{id}")
    @ApiMessage(value = "Delete company by id")
    @PreAuthorize("hasAuthority('DELETE /companies/{id}')")
    @Operation(
            summary = "Delete company by id",
            description = "Required permission: <b>DELETE /companies/{id}</b>"
    )
    public ResponseEntity<ApiResponse<CompanyResponse>> deleteCompanyById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>("Delete company", CompanyResponse.from(deleteCompany.execute(id))));
    }

    @PostMapping("/me/users")
    @ApiMessage(value = "Add another user to current user's company")
    @PreAuthorize("hasAuthority('POST /companies/me/users')")
    @Operation(
            summary = "Add another user to current user's company",
            description = "Required permission: <b>POST /companies/me/users</b>"
    )
    public ResponseEntity<Void> addMemberToCompany(@Valid @RequestBody MemberRequest request) {
        addCompanyMember.execute(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me/users")
    @ApiMessage(value = "Remove another user from current user's company")
    @PreAuthorize("hasAuthority('PUT /companies/me/users')")
    @Operation(
            summary = "Remove another user from current user's company",
            description = "Required permission: <b>PUT /companies/me/users</b>"
    )
    public ResponseEntity<Void> removeMemberFromCompany(@Valid @RequestBody MemberRequest request) {
        removeCompanyMember.execute(request.getEmail());
        return ResponseEntity.ok().build();
    }
}
