package com.metahrms.employee_management.dto.request.Contract;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Filter parameters for contract type list")
public class ContractTypeFilterDto {

    @Schema(description = "Page number (zero-based)", example = "0")
    Integer page = 0;

    @Schema(description = "Page size", example = "10")
    Integer pageSize = 10;

    @Schema(description = "Search by typeCode or typeName")
    String keyword;

    @Schema(description = "Filter by isActive", example = "true")
    Boolean isActive;
}
