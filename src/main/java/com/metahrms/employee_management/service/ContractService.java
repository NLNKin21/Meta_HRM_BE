package com.metahrms.employee_management.service;

import com.metahrms.employee_management.dto.ContractRequest;
import com.metahrms.employee_management.dto.ContractResponse;
import com.metahrms.employee_management.enums.ContractStatus;
import com.metahrms.employee_management.enums.ContractType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

public interface ContractService {

    ContractResponse create(ContractRequest request);

    ContractResponse update(Long id, ContractRequest request);

    void delete(Long id);

    ContractResponse getById(Long id);

    Page<ContractResponse> getAll(Pageable pageable);

    List<ContractResponse> getByEmployee(Long employeeId);

    List<ContractResponse> getByStatus(ContractStatus status);

    Page<ContractResponse> search(
            String contractNumber,
            ContractStatus status,
            ContractType contractType,
            Pageable pageable
    );

    void autoUpdateExpiredContracts();
}