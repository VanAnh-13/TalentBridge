package com.example.talentbridge.controller;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.talentbridge.annotation.ApiMessage;
import com.example.talentbridge.dto.request.skill.CreateSkillRequestDto;
import com.example.talentbridge.dto.request.skill.UpdateSkillResponseDto;
import com.example.talentbridge.dto.response.PageResponseDto;
import com.example.talentbridge.dto.response.skill.DefaultSkillResponseDto;
import com.example.talentbridge.model.Skill;
import com.example.talentbridge.service.SkillService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Skill")
@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    @ApiMessage(value = "Create Skill")
    @PreAuthorize("hasAuthority('POST /skills')")
    @Operation(
            summary = "Create Skill",
            description = "Required permission: <b>POST /skills</b>"
    )
    public ResponseEntity<DefaultSkillResponseDto> saveSkill(
            @Valid @RequestBody CreateSkillRequestDto createSkillRequestDto
    ) {
        return ResponseEntity
                .ok(skillService.saveSkill(createSkillRequestDto));
    }

    @GetMapping
    @ApiMessage(value = "Get Skill list")
    @PreAuthorize("permitAll()")
    @Operation(
            summary = "Get Skill list",
            description = "Public endpoint - no permission required"
    )
    @SecurityRequirements()
    public ResponseEntity<PageResponseDto<DefaultSkillResponseDto>> findAllSkills(
            @Filter Specification<Skill> spec,
            @PageableDefault(size = 5) Pageable pageable) {

        Page<DefaultSkillResponseDto> page = skillService.findAllSkills(spec, pageable);

        PageResponseDto<DefaultSkillResponseDto> res = new PageResponseDto<>(
                page.getContent(),
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    @ApiMessage(value = "Get Skill by id")
    @PreAuthorize("permitAll()")
    @Operation(
            summary = "Get Skill by id",
            description = "Public endpoint - no permission required"
    )
    @SecurityRequirements()
    public ResponseEntity<DefaultSkillResponseDto> findSkillById(@PathVariable Long id) {
        return ResponseEntity.ok(skillService.findSkillById(id));
    }

    @PutMapping
    @ApiMessage(value = "Update Skill")
    @PreAuthorize("hasAuthority('PUT /skills')")
    @Operation(
            summary = "Update Skill",
            description = "Required permission: <b>PUT /skills</b>"
    )
    public ResponseEntity<DefaultSkillResponseDto> updateSkill(
            @Valid @RequestBody UpdateSkillResponseDto updateSkillResponseDto) {
        return ResponseEntity.ok(skillService.updateSkillById(updateSkillResponseDto));

    }

    @DeleteMapping("/{id}")
    @ApiMessage(value = "Delete Skill by id")
    @PreAuthorize("hasAuthority('DELETE /skills/{id}')")
    @Operation(
            summary = "Delete Skill by id",
            description = "Required permission: <b>DELETE /skills/{id}</b>"
    )
    public ResponseEntity<DefaultSkillResponseDto> deleteSkillById(@PathVariable Long id) {
        return ResponseEntity.ok(skillService.deleteSkillById(id));
    }


}
