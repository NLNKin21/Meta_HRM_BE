package com.metahrms.employee_management.service.task;


import com.metahrms.employee_management.dto.request.task.taskstatus.TaskStatusCreateRequest;
import com.metahrms.employee_management.dto.request.task.taskstatus.TaskStatusUpdateRequest;
import com.metahrms.employee_management.dto.response.task.task.TaskStatusResponse;
import com.metahrms.employee_management.entity.Department;
import com.metahrms.employee_management.entity.Task.TaskStatus;
import com.metahrms.employee_management.exception.BusinessException;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.mapper.task.TaskStatusMapper;
import com.metahrms.employee_management.repository.DepartmentRepository;
import com.metahrms.employee_management.repository.Task.TaskStatusRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final DepartmentRepository departmentRepository;
    private final TaskStatusMapper taskStatusMapper;

    /**
     * Lấy tất cả task status active
     */
    @Transactional(readOnly = true)
    public List<TaskStatusResponse> getAllActiveStatuses() {
        log.info("Getting all active task statuses");
        return taskStatusRepository.findAllActiveOrderByIndex()
            .stream()
            .map(taskStatusMapper::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Lấy task status theo department (bao gồm common)
     */
    @Transactional(readOnly = true)
    public List<TaskStatusResponse> getStatusesByDepartment(Integer departmentId) {
        log.info("Getting task statuses for department: {}", departmentId);
        return taskStatusRepository.findByDepartmentIdOrCommon(departmentId)
            .stream()
            .map(taskStatusMapper::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Lấy task status theo ID
     */
    @Transactional(readOnly = true)
    public TaskStatusResponse getStatusById(Integer id) {
        log.info("Getting task status by ID: {}", id);
        TaskStatus taskStatus = findStatusByIdOrThrow(id);
        return taskStatusMapper.toResponse(taskStatus);
    }

    /**
     * Lấy default status
     */
    @Transactional(readOnly = true)
    public TaskStatus getDefaultStatus() {
        return taskStatusRepository.findDefaultStatus()
            .orElseThrow(() -> new BusinessException("No default status found"));
    }

    /**
     * Lấy default status theo department
     */
    @Transactional(readOnly = true)
    public TaskStatus getDefaultStatusByDepartment(Integer departmentId) {
        return taskStatusRepository.findDefaultStatusByDepartment(departmentId)
            .orElseGet(this::getDefaultStatus);
    }

    /**
     * Tạo task status mới
     */
    @Transactional
    public TaskStatusResponse createStatus(TaskStatusCreateRequest request) {
        log.info("Creating new task status: {}", request.getStatusName());

        // Validate department nếu có
        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
        }

        // Check duplicate name
        if (taskStatusRepository.existsByNameInDepartment(request.getStatusName(), request.getDepartmentId())) {
            throw new BusinessException("Status name already exists in this department");
        }

        // Build entity
        TaskStatus taskStatus = TaskStatus.builder()
            .statusName(request.getStatusName())
            .statusNameEn(request.getStatusNameEn())
            .orderIndex(request.getOrderIndex())
            .color(request.getColor() != null ? request.getColor() : "#1976d2")
            .icon(request.getIcon())
            .isCompleted(request.getIsCompleted() != null ? request.getIsCompleted() : false)
            .isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
            .department(department)
            .isActive(true)
            .build();

        // Nếu set là default, reset các default khác
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            resetDefaultStatus(request.getDepartmentId());
        }

        TaskStatus saved = taskStatusRepository.save(taskStatus);
        log.info("Created task status with ID: {}", saved.getId());

        return taskStatusMapper.toResponse(saved);
    }

    /**
     * Cập nhật task status
     */
    @Transactional
    public TaskStatusResponse updateStatus(Integer id, TaskStatusUpdateRequest request) {
        log.info("Updating task status ID: {}", id);

        TaskStatus taskStatus = findStatusByIdOrThrow(id);

        // Update fields nếu có
        if (request.getStatusName() != null) {
            taskStatus.setStatusName(request.getStatusName());
        }
        if (request.getStatusNameEn() != null) {
            taskStatus.setStatusNameEn(request.getStatusNameEn());
        }
        if (request.getOrderIndex() != null) {
            taskStatus.setOrderIndex(request.getOrderIndex());
        }
        if (request.getColor() != null) {
            taskStatus.setColor(request.getColor());
        }
        if (request.getIcon() != null) {
            taskStatus.setIcon(request.getIcon());
        }
        if (request.getIsCompleted() != null) {
            taskStatus.setIsCompleted(request.getIsCompleted());
        }
        if (request.getIsDefault() != null) {
            if (Boolean.TRUE.equals(request.getIsDefault())) {
                resetDefaultStatus(taskStatus.getDepartment() != null ? 
                    taskStatus.getDepartment().getId() : null);
            }
            taskStatus.setIsDefault(request.getIsDefault());
        }
        if (request.getIsActive() != null) {
            taskStatus.setIsActive(request.getIsActive());
        }

        TaskStatus saved = taskStatusRepository.save(taskStatus);
        log.info("Updated task status ID: {}", id);

        return taskStatusMapper.toResponse(saved);
    }

    /**
     * Xóa task status (soft delete)
     */
    @Transactional
    public void deleteStatus(Integer id) {
        log.info("Deleting task status ID: {}", id);

        TaskStatus taskStatus = findStatusByIdOrThrow(id);
        
        // Check if status has tasks
        if (!taskStatus.getTasks().isEmpty()) {
            throw new BusinessException("Cannot delete status that has tasks assigned");
        }

        taskStatus.setIsActive(false);
        taskStatusRepository.save(taskStatus);
        log.info("Deleted task status ID: {}", id);
    }

    // ========== HELPER METHODS ==========

    private TaskStatus findStatusByIdOrThrow(Integer id) {
        return taskStatusRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("TaskStatus", "id", id));
    }

    private void resetDefaultStatus(Integer departmentId) {
        List<TaskStatus> currentDefaults = taskStatusRepository.findByDepartmentIdOrCommon(departmentId)
            .stream()
            .filter(TaskStatus::getIsDefault)
            .collect(Collectors.toList());

        for (TaskStatus status : currentDefaults) {
            status.setIsDefault(false);
            taskStatusRepository.save(status);
        }
    }
}