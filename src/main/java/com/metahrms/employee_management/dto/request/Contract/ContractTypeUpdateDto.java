package com.metahrms.employee_management.dto.request.Contract;

import com.metahrms.employee_management.enums.DurationUnit;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "DTO for updating an existing contract type")
public class ContractTypeUpdateDto {

    @Size(max = 255, message = "Type name must not exceed 255 characters")
    @Schema(description = "Display name for this contract type")
    String typeName;

    @Schema(description = "Description of this contract type")
    String description;

    @Schema(description = "Internal notes for HR")
    String notes;

    @Schema(description = "Duration unit: MONTH, YEAR, or INDEFINITE")
    DurationUnit durationUnit;

    @Schema(description = "Duration value (null if INDEFINITE)")
    Integer durationValue;

    @Schema(description = "Whether uploading a file is required")
    Boolean requireFile;

    @Schema(description = "Template clause text for this contract type")
    String clauseTemplate;

    @Schema(description = "Whether this contract type is active")
    Boolean isActive;
}