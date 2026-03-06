<<<<<<< HEAD
package com.metahrms.employee_management.repository;

import com.metahrms.employee_management.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeIdAndAttendanceDate(Long employeeId, LocalDate date);

    List<Attendance> findByEmployeeIdAndIsDeletedFalseOrderByAttendanceDateDesc(Long employeeId);

    Page<Attendance> findByEmployeeIdAndIsDeletedFalse(Long employeeId, Pageable pageable);

    @Query("SELECT a FROM Attendance a WHERE a.employee.id = :employeeId " +
           "AND a.attendanceDate BETWEEN :startDate AND :endDate AND a.isDeleted = false " +
           "ORDER BY a.attendanceDate DESC")
    List<Attendance> findByEmployeeIdAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM Attendance a WHERE a.attendanceDate = :date AND a.isDeleted = false")
    List<Attendance> findByDate(@Param("date") LocalDate date);

    // Thống kê tổng giờ làm trong tháng
    @Query("SELECT SUM(a.workHours) FROM Attendance a WHERE a.employee.id = :employeeId " +
           "AND MONTH(a.attendanceDate) = :month AND YEAR(a.attendanceDate) = :year " +
           "AND a.isDeleted = false")
    Double sumWorkHoursByMonth(@Param("employeeId") Long employeeId, 
                               @Param("month") int month, 
                               @Param("year") int year);

    // Thống kê giờ làm thêm trong tháng
    @Query("SELECT SUM(a.overtimeHours) FROM Attendance a WHERE a.employee.id = :employeeId " +
           "AND MONTH(a.attendanceDate) = :month AND YEAR(a.attendanceDate) = :year " +
           "AND a.isDeleted = false")
    Double sumOvertimeHoursByMonth(@Param("employeeId") Long employeeId, 
                                   @Param("month") int month, 
                                   @Param("year") int year);

    // Đếm số ngày đi làm trong tháng
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employee.id = :employeeId " +
           "AND MONTH(a.attendanceDate) = :month AND YEAR(a.attendanceDate) = :year " +
           "AND a.checkInTime IS NOT NULL AND a.isDeleted = false")
    Integer countWorkingDaysByMonth(@Param("employeeId") Long employeeId, 
                                    @Param("month") int month, 
                                    @Param("year") int year);
}
=======
// package com.metahrms.employee_management.repository;

// import com.metahrms.employee_management.entity.Attendance;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import java.time.LocalDate;
// import java.util.List;
// import java.util.Optional;

// @Repository
// public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

//     Optional<Attendance> findByEmployeeIdAndAttendanceDate(Long employeeId, LocalDate date);

//     List<Attendance> findByEmployeeIdAndIsDeletedFalseOrderByAttendanceDateDesc(Long employeeId);

//     Page<Attendance> findByEmployeeIdAndIsDeletedFalse(Long employeeId, Pageable pageable);

//     @Query("SELECT a FROM Attendance a WHERE a.employee.id = :employeeId " +
//            "AND a.attendanceDate BETWEEN :startDate AND :endDate AND a.isDeleted = false " +
//            "ORDER BY a.attendanceDate DESC")
//     List<Attendance> findByEmployeeIdAndDateRange(
//             @Param("employeeId") Long employeeId,
//             @Param("startDate") LocalDate startDate,
//             @Param("endDate") LocalDate endDate);

//     @Query("SELECT a FROM Attendance a WHERE a.attendanceDate = :date AND a.isDeleted = false")
//     List<Attendance> findByDate(@Param("date") LocalDate date);

//     // Thống kê tổng giờ làm trong tháng
//     @Query("SELECT SUM(a.workHours) FROM Attendance a WHERE a.employee.id = :employeeId " +
//            "AND MONTH(a.attendanceDate) = :month AND YEAR(a.attendanceDate) = :year " +
//            "AND a.isDeleted = false")
//     Double sumWorkHoursByMonth(@Param("employeeId") Long employeeId, 
//                                @Param("month") int month, 
//                                @Param("year") int year);

//     // Thống kê giờ làm thêm trong tháng
//     @Query("SELECT SUM(a.overtimeHours) FROM Attendance a WHERE a.employee.id = :employeeId " +
//            "AND MONTH(a.attendanceDate) = :month AND YEAR(a.attendanceDate) = :year " +
//            "AND a.isDeleted = false")
//     Double sumOvertimeHoursByMonth(@Param("employeeId") Long employeeId, 
//                                    @Param("month") int month, 
//                                    @Param("year") int year);

//     // Đếm số ngày đi làm trong tháng
//     @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employee.id = :employeeId " +
//            "AND MONTH(a.attendanceDate) = :month AND YEAR(a.attendanceDate) = :year " +
//            "AND a.checkInTime IS NOT NULL AND a.isDeleted = false")
//     Integer countWorkingDaysByMonth(@Param("employeeId") Long employeeId, 
//                                     @Param("month") int month, 
//                                     @Param("year") int year);
// }
>>>>>>> 8c6be7d9d227d6c9e9aae8bb6ebeb4808ead22d3
