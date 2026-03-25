package com.metahrms.employee_management.repository.Task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.Task.TaskStatusTransition;

import java.util.List;

@Repository
public interface TaskStatusTransitionRepository extends JpaRepository<TaskStatusTransition, Integer> {

    @Query("SELECT tst FROM TaskStatusTransition tst " +
           "WHERE tst.fromStatus.id = :fromStatusId " +
           "AND tst.isActive = true")
    List<TaskStatusTransition> findByFromStatusId(@Param("fromStatusId") Integer fromStatusId);

    @Query("SELECT tst FROM TaskStatusTransition tst " +
           "WHERE tst.fromStatus.id = :fromStatusId " +
           "AND (tst.department.id = :deptId OR tst.department IS NULL) " +
           "AND tst.isActive = true")
    List<TaskStatusTransition> findByFromStatusAndDepartment(@Param("fromStatusId") Integer fromStatusId,
                                                             @Param("deptId") Integer deptId);

    @Query("SELECT COUNT(tst) > 0 FROM TaskStatusTransition tst " +
           "WHERE tst.fromStatus.id = :fromId " +
           "AND tst.toStatus.id = :toId " +
           "AND tst.isActive = true")
    boolean isTransitionAllowed(@Param("fromId") Integer fromId, 
                                @Param("toId") Integer toId);
}
