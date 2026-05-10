package com.metahrms.employee_management.entity;

import com.metahrms.employee_management.enums.DurationUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contract_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContractType extends BaseEntity {

    @Column(name = "type_code", nullable = false, unique = true, length = 50)
    private String typeCode;

    @Column(name = "type_name", nullable = false, length = 255)
    private String typeName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration_unit", nullable = false, length = 20)
    private DurationUnit durationUnit;

    // NULL nếu durationUnit = INDEFINITE
    @Column(name = "duration_value")
    private Integer durationValue;

    @Column(name = "require_file", nullable = false)
    private Boolean requireFile = true;

    @Column(name = "clause_template", columnDefinition = "TEXT")
    private String clauseTemplate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

    // Custom Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String typeCode;
        private String typeName;
        private String description;
        private String notes;
        private DurationUnit durationUnit;
        private Integer durationValue;
        private Boolean requireFile = true;
        private String clauseTemplate;
        private Boolean isActive = true;
        private Integer createdBy;
        private Integer updatedBy;

        public Builder typeCode(String typeCode) {
            this.typeCode = typeCode;
            return this;
        }

        public Builder typeName(String typeName) {
            this.typeName = typeName;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder durationUnit(DurationUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        public Builder durationValue(Integer durationValue) {
            this.durationValue = durationValue;
            return this;
        }

        public Builder requireFile(Boolean requireFile) {
            this.requireFile = requireFile;
            return this;
        }

        public Builder clauseTemplate(String clauseTemplate) {
            this.clauseTemplate = clauseTemplate;
            return this;
        }

        public Builder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder createdBy(Integer createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder updatedBy(Integer updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public ContractType build() {
            ContractType ct = new ContractType();
            ct.typeCode      = this.typeCode;
            ct.typeName      = this.typeName;
            ct.description   = this.description;
            ct.notes         = this.notes;
            ct.durationUnit  = this.durationUnit;
            ct.durationValue = this.durationValue;
            ct.requireFile   = this.requireFile;
            ct.clauseTemplate = this.clauseTemplate;
            ct.isActive      = this.isActive;
            ct.createdBy     = this.createdBy;
            ct.updatedBy     = this.updatedBy;
            ct.setIsDeleted(false);
            return ct;
        }
    }
}