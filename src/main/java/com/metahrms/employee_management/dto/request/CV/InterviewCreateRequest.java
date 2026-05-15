package com.metahrms.employee_management.dto.request.CV;


import com.metahrms.employee_management.enums.InterviewType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Tạo lịch phỏng vấn")
public class InterviewCreateRequest {

    @NotNull(message = "ID ứng viên không được để trống")
    @Schema(description = "ID ứng viên", example = "1", required = true)
    Integer candidateId;

    @NotNull(message = "Ngày giờ phỏng vấn không được để trống")
    @Schema(description = "Ngày giờ phỏng vấn (dd/MM/yyyy HH:mm)", example = "20/06/2026 09:00", required = true)
    String interviewDate;

    @Schema(description = "Thời lượng phỏng vấn (phút)", example = "60")
    @Builder.Default
    Integer durationMinutes = 60;

    @Schema(description = "Hình thức: ONLINE / OFFLINE", example = "OFFLINE")
    @Builder.Default
    InterviewType interviewType = InterviewType.OFFLINE;

    @Size(max = 500, message = "Địa điểm tối đa 500 ký tự")
    @Schema(description = "Địa điểm / link meeting", example = "Phòng họp A3, Tầng 5")
    String location;

    @NotNull(message = "ID người phỏng vấn không được để trống")
    @Schema(description = "ID nhân viên phỏng vấn (employeeId)", example = "5", required = true)
    Integer interviewerId;

    @Size(max = 1000, message = "Ghi chú tối đa 1000 ký tự")
    @Schema(description = "Ghi chú cho buổi phỏng vấn")
    String note;
}