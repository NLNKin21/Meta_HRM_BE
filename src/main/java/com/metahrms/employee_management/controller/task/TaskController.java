package com.metahrms.employee_management.controller.task;

import com.metahrms.employee_management.dto.request.task.task.TaskCreateRequest;
import com.metahrms.employee_management.dto.request.task.task.TaskStatusUpdateRequest;
import com.metahrms.employee_management.dto.request.task.task.TaskUpdateRequest;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.task.task.TaskDetailResponse;
import com.metahrms.employee_management.dto.response.task.task.TaskResponse;
import com.metahrms.employee_management.dto.response.task.task.TaskSummaryResponse;
import com.metahrms.employee_management.service.task.TaskService;
import com.metahrms.employee_management.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Management", description = "APIs for managing tasks")
public class TaskController {

    private final TaskService taskService;

    // ========== QUERY ENDPOINTS ==========

    /**
     * GET /tasks
     * Lấy tất cả tasks (có phân trang)
     */
    @GetMapping
    @Operation(summary = "Get all tasks", description = "Returns paginated list of all tasks")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getAllTasks(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") 
            ? Sort.by(sortBy).ascending() 
            : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TaskResponse> tasks = taskService.getAllTasks(pageable);
        return ResponseEntity.ok(
            ApiResponse.success(tasks, "Retrieved tasks successfully")
        );
    }

    /**
     * 🆕 GET /tasks/department/{departmentId}
     * Lấy tasks theo department VỚI FILTERS (cho Manager)
     */
    @GetMapping("/department/{departmentId}")
    @Operation(
        summary = "Get tasks by department with filters", 
        description = "Returns paginated tasks for specific department. Supports filtering by assignee, status, priority, and search."
    )
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getTasksByDepartment(
            @Parameter(description = "Department ID", required = true, example = "3")
            @PathVariable("departmentId") Integer departmentId,
            
            @Parameter(description = "Filter by assignee employee ID. NULL = all employees", example = "13")
            @RequestParam(name = "assigneeId", required = false) Integer assigneeId,
            
            @Parameter(description = "Filter by status ID", example = "1")
            @RequestParam(name = "statusId", required = false) Integer statusId,
            
            @Parameter(description = "Filter by priority: LOW, MEDIUM, HIGH, URGENT", example = "HIGH")
            @RequestParam(name = "priority", required = false) String priority,
            
            @Parameter(description = "Search keyword in task title", example = "employee")
            @RequestParam(name = "search", required = false) String search,
            
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(name = "page", defaultValue = "0") int page,
            
            @Parameter(description = "Page size", example = "20")
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        log.info("GET /tasks/department/{} - assigneeId: {}, statusId: {}, priority: {}, search: {}", 
                 departmentId, assigneeId, statusId, priority, search);
        
        Page<TaskResponse> tasks = taskService.getDepartmentTasksWithFilters(
            departmentId,
            assigneeId,
            statusId,
            priority,
            search,
            page,
            size
        );
        
        return ResponseEntity.ok(
            ApiResponse.success(tasks, "Retrieved tasks for department successfully")
        );
    }

    /**
     * 🆕 GET /tasks/my-tasks
     * Lấy tasks của user hiện tại VỚI FILTERS (cho Employee)
     */
    @GetMapping("/my-tasks")
    @Operation(
        summary = "Get my tasks with filters", 
        description = "Returns paginated tasks assigned to current user with optional filters"
    )
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getMyTasksWithFilters(
            @Parameter(description = "Filter by status ID")
            @RequestParam(name = "statusId", required = false) Integer statusId,
            
            @Parameter(description = "Filter by priority: LOW, MEDIUM, HIGH, URGENT")
            @RequestParam(name = "priority", required = false) String priority,
            
            @Parameter(description = "Search keyword in task title")
            @RequestParam(name = "search", required = false) String search,
            
            @Parameter(description = "Page number (0-based)")
            @RequestParam(name = "page", defaultValue = "0") int page,
            
            @Parameter(description = "Page size")
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        Integer userId = SecurityUtils.getCurrentUserId();
        log.info("GET /tasks/my-tasks - userId: {}, statusId: {}, priority: {}, search: {}", 
                 userId, statusId, priority, search);
        
        Page<TaskResponse> tasks = taskService.getUserTasksWithFilters(
            userId,
            statusId,
            priority,
            search,
            page,
            size
        );
        
        return ResponseEntity.ok(
            ApiResponse.success(tasks, "Retrieved my tasks successfully")
        );
    }

    /**
     * GET /tasks/assignee/me (giữ lại cho backward compatibility)
     * Lấy tasks của user hiện tại (không filter, trả về List)
     */
    @GetMapping("/assignee/me")
    @Operation(summary = "Get my tasks (simple)", description = "Returns all tasks assigned to current user")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getMyTasksSimple(
            @Parameter(description = "Only active tasks?")
            @RequestParam(name = "activeOnly", defaultValue = "false") boolean activeOnly,
            @RequestHeader("X-User-Id") Integer currentUserId
    ) {
        List<TaskResponse> tasks = activeOnly 
            ? taskService.getActiveTasksByAssignee(currentUserId)
            : taskService.getTasksByAssignee(currentUserId);
        
        return ResponseEntity.ok(
            ApiResponse.success(tasks, "Retrieved my tasks successfully")
        );
    }

    /**
     * GET /tasks/assignee/{assigneeId}
     * Lấy tasks theo assignee
     */
    @GetMapping("/assignee/{assigneeId}")
    @Operation(summary = "Get tasks by assignee", description = "Returns all tasks assigned to specific employee")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByAssignee(
            @Parameter(description = "Assignee Employee ID", required = true)
            @PathVariable("assigneeId") Integer assigneeId,
            @Parameter(description = "Only active tasks?")
            @RequestParam(name = "activeOnly", defaultValue = "false") boolean activeOnly
    ) {
        List<TaskResponse> tasks = activeOnly 
            ? taskService.getActiveTasksByAssignee(assigneeId)
            : taskService.getTasksByAssignee(assigneeId);
        
        return ResponseEntity.ok(
            ApiResponse.success(tasks, "Retrieved tasks for assignee successfully")
        );
    }

    /**
     * GET /tasks/reporter/{reporterId}
     * Lấy tasks theo reporter
     */
    @GetMapping("/reporter/{reporterId}")
    @Operation(summary = "Get tasks by reporter", description = "Returns all tasks created by specific employee")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByReporter(
            @Parameter(description = "Reporter Employee ID", required = true)
            @PathVariable("reporterId") Integer reporterId
    ) {
        List<TaskResponse> tasks = taskService.getTasksByReporter(reporterId);
        return ResponseEntity.ok(
            ApiResponse.success(tasks, "Retrieved tasks for reporter successfully")
        );
    }

    /**
     * GET /tasks/status/{statusId}
     * Lấy tasks theo status
     */
    @GetMapping("/status/{statusId}")
    @Operation(summary = "Get tasks by status", description = "Returns all tasks with specific status")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByStatus(
            @Parameter(description = "Task Status ID", required = true)
            @PathVariable("statusId") Integer statusId
    ) {
        List<TaskResponse> tasks = taskService.getTasksByStatus(statusId);
        return ResponseEntity.ok(
            ApiResponse.success(tasks, "Retrieved tasks for status successfully")
        );
    }

    /**
     * GET /tasks/project/{projectId}
     * Lấy tasks theo project
     */
    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get tasks by project", description = "Returns all tasks in specific project")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByProject(
            @Parameter(description = "Project ID", required = true)
            @PathVariable("projectId") Integer projectId
    ) {
        List<TaskResponse> tasks = taskService.getTasksByProject(projectId);
        return ResponseEntity.ok(
            ApiResponse.success(tasks, "Retrieved tasks for project successfully")
        );
    }

    /**
     * GET /tasks/{id}
     * Lấy task chi tiết
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get task detail", description = "Returns detailed task information including comments and history")
    public ResponseEntity<ApiResponse<TaskDetailResponse>> getTaskDetail(
            @Parameter(description = "Task ID", required = true)
            @PathVariable("id") Integer id,
            @Parameter(description = "Current user ID", required = true)
            @RequestHeader("X-User-Id") Integer currentUserId
    ) {
        TaskDetailResponse task = taskService.getTaskDetail(id, currentUserId);
        return ResponseEntity.ok(
            ApiResponse.success(task, "Retrieved task detail successfully")
        );
    }

    /**
     * GET /tasks/code/{taskCode}
     * Lấy task theo code
     */
    @GetMapping("/code/{taskCode}")
    @Operation(summary = "Get task by code", description = "Returns task by task code")
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskByCode(
            @Parameter(description = "Task Code", required = true, example = "TSK-20240115-001")
            @PathVariable String taskCode
    ) {
        TaskResponse task = taskService.getTaskByCode(taskCode);
        return ResponseEntity.ok(
            ApiResponse.success(task, "Retrieved task successfully")
        );
    }

    // ========== BOARD VIEW ==========

    /**
     * GET /tasks/board/department/{departmentId}
     * Lấy tasks cho Board view (Kanban) - có thể filter theo assignee
     */
    @GetMapping("/board/department/{departmentId}")
    @Operation(
        summary = "Get tasks for board view", 
        description = "Returns simplified task list for Kanban board with optional assignee filter"
    )
    public ResponseEntity<ApiResponse<List<TaskSummaryResponse>>> getTasksForBoard(
            @Parameter(description = "Department ID", required = true)
            @PathVariable("departmentId") Integer departmentId,
            
            @Parameter(description = "Filter by assignee ID (optional)")
            @RequestParam(name = "assigneeId", required = false) Integer assigneeId
    ) {
        log.info("GET /tasks/board/department/{} - assigneeId: {}", departmentId, assigneeId);
        
        List<TaskSummaryResponse> tasks = taskService.getTasksForBoardWithFilter(departmentId, assigneeId);
        return ResponseEntity.ok(
            ApiResponse.success(tasks, "Retrieved tasks for board successfully")
        );
    }

    // ========== SPECIAL QUERIES ==========

    /**
     * GET /tasks/overdue
     * Lấy overdue tasks
     */
    @GetMapping("/overdue")
    @Operation(summary = "Get overdue tasks", description = "Returns all tasks that passed due date")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getOverdueTasks() {
        List<TaskResponse> tasks = taskService.getOverdueTasks();
        return ResponseEntity.ok(
            ApiResponse.success(tasks, "Retrieved overdue tasks successfully")
        );
    }

    /**
     * GET /tasks/upcoming
     * Lấy upcoming deadline tasks
     */
    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming tasks", description = "Returns tasks due in next X days")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getUpcomingTasks(
            @Parameter(description = "Number of days", example = "7")
            @RequestParam(name = "days", defaultValue = "7") int days
    ) {
        List<TaskResponse> tasks = taskService.getUpcomingTasks(days);
        return ResponseEntity.ok(
            ApiResponse.success(tasks, String.format("Retrieved tasks due in next %d days", days))
        );
    }

    /**
     * GET /tasks/urgent/user/{userId}
     * Lấy urgent tasks của user
     */
    @GetMapping("/urgent/user/{userId}")
    @Operation(summary = "Get urgent tasks by user", description = "Returns urgent tasks assigned to user")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getUrgentTasksByUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable("userId") Integer userId
    ) {
        List<TaskResponse> tasks = taskService.getUrgentTasksByUser(userId);
        return ResponseEntity.ok(
            ApiResponse.success(tasks, "Retrieved urgent tasks successfully")
        );
    }

    // ========== CREATE, UPDATE, DELETE ==========

    /**
     * POST /tasks
     * Tạo task mới
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @Valid @RequestBody TaskCreateRequest request
    ) {
        Integer userId = SecurityUtils.getCurrentUserId();

        log.info("POST /tasks - Creating task: {}", request.getTitle());

        TaskResponse created = taskService.createTask(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(created, "Task created successfully")
        );
    }

    /**
     * PUT /tasks/{id}
     * Cập nhật task
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update task", description = "Updates an existing task")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable Integer id,
            @Valid @RequestBody TaskUpdateRequest request,
            @Parameter(description = "Current user ID", required = true)
            @RequestHeader("X-User-Id") Integer updatedBy
    ) {
        log.info("PUT /tasks/{} - Updating task", id);
        
        TaskResponse updated = taskService.updateTask(id, request, updatedBy);
        return ResponseEntity.ok(
            ApiResponse.success(updated, "Task updated successfully")
        );
    }

    /**
     * PUT /tasks/{id}/status
     * Cập nhật status của task
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Update task status", description = "Changes task status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(
            @Parameter(description = "Task ID", required = true)
            @PathVariable("id") Integer id,
            @Valid @RequestBody TaskStatusUpdateRequest request,
            @Parameter(description = "Current user ID", required = true)
            @RequestHeader("X-User-Id") Integer updatedBy
    ) {
        log.info("PUT /tasks/{}/status - Updating status to: {}", id, request.getStatusId());
        
        TaskResponse updated = taskService.updateTaskStatus(id, request, updatedBy);
        return ResponseEntity.ok(
            ApiResponse.success(updated, "Task status updated successfully")
        );
    }

    /**
     * DELETE /tasks/{id}
     * Xóa task (soft delete)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task", description = "Soft deletes a task")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable("id") Integer id,
            @Parameter(description = "Current user ID", required = true)
            @RequestHeader("X-User-Id") Integer deletedBy
    ) {
        log.info("DELETE /tasks/{}", id);
        
        taskService.deleteTask(id, deletedBy);
        return ResponseEntity.ok(
            ApiResponse.successMessage("Task deleted successfully")
        );
    }
}