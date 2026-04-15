package com.metahrms.employee_management.dto.response.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {

    private LocalDate date;
    private LocalDateTime generatedAt;

    // ====== Tổng quan hôm nay ======
    private Integer totalEmployees;         // Tổng NV active
    private Integer checkedInCount;         // Đã check-in hôm nay
    private Integer checkedOutCount;        // Đã check-out hôm nay
    private Integer notCheckedInCount;      // Chưa check-in (giờ làm đã bắt đầu)
    private Integer stillWorkingCount;      // Đang làm việc (đã in, chưa out)
    private Double  attendanceRate;         // % đi làm hôm nay

    // ====== Phân loại ======
    private Integer presentCount;
    private Integer lateCount;
    private Integer absentCount;
    private Integer leaveCount;
    private Integer earlyLeaveCount;

    // ====== Giờ công hôm nay ======
    private Double totalWorkHoursToday;
    private Double totalOvertimeHoursToday;

    // ====== Anomalies chưa xử lý ======
    private Integer unresolvedAnomalies;
    private Integer criticalAnomalies;

    // ====== Pending approvals ======
    private Integer pendingApprovals;       // Records chưa được duyệt

    // ====== Trend 7 ngày qua ======
    private List<DailyTrendDTO> weeklyTrend;

    // ====== Top departments ======
    private List<DepartmentRankDTO> departmentRanking;

    // ====== Recent activities ======
    private List<RecentActivityDTO> recentActivities;

    // ============================================
    // Nested DTOs
    // ============================================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyTrendDTO {
        private LocalDate date;
        private String dayOfWeek;
        private Integer present;
        private Integer absent;
        private Integer late;
        private Double attendanceRate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartmentRankDTO {
        private Integer deptId;
        private String deptName;
        private Integer totalEmployees;
        private Integer presentCount;
        private Double attendanceRate;
        private Integer lateCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivityDTO {
        private Integer attendanceId;
        private Integer employeeId;
        private String employeeName;
        private String deptName;
        private String action;              // CHECK_IN, CHECK_OUT
        private LocalDateTime time;
        private String status;
        private Double faceMatchScore;
    }
}