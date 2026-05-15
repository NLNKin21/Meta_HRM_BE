package com.metahrms.employee_management.dto.request.CV;


import com.metahrms.employee_management.enums.InterviewResult;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Đánh giá kết quả phỏng vấn")
public class InterviewUpdateResultRequest {

    @NotNull(message = "Kết quả phỏng vấn không được để trống")
    @Schema(description = "Kết quả: PASSED / FAILED", example = "PASSED", required = true)
    InterviewResult result;

    @Min(value = 1, message = "Điểm tối thiểu là 1")
    @Max(value = 10, message = "Điểm tối đa là 10")
    @Schema(description = "Điểm đánh giá (1-10)", example = "8")
    Integer score;

    @Schema(description = "Nhận xét chung")
    String feedback;

    @Schema(description = "Điểm mạnh")
    String strengths;

    @Schema(description = "Điểm yếu / cần cải thiện")
    String weaknesses;

    @Schema(description = "Ghi chú thêm")
    String note;
}