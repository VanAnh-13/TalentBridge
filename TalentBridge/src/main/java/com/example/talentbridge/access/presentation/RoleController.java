package com.example.talentbridge.access.presentation;

import com.example.talentbridge.access.application.CreateRole;
import com.example.talentbridge.access.application.DeleteRole;
import com.example.talentbridge.access.application.SearchRoles;
import com.example.talentbridge.access.application.UpdateRole;
import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.presentation.ApiMessage;
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

@Tag(name = "Role")
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final CreateRole createRole;
    private final SearchRoles searchRoles;
    private final UpdateRole updateRole;
    private final DeleteRole deleteRole;

    @PostMapping
    @ApiMessage(value = "Create Role")
    @PreAuthorize("hasAuthority('POST /roles')")
    @Operation(
            summary = "Create Role",
            description = "Required permission: <b>POST /roles</b>"
    )
    public ResponseEntity<RoleResponse> saveRole(@Valid @RequestBody RoleRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RoleResponse.from(createRole.execute(request.getName(), request.getDescription(), request.permissionIds())));
    }

    @GetMapping
    @ApiMessage(value = "Get Role list")
    @PreAuthorize("hasAuthority('GET /roles')")
    @Operation(
            summary = "Get Role list",
            description = "Required permission: <b>GET /roles</b>"
    )
    public ResponseEntity<PageResponseDto<RoleResponse>> findAllRoles(
            @RequestParam(value = "filter", required = false) String filter,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return ResponseEntity.ok(PageResponseDto.from(
                searchRoles.execute(FilterExpression.of(filter), PageRequests.from(pageable)).map(RoleResponse::from)));
    }

    @PutMapping("/{id}")
    @ApiMessage(value = "Update Role")
    @PreAuthorize("hasAuthority('PUT /roles/{id}')")
    @Operation(
            summary = "Update Role",
            description = "Required permission: <b>PUT /roles/{id}</b>"
    )
    public ResponseEntity<RoleResponse> updateRoleById(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequest request
    ) {
        return ResponseEntity.ok(RoleResponse.from(updateRole.execute(
                id, request.getName(), request.getDescription(), request.isActive(), request.permissionIds())));
    }

    @DeleteMapping("/{id}")
    @ApiMessage(value = "Delete Role by id")
    @PreAuthorize("hasAuthority('DELETE /roles/{id}')")
    @Operation(
            summary = "Delete Role by id",
            description = "Required permission: <b>DELETE /roles/{id}</b>"
    )
    public ResponseEntity<RoleResponse> deleteRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(RoleResponse.from(deleteRole.execute(id)));
    }
}
