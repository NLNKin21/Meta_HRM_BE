package com.metahrms.employee_management.service.impl;

import com.metahrms.employee_management.dto.request.location.CreateWorkLocationRequest;
import com.metahrms.employee_management.dto.request.location.UpdateWorkLocationRequest;
import com.metahrms.employee_management.dto.response.location.WorkLocationResponseDTO;
import com.metahrms.employee_management.entity.Attendance.WorkLocation;
import com.metahrms.employee_management.repository.Attendance.WorkLocationRepository;
import com.metahrms.employee_management.service.WorkLocationService;
import com.metahrms.employee_management.util.SecurityUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkLocationServiceImpl implements WorkLocationService {

    private final WorkLocationRepository workLocationRepository;

    @Override
    @Transactional
    public WorkLocationResponseDTO create(CreateWorkLocationRequest request) {
        log.info("[LOCATION] Creating location: name={}, code={}", request.getName(), request.getCode());

        // Validate unique code
        // Dùng method mới existsByCodeAndIsDeletedFalse (không đụng existsByCode cũ)
        if (request.getCode() != null && !request.getCode().isBlank()) {
            if (workLocationRepository.existsByCodeAndIsDeletedFalse(request.getCode())) {
                throw new IllegalArgumentException("Location code already exists: " + request.getCode());
            }
        }

        WorkLocation location = new WorkLocation();
        location.setName(request.getName());
        location.setCode(request.getCode());
        location.setAddress(request.getAddress());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setRadius(request.getRadius() != null ? request.getRadius() : 100);
        location.setDescription(request.getDescription());
        location.setContactPerson(request.getContactPerson());
        location.setContactPhone(request.getContactPhone());
        location.setIsActive(true);
        location.setCreatedBy(SecurityUtils.getCurrentUserId());

        WorkLocation saved = workLocationRepository.save(location);
        log.info("[LOCATION] Created location id={}", saved.getId());

        return toDTO(saved);
    }

    @Override
    @Transactional
    public WorkLocationResponseDTO update(Integer id, UpdateWorkLocationRequest request) {
        log.info("[LOCATION] Updating location id={}", id);

        // Dùng findByIdAndIsDeletedFalse (method mới) thay vì findById
        // → findById vẫn hoạt động nhưng có thể trả về record đã xoá
        WorkLocation location = workLocationRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Location not found: " + id));

        // Validate unique code khi thay đổi
        if (request.getCode() != null && !request.getCode().isBlank()) {
            // Dùng existsByCodeAndIdNotAndIsDeletedFalse (method mới)
            // thay vì existsByCodeAndIdNot cũ (cũ không check isDeleted)
            if (workLocationRepository.existsByCodeAndIdNotAndIsDeletedFalse(request.getCode(), id)) {
                throw new IllegalArgumentException("Location code already exists: " + request.getCode());
            }
            location.setCode(request.getCode());
        }

        // Chỉ update field != null (partial update)
        if (request.getName() != null) location.setName(request.getName());
        if (request.getAddress() != null) location.setAddress(request.getAddress());
        if (request.getLatitude() != null) location.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) location.setLongitude(request.getLongitude());
        if (request.getRadius() != null) location.setRadius(request.getRadius());
        if (request.getDescription() != null) location.setDescription(request.getDescription());
        if (request.getContactPerson() != null) location.setContactPerson(request.getContactPerson());
        if (request.getContactPhone() != null) location.setContactPhone(request.getContactPhone());
        if (request.getIsActive() != null) location.setIsActive(request.getIsActive());

        location.setUpdatedBy(SecurityUtils.getCurrentUserId());

        WorkLocation saved = workLocationRepository.save(location);
        log.info("[LOCATION] Updated location id={}", saved.getId());

        return toDTO(saved);
    }

    @Override
    @Transactional
    public void softDelete(Integer id) {
        log.info("[LOCATION] Soft deleting location id={}", id);

        WorkLocation location = workLocationRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Location not found: " + id));

        // Kiểm tra có attendance record nào đang dùng location này không
        long attendanceCount = workLocationRepository.countAttendanceByLocationId(id);
        if (attendanceCount > 0) {
            // Không block xoá, chỉ deactivate
            // → Lý do: attendance record cũ vẫn cần giữ locationId
            log.warn("[LOCATION] Location id={} has {} attendance records, deactivating only", 
                id, attendanceCount);
        }

        // BaseEntity.isDeleted = true
        location.setIsDeleted(true);
        location.setIsActive(false);
        location.setUpdatedBy(SecurityUtils.getCurrentUserId());

        workLocationRepository.save(location);
        log.info("[LOCATION] Soft deleted location id={}", id);
    }

    @Override
    public WorkLocationResponseDTO getById(Integer id) {
        WorkLocation location = workLocationRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Location not found: " + id));
        return toDTO(location);
    }

    @Override
    public Page<WorkLocationResponseDTO> getAll(Boolean isActive, String keyword, Pageable pageable) {
        return workLocationRepository.findAllWithFilters(isActive, keyword, pageable)
                .map(this::toDTO);
    }

    @Override
    @Transactional
    public void activate(Integer id) {
        log.info("[LOCATION] Activating location id={}", id);
        WorkLocation location = workLocationRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Location not found: " + id));
        location.setIsActive(true);
        location.setUpdatedBy(SecurityUtils.getCurrentUserId());
        workLocationRepository.save(location);
    }

    @Override
    @Transactional
    public void deactivate(Integer id) {
        log.info("[LOCATION] Deactivating location id={}", id);
        WorkLocation location = workLocationRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Location not found: " + id));
        location.setIsActive(false);
        location.setUpdatedBy(SecurityUtils.getCurrentUserId());
        workLocationRepository.save(location);
    }

    // ============================================
    // HELPER
    // ============================================

    private WorkLocationResponseDTO toDTO(WorkLocation entity) {
        return WorkLocationResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .address(entity.getAddress())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .radius(entity.getRadius())
                .description(entity.getDescription())
                .contactPerson(entity.getContactPerson())
                .contactPhone(entity.getContactPhone())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}