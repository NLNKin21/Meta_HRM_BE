package com.metahrms.employee_management.service.payroll;

import com.metahrms.employee_management.dto.request.payroll.*;
import com.metahrms.employee_management.dto.response.payroll.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface PayrollService {

    /**
     * BƯỚC 1: Tạo payslip DRAFT cho tất cả NV (hoặc danh sách cụ thể)
     */
    List<PayslipSummaryDTO> generatePayroll(GeneratePayrollRequest request);

    /**
     * BƯỚC 2: Tính lương cho toàn bộ tháng
     */
    List<PayslipSummaryDTO> calculatePayroll(Integer month, Integer year);

    /**
     * BƯỚC 2b: Tính lại lương cho 1 NV cụ thể
     */
    PayslipSummaryDTO calculateOneEmployee(Integer employeeId, Integer month, Integer year);

    /**
     * BƯỚC 3: Duyệt 1 payslip
     */
    void approvePayslip(Integer payslipId, ApprovePayslipRequest request);

    /**
     * BƯỚC 3b: Duyệt hàng loạt
     */
    void approveAll(Integer month, Integer year);

    /**
     * Từ chối payslip
     */
    void rejectPayslip(Integer payslipId, RejectPayslipRequest request);

    /**
     * BƯỚC 4: Đánh dấu đã trả lương
     */
    void markAsPaid(Integer payslipId);

    /**
     * BƯỚC 4b: Đánh dấu đã trả toàn bộ
     */
    void markAllAsPaid(Integer month, Integer year);

    /**
     * Chỉnh sửa manual 1 payslip (reset về CALCULATED)
     */
    PayslipSummaryDTO editPayslip(Integer payslipId, EditPayslipRequest request);

    /**
     * Lấy danh sách payslip trong tháng (admin)
     */
    List<PayslipSummaryDTO> getPayslipsByPeriod(Integer month, Integer year, String status);

    /**
     * Lấy chi tiết 1 payslip
     */
    PayslipFullDTO getPayslipDetail(Integer payslipId);

    /**
     * Lấy tổng kết tháng lương
     */
    PayrollPeriodSummaryDTO getPeriodSummary(Integer month, Integer year);

    /**
     * Employee: lấy phiếu lương của mình
     */
    List<PayslipSummaryDTO> getMyPayslips(Integer userId);

    /**
     * Employee: phiếu lương mới nhất
     */
    PayslipFullDTO getMyLatestPayslip(Integer userId);

    /**
     * Export Excel bảng lương tháng
     */
    void exportPayrollExcel(Integer month, Integer year, HttpServletResponse response) throws IOException;

    /**
     * Export file chuyển khoản Techcombank
     */
    void exportTechcombank(Integer month, Integer year, HttpServletResponse response) throws IOException;

    /**
     * Employee: xem chi tiết 1 phiếu lương của mình (có kiểm tra ownership)
     */
    PayslipFullDTO getMyPayslipDetail(Integer payslipId, Integer employeeId);
}