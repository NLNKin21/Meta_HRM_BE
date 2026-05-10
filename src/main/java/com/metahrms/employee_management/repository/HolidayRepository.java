package com.metahrms.employee_management.repository;

import com.metahrms.employee_management.entity.Leave.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    List<Holiday> findByHolidayDateBetweenAndActiveTrue(LocalDate startDate, LocalDate endDate);

     @Query("""
        SELECT h FROM Holiday h
        WHERE h.active = true
          AND h.holidayDate >= :from
          AND h.holidayDate <= :to
        ORDER BY h.holidayDate ASC
    """)
    List<Holiday> findActiveHolidaysInRange(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
}