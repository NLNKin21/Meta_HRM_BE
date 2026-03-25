package com.metahrms.employee_management.repository.Task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.Task.Task;

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

    @Query("SELECT t FROM Task t " +
           "WHERE t.department.id = :deptId " +
           "AND t.dueDate BETWEEN :today AND :nextWeek " +
           "AND t.status.isCompleted = false " +
           "AND t.isDeleted = false " +
           "ORDER BY t.dueDate ASC LIMIT 10")
    List<Task> findUpcomingTasksForDepartment(@Param("deptId") Integer deptId,
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
}