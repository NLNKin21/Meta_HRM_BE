package com.metahrms.employee_management.entity.Payroll;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

import com.metahrms.employee_management.entity.BaseEntity;

@Entity
@Table(name = "payroll_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class PayrollConfig extends BaseEntity {

    @Column(name = "config_key", nullable = false, unique = true, length = 100)
    private String configKey;

    @Column(name = "config_value", nullable = false, precision = 15, scale = 4)
    private BigDecimal configValue;

    @Column(name = "config_group", nullable = false, length = 50)
    private String configGroup;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "updated_by")
    private Integer updatedBy;
}