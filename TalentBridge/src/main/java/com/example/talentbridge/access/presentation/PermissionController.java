package com.example.talentbridge.access.presentation;

import com.example.talentbridge.access.application.CreatePermission;
import com.example.talentbridge.access.application.DeletePermission;
import com.example.talentbridge.access.application.SearchPermissions;
import com.example.talentbridge.access.application.UpdatePermission;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Permission")
@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final CreatePermission createPermission;
    private final SearchPermissions searchPermissions;
    private final UpdatePermission updatePermission;
    private final DeletePermission deletePermission;

    @PostMapping
    @PreAuthorize("hasAuthority('POST /permissions')")
    @ApiMessage(value = "Create Permission")
    @Operation(
            summary = "Create Permission",
            description = "Required permission: <b>POST /permissions</b>"
    )
    public PermissionResponse savePermission(@Valid @RequestBody PermissionRequest request) {
        return PermissionResponse.from(createPermission.execute(
                request.getName(), request.getApiPath(), request.getMethod(), request.getModule()));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('GET /permissions')")
    @ApiMessage("Get Permission list")
    @Operation(
            summary = "Get Permission list",
            description = "Required permission: <b>GET /permissions</b>"
    )
    public ResponseEntity<PageResponseDto<PermissionResponse>> findAllPermissions(
            @RequestParam(value = "filter", required = false) String filter,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return ResponseEntity.ok(PageResponseDto.from(
                searchPermissions.execute(FilterExpression.of(filter), PageRequests.from(pageable))
                        .map(PermissionResponse::from)));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('GET /permissions/all')")
    @ApiMessage("Get all Permissions (no pagination)")
    @Operation(
            summary = "Get all Permissions (no pagination)",
            description = "Required permission: <b>GET /permissions/all</b>"
    )
    public ResponseEntity<List<PermissionResponse>> findAllPermissionsNoPaging(
            @RequestParam(value = "filter", required = false) String filter
    ) {
        return ResponseEntity.ok(searchPermissions.executeUnpaged(FilterExpression.of(filter)).stream()
                .map(PermissionResponse::from).toList());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PUT /permissions/{id}')")
    @ApiMessage(value = "Update Permission")
    @Operation(
            summary = "Update Permission",
            description = "Required permission: <b>PUT /permissions/{id}</b>"
    )
    public PermissionResponse updatePermissionById(
            @Valid @RequestBody PermissionRequest request,
            @PathVariable Long id
    ) {
        return PermissionResponse.from(updatePermission.execute(
                id, request.getName(), request.getApiPath(), request.getMethod(), request.getModule()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE /permissions/{id}')")
    @ApiMessage(value = "Delete Permission")
    @Operation(
            summary = "Delete Permission",
            description = "Required permission: <b>DELETE /permissions/{id}</b>"
    )
    public PermissionResponse deletePermissionById(@PathVariable Long id) {
        return PermissionResponse.from(deletePermission.execute(id));
    }
}
