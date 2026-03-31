package com.metahrms.employee_management.dto.response.Leave;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerLeaveSummaryDto {
    private long pending;
    private long approved;
    private long rejected;
}