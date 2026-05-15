package com.metahrms.employee_management.dto.request.CV;


import com.metahrms.employee_management.enums.InterviewType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Đổi lịch phỏng vấn")
public class InterviewRescheduleRequest {

    @NotNull(message = "Ngày giờ mới không được để trống")
    @Schema(description = "Ngày giờ mới (dd/MM/yyyy HH:mm)", example = "25/06/2026 14:00", required = true)
    String interviewDate;

    @Schema(description = "Thời lượng (phút)", example = "60")
    Integer durationMinutes;

    @Schema(description = "Hình thức mới", example = "ONLINE")
    InterviewType interviewType;

    @Schema(description = "Địa điểm mới", example = "Google Meet: https://meet.google.com/xxx")
    String location;

    @Schema(description = "ID người phỏng vấn mới (nếu đổi)")
    Integer interviewerId;

    @Schema(description = "Lý do đổi lịch")
    String reason;
}
