package com.example.talentbridge.controller;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.talentbridge.shared.presentation.ApiMessage;
import com.example.talentbridge.dto.request.user.SelfUserUpdatePasswordRequestDto;
import com.example.talentbridge.dto.request.user.SelfUserUpdateProfileRequestDto;
import com.example.talentbridge.dto.request.user.UserCreateRequestDto;
import com.example.talentbridge.dto.request.user.UserUpdateRequestDto;
import com.example.talentbridge.shared.presentation.PageResponseDto;
import com.example.talentbridge.dto.response.user.DefaultUserResponseDto;
import com.example.talentbridge.model.User;
import com.example.talentbridge.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ApiMessage(value = "Create User")
    @PreAuthorize("hasAuthority('POST /users')")
    @Operation(
            summary = "Create User",
            description = "Required permission: <b>POST /users</b>"
    )
    public ResponseEntity<DefaultUserResponseDto> saveUser(
            @Valid @RequestBody UserCreateRequestDto userCreateRequestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.saveUser(userCreateRequestDto));
    }

    @GetMapping
    @ApiMessage(value = "Get User list")
    @PreAuthorize("hasAuthority('GET /users')")
    @Operation(
            summary = "Get User list",
            description = "Required permission: <b>GET /users</b>"
    )
    public ResponseEntity<PageResponseDto<DefaultUserResponseDto>> findAllUsers(
            @Filter Specification<User> spec,
            Pageable pageable
    ) {


        Page<DefaultUserResponseDto> page = userService.findAllUser(spec, pageable);

        PageResponseDto<DefaultUserResponseDto> res = new PageResponseDto<>(
                page.getContent(),
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    @ApiMessage(value = "Find User by id")
    @PreAuthorize("hasAuthority('GET /users/{id}')")
    @Operation(
            summary = "Find User by id",
            description = "Required permission: <b>GET /users/{id}</b>"
    )
    public ResponseEntity<DefaultUserResponseDto> findUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @PutMapping
    @ApiMessage(value = "Update User")
    @PreAuthorize("hasAuthority('PUT /users')")
    @Operation(
            summary = "Update User",
            description = "Required permission: <b>PUT /users</b>"
    )
    public ResponseEntity<DefaultUserResponseDto> updateUser(
            @Valid @RequestBody UserUpdateRequestDto userUpdateRequestDto
    ) {
        return ResponseEntity.ok(userService.updateUser(userUpdateRequestDto));
    }

    @DeleteMapping("/{id}")
    @ApiMessage(value = "Delete User by id")
    @PreAuthorize("hasAuthority('DELETE /users/{id}')")
    @Operation(
            summary = "Delete User by id",
            description = "Required permission: <b>DELETE /users/{id}</b>"
    )
    public ResponseEntity<DefaultUserResponseDto> deleteUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteUserById(id));
    }

    @PostMapping("/me/update-profile")
    @ApiMessage(value = "Update current user profile")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<DefaultUserResponseDto> updateSelfUserProfile(
            @Valid @RequestBody SelfUserUpdateProfileRequestDto selfUserUpdateProfileRequestDto
    ) {
        return ResponseEntity.ok(userService.updateSelfUserProfile(selfUserUpdateProfileRequestDto));
    }

    @PostMapping("/me/update-password")
    @ApiMessage(value = "Update current user password")
    @Operation(summary = "Update current user password")
    public ResponseEntity<DefaultUserResponseDto> updateSelfUserPassword(
            @Valid @RequestBody SelfUserUpdatePasswordRequestDto selfUserUpdatePasswordRequestDto
    ) {
        return ResponseEntity.ok(userService.updateSelfUserPassword(selfUserUpdatePasswordRequestDto));
    }

    @PostMapping("/me/upload-avatar")
    @ApiMessage(value = "Update current user avatar")
    @Operation(summary = "Update current user avatar")
    public void updateSelfUserAvatar(
            @RequestParam("avatar") MultipartFile avatarFile
    ) {
        userService.updateSelfUserAvatar(avatarFile);
    }


}
