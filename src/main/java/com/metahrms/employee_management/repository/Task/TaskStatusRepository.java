package com.metahrms.employee_management.repository.Task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.Task.TaskStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Integer> {

    // Lấy tất cả status active, sắp xếp theo order
    @Query("SELECT ts FROM TaskStatus ts WHERE ts.isActive = true ORDER BY ts.orderIndex ASC")
    List<TaskStatus> findAllActiveOrderByIndex();

    // Lấy status theo department (bao gồm cả common)
    @Query("SELECT ts FROM TaskStatus ts " +
           "WHERE (ts.department.id = :deptId OR ts.department IS NULL) " +
           "AND ts.isActive = true " +
           "ORDER BY ts.orderIndex ASC")
    List<TaskStatus> findByDepartmentIdOrCommon(@Param("deptId") Integer deptId);

    // Lấy status mặc định
    @Query("SELECT ts FROM TaskStatus ts WHERE ts.isDefault = true AND ts.isActive = true")
    Optional<TaskStatus> findDefaultStatus();

    // Lấy status mặc định theo department
    @Query("SELECT ts FROM TaskStatus ts " +
           "WHERE (ts.department.id = :deptId OR ts.department IS NULL) " +
           "AND ts.isDefault = true AND ts.isActive = true " +
           "ORDER BY ts.department.id DESC LIMIT 1")
    Optional<TaskStatus> findDefaultStatusByDepartment(@Param("deptId") Integer deptId);

    // Lấy status "completed"
    @Query("SELECT ts FROM TaskStatus ts WHERE ts.isCompleted = true AND ts.isActive = true")
    List<TaskStatus> findCompletedStatuses();

    // Check tên đã tồn tại trong department
    @Query("SELECT COUNT(ts) > 0 FROM TaskStatus ts " +
           "WHERE ts.statusName = :name " +
           "AND (ts.department.id = :deptId OR ts.department IS NULL)")
    boolean existsByNameInDepartment(@Param("name") String name, @Param("deptId") Integer deptId);
}
