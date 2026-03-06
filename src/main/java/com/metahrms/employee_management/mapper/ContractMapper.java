package com.metahrms.employee_management.mapper;

import com.metahrms.employee_management.dto.ContractRequest;
import com.metahrms.employee_management.dto.ContractResponse;
import com.metahrms.employee_management.entity.Contract;

public class ContractMapper {

    public static ContractResponse toResponse(Contract contract) {
        return ContractResponse.builder()
                .id(contract.getId())
                .contractNumber(contract.getContractNumber())
                .employeeId(contract.getEmployee().getId())
                .contractType(contract.getContractType())
                .startDate(contract.getStartDate())
                .endDate(contract.getEndDate())
                .signingDate(contract.getSigningDate())
                .salary(contract.getSalary())
                .fileUrl(contract.getFileUrl())
                .status(contract.getStatus())
                .terms(contract.getTerms())
                .notes(contract.getNotes())
                .build();
    }
}