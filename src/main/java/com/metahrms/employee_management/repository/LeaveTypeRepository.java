package com.metahrms.employee_management.repository;

import com.metahrms.employee_management.entity.Leave.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {
    boolean existsByCode(String code);
    Optional<LeaveType> findByCode(String code);

    @Query("""
        select distinct lt
        from LeaveType lt
        left join fetch lt.seniorityRules
        where lt.id = :id
    """)
    Optional<LeaveType> findByIdWithRules(Long id);

    @Query("""
        select distinct lt
        from LeaveType lt
        left join fetch lt.seniorityRules
    """)
    List<LeaveType> findAllWithRules();
}