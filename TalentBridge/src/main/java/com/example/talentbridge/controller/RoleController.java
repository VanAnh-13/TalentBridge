package com.example.talentbridge.controller;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.talentbridge.shared.presentation.ApiMessage;
import com.example.talentbridge.dto.request.role.DefaultRoleRequestDto;
import com.example.talentbridge.shared.presentation.PageResponseDto;
import com.example.talentbridge.dto.response.role.DefaultRoleResponseDto;
import com.example.talentbridge.model.Role;
import com.example.talentbridge.service.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    private final RoleService roleService;

    @PostMapping
    @ApiMessage(value = "Create Role")
    @PreAuthorize("hasAuthority('POST /roles')")
    @Operation(
            summary = "Create Role",
            description = "Required permission: <b>POST /roles</b>"
    )
    public ResponseEntity<DefaultRoleResponseDto> saveRole(
            @Valid @RequestBody DefaultRoleRequestDto defaultRoleRequestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(roleService.saveRole(defaultRoleRequestDto));
    }

    @GetMapping
    @ApiMessage(value = "Get Role list")
    @PreAuthorize("hasAuthority('GET /roles')")
    @Operation(
            summary = "Get Role list",
            description = "Required permission: <b>GET /roles</b>"
    )
    public ResponseEntity<PageResponseDto<DefaultRoleResponseDto>> findAllRoles(
            @Filter Specification<Role> spec,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        Page<DefaultRoleResponseDto> page = roleService.findAllRoles(spec, pageable);

        PageResponseDto<DefaultRoleResponseDto> res = new PageResponseDto<>(
                page.getContent(),
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return ResponseEntity.ok(res);
    }

    @PutMapping("/{id}")
    @ApiMessage(value = "Update Role")
    @PreAuthorize("hasAuthority('PUT /roles/{id}')")
    @Operation(
            summary = "Update Role",
            description = "Required permission: <b>PUT /roles/{id}</b>"
    )
    public ResponseEntity<DefaultRoleResponseDto> updateRoleById(
            @PathVariable Long id,
            @Valid @RequestBody DefaultRoleRequestDto defaultRoleRequestDto
    ) {
        return ResponseEntity.ok(roleService.updateRole(id, defaultRoleRequestDto));
    }

    @DeleteMapping("/{id}")
    @ApiMessage(value = "Delete Role by id")
    @PreAuthorize("hasAuthority('DELETE /roles/{id}')")
    @Operation(
            summary = "Delete Role by id",
            description = "Required permission: <b>DELETE /roles/{id}</b>"
    )
    public ResponseEntity<DefaultRoleResponseDto> deleteRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.deleteRoleById(id));
    }


}
