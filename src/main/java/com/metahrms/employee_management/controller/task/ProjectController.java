package com.metahrms.employee_management.controller.task;

import com.metahrms.employee_management.dto.request.task.project.ProjectCreateRequest;
import com.metahrms.employee_management.dto.request.task.project.ProjectUpdateRequest;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.task.project.ProjectResponse;
import com.metahrms.employee_management.service.task.ProjectService;
import com.metahrms.employee_management.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@Tag(name = "Project Management", description = "APIs for managing projects")
public class ProjectController {

    private final ProjectService projectService;

    /**
     * GET /api/projects
     * Lấy tất cả projects active
     */
    @GetMapping
    @Operation(summary = "Get all active projects", description = "Returns all active projects")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getAllProjects() {
        List<ProjectResponse> projects = projectService.getAllActiveProjects();
        return ResponseEntity.ok(
            ApiResponse.success(projects, "Retrieved all projects successfully")
        );
    }

    /**
     * GET /api/projects/department/{departmentId}
     * Lấy projects theo department
     */
    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get projects by department", description = "Returns projects for specific department")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjectsByDepartment(
            @Parameter(description = "Department ID", required = true)
            @PathVariable("departmentId") Integer departmentId) {
        
        List<ProjectResponse> projects = projectService.getProjectsByDepartment(departmentId);
        return ResponseEntity.ok(
            ApiResponse.success(projects, "Retrieved projects for department successfully")
        );
    }

    /**
     * GET /api/projects/manager/{managerId}
     * Lấy projects theo manager
     */
    @GetMapping("/manager/{managerId}")
    @Operation(summary = "Get projects by manager", description = "Returns projects managed by specific employee")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjectsByManager(
            @Parameter(description = "Manager Employee ID", required = true)
            @PathVariable("managerId") Integer managerId) {
        
        List<ProjectResponse> projects = projectService.getProjectsByManager(managerId);
        return ResponseEntity.ok(
            ApiResponse.success(projects, "Retrieved projects for manager successfully")
        );
    }

    /**
     * GET /api/projects/{id}
     * Lấy project theo ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID", description = "Returns a single project with task counts")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectById(
            @Parameter(description = "Project ID", required = true)
            @PathVariable("id") Integer id) {
        
        ProjectResponse project = projectService.getProjectById(id);
        return ResponseEntity.ok(
            ApiResponse.success(project, "Retrieved project successfully")
        );
    }

    /**
     * GET /api/projects/code/{projectCode}
     * Lấy project theo code
     */
    @GetMapping("/code/{projectCode}")
    @Operation(summary = "Get project by code", description = "Returns a single project by project code")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectByCode(
            @Parameter(description = "Project Code", required = true, example = "PRJ-2024-001")
            @PathVariable("projectCode") String projectCode) {
        
        ProjectResponse project = projectService.getProjectByCode(projectCode);
        return ResponseEntity.ok(
            ApiResponse.success(project, "Retrieved project successfully")
        );
    }

    /**
     * POST /api/projects
     * Tạo project mới
     */
    @PostMapping
    @Operation(summary = "Create new project", description = "Creates a new project")
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @Valid @RequestBody ProjectCreateRequest request) {
        
        // ✅ Sửa: bỏ @RequestHeader, lấy từ JWT
        Integer createdBy = SecurityUtils.getCurrentUserId();
        
        ProjectResponse created = projectService.createProject(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(created, "Project created successfully")
        );
    }

    /**
     * PUT /api/projects/{id}
     * Cập nhật project
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update project", description = "Updates an existing project")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @Parameter(description = "Project ID", required = true)
            @PathVariable("id") Integer id,
            @Valid @RequestBody ProjectUpdateRequest request) {
        
        ProjectResponse updated = projectService.updateProject(id, request);
        return ResponseEntity.ok(
            ApiResponse.success(updated, "Project updated successfully")
        );
    }

    /**
     * DELETE /api/projects/{id}
     * Xóa project
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete project", description = "Soft deletes a project (only if no active tasks)")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @Parameter(description = "Project ID", required = true)
            @PathVariable("id") Integer id) {
        
        projectService.deleteProject(id);
        return ResponseEntity.ok(
            ApiResponse.successMessage("Project deleted successfully")
        );
    }
}