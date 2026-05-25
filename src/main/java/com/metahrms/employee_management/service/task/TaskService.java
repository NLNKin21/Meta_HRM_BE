package com.metahrms.employee_management.service.task;


import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.User;
import com.metahrms.employee_management.entity.Task.Project;
import com.metahrms.employee_management.entity.Task.Task;
import com.metahrms.employee_management.entity.Task.TaskComment;
import com.metahrms.employee_management.entity.Task.TaskStatus;
import com.metahrms.employee_management.enums.Task.TaskPriority;
import com.metahrms.employee_management.enums.Task.TaskType;
import com.metahrms.employee_management.exception.BusinessException;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.exception.TaskException;
import com.metahrms.employee_management.mapper.task.TaskCommentMapper;
import com.metahrms.employee_management.mapper.task.TaskHistoryMapper;
import com.metahrms.employee_management.mapper.task.TaskMapper;
import com.metahrms.employee_management.dto.request.task.task.TaskCreateRequest;
import com.metahrms.employee_management.dto.request.task.task.TaskStatusUpdateRequest;
import com.metahrms.employee_management.dto.request.task.task.TaskUpdateRequest;
import com.metahrms.employee_management.dto.response.task.comment.TaskCommentResponse;
import com.metahrms.employee_management.dto.response.task.history.TaskHistoryResponse;
import com.metahrms.employee_management.dto.response.task.task.TaskDetailResponse;
import com.metahrms.employee_management.dto.response.task.task.TaskResponse;
import com.metahrms.employee_management.dto.response.task.task.TaskSummaryResponse;
import com.metahrms.employee_management.entity.Department;

import com.metahrms.employee_management.repository.DepartmentRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.UserRepository;
import com.metahrms.employee_management.repository.Task.ProjectRepository;
import com.metahrms.employee_management.repository.Task.TaskCommentRepository;
import com.metahrms.employee_management.repository.Task.TaskHistoryRepository;
import com.metahrms.employee_management.repository.Task.TaskRepository;
import com.metahrms.employee_management.repository.Task.TaskStatusRepository;
import com.metahrms.employee_management.util.TaskCodeGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final ProjectRepository projectRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final TaskCommentRepository commentRepository;
    private final TaskHistoryRepository historyRepository;
    private final UserRepository userRepository;
    
    private final TaskMapper taskMapper;
    private final TaskCommentMapper commentMapper;
    private final TaskHistoryMapper historyMapper;
    private final TaskCodeGenerator codeGenerator;
    
    private final TaskHistoryService historyService;
    private final NotificationService notificationService;
    private final TaskStatusService statusService;

    // ========== QUERY METHODS ==========

    /**
     * Lấy tất cả tasks (có phân trang)
     */
    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllTasks(Pageable pageable) {
        log.info("Getting all tasks with pagination");
        return taskRepository.findAll(pageable)
            .map(task -> {
                TaskResponse response = taskMapper.toTaskResponse(task);
                response.setCommentCount(commentRepository.countByTaskId(task.getId()).intValue());
                return response;
            });
    }

    /**
     * Lấy tasks theo department (có phân trang)
     */
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByDepartment(Integer departmentId, Pageable pageable) {
        log.info("Getting tasks for department: {}", departmentId);
        return taskRepository.findByDepartmentId(departmentId, pageable)
            .map(task -> {
                TaskResponse response = taskMapper.toTaskResponse(task);
                response.setCommentCount(commentRepository.countByTaskId(task.getId()).intValue());
                return response;
            });
    }

    /**
     * Lấy tasks theo assignee
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByAssignee(Integer assigneeId) {
        log.info("Getting tasks for assignee: {}", assigneeId);
        return taskRepository.findByAssigneeId(assigneeId)
            .stream()
            .map(task -> {
                TaskResponse response = taskMapper.toTaskResponse(task);
                response.setCommentCount(commentRepository.countByTaskId(task.getId()).intValue());
                return response;
            })
            .collect(Collectors.toList());
    }

    /**
     * Lấy active tasks của assignee
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getActiveTasksByAssignee(Integer assigneeId) {
        log.info("Getting active tasks for assignee: {}", assigneeId);
        return taskRepository.findActiveTasksByAssignee(assigneeId)
            .stream()
            .map(taskMapper::toTaskResponse)
            .collect(Collectors.toList());
    }

    /**
     * Lấy tasks theo reporter
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByReporter(Integer reporterId) {
        log.info("Getting tasks for reporter: {}", reporterId);
        return taskRepository.findByReporterId(reporterId)
            .stream()
            .map(taskMapper::toTaskResponse)
            .collect(Collectors.toList());
    }

    /**
     * Lấy tasks theo status
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByStatus(Integer statusId) {
        log.info("Getting tasks for status: {}", statusId);
        return taskRepository.findByStatusId(statusId)
            .stream()
            .map(taskMapper::toTaskResponse)
            .collect(Collectors.toList());
    }

    /**
     * Lấy tasks theo project
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProject(Integer projectId) {
        log.info("Getting tasks for project: {}", projectId);
        return taskRepository.findByProjectId(projectId)
            .stream()
            .map(taskMapper::toTaskResponse)
            .collect(Collectors.toList());
    }

    /**
     * Lấy task chi tiết theo ID
     */
    @Transactional(readOnly = true)
    public TaskDetailResponse getTaskDetail(Integer id, Integer currentUserId) {
        log.info("Getting task detail for ID: {}", id);
        
        Task task = taskRepository.findByIdWithDetails(id)
            .orElseThrow(() -> TaskException.taskNotFound(id));

        // Get comments
        List<TaskCommentResponse> comments = commentRepository.findByTaskIdOrderByCreatedAtDesc(id)
            .stream()
            .map(comment -> {
                TaskCommentResponse response = commentMapper.toResponse(comment);
                response.setCanEdit(comment.getUser().getId().equals(currentUserId));
                response.setCanDelete(comment.getUser().getId().equals(currentUserId));
                return response;
            })
            .collect(Collectors.toList());

        // Get histories
        List<TaskHistoryResponse> histories = historyRepository.findByTaskIdOrderByCreatedAtDesc(id)
            .stream()
            .map(historyMapper::toResponse)
            .collect(Collectors.toList());

        // Build detail response
        TaskDetailResponse response = taskMapper.toTaskDetailResponse(task);
        response.setComments(comments);
        response.setHistories(histories);
        response.setCommentCount(comments.size());

        // Set permissions
        boolean isReporter = task.getReporter().getId().equals(currentUserId);
        boolean isAssignee = task.getAssignee().getId().equals(currentUserId);
        response.setCanEdit(isReporter || isAssignee);
        response.setCanDelete(isReporter);
        response.setCanComment(true);

        return response;
    }

    /**
     * Lấy task theo code
     */
    @Transactional(readOnly = true)
    public TaskResponse getTaskByCode(String taskCode) {
        log.info("Getting task by code: {}", taskCode);
        Task task = taskRepository.findByTaskCode(taskCode)
            .orElseThrow(() -> TaskException.taskCodeNotFound(taskCode));
        return taskMapper.toTaskResponse(task);
    }

    // ========== BOARD VIEW (KANBAN) ==========

    /**
     * Lấy tasks cho Board view (theo department)
     */
    @Transactional(readOnly = true)
    public List<TaskSummaryResponse> getTasksForBoard(Integer departmentId) {
        log.info("Getting tasks for board - department: {}", departmentId);
        return taskRepository.findByDepartmentId(departmentId)
            .stream()
            .map(task -> {
                TaskSummaryResponse response = taskMapper.toTaskSummaryResponse(task);
                response.setCommentCount(commentRepository.countByTaskId(task.getId()).intValue());
                return response;
            })
            .collect(Collectors.toList());
    }

    // ========== DASHBOARD & STATISTICS ==========

    /**
     * Lấy overdue tasks
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getOverdueTasks() {
        log.info("Getting overdue tasks");
        return taskRepository.findOverdueTasks(LocalDate.now())
            .stream()
            .map(taskMapper::toTaskResponse)
            .collect(Collectors.toList());
    }

    /**
     * Lấy upcoming deadline tasks
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getUpcomingTasks(int days) {
        log.info("Getting tasks due in next {} days", days);
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);
        return taskRepository.findTasksDueBetween(today, endDate)
            .stream()
            .map(taskMapper::toTaskResponse)
            .collect(Collectors.toList());
    }

    /**
     * Lấy urgent tasks của user
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getUrgentTasksByUser(Integer userId) {
        log.info("Getting urgent tasks for user: {}", userId);
        return taskRepository.findUrgentTasksByUser(userId)
            .stream()
            .map(taskMapper::toTaskResponse)
            .collect(Collectors.toList());
    }

    // ========== CREATE, UPDATE, DELETE ==========

    /**
     * Tạo task mới
     */
    @Transactional
    public TaskResponse createTask(TaskCreateRequest request, Integer userId) {
        log.info("Creating new task: {}", request.getTitle());

        // 🔥 Convert user → employee (QUAN TRỌNG)
        Employee reporter = employeeRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee", "userId", userId));

        // Validate department
        Department department = departmentRepository.findById(request.getDepartmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

        // Validate assignee
        Employee assignee = employeeRepository.findById(request.getAssigneeId())
            .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getAssigneeId()));

        // Validate approver nếu có
        Employee approver = null;
        if (request.getApproverId() != null) {
            approver = employeeRepository.findById(request.getApproverId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getApproverId()));
        }

        // Validate project nếu có
        Project project = null;
        if (request.getProjectId() != null) {
            project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", request.getProjectId()));
        }

        // Validate dates
        if (request.getStartDate() != null && request.getDueDate() != null &&
            request.getDueDate().isBefore(request.getStartDate())) {
            throw TaskException.invalidDueDate();
        }

        // Default status
        TaskStatus defaultStatus = statusService.getDefaultStatusByDepartment(request.getDepartmentId());

        // Generate code
        String taskCode = codeGenerator.generateTaskCode();

        // Build task
        Task task = Task.builder()
            .taskCode(taskCode)
            .title(request.getTitle())
            .description(request.getDescription())
            .taskType(request.getTaskType() != null 
                ? TaskType.valueOf(request.getTaskType()) 
                : TaskType.TASK)
            .priority(request.getPriority() != null 
                ? TaskPriority.valueOf(request.getPriority()) 
                : TaskPriority.MEDIUM)
            .status(defaultStatus)
            .reporter(reporter) // ✅ đúng employee
            .assignee(assignee)
            .approver(approver)
            .department(department)
            .project(project)
            .estimatedHours(request.getEstimatedHours())
            .startDate(request.getStartDate())
            .dueDate(request.getDueDate())
            .isUrgent(Boolean.TRUE.equals(request.getIsUrgent()))
            .completionRate(0)
            .isLate(false)
            .isDeleted(false)
            .build();

        Task saved = taskRepository.save(task);

        log.info("Created task ID: {}, code: {}", saved.getId(), saved.getTaskCode());

        // ✅ FIX: truyền employeeId, không phải userId
        historyService.logTaskCreated(saved, reporter.getId());

        notificationService.sendTaskAssignedNotification(saved);

        return taskMapper.toTaskResponse(saved);
    }

    /**
     * Cập nhật task
     */
    @Transactional
    public TaskResponse updateTask(Integer id, TaskUpdateRequest request, Integer updatedBy) {
        log.info("Updating task ID: {}", id);

        Task task = taskRepository.findById(id)
            .orElseThrow(() -> TaskException.taskNotFound(id));

        // Track changes for history
        String oldTitle = task.getTitle();
        String oldDescription = task.getDescription();
        Integer oldAssigneeId = task.getAssignee().getId();
        Integer oldCompletionRate = task.getCompletionRate();

        // Update fields
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
            if (!request.getTitle().equals(oldTitle)) {
                historyService.logFieldChange(task, updatedBy, "title", oldTitle, request.getTitle());
            }
        }

        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
            if (!request.getDescription().equals(oldDescription)) {
                historyService.logFieldChange(task, updatedBy, "description", "Updated", "Updated");
            }
        }

        if (request.getTaskType() != null) {
            task.setTaskType(TaskType.valueOf(request.getTaskType()));
        }

        if (request.getPriority() != null) {
            String oldPriority = task.getPriority().name();
            task.setPriority(TaskPriority.valueOf(request.getPriority()));
            historyService.logFieldChange(task, updatedBy, "priority", oldPriority, request.getPriority());
        }

        if (request.getAssigneeId() != null && !request.getAssigneeId().equals(oldAssigneeId)) {
            Employee newAssignee = employeeRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getAssigneeId()));
            
            String oldAssigneeName = task.getAssignee().getFullName();
            task.setAssignee(newAssignee);
            
            historyService.logFieldChange(task, updatedBy, "assignee", oldAssigneeName, newAssignee.getFullName());
            
            // Notify new assignee
            notificationService.sendTaskAssignedNotification(task);
        }

        if (request.getApproverId() != null) {
            Employee approver = employeeRepository.findById(request.getApproverId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getApproverId()));
            task.setApprover(approver);
        }

        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", request.getProjectId()));
            task.setProject(project);
        }

        if (request.getEstimatedHours() != null) {
            task.setEstimatedHours(request.getEstimatedHours());
        }

        if (request.getActualHours() != null) {
            task.setActualHours(request.getActualHours());
        }

        if (request.getStartDate() != null) {
            task.setStartDate(request.getStartDate());
        }

        if (request.getDueDate() != null) {
            LocalDate oldDueDate = task.getDueDate();
            task.setDueDate(request.getDueDate());
            if (oldDueDate != null && !request.getDueDate().equals(oldDueDate)) {
                historyService.logFieldChange(task, updatedBy, "dueDate", 
                    oldDueDate.toString(), request.getDueDate().toString());
            }
        }

        if (request.getCompletionRate() != null) {
            task.setCompletionRate(request.getCompletionRate());
            if (!request.getCompletionRate().equals(oldCompletionRate)) {
                historyService.logFieldChange(task, updatedBy, "completionRate", 
                    String.valueOf(oldCompletionRate), String.valueOf(request.getCompletionRate()));
            }
        }

        if (request.getIsUrgent() != null) {
            task.setIsUrgent(request.getIsUrgent());
        }

        // Validate dates
        if (task.getStartDate() != null && task.getDueDate() != null) {
            if (task.getDueDate().isBefore(task.getStartDate())) {
                throw TaskException.invalidDueDate();
            }
        }

        // Check late status
        updateLateStatus(task);

        Task saved = taskRepository.save(task);
        log.info("Updated task ID: {}", id);

        return taskMapper.toTaskResponse(saved);
    }

    /**
     * Cập nhật status của task
     */
    @Transactional
    public TaskResponse updateTaskStatus(Integer id, TaskStatusUpdateRequest request, Integer updatedBy) {
        log.info("Updating task status - Task ID: {}, New Status ID: {}", id, request.getStatusId());

        Task task = taskRepository.findById(id)
            .orElseThrow(() -> TaskException.taskNotFound(id));

        TaskStatus oldStatus = task.getStatus();
        TaskStatus newStatus = taskStatusRepository.findById(request.getStatusId())
            .orElseThrow(() -> new ResourceNotFoundException("TaskStatus", "id", request.getStatusId()));

        // Check if task is already completed
        if (oldStatus.getIsCompleted()) {
            throw TaskException.taskAlreadyCompleted(task.getTaskCode());
        }
        validateStatusTransition(task, oldStatus, newStatus, updatedBy);

        // Update status
        task.setStatus(newStatus);

        // If new status is completed
        if (newStatus.getIsCompleted()) {
            task.setCompletedAt(LocalDateTime.now());
            task.setCompletionRate(100);
            updateLateStatus(task);
        }
        if (oldStatus.getId() == 3 && newStatus.getId() == 2) {
            task.setCompletedAt(null);
        }

        Task saved = taskRepository.save(task);

        // Log history
        historyService.logFieldChange(task, updatedBy, "status", 
            oldStatus.getStatusName(), newStatus.getStatusName());

        // Add comment if provided
        if (request.getComment() != null && !request.getComment().trim().isEmpty()) {
            User user = userRepository.findById(updatedBy)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", updatedBy));

            TaskComment comment = TaskComment.builder()
                .task(saved)
                .user(user)
                .content(buildStatusComment(oldStatus, newStatus, request.getComment()))
                .isDeleted(false)
                .build();

            commentRepository.save(comment);
        }

        // Send notification
        notificationService.sendStatusChangeNotification(saved, oldStatus.getStatusName());

        log.info("Updated task status - Task ID: {}, From: {} To: {}", 
            id, oldStatus.getStatusName(), newStatus.getStatusName());

        return taskMapper.toTaskResponse(saved);
    }

    /**
     * Validate chuyển trạng thái hợp lệ theo role
     *
     * Status IDs:
     * 1 = Chờ xử lý
     * 2 = Đang làm
     * 3 = Đang review
     * 4 = Hoàn thành
     * 5 = Hủy
     */
    private void validateStatusTransition(Task task, TaskStatus oldStatus, 
                                        TaskStatus newStatus, Integer userId) {
        // Lấy role của user trong phòng ban
        String roleInDept = getRoleInDept(userId);
        Integer oldId = oldStatus.getId();
        Integer newId = newStatus.getId();
        Integer assigneeUserId = getAssigneeUserId(task);

        boolean isAssignee = userId.equals(assigneeUserId);
        boolean isManager  = "HEAD".equals(roleInDept) || "DEPUTY".equals(roleInDept);
        boolean isStaff    = "STAFF".equals(roleInDept) || "LEADER".equals(roleInDept);

        // ✅ MANAGER: có thể hủy bất kỳ task nào
        if (isManager && newId == 5) return;

        // ✅ STAFF/ASSIGNEE transitions
        if (isAssignee || isStaff) {
            // Chờ xử lý → Đang làm (Bắt đầu thực hiện)
            if (oldId == 1 && newId == 2) return;
            // Đang làm → Đang review (Gửi báo cáo hoàn thành)
            if (oldId == 2 && newId == 3) return;
        }

        // ✅ MANAGER transitions
        if (isManager) {
            // Đang review → Đang làm (Chưa đạt - reject)
            if (oldId == 3 && newId == 2) return;
            // Đang review → Hoàn thành (Đạt - approve)
            if (oldId == 3 && newId == 4) return;
            // Chờ xử lý → Đang làm (Manager cũng có thể bắt đầu)
            if (oldId == 1 && newId == 2) return;
        }

        throw new BusinessException(
            String.format("Không thể chuyển từ '%s' sang '%s'. Vui lòng kiểm tra quyền hạn.",
                oldStatus.getStatusName(), newStatus.getStatusName())
        );
    }

    /**
     * Build nội dung comment tự động theo loại transition
     */
    private String buildStatusComment(TaskStatus oldStatus, TaskStatus newStatus, String userComment) {
        Integer oldId = oldStatus.getId();
        Integer newId = newStatus.getId();
        
        String prefix = "";
        
        if (oldId == 1 && newId == 2) {
            prefix = "🚀 Bắt đầu thực hiện công việc.\n";
        } else if (oldId == 2 && newId == 3) {
            prefix = "📋 Báo cáo hoàn thành:\n";
        } else if (oldId == 3 && newId == 2) {
            prefix = "❌ Chưa đạt yêu cầu, trả về thực hiện lại.\nLý do: ";
        } else if (oldId == 3 && newId == 4) {
            prefix = "✅ Đã phê duyệt hoàn thành.\n";
        } else if (newId == 5) {
            prefix = "🚫 Hủy công việc.\nLý do: ";
        }
        
        return prefix + userComment;
    }

    /**
     * Lấy roleInDept của user từ employee
     */
    private String getRoleInDept(Integer userId) {
        return employeeRepository.findByUserId(userId)
            .map(emp -> emp.getRoleInDept() != null ? emp.getRoleInDept().name() : "STAFF")
            .orElse("STAFF");
    }

    /**
     * Lấy userId của assignee
     */
    private Integer getAssigneeUserId(Task task) {
        if (task.getAssignee() == null) return null;
        // Task.assignee là Employee entity
        // Employee có userId
        return task.getAssignee().getUserId();
    }

    /**
     * Xóa task (soft delete)
     */
    @Transactional
    public void deleteTask(Integer id, Integer deletedBy) {
        log.info("Deleting task ID: {}", id);

        Task task = taskRepository.findById(id)
            .orElseThrow(() -> TaskException.taskNotFound(id));

        task.setIsDeleted(true);
        taskRepository.save(task);

        // Log history
        historyService.logTaskDeleted(task, deletedBy);

        log.info("Deleted task ID: {}", id);
    }

    // ========== HELPER METHODS ==========

    private void updateLateStatus(Task task) {
        if (task.getDueDate() != null && !task.getStatus().getIsCompleted()) {
            task.setIsLate(task.getDueDate().isBefore(LocalDate.now()));
        }
        if (task.getStatus().getIsCompleted() && task.getCompletedAt() != null && task.getDueDate() != null) {
            task.setIsLate(task.getCompletedAt().toLocalDate().isAfter(task.getDueDate()));
        }
    }


/**
     * Lấy tasks của department với filters (cho Manager)
     * 
     * @param departmentId ID phòng ban
     * @param assigneeId   ID nhân viên (null = tất cả)
     * @param statusId     ID trạng thái (null = tất cả)
     * @param priority     Độ ưu tiên: LOW, MEDIUM, HIGH, URGENT (null = tất cả)
     * @param search       Từ khóa tìm kiếm trong title (null = không search)
     * @param page         Số trang (0-based)
     * @param size         Kích thước trang
     * @return Page<TaskResponse>
     */
    @Transactional(readOnly = true)
    public Page<TaskResponse> getDepartmentTasksWithFilters(
            Integer departmentId,
            Integer assigneeId,
            Integer statusId,
            String priority,
            String search,
            int page,
            int size
    ) {
        log.info("Getting department tasks - deptId: {}, assigneeId: {}, statusId: {}, priority: {}, search: {}",
                departmentId, assigneeId, statusId, priority, search);

        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Department", "id", departmentId);
        }

        TaskPriority taskPriority = parsePriority(priority);
        String cleanSearch = cleanSearchString(search);

        // ✅ Giới hạn 6 tháng
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);

        Pageable pageable = PageRequest.of(page, size);

        // ✅ Dùng query mới có fromDate
        Page<Task> tasks = taskRepository.findDepartmentTasksInPeriod(
            departmentId,
            sixMonthsAgo,
            assigneeId,
            statusId,
            taskPriority,
            cleanSearch,
            pageable
        );

        log.info("Found {} tasks for department {} (last 6 months)", tasks.getTotalElements(), departmentId);

        return tasks.map(task -> {
            TaskResponse response = taskMapper.toTaskResponse(task);
            response.setCommentCount(
                commentRepository.countByTaskId(task.getId()).intValue()
            );
            return response;
        });
    }

    /**
     * Lấy tasks của user với filters (cho Employee)
     * 
     * @param assigneeId ID nhân viên
     * @param statusId   ID trạng thái (null = tất cả)
     * @param priority   Độ ưu tiên (null = tất cả)
     * @param search     Từ khóa tìm kiếm (null = không search)
     * @param page       Số trang
     * @param size       Kích thước trang
     * @return Page<TaskResponse>
     */
    @Transactional(readOnly = true)
    public Page<TaskResponse> getUserTasksWithFilters(
            Integer userId,
            Integer statusId,
            String priority,
            String search,
            int page,
            int size
    ) {
        Employee employee = employeeRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee", "userId", userId));

        Integer assigneeId = employee.getId();

        log.info("Getting user tasks - employeeId: {}, statusId: {}, priority: {}, search: {}",
                assigneeId, statusId, priority, search);

        TaskPriority taskPriority = parsePriority(priority);
        String cleanSearch = cleanSearchString(search);

        // ✅ Giới hạn 6 tháng
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);

        Pageable pageable = PageRequest.of(page, size);

        // ✅ Dùng query mới có fromDate
        Page<Task> tasks = taskRepository.findUserTasksInPeriod(
            assigneeId,
            sixMonthsAgo,
            statusId,
            taskPriority,
            cleanSearch,
            pageable
        );

        log.info("Found {} tasks for employee {} (last 6 months)", tasks.getTotalElements(), assigneeId);

        return tasks.map(task -> {
            TaskResponse response = taskMapper.toTaskResponse(task);
            response.setCommentCount(
                commentRepository.countByTaskId(task.getId()).intValue()
            );
            return response;
        });
    }

    /**
     * Lấy tasks cho Board view với filter (Kanban)
     */
    @Transactional(readOnly = true)
    public List<TaskSummaryResponse> getTasksForBoardWithFilter(
            Integer departmentId,
            Integer assigneeId
    ) {
        log.info("Getting board tasks - department: {}, assigneeId: {}", departmentId, assigneeId);

        // ✅ Giới hạn 6 tháng
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);

        List<Task> tasks;

        if (assigneeId != null) {
            tasks = taskRepository.findByAssigneeIdAndIsDeletedFalse(assigneeId)
                .stream()
                .filter(t -> t.getCreatedAt() != null &&
                            t.getCreatedAt().isAfter(sixMonthsAgo))
                .collect(Collectors.toList());
        } else {
            tasks = taskRepository.findByDepartmentIdAndIsDeletedFalse(departmentId)
                .stream()
                .filter(t -> t.getCreatedAt() != null &&
                            t.getCreatedAt().isAfter(sixMonthsAgo))
                .collect(Collectors.toList());
        }

        return tasks.stream()
            .map(task -> {
                TaskSummaryResponse response = taskMapper.toTaskSummaryResponse(task);
                response.setCommentCount(
                    commentRepository.countByTaskId(task.getId()).intValue()
                );
                return response;
            })
            .collect(Collectors.toList());
    }

    // ========== PRIVATE HELPER METHODS ==========

    /**
     * Parse priority string to enum (null-safe)
     */
    private TaskPriority parsePriority(String priority) {
        if (priority == null || priority.trim().isEmpty()) {
            return null;
        }
        try {
            return TaskPriority.valueOf(priority.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid priority value: {}", priority);
            return null;
        }
    }

    /**
     * Clean search string (null-safe)
     */
    private String cleanSearchString(String search) {
        if (search == null || search.trim().isEmpty()) {
            return null;
        }
        return search.trim();
    }
}