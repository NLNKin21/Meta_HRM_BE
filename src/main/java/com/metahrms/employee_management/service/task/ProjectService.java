package com.metahrms.employee_management.service.task;

import com.metahrms.employee_management.dto.request.task.project.ProjectCreateRequest;
import com.metahrms.employee_management.dto.request.task.project.ProjectUpdateRequest;
import com.metahrms.employee_management.dto.response.task.project.ProjectResponse;
import com.metahrms.employee_management.entity.Department;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.Task.Project;
import com.metahrms.employee_management.enums.Task.ProjectStatus;
import com.metahrms.employee_management.exception.BusinessException;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.mapper.task.ProjectMapper;
import com.metahrms.employee_management.repository.DepartmentRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.Task.ProjectRepository;
import com.metahrms.employee_management.util.TaskCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectMapper projectMapper;
    private final TaskCodeGenerator codeGenerator;

    /**
     * Lấy tất cả projects active
     */
    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllActiveProjects() {
        log.info("Getting all active projects");
        return projectRepository.findAllActive()
            .stream()
            .map(projectMapper::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Lấy projects theo department
     */
    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjectsByDepartment(Integer departmentId) {
        log.info("Getting projects for department: {}", departmentId);
        return projectRepository.findByDepartmentId(departmentId)
            .stream()
            .map(projectMapper::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Lấy projects theo manager
     */
    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjectsByManager(Integer managerId) {
        log.info("Getting projects for manager: {}", managerId);
        return projectRepository.findByManagerId(managerId)
            .stream()
            .map(projectMapper::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Lấy project theo ID
     */
    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Integer id) {
        log.info("Getting project by ID: {}", id);
        Project project = findProjectByIdOrThrow(id);
        ProjectResponse response = projectMapper.toResponse(project);
        
        // Add task counts
        response.setTaskCount(project.getTasks().size());
        response.setCompletedTaskCount(
            (int) project.getTasks().stream()
                .filter(task -> task.getStatus().getIsCompleted())
                .count()
        );
        
        return response;
    }

    /**
     * Lấy project theo code
     */
    @Transactional(readOnly = true)
    public ProjectResponse getProjectByCode(String projectCode) {
        log.info("Getting project by code: {}", projectCode);
        Project project = projectRepository.findByProjectCode(projectCode)
            .orElseThrow(() -> new ResourceNotFoundException("Project", "code", projectCode));
        return projectMapper.toResponse(project);
    }

    /**
     * Tạo project mới
     */
    @Transactional
    public ProjectResponse createProject(ProjectCreateRequest request, Integer createdBy) {
        log.info("Creating new project: {}", request.getProjectName());

        // Validate department
        Department department = departmentRepository.findById(request.getDepartmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

        // Validate manager nếu có
        Employee manager = null;
        if (request.getManagerId() != null) {
            manager = employeeRepository.findById(request.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getManagerId()));
        }

        // Validate dates
        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (request.getEndDate().isBefore(request.getStartDate())) {
                throw new BusinessException("End date cannot be before start date");
            }
        }

        // Generate project code
        long existingCount = projectRepository.count();
        String projectCode = codeGenerator.generateProjectCode((int) existingCount);

        // Build entity
        Project project = Project.builder()
            .projectCode(projectCode)
            .projectName(request.getProjectName())
            .description(request.getDescription())
            .department(department)
            .manager(manager)
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .status(ProjectStatus.PLANNING)
            .isActive(true)
            .build();

        Project saved = projectRepository.save(project);
        log.info("Created project with ID: {} and code: {}", saved.getId(), saved.getProjectCode());

        return projectMapper.toResponse(saved);
    }

    /**
     * Cập nhật project
     */
    @Transactional
    public ProjectResponse updateProject(Integer id, ProjectUpdateRequest request) {
        log.info("Updating project ID: {}", id);

        Project project = findProjectByIdOrThrow(id);

        // Update fields
        if (request.getProjectName() != null) {
            project.setProjectName(request.getProjectName());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        if (request.getManagerId() != null) {
            Employee manager = employeeRepository.findById(request.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getManagerId()));
            project.setManager(manager);
        }
        if (request.getStartDate() != null) {
            project.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            project.setEndDate(request.getEndDate());
        }
        if (request.getStatus() != null) {
            project.setStatus(ProjectStatus.valueOf(request.getStatus()));
        }

        // Validate dates
        if (project.getStartDate() != null && project.getEndDate() != null) {
            if (project.getEndDate().isBefore(project.getStartDate())) {
                throw new BusinessException("End date cannot be before start date");
            }
        }

        Project saved = projectRepository.save(project);
        log.info("Updated project ID: {}", id);

        return projectMapper.toResponse(saved);
    }

    /**
     * Xóa project (soft delete)
     */
    @Transactional
    public void deleteProject(Integer id) {
        log.info("Deleting project ID: {}", id);

        Project project = findProjectByIdOrThrow(id);

        // Check if project has active tasks
        long activeTasks = project.getTasks().stream()
            .filter(task -> !task.getStatus().getIsCompleted() && !task.getIsDeleted())
            .count();

        if (activeTasks > 0) {
            throw new BusinessException("Cannot delete project with active tasks");
        }

        project.setIsActive(false);
        projectRepository.save(project);
        log.info("Deleted project ID: {}", id);
    }

    // ========== HELPER METHODS ==========

    private Project findProjectByIdOrThrow(Integer id) {
        return projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
    }
}