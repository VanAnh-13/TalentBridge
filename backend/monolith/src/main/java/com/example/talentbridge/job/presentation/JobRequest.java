package com.example.talentbridge.job.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.talentbridge.job.domain.Level;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobRequest {

    @NotBlank(message = "Tên công việc không được để trống")
    private String name;

    @NotBlank(message = "Địa điểm làm việc không được để trống")
    private String location;

    @NotNull(message = "Mức lương không được để trống")
    @PositiveOrZero(message = "Mức lương phải lớn hơn hoặc bằng 0")
    private Double salary;

    @NotNull(message = "Số lượng tuyển không được để trống")
    @Positive(message = "Số lượng tuyển phải lớn hơn 0")
    private Integer quantity;

    @NotNull(message = "Cấp bậc không được để trống")
    private Level level;

    @NotBlank(message = "Mô tả công việc không được để trống")
    private String description;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private Instant startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private Instant endDate;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean active;

    @NotNull(message = "Công ty không được để trống")
    @Valid
    private CompanyId company;

    @NotNull(message = "Danh sách kỹ năng không được để trống")
    @Size(min = 1, message = "Phải có ít nhất 1 kỹ năng")
    @Valid
    private List<SkillId> skills;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompanyId {
        @NotNull(message = "ID công ty không được để trống")
        private Long id;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SkillId {
        @NotNull(message = "ID kỹ năng không được để trống")
        private Long id;
    }

    public com.example.talentbridge.job.application.JobData toData() {
        return new com.example.talentbridge.job.application.JobData(
                name, location, salary, quantity, level, description, startDate, endDate, active,
                company == null ? null : company.getId(),
                skills == null ? null : skills.stream().map(SkillId::getId).toList());
    }
}
