package com.metahrms.employee_management.service.impl.Leave;

import com.metahrms.employee_management.dto.request.Leave.LeaveApproveDto;
import com.metahrms.employee_management.dto.request.Leave.LeaveRejectDto;
import com.metahrms.employee_management.dto.response.Leave.ApprovalStepDto;
import com.metahrms.employee_management.dto.response.Leave.HrLeaveDashboardSummaryDto;
import com.metahrms.employee_management.dto.response.Leave.LeaveRequestResponseDto;
import com.metahrms.employee_management.dto.response.Leave.ManagerLeaveSummaryDto;
import com.metahrms.employee_management.entity.Department;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.Leave.LeaveApprovalHistory;
import com.metahrms.employee_management.entity.Leave.LeaveAttachment;
import com.metahrms.employee_management.entity.Leave.LeaveRequest;
import com.metahrms.employee_management.entity.Leave.LeaveType;
import com.metahrms.employee_management.enums.Leave.ApprovalAction;
import com.metahrms.employee_management.enums.Leave.ApprovalRole;
import com.metahrms.employee_management.enums.Leave.LeaveApprovalStage;
import com.metahrms.employee_management.enums.Leave.LeaveStatus;
import com.metahrms.employee_management.enums.RoleInDepartment;
import com.metahrms.employee_management.exception.BadRequestException;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.repository.DepartmentRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.LeaveApprovalHistoryRepository;
import com.metahrms.employee_management.repository.LeaveAttachmentRepository;
import com.metahrms.employee_management.repository.LeaveRequestRepository;
import com.metahrms.employee_management.repository.LeaveTypeRepository;
import com.metahrms.employee_management.service.HRNotificationHelperService;
import com.metahrms.employee_management.service.Leave.AttendanceIntegrationService;
import com.metahrms.employee_management.service.Leave.LeaveApprovalService;
import com.metahrms.employee_management.service.Leave.LeaveBalanceService;
import com.metahrms.employee_management.service.Leave.PayrollIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveApprovalServiceImpl implements LeaveApprovalService {

    private static final String ANNUAL_LEAVE_CODE = "ANNUAL_LEAVE";

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveApprovalHistoryRepository historyRepository;
    private final LeaveAttachmentRepository attachmentRepository;
    private final LeaveBalanceService leaveBalanceService;
    private final AttendanceIntegrationService attendanceIntegrationService;
    private final PayrollIntegrationService payrollIntegrationService;
    private final LeaveTypeRepository leaveTypeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final HRNotificationHelperService hrNotificationHelperService;

    private Integer resolveHrHeadId() {
        Department hrDepartment = departmentRepository
                .findByDeptNameAndIsDeletedFalse("Phòng Nhân sự")
                .orElseThrow(() -> new BadRequestException("Không tìm thấy phòng HR"));

        Employee hrHead = employeeRepository
                .findFirstByDeptIdAndRoleInDept(
                        hrDepartment.getId(),
                        RoleInDepartment.HEAD
                )
                .orElseThrow(() -> new BadRequestException("Không tìm thấy trưởng phòng HR"));

        return hrHead.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestResponseDto> getPendingForManager(Integer managerId) {
        return leaveRequestRepository
                .findByManagerIdAndStatusAndApprovalStageWithLeaveType(
                        managerId,
                        LeaveStatus.PENDING,
                        LeaveApprovalStage.WAITING_MANAGER
                )
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestResponseDto> getManagerHistory(
            Integer managerId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return leaveRequestRepository
                .findManagerHistoryByStatusesAndProcessedAtRangeWithLeaveType(
                        managerId,
                        List.of(LeaveStatus.APPROVED, LeaveStatus.REJECTED),
                        startDateTime,
                        endDateTime
                )
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ManagerLeaveSummaryDto getManagerSummary(
            Integer managerId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        long pending = leaveRequestRepository.countByManagerIdAndStatusAndApprovalStageCustom(
                managerId,
                LeaveStatus.PENDING,
                LeaveApprovalStage.WAITING_MANAGER
        );

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        long approved = leaveRequestRepository.countByManagerIdAndStatusAndApprovedAtBetween(
                managerId,
                LeaveStatus.APPROVED,
                startDateTime,
                endDateTime
        );

        long rejected = leaveRequestRepository.countByManagerIdAndStatusAndUpdatedAtBetween(
                managerId,
                LeaveStatus.REJECTED,
                startDateTime,
                endDateTime
        );

        return ManagerLeaveSummaryDto.builder()
                .pending(pending)
                .approved(approved)
                .rejected(rejected)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestResponseDto> getPendingForHr(Integer hrId) {
        return leaveRequestRepository
                .findByHrIdAndStatusAndApprovalStageWithLeaveType(
                        hrId,
                        LeaveStatus.PENDING,
                        LeaveApprovalStage.WAITING_HR
                )
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HrLeaveDashboardSummaryDto getHrDashboardSummary(Integer hrId) {
        long employeesOnLeave = leaveRequestRepository.countEmployeesOnLeaveToday();
        long pendingRequests = leaveRequestRepository.countPendingRequestsForHr(hrId);

        return HrLeaveDashboardSummaryDto.builder()
                .employeesOnLeave(employeesOnLeave)
                .pendingRequests(pendingRequests)
                .employeesOnLeaveChangePercent(0D)
                .pendingRequestsChangePercent(0D)
                .build();
    }

    @Override
    @Transactional
    public LeaveRequestResponseDto approve(Long leaveRequestId, LeaveApproveDto dto) {
        LeaveRequest request = getRequest(leaveRequestId);

        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException("Chỉ đơn PENDING mới được duyệt");
        }

        if (dto.getApproverRole() == ApprovalRole.MANAGER) {
            validateManager(request, dto.getApproverId());

            historyRepository.save(LeaveApprovalHistory.builder()
                    .leaveRequestId(request.getId())
                    .actorId(dto.getApproverId())
                    .actorRole(ApprovalRole.MANAGER)
                    .action(ApprovalAction.APPROVED)
                    .stage(LeaveApprovalStage.WAITING_MANAGER)
                    .note(dto.getNote())
                    .build());

            // <= 3 ngày: duyệt xong luôn
            if (request.getTotalDays().compareTo(java.math.BigDecimal.valueOf(3)) <= 0) {
                finalizeApproval(request);
                return map(request);
            }

            // > 3 ngày: chuyển HR duyệt tiếp
            Integer hrHeadId = request.getHrId();
            if (hrHeadId == null) {
                hrHeadId = resolveHrHeadId();
                request.setHrId(hrHeadId);
            }

            request.setStatus(LeaveStatus.PENDING);
            request.setApprovalStage(LeaveApprovalStage.WAITING_HR);
            leaveRequestRepository.save(request);

            // báo cho nhân viên biết manager đã duyệt và chuyển HR
            hrNotificationHelperService.notifyEmployeeLeaveApprovedByManager(
                    request.getEmployeeId(),
                    request.getId()
            );

            // báo cho HR có đơn cần xử lý
            hrNotificationHelperService.notifyHrLeaveWaitingForApproval(
                    hrHeadId,
                    request.getId(),
                    request.getEmployeeName()
            );

            return map(request);
        }

        if (dto.getApproverRole() == ApprovalRole.HR) {
            validateHr(request, dto.getApproverId());

            historyRepository.save(LeaveApprovalHistory.builder()
                    .leaveRequestId(request.getId())
                    .actorId(dto.getApproverId())
                    .actorRole(ApprovalRole.HR)
                    .action(ApprovalAction.APPROVED)
                    .stage(LeaveApprovalStage.WAITING_HR)
                    .note(dto.getNote())
                    .build());

            finalizeApproval(request);
            return map(request);
        }

        throw new BadRequestException("Vai trò duyệt không hợp lệ");
    }

    @Override
    @Transactional
    public LeaveRequestResponseDto reject(Long leaveRequestId, LeaveRejectDto dto) {
        LeaveRequest request = getRequest(leaveRequestId);

        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException("Chỉ đơn PENDING mới được từ chối");
        }

        if (dto.getApproverRole() == ApprovalRole.MANAGER) {
            validateManager(request, dto.getApproverId());

            historyRepository.save(LeaveApprovalHistory.builder()
                    .leaveRequestId(request.getId())
                    .actorId(dto.getApproverId())
                    .actorRole(ApprovalRole.MANAGER)
                    .action(ApprovalAction.REJECTED)
                    .stage(LeaveApprovalStage.WAITING_MANAGER)
                    .note(dto.getRejectReason())
                    .build());

        } else if (dto.getApproverRole() == ApprovalRole.HR) {
            validateHr(request, dto.getApproverId());

            historyRepository.save(LeaveApprovalHistory.builder()
                    .leaveRequestId(request.getId())
                    .actorId(dto.getApproverId())
                    .actorRole(ApprovalRole.HR)
                    .action(ApprovalAction.REJECTED)
                    .stage(LeaveApprovalStage.WAITING_HR)
                    .note(dto.getRejectReason())
                    .build());

        } else {
            throw new BadRequestException("Vai trò từ chối không hợp lệ");
        }

        request.setStatus(LeaveStatus.REJECTED);
        request.setApprovalStage(LeaveApprovalStage.COMPLETED);
        request.setRejectReason(dto.getRejectReason());
        request.setFinalApproved(false);
        request.setApprovedAt(null);

        leaveRequestRepository.save(request);

        hrNotificationHelperService.notifyEmployeeLeaveRejected(
                request.getEmployeeId(),
                request.getId(),
                dto.getRejectReason()
        );

        return map(request);
    }

    private void finalizeApproval(LeaveRequest request) {
        request.setStatus(LeaveStatus.APPROVED);
        request.setApprovalStage(LeaveApprovalStage.COMPLETED);
        request.setFinalApproved(true);
        request.setApprovedAt(LocalDateTime.now());

        leaveRequestRepository.save(request);

        LeaveType leaveType = request.getLeaveType();
        boolean deductBalance = Boolean.TRUE.equals(leaveType.getDeductBalance())
                || Boolean.TRUE.equals(leaveType.getDeductFromAnnualLeaveBalance());

        if (deductBalance) {
            int year = Year.from(request.getStartDate()).getValue();

            if (Boolean.TRUE.equals(leaveType.getDeductFromAnnualLeaveBalance())) {
                LeaveType annualLeave = leaveTypeRepository.findByCode(ANNUAL_LEAVE_CODE)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại nghỉ phép năm"));

                leaveBalanceService.approveDays(
                        request.getEmployeeId(),
                        annualLeave.getId(),
                        year,
                        request.getTotalDays()
                );
            } else {
                leaveBalanceService.approveDays(
                        request.getEmployeeId(),
                        leaveType.getId(),
                        year,
                        request.getTotalDays()
                );
            }
        }

        if (Boolean.TRUE.equals(leaveType.getCountsInAttendance())) {
            attendanceIntegrationService.handleFinalApprovedLeave(request);
        }

        if (Boolean.TRUE.equals(leaveType.getCountsInCompanyPayroll())
                || Boolean.TRUE.equals(leaveType.getDeductSalary())
                || Boolean.TRUE.equals(leaveType.getSocialInsurancePaid())) {
            payrollIntegrationService.handleFinalApprovedLeave(request);
        }

        hrNotificationHelperService.notifyEmployeeLeaveApprovedFinal(
                request.getEmployeeId(),
                request.getId()
        );
    }

    private void validateManager(LeaveRequest request, Integer approverId) {
        if (!LeaveApprovalStage.WAITING_MANAGER.equals(request.getApprovalStage())) {
            throw new BadRequestException("Đơn không ở bước chờ quản lý duyệt");
        }
        if (request.getManagerId() == null || !request.getManagerId().equals(approverId)) {
            throw new BadRequestException("Bạn không có quyền duyệt đơn này");
        }
    }

    private void validateHr(LeaveRequest request, Integer approverId) {
        if (!LeaveApprovalStage.WAITING_HR.equals(request.getApprovalStage())) {
            throw new BadRequestException("Đơn không ở bước chờ HR duyệt");
        }
        if (request.getHrId() == null || !request.getHrId().equals(approverId)) {
            throw new BadRequestException("Bạn không có quyền duyệt đơn này");
        }
    }

    private LeaveRequest getRequest(Long id) {
        return leaveRequestRepository.findByIdWithLeaveType(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn nghỉ"));
    }

    private LeaveRequestResponseDto map(LeaveRequest entity) {
        List<String> attachmentUrls = attachmentRepository.findByLeaveRequestId(entity.getId())
                .stream()
                .map(LeaveAttachment::getFileUrl)
                .toList();

        List<ApprovalStepDto> steps = historyRepository.findByLeaveRequestIdOrderByActionAtAsc(entity.getId())
                .stream()
                .map(h -> ApprovalStepDto.builder()
                        .actorId(h.getActorId())
                        .actorRole(h.getActorRole())
                        .action(h.getAction())
                        .stage(h.getStage())
                        .note(h.getNote())
                        .actionAt(h.getActionAt())
                        .build())
                        .toList();

        String managerName = null;
        String departmentName = null;
        String employeeName = entity.getEmployeeName();

        Employee employee = null;
        if (entity.getEmployeeId() != null) {
            employee = employeeRepository.findById(entity.getEmployeeId()).orElse(null);
        }

        if (employee != null) {
            if (employee.getFullName() != null && !employee.getFullName().isBlank()) {
                employeeName = employee.getFullName();
            }

            if (employee.getDeptId() != null) {
                Department department = departmentRepository.findById(employee.getDeptId()).orElse(null);
                if (department != null) {
                    departmentName = department.getDeptName();
                }
            }
        }

        if (entity.getManagerId() != null) {
            Employee manager = employeeRepository.findById(entity.getManagerId()).orElse(null);
            if (manager != null) {
                managerName = manager.getFullName();
            }
        }

        return LeaveRequestResponseDto.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployeeId())
                .employeeName(employeeName)
                .managerId(entity.getManagerId())
                .managerName(managerName)
                .departmentName(departmentName)
                .hrId(entity.getHrId())
                .leaveTypeId(entity.getLeaveType().getId())
                .leaveTypeCode(entity.getLeaveType().getCode())
                .leaveTypeName(entity.getLeaveType().getName())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .leaveUnit(entity.getLeaveUnit())
                .startSession(entity.getStartSession())
                .endSession(entity.getEndSession())
                .totalDays(entity.getTotalDays())
                .reason(entity.getReason())
                .status(entity.getStatus())
                .approvalStage(entity.getApprovalStage())
                .rejectReason(entity.getRejectReason())
                .cancelReason(entity.getCancelReason())
                .finalApproved(entity.getFinalApproved())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .submittedAt(entity.getSubmittedAt())
                .approvedAt(entity.getApprovedAt())
                .attachmentUrls(attachmentUrls)
                .approvalSteps(steps)
                .build();
    }
}