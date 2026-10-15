package com.example.talentbridge.interview.presentation;

import com.example.talentbridge.interview.domain.InterviewMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/** Proposes (resumeId set) or reschedules an interview. */
@Data
public class InterviewRequest {

    /** Only used when scheduling. */
    private Long resumeId;

    @NotNull(message = "Hình thức phỏng vấn không được để trống")
    private InterviewMode mode;

    @NotNull(message = "Địa điểm hoặc liên kết không được để trống")
    @Size(max = 500, message = "Địa điểm/liên kết tối đa 500 ký tự")
    private String location;

    @Size(max = 1000, message = "Ghi chú tối đa 1000 ký tự")
    private String note;

    private int durationMinutes = 60;

    @NotEmpty(message = "Phải đề xuất ít nhất một khung giờ")
    private List<Instant> slots;
}
