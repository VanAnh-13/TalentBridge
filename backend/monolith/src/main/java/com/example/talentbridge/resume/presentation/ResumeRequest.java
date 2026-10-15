package com.example.talentbridge.resume.presentation;

import com.example.talentbridge.resume.domain.ResumeStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResumeRequest {

    @NotBlank(message = "Email người dùng không được để trống")
    @Email(
            message = "Định dạng email không hợp lệ",
            regexp = "^[\\w\\-.]+@([\\w\\-]+\\.)+[\\w\\-]{2,4}$"
    )
    private String email;

    /** Accepted for compatibility; the server decides the initial status (always PENDING). */
    @NotNull(message = "Trạng thái hồ sơ không được để trống")
    private ResumeStatus status;

    @NotNull(message = "Người dùng không được để trống")
    private User user;

    @NotNull(message = "Công việc không được để trống")
    private Job job;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class User {
        private Long id;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Job {
        private Long id;
    }
}
