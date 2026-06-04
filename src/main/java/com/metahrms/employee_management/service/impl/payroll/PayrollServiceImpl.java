package com.metahrms.employee_management.service.impl.payroll;

import com.metahrms.employee_management.dto.request.payroll.*;
import com.metahrms.employee_management.dto.response.payroll.*;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.Leave.LeaveRequest;
import com.metahrms.employee_management.entity.Payroll.*;
import com.metahrms.employee_management.entity.Attendance.AttendanceRecord;
import com.metahrms.employee_management.exception.BusinessException;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.repository.*;
import com.metahrms.employee_management.repository.Attendance.AttendanceRecordRepository;
import com.metahrms.employee_management.repository.Payroll.*;
import com.metahrms.employee_management.service.payroll.PayrollService;
import com.metahrms.employee_management.util.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {

    private final PayslipRepository payslipRepository;
    private final PayslipDetailRepository payslipDetailRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final AllowanceRepository allowanceRepository;
    private final BonusRepository bonusRepository;
    private final DeductionRepository deductionRepository;
    private final EmployeeTaxInfoRepository taxInfoRepository;
    private final PayrollConfigRepository configRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    // ============================================================
    // BƯỚC 1: GENERATE - Tạo payslip DRAFT
    // ============================================================

    @Override
    @Transactional
    public List<PayslipSummaryDTO> generatePayroll(GeneratePayrollRequest request) {
        log.info("[PAYROLL] Generating payroll: month={}, year={}", request.getMonth(), request.getYear());

        int month = request.getMonth();
        int year = request.getYear();

        // Lấy danh sách NV
        List<Employee> employees;
        if (request.getEmployeeIds() != null && !request.getEmployeeIds().isEmpty()) {
            employees = employeeRepository.findByIdInAndIsDeletedFalse(request.getEmployeeIds());
        } else {
            employees = employeeRepository.findAll().stream()
                    .filter(e -> !Boolean.TRUE.equals(e.getIsDeleted()))
                    .filter(e -> "ACTIVE".equals(e.getStatus() != null ? e.getStatus().name() : ""))
                    .collect(Collectors.toList());
        }

        List<Payslip> result = new ArrayList<>();

        for (Employee emp : employees) {
            // Kiểm tra đã tạo chưa
            if (payslipRepository.existsByEmployeeIdAndMonthAndYearAndIsDeletedFalse(
                    emp.getId(), month, year)) {
                log.warn("[PAYROLL] Payslip already exists for emp={}, {}/{}", emp.getId(), month, year);
                continue;
            }

            Payslip payslip = Payslip.builder()
                    .employeeId(emp.getId())
                    .month(month)
                    .year(year)
                    .status("DRAFT")
                    .basicSalary(emp.getBasicSalary() != null ? emp.getBasicSalary() : BigDecimal.ZERO)
                    .actualBasicSalary(BigDecimal.ZERO)
                    .totalAllowances(BigDecimal.ZERO)
                    .overtimePay(BigDecimal.ZERO)
                    .totalBonus(BigDecimal.ZERO)
                    .grossSalary(BigDecimal.ZERO)
                    .insuranceSalary(BigDecimal.ZERO)
                    .socialInsurance(BigDecimal.ZERO)
                    .healthInsurance(BigDecimal.ZERO)
                    .unemploymentInsurance(BigDecimal.ZERO)
                    .totalInsurance(BigDecimal.ZERO)
                    .preTaxIncome(BigDecimal.ZERO)
                    .personalDeduction(BigDecimal.ZERO)
                    .dependentDeduction(BigDecimal.ZERO)
                    .taxableIncome(BigDecimal.ZERO)
                    .personalIncomeTax(BigDecimal.ZERO)
                    .latePenalty(BigDecimal.ZERO)
                    .otherDeductions(BigDecimal.ZERO)
                    .totalDeductionAmount(BigDecimal.ZERO)
                    .netSalary(BigDecimal.ZERO)
                    .companySocialInsurance(BigDecimal.ZERO)
                    .companyHealthInsurance(BigDecimal.ZERO)
                    .companyUnemployment(BigDecimal.ZERO)
                    .totalCompanyCost(BigDecimal.ZERO)
                    // Integer fields
                    .standardWorkDays(22)
                    .actualWorkDays(0)
                    .absentDays(0)
                    .totalLateTimes(0)
                    .totalLateMinutes(0)
                    // BigDecimal day fields
                    .paidLeaveDays(BigDecimal.ZERO)
                    .unpaidLeaveDays(BigDecimal.ZERO)
                    .overtimeHoursWeekday(BigDecimal.ZERO)
                    .overtimeHoursWeekend(BigDecimal.ZERO)
                    .overtimeHoursHoliday(BigDecimal.ZERO)
                    .totalOvertimeHours(BigDecimal.ZERO)
                    .build();

            result.add(payslipRepository.save(payslip));
            log.info("[PAYROLL] Created DRAFT payslip for emp={}", emp.getId());
        }

        log.info("[PAYROLL] Generated {} payslips for {}/{}", result.size(), month, year);

        return payslipRepository.findByMonthAndYearAndIsDeletedFalse(month, year)
                .stream().map(this::toSummaryDTO).collect(Collectors.toList());
    }

    // ============================================================
    // BƯỚC 2: CALCULATE - Tính lương
    // ============================================================

    @Override
    @Transactional
    public List<PayslipSummaryDTO> calculatePayroll(Integer month, Integer year) {
        log.info("[PAYROLL] Calculating payroll: {}/{}", month, year);

        List<Payslip> payslips = payslipRepository.findByMonthAndYearAndIsDeletedFalse(month, year);

        if (payslips.isEmpty()) {
            throw new IllegalStateException("No payslips found for " + month + "/" + year +
                    ". Please generate payroll first.");
        }

        // Load config 1 lần (tránh N+1)
        Map<String, BigDecimal> config = loadConfig();

        List<PayslipSummaryDTO> results = new ArrayList<>();
        for (Payslip payslip : payslips) {
            if ("PAID".equals(payslip.getStatus()) || "APPROVED".equals(payslip.getStatus())) {
                log.warn("[PAYROLL] Skipping emp={}, status={}", payslip.getEmployeeId(), payslip.getStatus());
                continue;
            }
            try {
                Payslip calculated = calculateSinglePayslip(payslip, config);
                results.add(toSummaryDTO(calculated));
            } catch (Exception e) {
                log.error("[PAYROLL] Failed to calculate emp={}: {}", payslip.getEmployeeId(), e.getMessage());
            }
        }

        log.info("[PAYROLL] Calculated {} payslips", results.size());
        return results;
    }

    @Override
    @Transactional
    public PayslipSummaryDTO calculateOneEmployee(Integer employeeId, Integer month, Integer year) {
        log.info("[PAYROLL] Re-calculating emp={}, {}/{}", employeeId, month, year);

        Payslip payslip = payslipRepository
                .findByEmployeeIdAndMonthAndYearAndIsDeletedFalse(employeeId, month, year)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payslip not found for emp=" + employeeId + " " + month + "/" + year));

        if ("PAID".equals(payslip.getStatus())) {
            throw new IllegalStateException("Cannot recalculate PAID payslip");
        }

        Map<String, BigDecimal> config = loadConfig();
        return toSummaryDTO(calculateSinglePayslip(payslip, config));
    }

    /**
     * Core: Tính lương cho 1 nhân viên
     */
    private Payslip calculateSinglePayslip(Payslip payslip, Map<String, BigDecimal> config) {
        int empId    = payslip.getEmployeeId();
        int month    = payslip.getMonth();
        int year     = payslip.getYear();

        log.debug("[PAYROLL] Calculating emp={}, {}/{}", empId, month, year);

        Employee employee = employeeRepository.findByIdAndIsDeletedFalse(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + empId));

        // Xoá detail cũ
        payslipDetailRepository.deleteByPayslipId(payslip.getId());
        List<PayslipDetail> details = new ArrayList<>();

        // ======================================================
        // BƯỚC A: NGÀY CÔNG
        // ======================================================

        int standardWorkDays = config.getOrDefault("STANDARD_WORK_DAYS", BigDecimal.valueOf(22)).intValue();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate   = YearMonth.of(year, month).atEndOfMonth();

        // Lấy attendance trong tháng
        List<AttendanceRecord> attendances = attendanceRecordRepository
                .findByEmployeeIdAndDateBetween(empId, startDate, endDate);

        // Đếm ngày đi làm thực tế
        int actualWorkDays = (int) attendances.stream()
                .filter(a -> a.getStatus() != null)
                .filter(a -> List.of("PRESENT", "LATE", "EARLY_LEAVE").contains(a.getStatus().name()))
                .count();

        // Tính OT theo loại ngày
        BigDecimal otWeekday = BigDecimal.ZERO;
        BigDecimal otWeekend = BigDecimal.ZERO;
        BigDecimal otHoliday = BigDecimal.ZERO;

        for (AttendanceRecord ar : attendances) {
            if (ar.getOvertimeHours() == null || ar.getOvertimeHours().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            DayOfWeek dow = ar.getDate().getDayOfWeek();
            if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
                otWeekend = otWeekend.add(ar.getOvertimeHours());
            } else {
                otWeekday = otWeekday.add(ar.getOvertimeHours());
            }
        }
        BigDecimal totalOT = otWeekday.add(otWeekend).add(otHoliday);

        // Tổng phút trễ và số lần trễ
        int totalLateTimes = 0;
        int totalLateMinutes = 0;
        int latePenaltyThreshold = config.getOrDefault("LATE_THRESHOLD_MINUTES", BigDecimal.valueOf(15)).intValue();

        for (AttendanceRecord ar : attendances) {
            if (ar.getLateMinutes() != null && ar.getLateMinutes() >= latePenaltyThreshold) {
                totalLateTimes++;
                totalLateMinutes += ar.getLateMinutes();
            }
        }

        // Lấy nghỉ phép đã approved trong tháng
        List<LeaveRequest> leaveRequests = leaveRequestRepository
                .findApprovedByEmployeeAndPeriod(empId, startDate, endDate);

        BigDecimal paidLeaveDays   = BigDecimal.ZERO;
        BigDecimal unpaidLeaveDays = BigDecimal.ZERO;

        for (LeaveRequest lr : leaveRequests) {
            BigDecimal days = lr.getTotalDays() != null ? lr.getTotalDays() : BigDecimal.ZERO;
            boolean isPaid = lr.getLeaveType() != null
                    && Boolean.TRUE.equals(lr.getLeaveType().getPaidLeave());
            if (isPaid) {
                paidLeaveDays = paidLeaveDays.add(days);
            } else {
                unpaidLeaveDays = unpaidLeaveDays.add(days);
            }
        }

        int absentDays = Math.max(0, standardWorkDays
                - actualWorkDays
                - paidLeaveDays.intValue()
                - unpaidLeaveDays.intValue());

        // ======================================================
        // BƯỚC B: THU NHẬP
        // ======================================================

        BigDecimal basicSalary = employee.getBasicSalary() != null
                ? employee.getBasicSalary() : BigDecimal.ZERO;

        // Lương CB thực tế = basicSalary × (ngày đi làm + nghỉ phép có lương) / ngày chuẩn
        BigDecimal workedAndPaid = BigDecimal.valueOf(actualWorkDays).add(paidLeaveDays);
        BigDecimal actualBasicSalary = basicSalary
                .multiply(workedAndPaid)
                .divide(BigDecimal.valueOf(standardWorkDays), 2, RoundingMode.HALF_UP);

        details.add(buildDetail(payslip.getId(), "EARNING", "BASIC_SALARY",
                "Lương cơ bản thực tế", actualBasicSalary,
                workedAndPaid, BigDecimal.ONE, 10));

        // Phụ cấp active trong tháng
        List<Allowance> allowances = allowanceRepository
                .findActiveByEmployeeIdAndDate(empId, endDate);

        BigDecimal totalAllowances = BigDecimal.ZERO;
        int sortOrder = 20;
        for (Allowance a : allowances) {
            totalAllowances = totalAllowances.add(a.getAmount());
            details.add(buildDetail(payslip.getId(), "EARNING",
                    "ALLOWANCE_" + a.getAllowanceType(),
                    a.getName(), a.getAmount(), null, null, sortOrder++));
        }

        // Lương OT
        BigDecimal hourlyRate = basicSalary
                .divide(BigDecimal.valueOf(standardWorkDays), 10, RoundingMode.HALF_UP)
                .divide(config.getOrDefault("STANDARD_WORK_HOURS", BigDecimal.valueOf(8)), 10, RoundingMode.HALF_UP);

        BigDecimal otRateWeekday = config.getOrDefault("OT_RATE_WEEKDAY", BigDecimal.valueOf(1.5));
        BigDecimal otRateWeekend = config.getOrDefault("OT_RATE_WEEKEND", BigDecimal.valueOf(2.0));
        BigDecimal otRateHoliday = config.getOrDefault("OT_RATE_HOLIDAY", BigDecimal.valueOf(3.0));

        BigDecimal otPayWeekday = otWeekday.multiply(hourlyRate).multiply(otRateWeekday)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal otPayWeekend = otWeekend.multiply(hourlyRate).multiply(otRateWeekend)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal otPayHoliday = otHoliday.multiply(hourlyRate).multiply(otRateHoliday)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal overtimePay = otPayWeekday.add(otPayWeekend).add(otPayHoliday);

        if (overtimePay.compareTo(BigDecimal.ZERO) > 0) {
            if (otWeekday.compareTo(BigDecimal.ZERO) > 0) {
                details.add(buildDetail(payslip.getId(), "EARNING", "OT_WEEKDAY",
                        "Lương OT ngày thường (" + otRateWeekday + "x)",
                        otPayWeekday, otWeekday, otRateWeekday, 30));
            }
            if (otWeekend.compareTo(BigDecimal.ZERO) > 0) {
                details.add(buildDetail(payslip.getId(), "EARNING", "OT_WEEKEND",
                        "Lương OT cuối tuần (" + otRateWeekend + "x)",
                        otPayWeekend, otWeekend, otRateWeekend, 31));
            }
            if (otHoliday.compareTo(BigDecimal.ZERO) > 0) {
                details.add(buildDetail(payslip.getId(), "EARNING", "OT_HOLIDAY",
                        "Lương OT ngày lễ (" + otRateHoliday + "x)",
                        otPayHoliday, otHoliday, otRateHoliday, 32));
            }
        }

        // Thưởng đã duyệt trong tháng
        BigDecimal totalBonus = bonusRepository
                .findByEmployeeIdAndMonthAndYearAndIsApprovedTrueAndIsDeletedFalse(empId, month, year)
                .stream()
                .map(Bonus::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        bonusRepository
                .findByEmployeeIdAndMonthAndYearAndIsApprovedTrueAndIsDeletedFalse(empId, month, year)
                .forEach(b -> details.add(buildDetail(payslip.getId(), "EARNING",
                        "BONUS_" + b.getBonusType(), b.getName(), b.getAmount(), null, null, 40)));

        // GROSS
        BigDecimal grossSalary = actualBasicSalary
                .add(totalAllowances)
                .add(overtimePay)
                .add(totalBonus);

        // ======================================================
        // BƯỚC C: BẢO HIỂM
        // ======================================================

        // Tính mức đóng BH = basicSalary + phụ cấp có is_insurance=true
        BigDecimal insuranceAllowances = allowanceRepository
                .findInsuranceAllowances(empId, endDate)
                .stream()
                .map(Allowance::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal baseInsuranceSalary = basicSalary.add(insuranceAllowances);

        // Override nếu NV có khai báo riêng
        EmployeeTaxInfo taxInfo = taxInfoRepository
                .findByEmployeeIdAndIsDeletedFalse(empId)
                .orElse(null);

        if (taxInfo != null && taxInfo.getSocialInsuranceSalary() != null) {
            baseInsuranceSalary = taxInfo.getSocialInsuranceSalary();
        }

        // Áp trần BH = 46,800,000 (cấu hình)
        BigDecimal maxInsuranceBase = config.getOrDefault("MAX_INSURANCE_BASE",
                BigDecimal.valueOf(46_800_000));
        BigDecimal insuranceSalary = baseInsuranceSalary.min(maxInsuranceBase);

        BigDecimal bhxhRate = config.getOrDefault("BHXH_EMPLOYEE_RATE", BigDecimal.valueOf(0.08));
        BigDecimal bhytRate = config.getOrDefault("BHYT_EMPLOYEE_RATE", BigDecimal.valueOf(0.015));
        BigDecimal bhtnRate = config.getOrDefault("BHTN_EMPLOYEE_RATE", BigDecimal.valueOf(0.01));

        BigDecimal socialInsurance      = insuranceSalary.multiply(bhxhRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal healthInsurance      = insuranceSalary.multiply(bhytRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal unemploymentInsurance = insuranceSalary.multiply(bhtnRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalInsurance       = socialInsurance.add(healthInsurance).add(unemploymentInsurance);

        details.add(buildDetail(payslip.getId(), "DEDUCTION", "BHXH_EMPLOYEE",
                "BHXH nhân viên đóng (8%)", socialInsurance,
                null, bhxhRate, 50));
        details.add(buildDetail(payslip.getId(), "DEDUCTION", "BHYT_EMPLOYEE",
                "BHYT nhân viên đóng (1.5%)", healthInsurance,
                null, bhytRate, 51));
        details.add(buildDetail(payslip.getId(), "DEDUCTION", "BHTN_EMPLOYEE",
                "BHTN nhân viên đóng (1%)", unemploymentInsurance,
                null, bhtnRate, 52));

        // Chi phí công ty
        BigDecimal cBhxhRate = config.getOrDefault("BHXH_COMPANY_RATE", BigDecimal.valueOf(0.175));
        BigDecimal cBhytRate = config.getOrDefault("BHYT_COMPANY_RATE", BigDecimal.valueOf(0.03));
        BigDecimal cBhtnRate = config.getOrDefault("BHTN_COMPANY_RATE", BigDecimal.valueOf(0.01));

        BigDecimal companySI = insuranceSalary.multiply(cBhxhRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal companyHI = insuranceSalary.multiply(cBhytRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal companyUI = insuranceSalary.multiply(cBhtnRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalCompanyCost = grossSalary.add(companySI).add(companyHI).add(companyUI);

        // ======================================================
        // BƯỚC D: THUẾ TNCN (Lũy tiến)
        // ======================================================

        BigDecimal personalDeduction = config.getOrDefault("PERSONAL_DEDUCTION",
                BigDecimal.valueOf(11_000_000));

        int dependents = (taxInfo != null && taxInfo.getNumberOfDependents() != null)
                ? taxInfo.getNumberOfDependents() : 0;
        BigDecimal dependentDeductionPerPerson = config.getOrDefault("DEPENDENT_DEDUCTION",
                BigDecimal.valueOf(4_400_000));
        BigDecimal dependentDeduction = dependentDeductionPerPerson.multiply(BigDecimal.valueOf(dependents));

        // Thu nhập trước thuế = Gross - BH NV
        BigDecimal preTaxIncome = grossSalary.subtract(totalInsurance);

        // Thu nhập chịu thuế = preTax - giảm trừ bản thân - giảm trừ người phụ thuộc
        BigDecimal taxableIncome = preTaxIncome
                .subtract(personalDeduction)
                .subtract(dependentDeduction);

        BigDecimal pit = calculatePIT(taxableIncome);

        if (pit.compareTo(BigDecimal.ZERO) > 0) {
            details.add(buildDetail(payslip.getId(), "DEDUCTION", "PIT",
                    "Thuế thu nhập cá nhân", pit, null, null, 60));
        }

        // ======================================================
        // BƯỚC E: KHẤU TRỪ KHÁC
        // ======================================================

        // Phạt đi trễ
        BigDecimal penaltyPerTime = config.getOrDefault("LATE_PENALTY_PER_TIME",
                BigDecimal.valueOf(50_000));
        BigDecimal latePenalty = penaltyPerTime.multiply(BigDecimal.valueOf(totalLateTimes));

        if (latePenalty.compareTo(BigDecimal.ZERO) > 0) {
            details.add(buildDetail(payslip.getId(), "DEDUCTION", "LATE_PENALTY",
                    "Phạt đi trễ (" + totalLateTimes + " lần × " +
                            String.format("%,.0f", penaltyPerTime.doubleValue()) + "đ)",
                    latePenalty, BigDecimal.valueOf(totalLateTimes), penaltyPerTime, 70));
        }

        // Khấu trừ khác đã approved
        BigDecimal otherDeductions = deductionRepository
                .findByEmployeeIdAndMonthAndYearAndIsApprovedTrueAndIsDeletedFalse(empId, month, year)
                .stream()
                .map(Deduction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        deductionRepository
                .findByEmployeeIdAndMonthAndYearAndIsApprovedTrueAndIsDeletedFalse(empId, month, year)
                .forEach(d -> details.add(buildDetail(payslip.getId(), "DEDUCTION",
                        "DEDUCTION_" + d.getDeductionType(), d.getName(), d.getAmount(),
                        null, null, 80)));

        // ======================================================
        // BƯỚC F: KẾT QUẢ
        // ======================================================

        BigDecimal totalDeductionAmount = totalInsurance
                .add(pit)
                .add(latePenalty)
                .add(otherDeductions);

        BigDecimal netSalary = grossSalary.subtract(totalDeductionAmount);
        // Net không âm
        if (netSalary.compareTo(BigDecimal.ZERO) < 0) netSalary = BigDecimal.ZERO;

        // ======================================================
        // Cập nhật Payslip
        // ======================================================

        payslip.setStandardWorkDays(standardWorkDays);
        payslip.setActualWorkDays(actualWorkDays);
        payslip.setPaidLeaveDays(paidLeaveDays);
        payslip.setUnpaidLeaveDays(unpaidLeaveDays);
        payslip.setAbsentDays(absentDays);
        payslip.setOvertimeHoursWeekday(otWeekday);
        payslip.setOvertimeHoursWeekend(otWeekend);
        payslip.setOvertimeHoursHoliday(otHoliday);
        payslip.setTotalOvertimeHours(totalOT);
        payslip.setTotalLateTimes(totalLateTimes);
        payslip.setTotalLateMinutes(totalLateMinutes);

        payslip.setBasicSalary(basicSalary);
        payslip.setActualBasicSalary(actualBasicSalary);
        payslip.setTotalAllowances(totalAllowances);
        payslip.setOvertimePay(overtimePay);
        payslip.setTotalBonus(totalBonus);
        payslip.setGrossSalary(grossSalary);

        payslip.setInsuranceSalary(insuranceSalary);
        payslip.setSocialInsurance(socialInsurance);
        payslip.setHealthInsurance(healthInsurance);
        payslip.setUnemploymentInsurance(unemploymentInsurance);
        payslip.setTotalInsurance(totalInsurance);

        payslip.setPreTaxIncome(preTaxIncome);
        payslip.setPersonalDeduction(personalDeduction);
        payslip.setDependentDeduction(dependentDeduction);
        payslip.setTaxableIncome(taxableIncome.max(BigDecimal.ZERO));
        payslip.setPersonalIncomeTax(pit);

        payslip.setLatePenalty(latePenalty);
        payslip.setOtherDeductions(otherDeductions);
        payslip.setTotalDeductionAmount(totalDeductionAmount);
        payslip.setNetSalary(netSalary);

        payslip.setCompanySocialInsurance(companySI);
        payslip.setCompanyHealthInsurance(companyHI);
        payslip.setCompanyUnemployment(companyUI);
        payslip.setTotalCompanyCost(totalCompanyCost);

        payslip.setStatus("CALCULATED");
        payslip.setCalculatedBy(SecurityUtils.getCurrentUserId());
        payslip.setCalculatedAt(LocalDateTime.now());

        Payslip saved = payslipRepository.save(payslip);

        // Lưu details
        details.forEach(d -> d.setPayslipId(saved.getId()));
        payslipDetailRepository.saveAll(details);

        log.info("[PAYROLL] Calculated emp={}: gross={}, net={}, pit={}",
                empId, grossSalary, netSalary, pit);

        return saved;
    }

    // ============================================================
    // BƯỚC 3: APPROVE / REJECT
    // ============================================================

    @Override
    @Transactional
    public void approvePayslip(Integer payslipId, ApprovePayslipRequest request) {
        Payslip payslip = getPayslipById(payslipId);

        if (!"CALCULATED".equals(payslip.getStatus())) {
            throw new IllegalStateException(
                "Only CALCULATED payslip can be approved. Current: " + payslip.getStatus());
        }

        payslip.setStatus("APPROVED");
        payslip.setApprovedBy(SecurityUtils.getCurrentUserId());
        payslip.setApprovedAt(LocalDateTime.now());
        if (request.getNote() != null) payslip.setNote(request.getNote());

        payslipRepository.save(payslip);
        log.info("[PAYROLL] Approved payslip id={}", payslipId);
    }

    @Override
    @Transactional
    public void approveAll(Integer month, Integer year) {
        log.info("[PAYROLL] Approving all payslips for {}/{}", month, year);
        Integer userId = SecurityUtils.getCurrentUserId();

        List<Payslip> payslips = payslipRepository
                .findByMonthAndYearAndStatusAndIsDeletedFalse(month, year, "CALCULATED");

        payslips.forEach(p -> {
            p.setStatus("APPROVED");
            p.setApprovedBy(userId);
            p.setApprovedAt(LocalDateTime.now());
        });

        payslipRepository.saveAll(payslips);
        log.info("[PAYROLL] Approved {} payslips", payslips.size());
    }

    @Override
    @Transactional
    public void rejectPayslip(Integer payslipId, RejectPayslipRequest request) {
        Payslip payslip = getPayslipById(payslipId);

        if ("PAID".equals(payslip.getStatus())) {
            throw new IllegalStateException("Cannot reject PAID payslip");
        }

        payslip.setStatus("REJECTED");
        payslip.setRejectedBy(SecurityUtils.getCurrentUserId());
        payslip.setRejectedAt(LocalDateTime.now());
        payslip.setRejectReason(request.getReason());

        payslipRepository.save(payslip);
        log.info("[PAYROLL] Rejected payslip id={}, reason={}", payslipId, request.getReason());
    }

    // ============================================================
    // BƯỚC 4: PAY
    // ============================================================

    @Override
    @Transactional
    public void markAsPaid(Integer payslipId) {
        Payslip payslip = getPayslipById(payslipId);

        if (!"APPROVED".equals(payslip.getStatus())) {
            throw new IllegalStateException("Only APPROVED payslip can be marked as PAID");
        }

        payslip.setStatus("PAID");
        payslip.setPaidAt(LocalDateTime.now());
        payslipRepository.save(payslip);
        log.info("[PAYROLL] Marked as PAID: id={}", payslipId);
    }

    @Override
    @Transactional
    public void markAllAsPaid(Integer month, Integer year) {
        List<Payslip> approved = payslipRepository
                .findByMonthAndYearAndStatusAndIsDeletedFalse(month, year, "APPROVED");

        approved.forEach(p -> {
            p.setStatus("PAID");
            p.setPaidAt(LocalDateTime.now());
        });

        payslipRepository.saveAll(approved);
        log.info("[PAYROLL] Marked {} payslips as PAID for {}/{}", approved.size(), month, year);
    }

    // ============================================================
    // EDIT MANUAL
    // ============================================================

    @Override
    @Transactional
    public PayslipSummaryDTO editPayslip(Integer payslipId, EditPayslipRequest request) {
        Payslip payslip = getPayslipById(payslipId);

        if ("PAID".equals(payslip.getStatus())) {
            throw new IllegalStateException("Cannot edit PAID payslip");
        }

        // Override các field nếu được cung cấp
        if (request.getTotalBonus() != null) {
            payslip.setTotalBonus(request.getTotalBonus());
        }
        if (request.getOtherDeductions() != null) {
            payslip.setOtherDeductions(request.getOtherDeductions());
        }
        if (request.getOvertimePay() != null) {
            payslip.setOvertimePay(request.getOvertimePay());
        }

        // Tính lại gross và net
        BigDecimal gross = payslip.getActualBasicSalary()
                .add(payslip.getTotalAllowances())
                .add(payslip.getOvertimePay())
                .add(payslip.getTotalBonus());
        payslip.setGrossSalary(gross);

        BigDecimal totalDeduction = payslip.getTotalInsurance()
                .add(payslip.getPersonalIncomeTax())
                .add(payslip.getLatePenalty())
                .add(payslip.getOtherDeductions());
        payslip.setTotalDeductionAmount(totalDeduction);
        payslip.setNetSalary(gross.subtract(totalDeduction).max(BigDecimal.ZERO));

        if (request.getNote() != null) payslip.setNote(request.getNote());

        // Reset về CALCULATED (cần duyệt lại)
        payslip.setStatus("CALCULATED");
        payslip.setApprovedBy(null);
        payslip.setApprovedAt(null);

        return toSummaryDTO(payslipRepository.save(payslip));
    }

    // ============================================================
    // QUERY
    // ============================================================

    @Override
    public List<PayslipSummaryDTO> getPayslipsByPeriod(Integer month, Integer year, String status) {
        List<Payslip> payslips;
        if (status != null && !status.isBlank()) {
            payslips = payslipRepository.findByMonthAndYearAndStatusAndIsDeletedFalse(month, year, status.toUpperCase());
        } else {
            payslips = payslipRepository.findByMonthAndYearAndIsDeletedFalse(month, year);
        }
        return payslips.stream().map(this::toSummaryDTO).collect(Collectors.toList());
    }

    @Override
    public PayslipFullDTO getPayslipDetail(Integer payslipId) {
        Payslip payslip = getPayslipById(payslipId);
        return toFullDTO(payslip);
    }

    

    @Override
    public PayrollPeriodSummaryDTO getPeriodSummary(Integer month, Integer year) {
        List<Payslip> payslips = payslipRepository.findByMonthAndYearAndIsDeletedFalse(month, year);

        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalNet   = BigDecimal.ZERO;
        BigDecimal totalIns   = BigDecimal.ZERO;
        BigDecimal totalPIT   = BigDecimal.ZERO;
        BigDecimal totalBonus = BigDecimal.ZERO;
        BigDecimal totalOT    = BigDecimal.ZERO;
        BigDecimal totalCost  = BigDecimal.ZERO;

        int draft = 0, calculated = 0, approved = 0, paid = 0, rejected = 0;

        for (Payslip p : payslips) {
        switch (p.getStatus() != null ? p.getStatus() : "") {
            case "DRAFT"      -> draft++;
            case "CALCULATED" -> calculated++;
            case "APPROVED"   -> approved++;
            case "PAID"       -> paid++;
            case "REJECTED"   -> rejected++;
            }
            totalGross = totalGross.add(safe(p.getGrossSalary()));
            totalNet   = totalNet.add(safe(p.getNetSalary()));
            totalIns   = totalIns.add(safe(p.getTotalInsurance()));
            totalPIT   = totalPIT.add(safe(p.getPersonalIncomeTax()));
            totalBonus = totalBonus.add(safe(p.getTotalBonus()));
            totalOT    = totalOT.add(safe(p.getOvertimePay()));
            totalCost  = totalCost.add(safe(p.getTotalCompanyCost()));
        }

        // By department
        List<Employee> allEmps = employeeRepository.findAll().stream()
                .filter(e -> !Boolean.TRUE.equals(e.getIsDeleted()))
                .collect(Collectors.toList());

        Map<Integer, List<Payslip>> byDept = new HashMap<>();
        for (Payslip p : payslips) {
            Integer deptId = allEmps.stream()
                    .filter(e -> e.getId().equals(p.getEmployeeId()))
                    .map(Employee::getDeptId)
                    .findFirst().orElse(null);
            if (deptId != null) {
                byDept.computeIfAbsent(deptId, k -> new ArrayList<>()).add(p);
            }
        }

        List<PayrollPeriodSummaryDTO.DeptPayrollSummaryDTO> deptList = new ArrayList<>();
        byDept.forEach((deptId, deptPayslips) -> {
            String deptName = departmentRepository.findByIdAndIsDeletedFalse(deptId)
                    .map(d -> d.getDeptName()).orElse("Unknown");

            BigDecimal deptNet = deptPayslips.stream()
                    .map(ps -> safe(ps.getNetSalary()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal deptGross = deptPayslips.stream()
                    .map(ps -> safe(ps.getGrossSalary()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal deptCost = deptPayslips.stream()
                    .map(ps -> safe(ps.getTotalCompanyCost()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            deptList.add(PayrollPeriodSummaryDTO.DeptPayrollSummaryDTO.builder()
                    .deptId(deptId)
                    .deptName(deptName)
                    .employeeCount(deptPayslips.size())
                    .totalNetSalary(deptNet)
                    .totalGrossSalary(deptGross)
                    .totalCompanyCost(deptCost)
                    .build());
        });

        deptList.sort(Comparator.comparing(
                PayrollPeriodSummaryDTO.DeptPayrollSummaryDTO::getTotalNetSalary).reversed());

        String monthName = Month.of(month).getDisplayName(TextStyle.FULL, new Locale("vi", "VN"));

        return PayrollPeriodSummaryDTO.builder()
                .month(month).year(year).monthName(monthName)
                .totalEmployees(payslips.size())
                .draftCount(draft).calculatedCount(calculated)
                .approvedCount(approved).paidCount(paid).rejectedCount(rejected)
                .totalGrossSalary(totalGross).totalNetSalary(totalNet)
                .totalInsurance(totalIns).totalPIT(totalPIT)
                .totalBonus(totalBonus).totalOvertimePay(totalOT)
                .totalCompanyCost(totalCost)
                .byDepartment(deptList)
                .build();
    }

    @Override
    public List<PayslipSummaryDTO> getMyPayslips(Integer userId) {
        Employee employee = employeeRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for userId: " + userId));

        return payslipRepository.findApprovedByEmployeeId(employee.getId())
                .stream().map(this::toSummaryDTO).collect(Collectors.toList());
    }

    @Override
    public PayslipFullDTO getMyLatestPayslip(Integer userId) {
        Employee employee = employeeRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for userId: " + userId));

        Payslip payslip = payslipRepository.findLatestApprovedByEmployeeId(employee.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No approved payslip found"));

        return toFullDTO(payslip);
    }

    // ============================================================
    // EXPORT EXCEL
    // ============================================================

    @Override
    public void exportPayrollExcel(Integer month, Integer year, HttpServletResponse response) throws IOException {
        log.info("[PAYROLL] Exporting Excel: {}/{}", month, year);

        List<Payslip> payslips = payslipRepository.findByMonthAndYearAndIsDeletedFalse(month, year);

        if (payslips.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                "{\"success\":false,\"message\":\"Chưa có dữ liệu lương tháng " + month + "/" + year + "\"}"
            );
            return;
        }

        String fileName = String.format("payroll_%d_%02d.xlsx", year, month);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Bảng lương");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle  = createTitleStyle(workbook);
            CellStyle moneyStyle  = createMoneyStyle(workbook);
            CellStyle dataStyle   = createDataStyle(workbook);

            int rowNum = 0;

            // Title
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(String.format("BẢNG LƯƠNG THÁNG %d/%d", month, year));
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 18));
            rowNum++;

            // Header
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {
                "#", "Họ tên", "Phòng ban", "Lương CB",
                "Ngày công", "Nghỉ phép", "OT (giờ)",
                "Lương CB thực tế", "Phụ cấp", "Lương OT", "Thưởng",
                "GROSS", "BHXH+BHYT+BHTN", "Thuế TNCN",
                "Phạt trễ", "KT khác", "Tổng khấu trừ", "NET", "Chi phí công ty"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            // Data rows
            int idx = 1;
            for (Payslip p : payslips) {
                String empName = null;
                String deptName = null;
                try {
                    Employee emp = employeeRepository.findByIdAndIsDeletedFalse(p.getEmployeeId()).orElse(null);
                    if (emp != null) {
                        empName = emp.getFullName();
                        if (emp.getDeptId() != null) {
                            deptName = departmentRepository.findByIdAndIsDeletedFalse(emp.getDeptId())
                                    .map(d -> d.getDeptName()).orElse("-");
                        }
                    }
                } catch (Exception ignored) {}

                Row row = sheet.createRow(rowNum++);
                int col = 0;
                row.createCell(col++).setCellValue(idx++);
                setCell(row, col++, empName, dataStyle);
                setCell(row, col++, deptName, dataStyle);
                setMoneyCell(row, col++, p.getBasicSalary(), moneyStyle);
                row.createCell(col++).setCellValue(p.getActualWorkDays());
                row.createCell(col++).setCellValue(p.getPaidLeaveDays().doubleValue());
                row.createCell(col++).setCellValue(p.getTotalOvertimeHours().doubleValue());
                setMoneyCell(row, col++, p.getActualBasicSalary(), moneyStyle);
                setMoneyCell(row, col++, p.getTotalAllowances(), moneyStyle);
                setMoneyCell(row, col++, p.getOvertimePay(), moneyStyle);
                setMoneyCell(row, col++, p.getTotalBonus(), moneyStyle);
                setMoneyCell(row, col++, p.getGrossSalary(), moneyStyle);
                setMoneyCell(row, col++, p.getTotalInsurance(), moneyStyle);
                setMoneyCell(row, col++, p.getPersonalIncomeTax(), moneyStyle);
                setMoneyCell(row, col++, p.getLatePenalty(), moneyStyle);
                setMoneyCell(row, col++, p.getOtherDeductions(), moneyStyle);
                setMoneyCell(row, col++, p.getTotalDeductionAmount(), moneyStyle);
                setMoneyCell(row, col++, p.getNetSalary(), moneyStyle);
                setMoneyCell(row, col++, p.getTotalCompanyCost(), moneyStyle);
            }

            // Auto-size
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            log.error("[PAYROLL] Export Excel error: {}", e.getMessage(), e);
        }
    }

    // ============================================================
    // EXPORT TECHCOMBANK
    // ============================================================

    @Override
    public void exportTechcombank(Integer month, Integer year, HttpServletResponse response) throws IOException {
        log.info("[PAYROLL] Exporting Techcombank format: {}/{}", month, year);

        List<Payslip> payslips = payslipRepository.findByMonthAndYearAndStatusInAndIsDeletedFalse(
                month,
                year,
                List.of("APPROVED", "PAID")
            );
        if (payslips.isEmpty()) {
            throw new IllegalStateException(
                "Không có phiếu lương APPROVED hoặc PAID cho tháng " + month + "/" + year
            );
        }

        String fileName = String.format("TCB_payroll_%d_%02d.xlsx", year, month);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Lệnh chuyển tiền");
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle   = createDataStyle(workbook);
            CellStyle moneyStyle  = createMoneyStyle(workbook);

            int rowNum = 0;
            Row headerRow = sheet.createRow(rowNum++);
            String[] tcbHeaders = {
                "STT", "Số tài khoản người thụ hưởng",
                "Tên người thụ hưởng", "Số tiền",
                "Nội dung chuyển tiền", "Ngân hàng/Chi nhánh"
            };
            for (int i = 0; i < tcbHeaders.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(tcbHeaders[i]);
                c.setCellStyle(headerStyle);
            }

            int idx = 1;
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (Payslip p : payslips) {
                EmployeeTaxInfo taxInfo = taxInfoRepository
                        .findByEmployeeIdAndIsDeletedFalse(p.getEmployeeId()).orElse(null);

                if (taxInfo == null || taxInfo.getBankAccountNumber() == null) {
                    log.warn("[PAYROLL-TCB] No bank info for emp={}", p.getEmployeeId());
                    continue;
                }

                String empName = employeeRepository.findByIdAndIsDeletedFalse(p.getEmployeeId())
                        .map(Employee::getFullName).orElse("Unknown");

                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(idx++);
                setCell(row, 1, taxInfo.getBankAccountNumber(), dataStyle);
                setCell(row, 2, taxInfo.getBankAccountHolder() != null
                        ? taxInfo.getBankAccountHolder() : empName.toUpperCase(), dataStyle);
                setMoneyCell(row, 3, p.getNetSalary(), moneyStyle);
                setCell(row, 4,
                        String.format("Luong thang %d/%d - %s", month, year, empName),
                        dataStyle);
                setCell(row, 5,
                        (taxInfo.getBankName() != null ? taxInfo.getBankName() : "Techcombank") +
                        " - " + (taxInfo.getBankBranch() != null ? taxInfo.getBankBranch() : ""),
                        dataStyle);

                totalAmount = totalAmount.add(p.getNetSalary());
            }

            Row totalRow = sheet.createRow(rowNum);
            totalRow.createCell(2).setCellValue("TỔNG CỘNG");
            setMoneyCell(totalRow, 3, totalAmount, moneyStyle);

            for (int i = 0; i < tcbHeaders.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            log.error("[PAYROLL-TCB] Export error: {}", e.getMessage(), e);
        }

        log.info("[PAYROLL-TCB] Exported {} records", payslips.size());
    }
    
    // ============================================================
    // HELPER METHODS
    // ============================================================

    /**
     * Tính thuế TNCN lũy tiến (VND, theo tháng)
     */
    private BigDecimal calculatePIT(BigDecimal taxableIncome) {
        if (taxableIncome == null || taxableIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Biểu thuế lũy tiến theo tháng (VNĐ)
        // Bậc 1: ≤ 5 triệu          → 5%
        // Bậc 2: 5-10 triệu         → 10%
        // Bậc 3: 10-18 triệu        → 15%
        // Bậc 4: 18-32 triệu        → 20%
        // Bậc 5: 32-52 triệu        → 25%
        // Bậc 6: 52-80 triệu        → 30%
        // Bậc 7: > 80 triệu         → 35%

        long[][] brackets = {
            {         0,  5_000_000, 5},
            { 5_000_000, 10_000_000, 10},
            {10_000_000, 18_000_000, 15},
            {18_000_000, 32_000_000, 20},
            {32_000_000, 52_000_000, 25},
            {52_000_000, 80_000_000, 30},
            {80_000_000, Long.MAX_VALUE, 35}
        };

        BigDecimal tax      = BigDecimal.ZERO;
        BigDecimal income   = taxableIncome;

        for (long[] bracket : brackets) {
            BigDecimal low  = BigDecimal.valueOf(bracket[0]);
            BigDecimal high = bracket[1] == Long.MAX_VALUE
                    ? income : BigDecimal.valueOf(bracket[1]);
            int rate = (int) bracket[2];

            if (income.compareTo(low) <= 0) break;

            BigDecimal taxableInBracket = income.min(high).subtract(low);
            tax = tax.add(
                taxableInBracket.multiply(BigDecimal.valueOf(rate))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
            );

            if (income.compareTo(high) <= 0) break;
        }

        return tax.setScale(0, RoundingMode.HALF_UP);
    }

    private Map<String, BigDecimal> loadConfig() {
        return configRepository.findByIsDeletedFalseAndIsActiveTrue()
                .stream()
                .collect(Collectors.toMap(
                        PayrollConfig::getConfigKey,
                        PayrollConfig::getConfigValue
                ));
    }

    private PayslipDetail buildDetail(Integer payslipId, String type, String code,
                                      String name, BigDecimal amount,
                                      BigDecimal quantity, BigDecimal rate, int sortOrder) {
        return PayslipDetail.builder()
                .payslipId(payslipId)
                .itemType(type)
                .itemCode(code)
                .itemName(name)
                .amount(amount != null ? amount : BigDecimal.ZERO)
                .quantity(quantity)
                .rate(rate)
                .sortOrder(sortOrder)
                .build();
    }

    private Payslip getPayslipById(Integer id) {
        return payslipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payslip not found: " + id));
    }

    private PayslipSummaryDTO toSummaryDTO(Payslip p) {
        String empName = null;
        String deptName = null;

        try {
            Employee emp = employeeRepository.findByIdAndIsDeletedFalse(p.getEmployeeId()).orElse(null);
            if (emp != null) {
                empName = emp.getFullName();
                if (emp.getDeptId() != null) {
                    deptName = departmentRepository.findByIdAndIsDeletedFalse(emp.getDeptId())
                            .map(d -> d.getDeptName()).orElse(null);
                }
            }
        } catch (Exception ignored) {}

        return PayslipSummaryDTO.builder()
                .id(p.getId())
                .employeeId(p.getEmployeeId())
                .employeeName(empName)
                .deptName(deptName)
                .month(p.getMonth())
                .year(p.getYear())
                .status(p.getStatus())
                .standardWorkDays(safeInt(p.getStandardWorkDays()))
                .actualWorkDays(safeInt(p.getActualWorkDays()))
                .paidLeaveDays(safe(p.getPaidLeaveDays()))
                .unpaidLeaveDays(safe(p.getUnpaidLeaveDays()))
                .absentDays(safeInt(p.getAbsentDays()))
                .totalOvertimeHours(safe(p.getTotalOvertimeHours()))
                .totalLateTimes(safeInt(p.getTotalLateTimes()))
                .basicSalary(safe(p.getBasicSalary()))
                .actualBasicSalary(safe(p.getActualBasicSalary()))
                .totalAllowances(safe(p.getTotalAllowances()))
                .overtimePay(safe(p.getOvertimePay()))
                .totalBonus(safe(p.getTotalBonus()))
                .grossSalary(safe(p.getGrossSalary()))
                .totalInsurance(safe(p.getTotalInsurance()))
                .personalIncomeTax(safe(p.getPersonalIncomeTax()))
                .latePenalty(safe(p.getLatePenalty()))
                .otherDeductions(safe(p.getOtherDeductions()))
                .totalDeductionAmount(safe(p.getTotalDeductionAmount()))
                .netSalary(safe(p.getNetSalary()))
                .totalCompanyCost(safe(p.getTotalCompanyCost()))
                .paymentMethod(p.getPaymentMethod())
                .note(p.getNote())
                .build();
    }

    private PayslipFullDTO toFullDTO(Payslip p) {
        String empName = null;
        String deptName = null;
        String bankAccount = null;
        String bankHolder  = null;
        String bankName    = null;

        try {
            Employee emp = employeeRepository.findByIdAndIsDeletedFalse(p.getEmployeeId()).orElse(null);
            if (emp != null) {
                empName = emp.getFullName();
                if (emp.getDeptId() != null) {
                    deptName = departmentRepository.findByIdAndIsDeletedFalse(emp.getDeptId())
                            .map(d -> d.getDeptName()).orElse(null);
                }
            }
            EmployeeTaxInfo taxInfo = taxInfoRepository
                    .findByEmployeeIdAndIsDeletedFalse(p.getEmployeeId()).orElse(null);
            if (taxInfo != null) {
                bankAccount = taxInfo.getBankAccountNumber();
                bankHolder  = taxInfo.getBankAccountHolder();
                bankName    = taxInfo.getBankName();
            }
        } catch (Exception ignored) {}

        // Load details
        List<PayslipDetail> allDetails = payslipDetailRepository
                .findByPayslipIdAndIsDeletedFalseOrderBySortOrder(p.getId());

        List<PayslipDetailItemDTO> earnings = allDetails.stream()
                .filter(d -> "EARNING".equals(d.getItemType()))
                .map(this::toDetailItemDTO)
                .collect(Collectors.toList());

        List<PayslipDetailItemDTO> deductions = allDetails.stream()
                .filter(d -> "DEDUCTION".equals(d.getItemType()))
                .map(this::toDetailItemDTO)
                .collect(Collectors.toList());

        return PayslipFullDTO.builder()
                .id(p.getId())
                .employeeId(p.getEmployeeId())
                .employeeName(empName)
                .deptName(deptName)
                .month(p.getMonth())
                .year(p.getYear())
                .status(p.getStatus())
                .bankName(bankName)
                .bankAccountNumber(bankAccount)
                .bankAccountHolder(bankHolder)
                // Ngày công
                .standardWorkDays(p.getStandardWorkDays())
                .actualWorkDays(p.getActualWorkDays())
                .paidLeaveDays(p.getPaidLeaveDays())
                .unpaidLeaveDays(p.getUnpaidLeaveDays())
                .absentDays(p.getAbsentDays())
                .overtimeHoursWeekday(p.getOvertimeHoursWeekday())
                .overtimeHoursWeekend(p.getOvertimeHoursWeekend())
                .overtimeHoursHoliday(p.getOvertimeHoursHoliday())
                .totalOvertimeHours(p.getTotalOvertimeHours())
                .totalLateTimes(p.getTotalLateTimes())
                .totalLateMinutes(p.getTotalLateMinutes())
                // Thu nhập
                .basicSalary(p.getBasicSalary())
                .actualBasicSalary(p.getActualBasicSalary())
                .totalAllowances(p.getTotalAllowances())
                .overtimePay(p.getOvertimePay())
                .totalBonus(p.getTotalBonus())
                .grossSalary(p.getGrossSalary())
                // BH
                .insuranceSalary(p.getInsuranceSalary())
                .socialInsurance(p.getSocialInsurance())
                .healthInsurance(p.getHealthInsurance())
                .unemploymentInsurance(p.getUnemploymentInsurance())
                .totalInsurance(p.getTotalInsurance())
                // Thuế
                .taxableIncome(p.getTaxableIncome())
                .personalDeduction(p.getPersonalDeduction())
                .dependentDeduction(p.getDependentDeduction())
                .personalIncomeTax(p.getPersonalIncomeTax())
                // Khấu trừ
                .latePenalty(p.getLatePenalty())
                .otherDeductions(p.getOtherDeductions())
                .totalDeductionAmount(p.getTotalDeductionAmount())
                // Kết quả
                .netSalary(p.getNetSalary())
                // Công ty
                .companySocialInsurance(p.getCompanySocialInsurance())
                .companyHealthInsurance(p.getCompanyHealthInsurance())
                .companyUnemployment(p.getCompanyUnemployment())
                .totalCompanyCost(p.getTotalCompanyCost())
                // Chi tiết
                .earnings(earnings)
                .deductions(deductions)
                // Meta
                .note(p.getNote())
                .calculatedAt(p.getCalculatedAt())
                .approvedAt(p.getApprovedAt())
                .paidAt(p.getPaidAt())
                .build();
    }

    private PayslipDetailItemDTO toDetailItemDTO(PayslipDetail d) {
        return PayslipDetailItemDTO.builder()
                .id(d.getId())
                .itemType(d.getItemType())
                .itemCode(d.getItemCode())
                .itemName(d.getItemName())
                .amount(d.getAmount())
                .quantity(d.getQuantity())
                .rate(d.getRate())
                .note(d.getNote())
                .sortOrder(d.getSortOrder())
                .build();
    }

    @Override
    public PayslipFullDTO getMyPayslipDetail(Integer payslipId, Integer employeeId) {
        Payslip payslip = payslipRepository.findById(payslipId)
            .orElseThrow(() -> new ResourceNotFoundException("Payslip not found: " + payslipId));
        
        // Kiểm tra ownership — chỉ được xem phiếu lương của mình
        if (!payslip.getEmployeeId().equals(employeeId)) {
            throw new BusinessException("Access denied: This payslip does not belong to you");
        }
        
        return getPayslipDetail(payslipId);
    }

    // Excel helpers
    private void setCell(Row row, int col, String value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value != null ? value : "");
        c.setCellStyle(style);
    }

    private void setMoneyCell(Row row, int col, BigDecimal value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value != null ? value.doubleValue() : 0);
        c.setCellStyle(style);
    }

    private CellStyle createHeaderStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createTitleStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createMoneyStyle(XSSFWorkbook wb) {
        CellStyle style = createDataStyle(wb);
        DataFormat format = wb.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private BigDecimal safe(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private int safeInt(Integer v) {
        return v != null ? v : 0;
    }
}