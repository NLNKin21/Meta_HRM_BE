package com.metahrms.employee_management.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metahrms.employee_management.dto.request.Contract.ContractTypeCreateDto;
import com.metahrms.employee_management.dto.request.Contract.ContractTypeFilterDto;
import com.metahrms.employee_management.dto.request.Contract.ContractTypeUpdateDto;
import com.metahrms.employee_management.dto.response.PagedResponse;
import com.metahrms.employee_management.dto.response.Contract.ContractTypeResponse;
import com.metahrms.employee_management.entity.ContractType;
import com.metahrms.employee_management.enums.DurationUnit;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.repository.ContractTypeRepository;
import com.metahrms.employee_management.specification.ContractTypeSpecification;
import com.metahrms.employee_management.util.SecurityUtils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ContractTypeService {

    ContractTypeRepository contractTypeRepository;

    // ─────────────────────────────────────────────────────────────
    // GET LIST (phân trang + filter)
    // ─────────────────────────────────────────────────────────────
    public PagedResponse<ContractTypeResponse> getContractTypes(ContractTypeFilterDto filterDto) {
        log.info("Fetching contract types - keyword: {}, isActive: {}",
                filterDto.getKeyword(), filterDto.getIsActive());

        Specification<ContractType> spec = ContractTypeSpecification.filter(
                filterDto.getKeyword(),
                filterDto.getIsActive()
        );

        Pageable pageable = PageRequest.of(
                filterDto.getPage(),
                filterDto.getPageSize(),
                Sort.by(Sort.Direction.ASC, "typeName")
        );

        Page<ContractType> page = contractTypeRepository.findAll(spec, pageable);

        List<ContractTypeResponse> content = page.getContent()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return PagedResponse.<ContractTypeResponse>builder()
                .content(content)
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    // ─────────────────────────────────────────────────────────────
    // GET ALL ACTIVE (dùng cho dropdown)
    // ─────────────────────────────────────────────────────────────
    public List<ContractTypeResponse> getAllActiveTypes() {
        return contractTypeRepository.findAllActiveTypes()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────
    // GET BY ID
    // ─────────────────────────────────────────────────────────────
    public ContractTypeResponse getContractTypeById(Integer id) {
        log.info("Fetching contract type id: {}", id);
        ContractType contractType = findByIdOrThrow(id);
        return toResponse(contractType);
    }

    // ─────────────────────────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────────────────────────
    @Transactional
    public ContractTypeResponse createContractType(ContractTypeCreateDto createDto) {
        log.info("Creating contract type: {}", createDto.getTypeCode());

        // Validate typeCode unique
        if (contractTypeRepository.existsByTypeCodeAndIsDeletedFalse(
                createDto.getTypeCode().toUpperCase())) {
            throw new IllegalArgumentException(
                "Contract type code already exists: " + createDto.getTypeCode());
        }

        // Validate durationValue khi không phải INDEFINITE
        validateDuration(createDto.getDurationUnit(), createDto.getDurationValue());

        Integer currentUserId = SecurityUtils.getCurrentUserId();

        ContractType contractType = ContractType.builder()
                .typeCode(createDto.getTypeCode().toUpperCase().trim())
                .typeName(createDto.getTypeName().trim())
                .description(createDto.getDescription())
                .notes(createDto.getNotes())
                .durationUnit(createDto.getDurationUnit())
                .durationValue(
                    createDto.getDurationUnit() == DurationUnit.INDEFINITE
                        ? null
                        : createDto.getDurationValue()
                )
                .requireFile(
                    createDto.getRequireFile() != null
                        ? createDto.getRequireFile()
                        : true
                )
                .clauseTemplate(createDto.getClauseTemplate())
                .isActive(
                    createDto.getIsActive() != null
                        ? createDto.getIsActive()
                        : true
                )
                .createdBy(currentUserId)
                .updatedBy(currentUserId)
                .build();

        ContractType saved = contractTypeRepository.save(contractType);
        log.info("Contract type created: id={}, code={}", saved.getId(), saved.getTypeCode());
        return toResponse(saved);
    }

    // ─────────────────────────────────────────────────────────────
    // UPDATE
    // ─────────────────────────────────────────────────────────────
    @Transactional
    public ContractTypeResponse updateContractType(Integer id, ContractTypeUpdateDto updateDto) {
        log.info("Updating contract type id: {}", id);

        ContractType contractType = findByIdOrThrow(id);

        // Validate duration nếu đổi
        DurationUnit newUnit = updateDto.getDurationUnit() != null
                ? updateDto.getDurationUnit()
                : contractType.getDurationUnit();
        Integer newValue = updateDto.getDurationValue() != null
                ? updateDto.getDurationValue()
                : contractType.getDurationValue();
        validateDuration(newUnit, newValue);

        // Áp dụng các thay đổi (chỉ update field không null)
        if (updateDto.getTypeName() != null) {
            contractType.setTypeName(updateDto.getTypeName().trim());
        }
        if (updateDto.getDescription() != null) {
            contractType.setDescription(updateDto.getDescription());
        }
        if (updateDto.getNotes() != null) {
            contractType.setNotes(updateDto.getNotes());
        }
        if (updateDto.getDurationUnit() != null) {
            contractType.setDurationUnit(updateDto.getDurationUnit());
        }
        if (updateDto.getDurationUnit() == DurationUnit.INDEFINITE) {
            contractType.setDurationValue(null);
        } else if (updateDto.getDurationValue() != null) {
            contractType.setDurationValue(updateDto.getDurationValue());
        }
        if (updateDto.getRequireFile() != null) {
            contractType.setRequireFile(updateDto.getRequireFile());
        }
        if (updateDto.getClauseTemplate() != null) {
            contractType.setClauseTemplate(updateDto.getClauseTemplate());
        }
        if (updateDto.getIsActive() != null) {
            contractType.setIsActive(updateDto.getIsActive());
        }

        contractType.setUpdatedBy(SecurityUtils.getCurrentUserId());

        ContractType updated = contractTypeRepository.save(contractType);
        log.info("Contract type updated: id={}", updated.getId());
        return toResponse(updated);
    }

    // ─────────────────────────────────────────────────────────────
    // TOGGLE ACTIVE
    // ─────────────────────────────────────────────────────────────
    @Transactional
    public ContractTypeResponse toggleActive(Integer id) {
        log.info("Toggling active status for contract type id: {}", id);

        ContractType contractType = findByIdOrThrow(id);
        contractType.setIsActive(!contractType.getIsActive());
        contractType.setUpdatedBy(SecurityUtils.getCurrentUserId());

        ContractType updated = contractTypeRepository.save(contractType);
        log.info("Contract type id={} isActive={}", updated.getId(), updated.getIsActive());
        return toResponse(updated);
    }

    // ─────────────────────────────────────────────────────────────
    // DELETE (soft delete)
    // ─────────────────────────────────────────────────────────────
    @Transactional
    public void deleteContractType(Integer id) {
        log.info("Deleting contract type id: {}", id);

        ContractType contractType = findByIdOrThrow(id);

        // Không cho xóa nếu đang được dùng
        if (contractTypeRepository.isUsedInContracts(id)) {
            throw new IllegalStateException(
                "Cannot delete contract type that is already used in contracts. " +
                "Please deactivate it instead.");
        }

        contractType.setIsDeleted(true);
        contractType.setIsActive(false);
        contractType.setUpdatedBy(SecurityUtils.getCurrentUserId());

        contractTypeRepository.save(contractType);
        log.info("Contract type soft deleted: id={}", id);
    }

    // ─────────────────────────────────────────────────────────────
    // HELPER: findByIdOrThrow
    // ─────────────────────────────────────────────────────────────
    private ContractType findByIdOrThrow(Integer id) {
        ContractType ct = contractTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Contract type not found with id: " + id));

        if (Boolean.TRUE.equals(ct.getIsDeleted())) {
            throw new ResourceNotFoundException(
                "Contract type has been deleted: id=" + id);
        }
        return ct;
    }

    // ─────────────────────────────────────────────────────────────
    // HELPER: validateDuration
    // ─────────────────────────────────────────────────────────────
    private void validateDuration(DurationUnit unit, Integer value) {
        if (unit == null) return;
        if (unit != DurationUnit.INDEFINITE && (value == null || value <= 0)) {
            throw new IllegalArgumentException(
                "Duration value must be a positive number when unit is " + unit);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // HELPER: toResponse + buildDurationLabel
    // ─────────────────────────────────────────────────────────────
    private ContractTypeResponse toResponse(ContractType ct) {
        return ContractTypeResponse.builder()
                .id(ct.getId())
                .typeCode(ct.getTypeCode())
                .typeName(ct.getTypeName())
                .description(ct.getDescription())
                .notes(ct.getNotes())
                .durationUnit(ct.getDurationUnit())
                .durationValue(ct.getDurationValue())
                .durationLabel(buildDurationLabel(ct.getDurationUnit(), ct.getDurationValue()))
                .requireFile(ct.getRequireFile())
                .clauseTemplate(ct.getClauseTemplate())
                .isActive(ct.getIsActive())
                .createdAt(ct.getCreatedAt())
                .updatedAt(ct.getUpdatedAt())
                .createdBy(ct.getCreatedBy())
                .updatedBy(ct.getUpdatedBy())
                .build();
    }

    private String buildDurationLabel(DurationUnit unit, Integer value) {
        if (unit == null) return "";
        return switch (unit) {
            case INDEFINITE -> "Không xác định";
            case MONTH -> value + " tháng";
            case YEAR -> value + " năm";
        };
    }
}