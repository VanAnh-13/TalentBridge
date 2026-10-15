package com.example.talentbridge.skill.presentation;

import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.presentation.ApiMessage;
import com.example.talentbridge.shared.presentation.PageRequests;
import com.example.talentbridge.shared.presentation.PageResponseDto;
import com.example.talentbridge.skill.application.CreateSkill;
import com.example.talentbridge.skill.application.DeleteSkill;
import com.example.talentbridge.skill.application.GetSkill;
import com.example.talentbridge.skill.application.SearchSkills;
import com.example.talentbridge.skill.application.UpdateSkill;
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

@Tag(name = "Skill")
@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
public class SkillController {

    private final CreateSkill createSkill;
    private final SearchSkills searchSkills;
    private final GetSkill getSkill;
    private final UpdateSkill updateSkill;
    private final DeleteSkill deleteSkill;

    @PostMapping
    @ApiMessage(value = "Create Skill")
    @PreAuthorize("hasAuthority('POST /skills')")
    @Operation(
            summary = "Create Skill",
            description = "Required permission: <b>POST /skills</b>"
    )
    public ResponseEntity<SkillResponse> saveSkill(@Valid @RequestBody CreateSkillRequest request) {
        return ResponseEntity.ok(SkillResponse.from(createSkill.execute(request.getName())));
    }

    @GetMapping
    @ApiMessage(value = "Get Skill list")
    @PreAuthorize("hasAuthority('GET /skills') OR isAnonymous()")
    @Operation(
            summary = "Get Skill list",
            description = "Required permission: <b>GET /skills</b>"
    )
    @SecurityRequirements()
    public ResponseEntity<PageResponseDto<SkillResponse>> findAllSkills(
            @RequestParam(value = "filter", required = false) String filter,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return ResponseEntity.ok(PageResponseDto.from(
                searchSkills.execute(FilterExpression.of(filter), PageRequests.from(pageable))
                        .map(SkillResponse::from)));
    }

    @GetMapping("/{id}")
    @ApiMessage(value = "Get Skill by id")
    @PreAuthorize("hasAuthority('GET /skills/{id}') OR isAnonymous()")
    @Operation(
            summary = "Get Skill by id",
            description = "Required permission: <b>GET /skills/{id}</b>"
    )
    @SecurityRequirements()
    public ResponseEntity<SkillResponse> findSkillById(@PathVariable Long id) {
        return ResponseEntity.ok(SkillResponse.from(getSkill.execute(id)));
    }

    @PutMapping
    @ApiMessage(value = "Update Skill")
    @PreAuthorize("hasAuthority('PUT /skills')")
    @Operation(
            summary = "Update Skill",
            description = "Required permission: <b>PUT /skills</b>"
    )
    public ResponseEntity<SkillResponse> updateSkill(@Valid @RequestBody UpdateSkillRequest request) {
        return ResponseEntity.ok(SkillResponse.from(updateSkill.execute(request.getId(), request.getName())));
    }

    @DeleteMapping("/{id}")
    @ApiMessage(value = "Delete Skill by id")
    @PreAuthorize("hasAuthority('DELETE /skills/{id}')")
    @Operation(
            summary = "Delete Skill by id",
            description = "Required permission: <b>DELETE /skills/{id}</b>"
    )
    public ResponseEntity<SkillResponse> deleteSkillById(@PathVariable Long id) {
        return ResponseEntity.ok(SkillResponse.from(deleteSkill.execute(id)));
    }
}
