package com.metahrms.employee_management.service.impl.Leave;

import com.metahrms.employee_management.dto.response.Leave.LeavePayrollImpactDto;
import com.metahrms.employee_management.entity.Leave.LeaveRequest;
import com.metahrms.employee_management.entity.Leave.LeaveType;
import com.metahrms.employee_management.enums.Leave.LeaveTypeCode;
import com.metahrms.employee_management.service.Leave.PayrollIntegrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class PayrollIntegrationServiceImpl implements PayrollIntegrationService {

    @Override
    public LeavePayrollImpactDto calculateImpact(LeaveRequest request, BigDecimal dailySalary) {
        LeaveType leaveType = request.getLeaveType();

        if (LeaveTypeCode.MATERNITY_LEAVE.name().equals(leaveType.getCode())) {
            return LeavePayrollImpactDto.builder()
                    .leaveRequestId(request.getId())
                    .leaveTypeCode(leaveType.getCode())
                    .totalDays(request.getTotalDays())
                    .countsInCompanyPayroll(false)
                    .deductSalary(false)
                    .socialInsurancePaid(true)
                    .salaryDeduction(BigDecimal.ZERO)
                    .companyPayrollAmount(BigDecimal.ZERO)
                    .note("Thai sản: Payroll công ty = 0, BHXH chi trả")
                    .build();
        }

        if (Boolean.FALSE.equals(leaveType.getCountsInCompanyPayroll())) {
            return LeavePayrollImpactDto.builder()
                    .leaveRequestId(request.getId())
                    .leaveTypeCode(leaveType.getCode())
                    .totalDays(request.getTotalDays())
                    .countsInCompanyPayroll(false)
                    .deductSalary(leaveType.getDeductSalary())
                    .socialInsurancePaid(leaveType.getSocialInsurancePaid())
                    .salaryDeduction(BigDecimal.ZERO)
                    .companyPayrollAmount(BigDecimal.ZERO)
                    .note("Không tính vào payroll công ty")
                    .build();
        }

        BigDecimal salaryDeduction = BigDecimal.ZERO;
        BigDecimal companyPayrollAmount = dailySalary.multiply(request.getTotalDays());

        if (Boolean.TRUE.equals(leaveType.getDeductSalary())) {
            salaryDeduction = dailySalary.multiply(request.getTotalDays());
            companyPayrollAmount = BigDecimal.ZERO;
        }

        return LeavePayrollImpactDto.builder()
                .leaveRequestId(request.getId())
                .leaveTypeCode(leaveType.getCode())
                .totalDays(request.getTotalDays())
                .countsInCompanyPayroll(true)
                .deductSalary(leaveType.getDeductSalary())
                .socialInsurancePaid(leaveType.getSocialInsurancePaid())
                .salaryDeduction(salaryDeduction)
                .companyPayrollAmount(companyPayrollAmount)
                .note(Boolean.TRUE.equals(leaveType.getDeductSalary()) ? "Nghỉ bị trừ lương" : "Nghỉ có lương")
                .build();
    }

    @Override
    public void handleFinalApprovedLeave(LeaveRequest request) {
        log.info("Process payroll impact for leave request {}", request.getId());
    }
}