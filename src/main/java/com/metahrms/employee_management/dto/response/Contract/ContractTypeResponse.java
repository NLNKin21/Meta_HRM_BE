package com.metahrms.employee_management.dto.response.Contract;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.metahrms.employee_management.enums.DurationUnit;

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
@Schema(description = "Response object for contract type")
public class ContractTypeResponse {

    @Schema(description = "Unique ID", example = "1")
    Integer id;

    @Schema(description = "Unique code", example = "FULL_TIME")
    String typeCode;

    @Schema(description = "Display name", example = "Hợp đồng lao động toàn thời gian")
    String typeName;

    @Schema(description = "Description")
    String description;

    @Schema(description = "Internal notes")
    String notes;

    @Schema(description = "Duration unit", example = "YEAR")
    DurationUnit durationUnit;

    @Schema(description = "Duration value", example = "3")
    Integer durationValue;

    // Label tổng hợp cho UI: "3 năm" / "2 tháng" / "Không xác định"
    @Schema(description = "Human-readable duration label", example = "3 năm")
    String durationLabel;

    @Schema(description = "Whether file upload is required", example = "true")
    Boolean requireFile;

    @Schema(description = "Template clause text")
    String clauseTemplate;

    @Schema(description = "Whether this type is active", example = "true")
    Boolean isActive;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(description = "Creation timestamp")
    LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(description = "Last updated timestamp")
    LocalDateTime updatedAt;

    @Schema(description = "ID of user who created", example = "5")
    Integer createdBy;

    @Schema(description = "ID of user who last updated", example = "5")
    Integer updatedBy;

    // Custom Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private String typeCode;
        private String typeName;
        private String description;
        private String notes;
        private DurationUnit durationUnit;
        private Integer durationValue;
        private String durationLabel;
        private Boolean requireFile;
        private String clauseTemplate;
        private Boolean isActive;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Integer createdBy;
        private Integer updatedBy;

        public Builder id(Integer id) { this.id = id; return this; }
        public Builder typeCode(String typeCode) { this.typeCode = typeCode; return this; }
        public Builder typeName(String typeName) { this.typeName = typeName; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder durationUnit(DurationUnit durationUnit) { this.durationUnit = durationUnit; return this; }
        public Builder durationValue(Integer durationValue) { this.durationValue = durationValue; return this; }
        public Builder durationLabel(String durationLabel) { this.durationLabel = durationLabel; return this; }
        public Builder requireFile(Boolean requireFile) { this.requireFile = requireFile; return this; }
        public Builder clauseTemplate(String clauseTemplate) { this.clauseTemplate = clauseTemplate; return this; }
        public Builder isActive(Boolean isActive) { this.isActive = isActive; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder createdBy(Integer createdBy) { this.createdBy = createdBy; return this; }
        public Builder updatedBy(Integer updatedBy) { this.updatedBy = updatedBy; return this; }

        public ContractTypeResponse build() {
            ContractTypeResponse r = new ContractTypeResponse();
            r.id             = this.id;
            r.typeCode       = this.typeCode;
            r.typeName       = this.typeName;
            r.description    = this.description;
            r.notes          = this.notes;
            r.durationUnit   = this.durationUnit;
            r.durationValue  = this.durationValue;
            r.durationLabel  = this.durationLabel;
            r.requireFile    = this.requireFile;
            r.clauseTemplate = this.clauseTemplate;
            r.isActive       = this.isActive;
            r.createdAt      = this.createdAt;
            r.updatedAt      = this.updatedAt;
            r.createdBy      = this.createdBy;
            r.updatedBy      = this.updatedBy;
            return r;
        }
    }
}