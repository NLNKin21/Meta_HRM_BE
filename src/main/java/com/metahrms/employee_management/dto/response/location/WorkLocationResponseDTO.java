package com.metahrms.employee_management.dto.response.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkLocationResponseDTO {

    private Integer id;
    private String name;
    private String code;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer radius;
    private String description;
    private String contactPerson;
    private String contactPhone;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}