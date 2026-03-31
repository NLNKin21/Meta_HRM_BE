package com.metahrms.employee_management.controller.task;

import com.metahrms.employee_management.dto.request.task.taskstatus.TaskStatusCreateRequest;
import com.metahrms.employee_management.dto.request.task.taskstatus.TaskStatusUpdateRequest;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.task.task.TaskStatusResponse;
import com.metahrms.employee_management.service.task.TaskStatusService;

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
@RequestMapping("/task-statuses")
@RequiredArgsConstructor
@Tag(name = "Task Status Management", description = "APIs for managing task statuses")
public class TaskStatusController {

    private final TaskStatusService taskStatusService;

    /**
     * GET /api/task-statuses
     * Lấy tất cả task status active
     */
    @GetMapping
    @Operation(summary = "Get all active task statuses", description = "Returns all active task statuses ordered by index")
    public ResponseEntity<ApiResponse<List<TaskStatusResponse>>> getAllStatuses() {
        List<TaskStatusResponse> statuses = taskStatusService.getAllActiveStatuses();
        return ResponseEntity.ok(
            ApiResponse.success(statuses, "Retrieved all task statuses successfully")
        );
    }

    /**
     * GET /api/task-statuses/department/{departmentId}
     * Lấy task status theo department
     */
    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get task statuses by department", 
               description = "Returns task statuses for specific department (including common statuses)")
    public ResponseEntity<ApiResponse<List<TaskStatusResponse>>> getStatusesByDepartment(
            @Parameter(description = "Department ID", required = true)
            @PathVariable("departmentId") Integer departmentId) {
        
        List<TaskStatusResponse> statuses = taskStatusService.getStatusesByDepartment(departmentId);
        return ResponseEntity.ok(
            ApiResponse.success(statuses, "Retrieved task statuses for department successfully")
        );
    }

    /**
     * GET /api/task-statuses/{id}
     * Lấy task status theo ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get task status by ID", description = "Returns a single task status by ID")
    public ResponseEntity<ApiResponse<TaskStatusResponse>> getStatusById(
            @Parameter(description = "Task Status ID", required = true)
            @PathVariable("id") Integer id) {
        
        TaskStatusResponse status = taskStatusService.getStatusById(id);
        return ResponseEntity.ok(
            ApiResponse.success(status, "Retrieved task status successfully")
        );
    }

    /**
     * POST /api/task-statuses
     * Tạo task status mới
     */
    @PostMapping
    @Operation(summary = "Create new task status", description = "Creates a new task status")
    public ResponseEntity<ApiResponse<TaskStatusResponse>> createStatus(
            @Valid @RequestBody TaskStatusCreateRequest request) {
        
        TaskStatusResponse created = taskStatusService.createStatus(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(created, "Task status created successfully")
        );
    }

    /**
     * PUT /api/task-statuses/{id}
     * Cập nhật task status
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update task status", description = "Updates an existing task status")
    public ResponseEntity<ApiResponse<TaskStatusResponse>> updateStatus(
            @Parameter(description = "Task Status ID", required = true)
            @PathVariable Integer id,
            @Valid @RequestBody TaskStatusUpdateRequest request) {
        
        TaskStatusResponse updated = taskStatusService.updateStatus(id, request);
        return ResponseEntity.ok(
            ApiResponse.success(updated, "Task status updated successfully")
        );
    }

    /**
     * DELETE /api/task-statuses/{id}
     * Xóa task status
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task status", description = "Soft deletes a task status")
    public ResponseEntity<ApiResponse<Void>> deleteStatus(
            @Parameter(description = "Task Status ID", required = true)
            @PathVariable Integer id) {
        
        taskStatusService.deleteStatus(id);
        return ResponseEntity.ok(
            ApiResponse.successMessage("Task status deleted successfully")
        );
    }
}
