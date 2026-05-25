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

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get tasks by department with filters")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getTasksByDepartment(
            @PathVariable("departmentId") Integer departmentId,
            @RequestParam(name = "assigneeId", required = false) Integer assigneeId,
            @RequestParam(name = "statusId", required = false) Integer statusId,
            @RequestParam(name = "priority", required = false) String priority,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        log.info("GET /tasks/department/{} - assigneeId: {}, statusId: {}, priority: {}, search: {}",
                departmentId, assigneeId, statusId, priority, search);

        Page<TaskResponse> tasks = taskService.getDepartmentTasksWithFilters(
                departmentId, assigneeId, statusId, priority, search, page, size
        );

        return ResponseEntity.ok(
                ApiResponse.success(tasks, "Retrieved tasks for department successfully")
        );
    }

    @GetMapping("/my-tasks")
    @Operation(summary = "Get my tasks with filters")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getMyTasksWithFilters(
            @RequestParam(name = "statusId", required = false) Integer statusId,
            @RequestParam(name = "priority", required = false) String priority,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        // ✅ Lấy từ JWT
        Integer userId = SecurityUtils.getCurrentUserId();
        log.info("GET /tasks/my-tasks - userId: {}, statusId: {}, priority: {}, search: {}",
                userId, statusId, priority, search);

        Page<TaskResponse> tasks = taskService.getUserTasksWithFilters(
                userId, statusId, priority, search, page, size
        );

        return ResponseEntity.ok(
                ApiResponse.success(tasks, "Retrieved my tasks successfully")
        );
    }

    /**
     * Giữ lại cho backward compatibility nhưng đã fix lấy userId từ JWT
     */
    @GetMapping("/assignee/me")
    @Operation(summary = "Get my tasks (simple)")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getMyTasksSimple(
            @RequestParam(name = "activeOnly", defaultValue = "false") boolean activeOnly
    ) {
        // ✅ Sửa: bỏ @RequestHeader, lấy từ JWT
        Integer currentUserId = SecurityUtils.getCurrentUserId();

        List<TaskResponse> tasks = activeOnly
                ? taskService.getActiveTasksByAssignee(currentUserId)
                : taskService.getTasksByAssignee(currentUserId);

        return ResponseEntity.ok(
                ApiResponse.success(tasks, "Retrieved my tasks successfully")
        );
    }

    @GetMapping("/assignee/{assigneeId}")
    @Operation(summary = "Get tasks by assignee")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByAssignee(
            @PathVariable("assigneeId") Integer assigneeId,
            @RequestParam(name = "activeOnly", defaultValue = "false") boolean activeOnly
    ) {
        List<TaskResponse> tasks = activeOnly
                ? taskService.getActiveTasksByAssignee(assigneeId)
                : taskService.getTasksByAssignee(assigneeId);

        return ResponseEntity.ok(
                ApiResponse.success(tasks, "Retrieved tasks for assignee successfully")
        );
    }

    @GetMapping("/reporter/{reporterId}")
    @Operation(summary = "Get tasks by reporter")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByReporter(
            @PathVariable("reporterId") Integer reporterId
    ) {
        List<TaskResponse> tasks = taskService.getTasksByReporter(reporterId);
        return ResponseEntity.ok(
                ApiResponse.success(tasks, "Retrieved tasks for reporter successfully")
        );
    }

    @GetMapping("/status/{statusId}")
    @Operation(summary = "Get tasks by status")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByStatus(
            @PathVariable("statusId") Integer statusId
    ) {
        List<TaskResponse> tasks = taskService.getTasksByStatus(statusId);
        return ResponseEntity.ok(
                ApiResponse.success(tasks, "Retrieved tasks for status successfully")
        );
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get tasks by project")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByProject(
            @PathVariable("projectId") Integer projectId
    ) {
        List<TaskResponse> tasks = taskService.getTasksByProject(projectId);
        return ResponseEntity.ok(
                ApiResponse.success(tasks, "Retrieved tasks for project successfully")
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task detail")
    public ResponseEntity<ApiResponse<TaskDetailResponse>> getTaskDetail(
            @PathVariable("id") Integer id
    ) {
        // ✅ Sửa: bỏ @RequestHeader, lấy từ JWT
        Integer currentUserId = SecurityUtils.getCurrentUserId();

        TaskDetailResponse task = taskService.getTaskDetail(id, currentUserId);
        return ResponseEntity.ok(
                ApiResponse.success(task, "Retrieved task detail successfully")
        );
    }

    @GetMapping("/code/{taskCode}")
    @Operation(summary = "Get task by code")
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskByCode(
            @PathVariable String taskCode
    ) {
        TaskResponse task = taskService.getTaskByCode(taskCode);
        return ResponseEntity.ok(
                ApiResponse.success(task, "Retrieved task successfully")
        );
    }

    // ========== BOARD VIEW ==========

    @GetMapping("/board/department/{departmentId}")
    @Operation(summary = "Get tasks for board view")
    public ResponseEntity<ApiResponse<List<TaskSummaryResponse>>> getTasksForBoard(
            @PathVariable("departmentId") Integer departmentId,
            @RequestParam(name = "assigneeId", required = false) Integer assigneeId
    ) {
        log.info("GET /tasks/board/department/{} - assigneeId: {}", departmentId, assigneeId);

        List<TaskSummaryResponse> tasks = taskService.getTasksForBoardWithFilter(departmentId, assigneeId);
        return ResponseEntity.ok(
                ApiResponse.success(tasks, "Retrieved tasks for board successfully")
        );
    }

    // ========== SPECIAL QUERIES ==========

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue tasks")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getOverdueTasks() {
        List<TaskResponse> tasks = taskService.getOverdueTasks();
        return ResponseEntity.ok(
                ApiResponse.success(tasks, "Retrieved overdue tasks successfully")
        );
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming tasks")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getUpcomingTasks(
            @RequestParam(name = "days", defaultValue = "7") int days
    ) {
        List<TaskResponse> tasks = taskService.getUpcomingTasks(days);
        return ResponseEntity.ok(
                ApiResponse.success(tasks, String.format("Retrieved tasks due in next %d days", days))
        );
    }

    @GetMapping("/urgent/user/{userId}")
    @Operation(summary = "Get urgent tasks by user")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getUrgentTasksByUser(
            @PathVariable("userId") Integer userId
    ) {
        List<TaskResponse> tasks = taskService.getUrgentTasksByUser(userId);
        return ResponseEntity.ok(
                ApiResponse.success(tasks, "Retrieved urgent tasks successfully")
        );
    }

    // ========== CREATE, UPDATE, DELETE ==========

    @PostMapping
    @Operation(summary = "Create task")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @Valid @RequestBody TaskCreateRequest request
    ) {
        // ✅ Đã đúng - giữ nguyên
        Integer userId = SecurityUtils.getCurrentUserId();
        log.info("POST /tasks - Creating task: {}", request.getTitle());

        TaskResponse created = taskService.createTask(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(created, "Task created successfully")
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable("id") Integer id,
            @Valid @RequestBody TaskUpdateRequest request
    ) {
        Integer updatedBy = SecurityUtils.getCurrentUserId();
        log.info("PUT /tasks/{} - Updating task by userId: {}", id, updatedBy);

        TaskResponse updated = taskService.updateTask(id, request, updatedBy);
        return ResponseEntity.ok(
                ApiResponse.success(updated, "Task updated successfully")
        );
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update task status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(
            @PathVariable("id") Integer id,
            @Valid @RequestBody TaskStatusUpdateRequest request
    ) {
        // ✅ Sửa: bỏ @RequestHeader, lấy từ JWT
        Integer updatedBy = SecurityUtils.getCurrentUserId();
        log.info("PUT /tasks/{}/status - Updating status to: {} by userId: {}", 
                 id, request.getStatusId(), updatedBy);

        TaskResponse updated = taskService.updateTaskStatus(id, request, updatedBy);
        return ResponseEntity.ok(
                ApiResponse.success(updated, "Task status updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable("id") Integer id
    ) {
        // ✅ Sửa: bỏ @RequestHeader, lấy từ JWT
        Integer deletedBy = SecurityUtils.getCurrentUserId();
        log.info("DELETE /tasks/{} by userId: {}", id, deletedBy);

        taskService.deleteTask(id, deletedBy);
        return ResponseEntity.ok(
                ApiResponse.successMessage("Task deleted successfully")
        );
    }
}