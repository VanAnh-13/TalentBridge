package com.example.talentbridge.resume.presentation;

import com.example.talentbridge.resume.domain.ResumeStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateResumeStatusRequest {

    @NotNull(message = "ID hồ sơ không được để trống")
    private Long id;

    @NotNull(message = "Trạng thái hồ sơ không được để trống")
    private ResumeStatus status;
}
