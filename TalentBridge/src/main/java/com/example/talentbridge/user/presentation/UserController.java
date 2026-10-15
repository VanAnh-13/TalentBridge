package com.example.talentbridge.user.presentation;

import com.example.talentbridge.user.application.ChangeOwnPassword;
import com.example.talentbridge.user.application.CreateUser;
import com.example.talentbridge.user.application.DeleteUser;
import com.example.talentbridge.user.application.GetUser;
import com.example.talentbridge.user.application.SearchUsers;
import com.example.talentbridge.user.application.UpdateOwnAvatar;
import com.example.talentbridge.user.application.UpdateOwnProfile;
import com.example.talentbridge.user.application.UpdateUser;
import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.presentation.ApiMessage;
import com.example.talentbridge.shared.presentation.FileUploads;
import com.example.talentbridge.shared.presentation.PageRequests;
import com.example.talentbridge.shared.presentation.PageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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

    private final CreateUser createUser;
    private final SearchUsers searchUsers;
    private final GetUser getUser;
    private final UpdateUser updateUser;
    private final DeleteUser deleteUser;
    private final UpdateOwnProfile updateOwnProfile;
    private final ChangeOwnPassword changeOwnPassword;
    private final UpdateOwnAvatar updateOwnAvatar;

    @PostMapping
    @ApiMessage(value = "Create User")
    @PreAuthorize("hasAuthority('POST /users')")
    @Operation(
            summary = "Create User",
            description = "Required permission: <b>POST /users</b>"
    )
    public ResponseEntity<UserResponse> saveUser(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(createUser.execute(request.toData())));
    }

    @GetMapping
    @ApiMessage(value = "Get User list")
    @PreAuthorize("hasAuthority('GET /users')")
    @Operation(
            summary = "Get User list",
            description = "Required permission: <b>GET /users</b>"
    )
    public ResponseEntity<PageResponseDto<UserResponse>> findAllUsers(
            @RequestParam(value = "filter", required = false) String filter,
            Pageable pageable
    ) {
        return ResponseEntity.ok(PageResponseDto.from(
                searchUsers.execute(FilterExpression.of(filter), PageRequests.from(pageable)).map(UserResponse::from)));
    }

    @GetMapping("/{id}")
    @ApiMessage(value = "Find User by id")
    @PreAuthorize("hasAuthority('GET /users/{id}')")
    @Operation(
            summary = "Find User by id",
            description = "Required permission: <b>GET /users/{id}</b>"
    )
    public ResponseEntity<UserResponse> findUserById(@PathVariable Long id) {
        return ResponseEntity.ok(UserResponse.from(getUser.execute(id)));
    }

    @PutMapping
    @ApiMessage(value = "Update User")
    @PreAuthorize("hasAuthority('PUT /users')")
    @Operation(
            summary = "Update User",
            description = "Required permission: <b>PUT /users</b>"
    )
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(UserResponse.from(updateUser.execute(request.getId(), request.toData())));
    }

    @DeleteMapping("/{id}")
    @ApiMessage(value = "Delete User by id")
    @PreAuthorize("hasAuthority('DELETE /users/{id}')")
    @Operation(
            summary = "Delete User by id",
            description = "Required permission: <b>DELETE /users/{id}</b>"
    )
    public ResponseEntity<UserResponse> deleteUserById(@PathVariable Long id) {
        return ResponseEntity.ok(UserResponse.from(deleteUser.execute(id)));
    }

    @PostMapping("/me/update-profile")
    @ApiMessage(value = "Update current user profile")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UserResponse> updateSelfUserProfile(@Valid @RequestBody SelfProfileRequest request) {
        return ResponseEntity.ok(UserResponse.from(updateOwnProfile.execute(
                request.getName(), request.getDob(), request.getAddress(), request.getGender())));
    }

    @PostMapping("/me/update-password")
    @ApiMessage(value = "Update current user password")
    @Operation(summary = "Update current user password")
    public ResponseEntity<UserResponse> updateSelfUserPassword(@Valid @RequestBody SelfPasswordRequest request) {
        return ResponseEntity.ok(UserResponse.from(changeOwnPassword.execute(request.getOldPassword(), request.getNewPassword())));
    }

    @PostMapping("/me/upload-avatar")
    @ApiMessage(value = "Update current user avatar")
    @Operation(summary = "Update current user avatar")
    public void updateSelfUserAvatar(@RequestParam("avatar") MultipartFile avatarFile) {
        updateOwnAvatar.execute(FileUploads.from(avatarFile));
    }
}
