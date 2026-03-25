package com.metahrms.employee_management.repository.Task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.Task.TaskHistory;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Integer> {

    @Query("SELECT th FROM TaskHistory th " +
           "JOIN FETCH th.user " +
           "WHERE th.task.id = :taskId " +
           "ORDER BY th.createdAt DESC")
    List<TaskHistory> findByTaskIdOrderByCreatedAtDesc(@Param("taskId") Integer taskId);

    @Query("SELECT th FROM TaskHistory th " +
           "WHERE th.task.id = :taskId " +
           "AND th.fieldName = :fieldName " +
           "ORDER BY th.createdAt DESC")
    List<TaskHistory> findByTaskIdAndFieldName(@Param("taskId") Integer taskId, 
                                                @Param("fieldName") String fieldName);

    @Query("SELECT th FROM TaskHistory th " +
           "WHERE th.task.id = :taskId " +
           "AND th.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY th.createdAt DESC")
    List<TaskHistory> findByTaskIdAndDateRange(@Param("taskId") Integer taskId,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);
}