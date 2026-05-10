package com.metahrms.employee_management.dto.request.Contract;

import com.metahrms.employee_management.enums.DurationUnit;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO for creating a new contract type")
public class ContractTypeCreateDto {

    @NotBlank(message = "Type code is required")
    @Size(max = 50, message = "Type code must not exceed 50 characters")
    @Schema(description = "Unique code for this contract type", example = "FULL_TIME", required = true)
    String typeCode;

    @NotBlank(message = "Type name is required")
    @Size(max = 255, message = "Type name must not exceed 255 characters")
    @Schema(description = "Display name for this contract type", 
            example = "Hợp đồng lao động toàn thời gian", required = true)
    String typeName;

    @Schema(description = "Description of this contract type")
    String description;

    @Schema(description = "Internal notes for HR")
    String notes;

    @NotNull(message = "Duration unit is required")
    @Schema(description = "Duration unit: MONTH, YEAR, or INDEFINITE", 
            example = "YEAR", required = true)
    DurationUnit durationUnit;

    @Schema(description = "Duration value (null if INDEFINITE)", example = "3")
    Integer durationValue;

    @Schema(description = "Whether uploading a file is required", example = "true")
    Boolean requireFile = true;

    @Schema(description = "Template clause text for this contract type")
    String clauseTemplate;

    @Schema(description = "Whether this contract type is active", example = "true")
    Boolean isActive = true;
}