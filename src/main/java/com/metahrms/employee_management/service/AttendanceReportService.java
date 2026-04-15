package com.metahrms.employee_management.service;

import com.metahrms.employee_management.dto.response.report.CompanyDailyReportDTO;
import com.metahrms.employee_management.dto.response.report.CompanyMonthlyReportDTO;
import com.metahrms.employee_management.dto.response.report.DashboardDTO;
import com.metahrms.employee_management.dto.response.report.DepartmentComparisonDTO;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

public interface AttendanceReportService {

    /**
     * Dashboard realtime - hôm nay
     */
    DashboardDTO getDashboard();

    /**
     * Báo cáo ngày toàn công ty
     */
    CompanyDailyReportDTO getDailyReport(LocalDate date);

    /**
     * Báo cáo tháng toàn công ty
     */
    CompanyMonthlyReportDTO getMonthlyReport(int year, int month);

    /**
     * So sánh các phòng ban trong khoảng thời gian
     */
    DepartmentComparisonDTO getDepartmentComparison(LocalDate startDate, LocalDate endDate);

    /**
     * Export Excel - daily report theo phòng ban
     */
    void exportDepartmentMonthlyExcel(
        Integer deptId,
        int year,
        int month,
        HttpServletResponse response
    ) throws IOException;

    /**
     * Export Excel - toàn công ty theo tháng
     */
    void exportCompanyMonthlyExcel(
        int year,
        int month,
        HttpServletResponse response
    ) throws IOException;
}