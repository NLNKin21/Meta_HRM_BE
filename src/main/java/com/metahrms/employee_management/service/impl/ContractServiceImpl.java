package com.metahrms.employee_management.service.impl;

import com.metahrms.employee_management.repository.ContractRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.service.ContractService;
import com.metahrms.employee_management.dto.ContractRequest;
import com.metahrms.employee_management.dto.ContractResponse;
import com.metahrms.employee_management.entity.Contract;
import com.metahrms.employee_management.entity.Employee; //
import com.metahrms.employee_management.enums.ContractStatus;
import com.metahrms.employee_management.enums.ContractType;
import com.metahrms.employee_management.mapper.ContractMapper; //

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public ContractResponse create(ContractRequest request) {

        if (contractRepository.existsByContractNumberAndIsDeletedFalse(request.getContractNumber())) {
            throw new IllegalArgumentException("Contract number already exists");
        }

        validateDate(request.getStartDate(), request.getEndDate());

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        contractRepository.findByEmployeeIdAndStatusAndIsDeletedFalse(
                employee.getId(),
                ContractStatus.ACTIVE
        ).ifPresent(c -> {
            throw new IllegalStateException("Employee already has ACTIVE contract");
        });

        Contract contract = Contract.builder()
                .contractNumber(request.getContractNumber())
                .employee(employee)
                .contractType(request.getContractType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .signingDate(request.getSigningDate())
                .salary(request.getSalary())
                .fileUrl(request.getFileUrl())
                .terms(request.getTerms())
                .notes(request.getNotes())
                .status(ContractStatus.ACTIVE)
                .build();

        updateStatusIfExpired(contract);

        return ContractMapper.toResponse(contractRepository.save(contract));
    }

    @Override
    public ContractResponse update(Long id, ContractRequest request) {

        Contract contract = contractRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found"));

        validateDate(request.getStartDate(), request.getEndDate());

        contract.setStartDate(request.getStartDate());
        contract.setEndDate(request.getEndDate());
        contract.setSigningDate(request.getSigningDate());
        contract.setSalary(request.getSalary());
        contract.setContractType(request.getContractType());
        contract.setFileUrl(request.getFileUrl());
        contract.setTerms(request.getTerms());
        contract.setNotes(request.getNotes());

        updateStatusIfExpired(contract);

        return ContractMapper.toResponse(contractRepository.save(contract));
    }

    @Override
    public void delete(Long id) {
        Contract contract = contractRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found"));

        contract.setIsDeleted(true);
        contract.setStatus(ContractStatus.TERMINATED);

        contractRepository.save(contract);
    }

    @Override
    public ContractResponse getById(Long id) {
        return ContractMapper.toResponse(
                contractRepository.findByIdAndIsDeletedFalse(id)
                        .orElseThrow(() -> new IllegalArgumentException("Contract not found"))
        );
    }

    @Override
    public Page<ContractResponse> getAll(Pageable pageable) {
        return contractRepository.findByIsDeletedFalse(pageable)
                .map(ContractMapper::toResponse);
    }

    @Override
    public List<ContractResponse> getByEmployee(Long employeeId) {
        return contractRepository
                .findByEmployeeIdAndIsDeletedFalseOrderByStartDateDesc(employeeId)
                .stream()
                .map(ContractMapper::toResponse)
                .toList();
    }

    @Override
    public List<ContractResponse> getByStatus(ContractStatus status) {
        return contractRepository.findByStatusAndIsDeletedFalse(status)
                .stream()
                .map(ContractMapper::toResponse)
                .toList();
    }

    @Override
    public Page<ContractResponse> search(String contractNumber,
                                         ContractStatus status,
                                         ContractType contractType,
                                         Pageable pageable) {
        return contractRepository.searchContracts(
                contractNumber,
                status,
                contractType,
                pageable
        ).map(ContractMapper::toResponse);
    }

    @Override
    public void autoUpdateExpiredContracts() {

        List<Contract> expiredContracts =
                contractRepository.findAllExpired(LocalDate.now());

        for (Contract contract : expiredContracts) {
            contract.setStatus(ContractStatus.EXPIRED);
        }

        contractRepository.saveAll(expiredContracts);
    }

    private void validateDate(LocalDate start, LocalDate end) {
        if (start == null)
            throw new IllegalArgumentException("Start date is required");

        if (end != null && end.isBefore(start))
            throw new IllegalArgumentException("End date must be after start date");
    }

    private void updateStatusIfExpired(Contract contract) {
        if (contract.getEndDate() != null &&
                contract.getEndDate().isBefore(LocalDate.now())) {
            contract.setStatus(ContractStatus.EXPIRED);
        }
    }
}