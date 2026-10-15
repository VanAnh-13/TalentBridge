package com.example.talentbridge.controller;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.talentbridge.shared.presentation.ApiMessage;
import com.example.talentbridge.dto.request.permission.DefaultPermissionRequestDto;
import com.example.talentbridge.shared.presentation.PageResponseDto;
import com.example.talentbridge.dto.response.permission.DefaultPermissionResponseDto;
import com.example.talentbridge.model.Permission;
import com.example.talentbridge.service.PermissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    private final PermissionService permissionService;

    @PostMapping
    @PreAuthorize("hasAuthority('POST /permissions')")
    @ApiMessage(value = "Create Permission")
    @Operation(
            summary = "Create Permission",
            description = "Required permission: <b>POST /permissions</b>"
    )
    public DefaultPermissionResponseDto savePermission(
            @Valid @RequestBody DefaultPermissionRequestDto defaultPermissionRequestDto
    ) {
        return permissionService.savePermission(defaultPermissionRequestDto);
    }


    @GetMapping
    @PreAuthorize("hasAuthority('GET /permissions')")
    @ApiMessage("Get Permission list")
    @Operation(
            summary = "Get Permission list",
            description = "Required permission: <b>GET /permissions</b>"
    )
    public ResponseEntity<?> findAllPermissions(
            @Filter Specification<Permission> spec,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        Page<DefaultPermissionResponseDto> page = permissionService.findAllPermission(spec, pageable);

        PageResponseDto<DefaultPermissionResponseDto> res = new PageResponseDto<>(
                page.getContent(),
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return ResponseEntity.ok(res);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('GET /permissions/all')")
    @ApiMessage("Get all Permissions (no pagination)")
    @Operation(
            summary = "Get all Permissions (no pagination)",
            description = "Required permission: <b>GET /permissions/all</b>"
    )
    public ResponseEntity<?> findAllPermissionsNoPaging(
            @Filter Specification<Permission> spec
    ) {
        List<DefaultPermissionResponseDto> list = permissionService
                .findAllPermission(spec, Pageable.unpaged())
                .getContent();

        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PUT /permissions/{id}')")
    @ApiMessage(value = "Update Permission")
    @Operation(
            summary = "Update Permission",
            description = "Required permission: <b>PUT /permissions/{id}</b>"
    )
    public DefaultPermissionResponseDto updatePermissionById(
            @Valid @RequestBody DefaultPermissionRequestDto defaultPermissionRequestDto,
            @PathVariable Long id
    ) {
        return permissionService.updatePermission(id, defaultPermissionRequestDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE /permissions/{id}')")
    @ApiMessage(value = "Delete Permission")
    @Operation(
            summary = "Delete Permission",
            description = "Required permission: <b>DELETE /permissions/{id}</b>"
    )
    public DefaultPermissionResponseDto deletePermissionById(
            @PathVariable Long id
    ) {
        return permissionService.deletePermission(id);
    }
}
