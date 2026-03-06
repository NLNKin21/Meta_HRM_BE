package com.metahrms.employee_management.dto;

import com.metahrms.employee_management.enums.ContractStatus;
import com.metahrms.employee_management.enums.ContractType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ContractResponse {

    private Long id;
    private String contractNumber;
    private Long employeeId;
    private ContractType contractType;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate signingDate;
    private BigDecimal salary;
    private String fileUrl;
    private ContractStatus status;
    private String terms;
    private String notes;
}