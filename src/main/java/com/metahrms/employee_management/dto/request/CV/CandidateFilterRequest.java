package com.metahrms.employee_management.dto.request.CV;


import com.metahrms.employee_management.enums.CandidateStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Bộ lọc danh sách ứng viên cho HR")
public class CandidateFilterRequest {

    @Schema(description = "Lọc theo trạng thái")
    CandidateStatus status;

    @Schema(description = "Lọc theo phòng ban")
    Integer departmentId;

    @Schema(description = "Tìm kiếm theo tên, email, vị trí")
    String keyword;

    @Schema(description = "Trang (0-based)", example = "0")
    @Builder.Default
    Integer page = 0;

    @Schema(description = "Số bản ghi / trang", example = "20")
    @Builder.Default
    Integer size = 20;
}
