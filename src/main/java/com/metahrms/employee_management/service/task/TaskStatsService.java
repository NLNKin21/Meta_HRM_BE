package com.metahrms.employee_management.service.task;


import com.metahrms.employee_management.dto.response.task.task.TaskResponse;
import com.metahrms.employee_management.dto.response.task.taskstatus.TaskStatsResponse;
import com.metahrms.employee_management.dto.response.task.taskstatus.TaskStatsResponse.DepartmentTaskStats;
import com.metahrms.employee_management.entity.Department;
import com.metahrms.employee_management.entity.Task.TaskStatus;
import com.metahrms.employee_management.mapper.task.TaskMapper;
import com.metahrms.employee_management.repository.DepartmentRepository;
import com.metahrms.employee_management.repository.Task.TaskRepository;
import com.metahrms.employee_management.repository.Task.TaskStatusRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskStatsService {

    private final TaskRepository taskRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final DepartmentRepository departmentRepository;
    private final TaskMapper taskMapper;

    /**
     * Lấy thống kê tổng quan cho Dashboard
     */
    @Transactional(readOnly = true)
    public TaskStatsResponse getDashboardStats(Integer userId, Integer departmentId) {
        log.info("Getting dashboard stats for user: {}, department: {}", userId, departmentId);

        // Total counts
        long totalTasks = taskRepository.count();
        long overdueTasks = taskRepository.findOverdueTasks(LocalDate.now()).size();

        // Get completed statuses
        List<TaskStatus> completedStatuses = taskStatusRepository.findCompletedStatuses();
        Set<Integer> completedStatusIds = completedStatuses.stream()
            .map(TaskStatus::getId)
            .collect(Collectors.toSet());

        // Active vs Completed
        long completedTasks = 0;
        long activeTasks = 0;
        
        List<TaskStatus> allStatuses = taskStatusRepository.findAllActiveOrderByIndex();
        Map<String, Long> tasksByStatus = new LinkedHashMap<>();
        
        for (TaskStatus status : allStatuses) {
            long count = taskRepository.findByStatusId(status.getId()).size();
            tasksByStatus.put(status.getStatusName(), count);
            
            if (completedStatusIds.contains(status.getId())) {
                completedTasks += count;
            } else {
                activeTasks += count;
            }
        }

        // Tasks by priority
        Map<String, Long> tasksByPriority = new LinkedHashMap<>();
        tasksByPriority.put("URGENT", countTasksByPriority("URGENT"));
        tasksByPriority.put("HIGH", countTasksByPriority("HIGH"));
        tasksByPriority.put("MEDIUM", countTasksByPriority("MEDIUM"));
        tasksByPriority.put("LOW", countTasksByPriority("LOW"));

        // Tasks by department
        List<DepartmentTaskStats> tasksByDepartment = getDepartmentStats();

        // Upcoming tasks (next 7 days)
        List<TaskResponse> upcomingTasks = taskRepository
            .findTasksDueBetween(LocalDate.now(), LocalDate.now().plusDays(7))
            .stream()
            .limit(10)
            .map(taskMapper::toTaskResponse)
            .collect(Collectors.toList());

        // User's urgent tasks
        List<TaskResponse> myUrgentTasks = new ArrayList<>();
        if (userId != null) {
            myUrgentTasks = taskRepository.findUrgentTasksByUser(userId)
                .stream()
                .limit(5)
                .map(taskMapper::toTaskResponse)
                .collect(Collectors.toList());
        }

        return TaskStatsResponse.builder()
            .totalTasks(totalTasks)
            .activeTasks(activeTasks)
            .completedTasks(completedTasks)
            .overdueTasks(overdueTasks)
            .tasksByStatus(tasksByStatus)
            .tasksByPriority(tasksByPriority)
            .tasksByDepartment(tasksByDepartment)
            .upcomingTasks(upcomingTasks)
            .myUrgentTasks(myUrgentTasks)
            .build();
    }

    /**
     * Lấy thống kê theo department
     */
    @Transactional(readOnly = true)
    public TaskStatsResponse getStatsByDepartment(Integer departmentId) {
        log.info("Getting stats for department: {}", departmentId);

        long totalTasks = taskRepository.countByDepartmentId(departmentId);
        long overdueTasks = taskRepository.countLateTasksByDepartment(departmentId);

        // Get statuses for this department
        List<TaskStatus> statuses = taskStatusRepository.findByDepartmentIdOrCommon(departmentId);
        
        Map<String, Long> tasksByStatus = new LinkedHashMap<>();
        long completedTasks = 0;
        long activeTasks = 0;

        for (TaskStatus status : statuses) {
            long count = taskRepository.countByDepartmentAndStatus(departmentId, status.getId());
            tasksByStatus.put(status.getStatusName(), count);
            
            if (status.getIsCompleted()) {
                completedTasks += count;
            } else {
                activeTasks += count;
            }
        }

        // Upcoming tasks
        List<TaskResponse> upcomingTasks = taskRepository
            .findUpcomingTasksForDepartment(departmentId, LocalDate.now(), LocalDate.now().plusDays(7))
            .stream()
            .map(taskMapper::toTaskResponse)
            .collect(Collectors.toList());

        return TaskStatsResponse.builder()
            .totalTasks(totalTasks)
            .activeTasks(activeTasks)
            .completedTasks(completedTasks)
            .overdueTasks(overdueTasks)
            .tasksByStatus(tasksByStatus)
            .upcomingTasks(upcomingTasks)
            .build();
    }

    // ========== HELPER METHODS ==========

    private long countTasksByPriority(String priority) {
        // Implement based on your needs - simple example
        return taskRepository.findAll().stream()
            .filter(t -> t.getPriority() != null && t.getPriority().name().equals(priority))
            .filter(t -> !t.getIsDeleted())
            .count();
    }

    private List<DepartmentTaskStats> getDepartmentStats() {
        List<DepartmentTaskStats> stats = new ArrayList<>();
        List<Department> departments = departmentRepository.findAll();

        for (Department dept : departments) {
            long total = taskRepository.countByDepartmentId(dept.getId());
            
            // Count active tasks
            long active = taskRepository.findByDepartmentId(dept.getId()).stream()
                .filter(t -> !t.getStatus().getIsCompleted() && !t.getIsDeleted())
                .count();

            DepartmentTaskStats deptStats = DepartmentTaskStats.builder()
                .departmentId(dept.getId())
                .departmentName(dept.getDeptName())
                .totalTasks(total)
                .activeTasks(active)
                .completedTasks(total - active)
                .build();

            stats.add(deptStats);
        }

        return stats;
    }
}
