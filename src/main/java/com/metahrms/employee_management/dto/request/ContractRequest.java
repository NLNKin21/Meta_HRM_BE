package com.metahrms.employee_management.dto;

import com.metahrms.employee_management.enums.ContractType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ContractRequest {

    private String contractNumber;
    private Long employeeId;
    private ContractType contractType;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate signingDate;
    private BigDecimal salary;
    private String fileUrl;
    private String terms;
    private String notes;
}