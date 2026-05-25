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
import org.springframework.data.domain.PageRequest;
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

    private static final int MONTHS_PERIOD = 6;

    // ✅ Trả LocalDate - nhất quán với Repository
    private LocalDate getSixMonthsAgo() {
        return LocalDate.now().minusMonths(MONTHS_PERIOD);
    }

    // ==================== DASHBOARD STATS (toàn hệ thống) ====================

    @Transactional(readOnly = true)
    public TaskStatsResponse getDashboardStats(Integer userId, Integer departmentId) {
        log.info("Getting dashboard stats - userId: {}, deptId: {}", userId, departmentId);

        // ✅ Nếu có departmentId → trả stats riêng của dept đó
        if (departmentId != null) {
            return getStatsByDepartment(departmentId);
        }

        LocalDate sixMonthsAgo = getSixMonthsAgo();

        // ✅ Dùng startDate (LocalDate) để filter
        long totalTasks = taskRepository.findAll().stream()
            .filter(t -> !t.getIsDeleted())
            .filter(t -> t.getStartDate() != null && !t.getStartDate().isBefore(sixMonthsAgo))
            .count();

        long overdueTasks = taskRepository.findOverdueTasks(LocalDate.now()).stream()
            .filter(t -> t.getStartDate() != null && !t.getStartDate().isBefore(sixMonthsAgo))
            .count();

        List<TaskStatus> completedStatuses = taskStatusRepository.findCompletedStatuses();
        Set<Integer> completedStatusIds = completedStatuses.stream()
            .map(TaskStatus::getId)
            .collect(Collectors.toSet());

        long completedTasks = 0;
        long activeTasks = 0;

        List<TaskStatus> allStatuses = taskStatusRepository.findAllActiveOrderByIndex();
        Map<String, Long> tasksByStatus = new LinkedHashMap<>();

        for (TaskStatus status : allStatuses) {
            long count = taskRepository.findByStatusId(status.getId()).stream()
                .filter(t -> !t.getIsDeleted())
                .filter(t -> t.getStartDate() != null && !t.getStartDate().isBefore(sixMonthsAgo))
                .count();
            tasksByStatus.put(status.getStatusName(), count);
            if (completedStatusIds.contains(status.getId())) completedTasks += count;
            else activeTasks += count;
        }

        Map<String, Long> tasksByPriority = new LinkedHashMap<>();
        tasksByPriority.put("URGENT", countTasksByPriorityInPeriod("URGENT", sixMonthsAgo));
        tasksByPriority.put("HIGH",   countTasksByPriorityInPeriod("HIGH",   sixMonthsAgo));
        tasksByPriority.put("MEDIUM", countTasksByPriorityInPeriod("MEDIUM", sixMonthsAgo));
        tasksByPriority.put("LOW",    countTasksByPriorityInPeriod("LOW",    sixMonthsAgo));

        List<DepartmentTaskStats> tasksByDepartment = getDepartmentStats(sixMonthsAgo);

        List<TaskResponse> upcomingTasks = taskRepository
            .findTasksDueBetween(LocalDate.now(), LocalDate.now().plusDays(7))
            .stream()
            .filter(t -> t.getStartDate() != null && !t.getStartDate().isBefore(sixMonthsAgo))
            .limit(10)
            .map(taskMapper::toTaskResponse)
            .collect(Collectors.toList());

        List<TaskResponse> myUrgentTasks = new ArrayList<>();
        if (userId != null) {
            myUrgentTasks = taskRepository.findUrgentTasksByUser(userId)
                .stream()
                .filter(t -> t.getStartDate() != null && !t.getStartDate().isBefore(sixMonthsAgo))
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

    // ==================== DEPARTMENT STATS ====================

    @Transactional(readOnly = true)
    public TaskStatsResponse getStatsByDepartment(Integer departmentId) {
        log.info("Getting stats for department: {} (last {} months)", departmentId, MONTHS_PERIOD);

        // ✅ LocalDate - nhất quán
        LocalDate sixMonthsAgo = getSixMonthsAgo();

        // ✅ Dùng query mới - lọc 6 tháng + chưa xóa
        long totalTasks     = taskRepository.countByDepartmentIdInPeriod(departmentId, sixMonthsAgo);
        long activeTasks    = taskRepository.countActiveByDepartmentInPeriod(departmentId, sixMonthsAgo);
        long completedTasks = taskRepository.countCompletedByDepartmentInPeriod(departmentId, sixMonthsAgo);
        long overdueTasks   = taskRepository.countOverdueByDepartmentInPeriod(departmentId, sixMonthsAgo);

        log.info("Dept {} ({}m) - total:{}, active:{}, completed:{}, overdue:{}",
            departmentId, MONTHS_PERIOD, totalTasks, activeTasks, completedTasks, overdueTasks);

        // Thống kê theo từng status - lọc 6 tháng
        List<TaskStatus> statuses = taskStatusRepository.findByDepartmentIdOrCommon(departmentId);
        Map<String, Long> tasksByStatus = new LinkedHashMap<>();

        for (TaskStatus status : statuses) {
            long count = taskRepository.countByDepartmentAndStatusInPeriod(
                departmentId, status.getId(), sixMonthsAgo
            );
            tasksByStatus.put(status.getStatusName(), count);
        }

        // ✅ Upcoming tasks - JPQL + Pageable thay native SQL
        List<TaskResponse> upcomingTasks = taskRepository
            .findTop10UpcomingTasksForDepartment(
                departmentId,
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                PageRequest.of(0, 10)
            )
            .stream()
            .filter(t -> t.getStartDate() != null && !t.getStartDate().isBefore(sixMonthsAgo))
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

    // ==================== HELPER METHODS ====================

    // ✅ Dùng startDate (LocalDate) - không cần convert
    private long countTasksByPriorityInPeriod(String priority, LocalDate fromDate) {
        return taskRepository.findAll().stream()
            .filter(t -> !t.getIsDeleted())
            .filter(t -> t.getPriority() != null && t.getPriority().name().equals(priority))
            .filter(t -> t.getStartDate() != null && !t.getStartDate().isBefore(fromDate))
            .count();
    }

    // ✅ Param là LocalDate - nhất quán với Repository
    private List<DepartmentTaskStats> getDepartmentStats(LocalDate fromDate) {
        List<DepartmentTaskStats> stats = new ArrayList<>();
        List<Department> departments = departmentRepository.findAll();

        for (Department dept : departments) {
            try {
                long total     = taskRepository.countByDepartmentIdInPeriod(dept.getId(), fromDate);
                long active    = taskRepository.countActiveByDepartmentInPeriod(dept.getId(), fromDate);
                long completed = taskRepository.countCompletedByDepartmentInPeriod(dept.getId(), fromDate);

                stats.add(DepartmentTaskStats.builder()
                    .departmentId(dept.getId())
                    .departmentName(dept.getDeptName())
                    .totalTasks(total)
                    .activeTasks(active)
                    .completedTasks(completed)
                    .build());
            } catch (Exception e) {
                log.warn("Failed to get stats for dept {}: {}", dept.getId(), e.getMessage());
            }
        }
        return stats;
    }
}