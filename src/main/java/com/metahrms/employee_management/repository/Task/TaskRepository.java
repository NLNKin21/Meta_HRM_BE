package com.metahrms.employee_management.repository.Task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.Task.Task;
import com.metahrms.employee_management.enums.Task.TaskPriority;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer>, JpaSpecificationExecutor<Task> {

    // ========== TÌM KIẾM CƠ BẢN ==========
    
    Optional<Task> findByTaskCode(String taskCode);

    @Query("SELECT t FROM Task t " +
           "LEFT JOIN FETCH t.status " +
           "LEFT JOIN FETCH t.assignee " +
           "LEFT JOIN FETCH t.reporter " +
           "LEFT JOIN FETCH t.department " +
           "WHERE t.id = :id AND t.isDeleted = false")
    Optional<Task> findByIdWithDetails(@Param("id") Integer id);

    // ========== LỌC THEO DEPARTMENT ==========
    
    @Query("SELECT t FROM Task t WHERE t.department.id = :deptId AND t.isDeleted = false ORDER BY t.createdAt DESC")
    List<Task> findByDepartmentId(@Param("deptId") Integer deptId);

    @Query("SELECT t FROM Task t WHERE t.department.id = :deptId AND t.isDeleted = false")
    Page<Task> findByDepartmentId(@Param("deptId") Integer deptId, Pageable pageable);

    // ========== LỌC THEO ASSIGNEE ==========
    
    @Query("SELECT t FROM Task t WHERE t.assignee.id = :assigneeId AND t.isDeleted = false ORDER BY t.dueDate ASC")
    List<Task> findByAssigneeId(@Param("assigneeId") Integer assigneeId);

    @Query("SELECT t FROM Task t " +
           "WHERE t.assignee.id = :assigneeId " +
           "AND t.status.isCompleted = false " +
           "AND t.isDeleted = false " +
           "ORDER BY t.priority DESC, t.dueDate ASC")
    List<Task> findActiveTasksByAssignee(@Param("assigneeId") Integer assigneeId);

    // ========== LỌC THEO REPORTER ==========
    
    @Query("SELECT t FROM Task t WHERE t.reporter.id = :reporterId AND t.isDeleted = false ORDER BY t.createdAt DESC")
    List<Task> findByReporterId(@Param("reporterId") Integer reporterId);

    // ========== LỌC THEO STATUS ==========
    
    @Query("SELECT t FROM Task t WHERE t.status.id = :statusId AND t.isDeleted = false ORDER BY t.createdAt DESC")
    List<Task> findByStatusId(@Param("statusId") Integer statusId);

    @Query("SELECT t FROM Task t " +
           "WHERE t.status.id = :statusId " +
           "AND t.department.id = :deptId " +
           "AND t.isDeleted = false " +
           "ORDER BY t.createdAt DESC")
    List<Task> findByStatusIdAndDepartmentId(@Param("statusId") Integer statusId, 
                                              @Param("deptId") Integer deptId);

    // ========== LỌC THEO PROJECT ==========
    
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.isDeleted = false ORDER BY t.createdAt DESC")
    List<Task> findByProjectId(@Param("projectId") Integer projectId);

    // ========== DEADLINE & OVERDUE ==========
    
    @Query("SELECT t FROM Task t " +
           "WHERE t.dueDate BETWEEN :startDate AND :endDate " +
           "AND t.status.isCompleted = false " +
           "AND t.isDeleted = false " +
           "ORDER BY t.dueDate ASC")
    List<Task> findTasksDueBetween(@Param("startDate") LocalDate startDate, 
                                    @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM Task t " +
           "WHERE t.dueDate < :today " +
           "AND t.status.isCompleted = false " +
           "AND t.isDeleted = false " +
           "ORDER BY t.dueDate ASC")
    List<Task> findOverdueTasks(@Param("today") LocalDate today);

    @Query("SELECT t FROM Task t " +
           "WHERE t.dueDate = :date " +
           "AND t.status.isCompleted = false " +
           "AND t.isDeleted = false")
    List<Task> findTasksDueOnDate(@Param("date") LocalDate date);

    // ========== STATISTICS ==========
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.department.id = :deptId AND t.isDeleted = false")
    Long countByDepartmentId(@Param("deptId") Integer deptId);

    @Query("SELECT COUNT(t) FROM Task t " +
           "WHERE t.assignee.id = :assigneeId " +
           "AND t.status.isCompleted = false " +
           "AND t.isDeleted = false")
    Long countActiveTasksByAssignee(@Param("assigneeId") Integer assigneeId);

    @Query("SELECT COUNT(t) FROM Task t " +
           "WHERE t.department.id = :deptId " +
           "AND t.status.id = :statusId " +
           "AND t.isDeleted = false")
    Long countByDepartmentAndStatus(@Param("deptId") Integer deptId, 
                                     @Param("statusId") Integer statusId);

    @Query("SELECT COUNT(t) FROM Task t " +
           "WHERE t.isLate = true " +
           "AND t.department.id = :deptId " +
           "AND t.isDeleted = false")
    Long countLateTasksByDepartment(@Param("deptId") Integer deptId);

    // ========== DASHBOARD QUERIES ==========
    
    @Query("SELECT t FROM Task t " +
           "WHERE t.assignee.id = :userId " +
           "AND t.isUrgent = true " +
           "AND t.status.isCompleted = false " +
           "AND t.isDeleted = false " +
           "ORDER BY t.dueDate ASC")
    List<Task> findUrgentTasksByUser(@Param("userId") Integer userId);

     @Query(value = "SELECT * FROM tasks t " +
           "JOIN task_statuses ts ON t.status_id = ts.id " +
           "WHERE t.department_id = :deptId " +
           "AND t.due_date BETWEEN :today AND :nextWeek " +
           "AND ts.is_completed = false " +
           "AND t.is_deleted = false " +
           "ORDER BY t.due_date ASC LIMIT 10", 
           nativeQuery = true)
    List<Task> findTop10UpcomingTasksForDepartment(@Param("deptId") Integer deptId,
                                                    @Param("today") LocalDate today,
                                                    @Param("nextWeek") LocalDate nextWeek);


    // ========== VALIDATION ==========
    
    boolean existsByTaskCode(String taskCode);

    @Query("SELECT COUNT(t) > 0 FROM Task t " +
           "WHERE t.taskCode = :code AND t.id != :excludeId")
    boolean existsByTaskCodeExcludingId(@Param("code") String code, 
                                        @Param("excludeId") Integer excludeId);


       /**
     * Lấy tasks của một assignee (chưa bị xóa)
     */
    @Query("SELECT t FROM Task t WHERE t.assignee.id = :assigneeId AND t.isDeleted = false")
    List<Task> findByAssigneeIdAndIsDeletedFalse(@Param("assigneeId") Integer assigneeId);

    /**
     * Lấy tasks theo department
     */
    @Query("SELECT t FROM Task t WHERE t.department.id = :departmentId AND t.isDeleted = false")
    List<Task> findByDepartmentIdAndIsDeletedFalse(@Param("departmentId") Integer departmentId);

    /**
     * Đếm tasks của assignee
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignee.id = :assigneeId AND t.isDeleted = false")
    Long countByAssigneeId(@Param("assigneeId") Integer assigneeId);

    /**
     * Đếm tasks quá hạn của assignee
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignee.id = :assigneeId " +
           "AND t.isLate = true AND t.isDeleted = false")
    Long countOverdueByAssigneeId(@Param("assigneeId") Integer assigneeId);


    // ========== 🆕 QUERY CHO MANAGER - FILTER TASKS ==========
    
    /**
     * Lấy tasks của department với các filter tùy chọn (cho Manager)
     * - assigneeId: NULL = tất cả nhân viên, có giá trị = chỉ nhân viên đó
     * - statusId: NULL = tất cả trạng thái
     * - priority: NULL = tất cả độ ưu tiên
     * - search: NULL = không search
     */
    @Query("SELECT t FROM Task t " +
           "WHERE t.department.id = :deptId " +
           "AND t.isDeleted = false " +
           "AND (:assigneeId IS NULL OR t.assignee.id = :assigneeId) " +
           "AND (:statusId IS NULL OR t.status.id = :statusId) " +
           "AND (:priority IS NULL OR t.priority = :priority) " +
           "AND (:search IS NULL OR :search = '' OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY t.createdAt DESC")
    Page<Task> findDepartmentTasksWithFilters(
        @Param("deptId") Integer deptId,
        @Param("assigneeId") Integer assigneeId,
        @Param("statusId") Integer statusId,
        @Param("priority") TaskPriority priority,
        @Param("search") String search,
        Pageable pageable
    );

    /**
     * Lấy tasks của user với filters (cho Employee view)
     */
    @Query("SELECT t FROM Task t " +
           "WHERE t.assignee.id = :assigneeId " +
           "AND t.isDeleted = false " +
           "AND (:statusId IS NULL OR t.status.id = :statusId) " +
           "AND (:priority IS NULL OR t.priority = :priority) " +
           "AND (:search IS NULL OR :search = '' OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY t.createdAt DESC")
    Page<Task> findUserTasksWithFilters(
        @Param("assigneeId") Integer assigneeId,
        @Param("statusId") Integer statusId,
        @Param("priority") TaskPriority priority,
        @Param("search") String search,
        Pageable pageable
    );

    /**
     * Đếm tasks theo department + filters (cho statistics)
     */
    @Query("SELECT COUNT(t) FROM Task t " +
           "WHERE t.department.id = :deptId " +
           "AND t.isDeleted = false " +
           "AND (:assigneeId IS NULL OR t.assignee.id = :assigneeId) " +
           "AND (:statusId IS NULL OR t.status.id = :statusId) " +
           "AND (:priority IS NULL OR t.priority = :priority)")
    Long countDepartmentTasksWithFilters(
        @Param("deptId") Integer deptId,
        @Param("assigneeId") Integer assigneeId,
        @Param("statusId") Integer statusId,
        @Param("priority") String priority
    );

    /**
     * Lấy tasks với full details (cho Kanban/Calendar)
     */
    @Query("SELECT t FROM Task t " +
           "LEFT JOIN FETCH t.status " +
           "LEFT JOIN FETCH t.assignee " +
           "LEFT JOIN FETCH t.reporter " +
           "LEFT JOIN FETCH t.department " +
           "WHERE t.department.id = :deptId " +
           "AND t.isDeleted = false " +
           "AND (:assigneeId IS NULL OR t.assignee.id = :assigneeId) " +
           "ORDER BY t.createdAt DESC")
    List<Task> findDepartmentTasksWithDetails(
        @Param("deptId") Integer deptId,
        @Param("assigneeId") Integer assigneeId
    );

    // ========== 🆕 DEPARTMENT STATS QUERIES ==========
    
    /**
     * Đếm tasks đang thực hiện của department
     */
    @Query("SELECT COUNT(t) FROM Task t " +
           "WHERE t.department.id = :deptId " +
           "AND t.status.isCompleted = false " +
           "AND t.status.statusName <> 'Backlog' " +
           "AND t.isDeleted = false")
    Long countInProgressTasksByDepartment(@Param("deptId") Integer deptId);
    /**
     * Đếm tasks đã hoàn thành của department
     */
    @Query("SELECT COUNT(t) FROM Task t " +
           "WHERE t.department.id = :deptId " +
           "AND t.status.isCompleted = true " +
           "AND t.isDeleted = false")
    Long countCompletedTasksByDepartment(@Param("deptId") Integer deptId);

    /**
     * Lấy danh sách tasks quá hạn của department
     */
    @Query("SELECT t FROM Task t " +
           "WHERE t.department.id = :deptId " +
           "AND t.isLate = true " +
           "AND t.status.isCompleted = false " +
           "AND t.isDeleted = false " +
           "ORDER BY t.dueDate ASC")
    List<Task> findOverdueTasksByDepartment(@Param("deptId") Integer deptId);

    /**
     * Lấy tasks urgent của department
     */
    @Query("SELECT t FROM Task t " +
           "WHERE t.department.id = :deptId " +
           "AND t.isUrgent = true " +
           "AND t.status.isCompleted = false " +
           "AND t.isDeleted = false " +
           "ORDER BY t.dueDate ASC")
    List<Task> findUrgentTasksByDepartment(@Param("deptId") Integer deptId);

    /**
     * Đếm tasks pending (chờ xử lý) của department
     */
    @Query("SELECT COUNT(t) FROM Task t " +
           "WHERE t.department.id = :deptId " +
           "AND t.status.statusName = 'Chờ xử lý' " +
           "AND t.isDeleted = false")
    Long countPendingTasksByDepartment(@Param("deptId") Integer deptId);

}