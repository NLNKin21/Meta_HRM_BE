package com.metahrms.employee_management.dto.request.CV;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Duyệt ứng viên và tự động tạo tài khoản")
public class CandidateApproveRequest {

    @Schema(description = "Ghi chú khi duyệt")
    String note;

    @Schema(description = "Role cho user mới (mặc định EMPLOYEE)", example = "EMPLOYEE")
    @Builder.Default
    String userRole = "EMPLOYEE";
}