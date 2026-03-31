package com.metahrms.employee_management.service.impl.Leave;

import com.metahrms.employee_management.dto.request.Leave.LeaveCancelDto;
import com.metahrms.employee_management.dto.request.Leave.LeaveRequestCreateDto;
import com.metahrms.employee_management.dto.request.Leave.LeaveRequestUpdateDraftDto;
import com.metahrms.employee_management.dto.request.Leave.LeaveSubmitDto;
import com.metahrms.employee_management.dto.response.Leave.ApprovalStepDto;
import com.metahrms.employee_management.dto.response.Leave.LeaveCalendarItemDto;
import com.metahrms.employee_management.dto.response.Leave.LeaveRequestResponseDto;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.Leave.Holiday;
import com.metahrms.employee_management.entity.Leave.LeaveApprovalHistory;
import com.metahrms.employee_management.entity.Leave.LeaveAttachment;
import com.metahrms.employee_management.entity.Leave.LeaveRequest;
import com.metahrms.employee_management.entity.Leave.LeaveType;
import com.metahrms.employee_management.enums.EmployeeStatus;
import com.metahrms.employee_management.enums.RoleInDepartment;
import com.metahrms.employee_management.enums.Leave.ApprovalAction;
import com.metahrms.employee_management.enums.Leave.ApprovalRole;
import com.metahrms.employee_management.enums.Leave.LeaveApprovalStage;
import com.metahrms.employee_management.enums.Leave.LeaveStatus;
import com.metahrms.employee_management.enums.Leave.LeaveTypeCode;
import com.metahrms.employee_management.exception.BadRequestException;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.HolidayRepository;
import com.metahrms.employee_management.repository.LeaveApprovalHistoryRepository;
import com.metahrms.employee_management.repository.LeaveAttachmentRepository;
import com.metahrms.employee_management.repository.LeaveRequestRepository;
import com.metahrms.employee_management.repository.LeaveTypeRepository;
import com.metahrms.employee_management.service.Leave.LeaveBalanceService;
import com.metahrms.employee_management.service.Leave.LeaveRequestService;
import com.metahrms.employee_management.service.Leave.NotificationService;
import com.metahrms.employee_management.util.LeaveCalculationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceService leaveBalanceService;
    private final HolidayRepository holidayRepository;
    private final LeaveApprovalHistoryRepository historyRepository;
    private final LeaveAttachmentRepository attachmentRepository;
    private final NotificationService notificationService;
    private final EmployeeRepository employeeRepository;
    

    @Override
    @Transactional
    public LeaveRequestResponseDto createDraft(LeaveRequestCreateDto dto) {
        LeaveType leaveType = getLeaveType(dto.getLeaveTypeId());
        validateLeaveTypeForCreate(leaveType);

        Employee employee = getEmployee(dto.getEmployeeId());
        BigDecimal totalDays = calculateDays(dto);

        Integer resolvedManagerId = resolveManagerId(employee);
        Integer resolvedHrId = dto.getHrId();

        LeaveRequest request = LeaveRequest.builder()
                .employeeId(dto.getEmployeeId())
                .employeeName(resolveEmployeeName(employee))
                .managerId(resolvedManagerId)
                .hrId(resolvedHrId)
                .leaveType(leaveType)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .leaveUnit(dto.getLeaveUnit())
                .startSession(dto.getStartSession())
                .endSession(dto.getEndSession())
                .totalDays(totalDays)
                .reason(dto.getReason())
                .status(LeaveStatus.DRAFT)
                .approvalStage(LeaveApprovalStage.NONE)
                .finalApproved(false)
                .build();

        request = leaveRequestRepository.save(request);
        saveAttachments(request.getId(), dto.getAttachments());

        historyRepository.save(LeaveApprovalHistory.builder()
                .leaveRequestId(request.getId())
                .actorId(dto.getEmployeeId())
                .actorRole(ApprovalRole.EMPLOYEE)
                .action(ApprovalAction.CREATED_DRAFT)
                .stage(LeaveApprovalStage.NONE)
                .note("Tạo nháp đơn nghỉ")
                .build());

        return map(request);
    }

    @Override
    @Transactional
    public LeaveRequestResponseDto updateDraft(Long id, LeaveRequestUpdateDraftDto dto) {
        LeaveRequest request = getRequest(id);

        if (request.getStatus() != LeaveStatus.DRAFT) {
            throw new BadRequestException("Chỉ đơn DRAFT mới được chỉnh sửa");
        }

        if (!request.getEmployeeId().equals(dto.getEmployeeId())) {
            throw new BadRequestException("Bạn không có quyền sửa đơn này");
        }

        LeaveType leaveType = getLeaveType(dto.getLeaveTypeId());
        validateLeaveTypeForUpdate(leaveType);

        Employee employee = getEmployee(dto.getEmployeeId());
        BigDecimal totalDays = calculateDays(dto);

        Integer resolvedManagerId = resolveManagerId(employee);
        Integer resolvedHrId = dto.getHrId();

        request.setEmployeeName(resolveEmployeeName(employee));
        request.setManagerId(resolvedManagerId);
        request.setHrId(resolvedHrId);
        request.setLeaveType(leaveType);
        request.setStartDate(dto.getStartDate());
        request.setEndDate(dto.getEndDate());
        request.setLeaveUnit(dto.getLeaveUnit());
        request.setStartSession(dto.getStartSession());
        request.setEndSession(dto.getEndSession());
        request.setTotalDays(totalDays);
        request.setReason(dto.getReason());

        leaveRequestRepository.save(request);

        attachmentRepository.deleteByLeaveRequestId(request.getId());
        saveAttachments(request.getId(), dto.getAttachments());

        historyRepository.save(LeaveApprovalHistory.builder()
                .leaveRequestId(request.getId())
                .actorId(dto.getEmployeeId())
                .actorRole(ApprovalRole.EMPLOYEE)
                .action(ApprovalAction.UPDATED_DRAFT)
                .stage(LeaveApprovalStage.NONE)
                .note("Cập nhật nháp đơn nghỉ")
                .build());

        return map(request);
    }

    @Override
    @Transactional
    public LeaveRequestResponseDto submit(Long id, LeaveSubmitDto dto) {
        LeaveRequest request = getRequest(id);

        if (request.getStatus() != LeaveStatus.DRAFT) {
            throw new BadRequestException("Chỉ đơn DRAFT mới có thể gửi");
        }

        if (!request.getEmployeeId().equals(dto.getEmployeeId())) {
            throw new BadRequestException("Bạn không có quyền gửi đơn này");
        }

        validateBeforeSubmit(request);

        Employee employee = getEmployee(request.getEmployeeId());

        request.setStatus(LeaveStatus.PENDING);
        request.setSubmittedAt(LocalDateTime.now());

        if (employee.getRoleInDept() == RoleInDepartment.HEAD) {
            if (request.getHrId() == null) {
                throw new BadRequestException("Đơn của HEAD cần có HR để duyệt");
            }
            request.setApprovalStage(LeaveApprovalStage.WAITING_HR);
        } else {
            if (request.getManagerId() == null) {
                throw new BadRequestException("Không tìm thấy quản lý để duyệt đơn");
            }
            request.setApprovalStage(LeaveApprovalStage.WAITING_MANAGER);
        }

        leaveRequestRepository.save(request);

        Long chargeBalanceTypeId = resolveChargeBalanceTypeId(request.getLeaveType());
        if (chargeBalanceTypeId != null) {
            leaveBalanceService.addPendingDays(
                    request.getEmployeeId(),
                    chargeBalanceTypeId,
                    Year.from(request.getStartDate()).getValue(),
                    request.getTotalDays()
            );
        }

        historyRepository.save(LeaveApprovalHistory.builder()
                .leaveRequestId(request.getId())
                .actorId(dto.getEmployeeId())
                .actorRole(ApprovalRole.EMPLOYEE)
                .action(ApprovalAction.SUBMITTED)
                .stage(request.getApprovalStage())
                .note("Nhân viên gửi đơn nghỉ")
                .build());

        if (request.getApprovalStage() == LeaveApprovalStage.WAITING_MANAGER) {
            notificationService.notifyManager(
                    request.getManagerId(),
                    "Có đơn nghỉ phép mới cần duyệt: #" + request.getId()
            );
        } else if (request.getApprovalStage() == LeaveApprovalStage.WAITING_HR) {
            notificationService.notifyHr(
                    request.getHrId(),
                    "Có đơn nghỉ phép mới cần HR duyệt: #" + request.getId()
            );
        }

        return map(request);
    }

    @Override
    @Transactional
    public LeaveRequestResponseDto cancel(Long id, LeaveCancelDto dto) {
        LeaveRequest request = getRequest(id);

        if (!request.getEmployeeId().equals(dto.getEmployeeId())) {
            throw new BadRequestException("Bạn không có quyền hủy đơn này");
        }

        if (request.getStatus() == LeaveStatus.DRAFT) {
            request.setStatus(LeaveStatus.CANCELLED);
            request.setCancelReason(dto.getCancelReason());
            request.setCancelledAt(LocalDateTime.now());
            leaveRequestRepository.save(request);
            saveCancelHistory(request, dto.getEmployeeId(), dto.getCancelReason());
            return map(request);
        }

        if (request.getStatus() == LeaveStatus.PENDING) {
            rollbackPendingBalance(request);

            request.setStatus(LeaveStatus.CANCELLED);
            request.setCancelReason(dto.getCancelReason());
            request.setCancelledAt(LocalDateTime.now());
            leaveRequestRepository.save(request);

            saveCancelHistory(request, dto.getEmployeeId(), dto.getCancelReason());

            if (request.getManagerId() != null) {
                notificationService.notifyManager(
                        request.getManagerId(),
                        "Đơn nghỉ #" + request.getId() + " đã bị nhân viên hủy"
                );
            }

            if (request.getHrId() != null) {
                notificationService.notifyHr(
                        request.getHrId(),
                        "Đơn nghỉ #" + request.getId() + " đã bị nhân viên hủy"
                );
            }

            return map(request);
        }

        if (request.getStatus() == LeaveStatus.APPROVED) {
            if (!request.getStartDate().isAfter(LocalDate.now())) {
                throw new BadRequestException("Đơn APPROVED chỉ được hủy khi chưa đến ngày nghỉ");
            }

            rollbackUsedBalance(request);

            request.setStatus(LeaveStatus.CANCELLED);
            request.setCancelReason(dto.getCancelReason());
            request.setCancelledAt(LocalDateTime.now());
            leaveRequestRepository.save(request);

            saveCancelHistory(request, dto.getEmployeeId(), dto.getCancelReason());

            if (request.getManagerId() != null) {
                notificationService.notifyManager(
                        request.getManagerId(),
                        "Đơn nghỉ #" + request.getId() + " đã bị nhân viên hủy"
                );
            }

            if (request.getHrId() != null) {
                notificationService.notifyHr(
                        request.getHrId(),
                        "Đơn nghỉ #" + request.getId() + " đã bị nhân viên hủy"
                );
            }

            return map(request);
        }

        throw new BadRequestException("Không thể hủy đơn ở trạng thái hiện tại");
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveRequestResponseDto getById(Long id) {
        return map(getRequestWithLeaveType(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestResponseDto> getByEmployee(Integer employeeId) {
        return leaveRequestRepository.findByEmployeeIdWithLeaveType(employeeId)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveCalendarItemDto> getCalendar(LocalDate startDate, LocalDate endDate) {
        return leaveRequestRepository.findByStatusAndDateRangeWithLeaveType(
                        LeaveStatus.APPROVED,
                        endDate,
                        startDate
                ).stream()
                .map(item -> LeaveCalendarItemDto.builder()
                        .leaveRequestId(item.getId())
                        .employeeId(item.getEmployeeId())
                        .employeeName(item.getEmployeeName())
                        .leaveTypeName(item.getLeaveType().getName())
                        .startDate(item.getStartDate())
                        .endDate(item.getEndDate())
                        .totalDays(item.getTotalDays())
                        .status(item.getStatus().name())
                        .build())
                .toList();
    }

    private void validateLeaveTypeForCreate(LeaveType leaveType) {
        if (!Boolean.TRUE.equals(leaveType.getActive())) {
            throw new BadRequestException("Loại nghỉ hiện không hoạt động");
        }
    }

    private void validateLeaveTypeForUpdate(LeaveType leaveType) {
        if (!Boolean.TRUE.equals(leaveType.getActive())) {
            throw new BadRequestException("Loại nghỉ hiện không hoạt động");
        }
    }

    private void validateAttachments(List<LeaveRequestCreateDto.AttachmentInput> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return;
        }

        if (attachments.size() > 5) {
            throw new BadRequestException("Tối đa 5 file");
        }

        for (LeaveRequestCreateDto.AttachmentInput a : attachments) {
            if (a.getFileSize() > 5 * 1024 * 1024) {
                throw new BadRequestException("Mỗi file không được vượt quá 5MB");
            }
        }
    }

    private void saveAttachments(Long requestId, List<LeaveRequestCreateDto.AttachmentInput> attachments) {
        validateAttachments(attachments);

        if (attachments == null || attachments.isEmpty()) {
            return;
        }

        for (LeaveRequestCreateDto.AttachmentInput a : attachments) {
            attachmentRepository.save(LeaveAttachment.builder()
                    .leaveRequestId(requestId)
                    .fileName(a.getFileName())
                    .fileUrl(a.getFileUrl())
                    .fileType(a.getFileType())
                    .fileSize(a.getFileSize())
                    .build());
        }
    }

    private BigDecimal calculateDays(LeaveRequestCreateDto dto) {
        List<Holiday> holidays = holidayRepository.findByHolidayDateBetweenAndActiveTrue(
                dto.getStartDate(),
                dto.getEndDate()
        );
        Set<LocalDate> holidayDates = holidays.stream()
                .map(Holiday::getHolidayDate)
                .collect(Collectors.toSet());

        return LeaveCalculationUtil.calculateLeaveDays(
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getLeaveUnit(),
                dto.getStartSession(),
                dto.getEndSession(),
                holidayDates
        );
    }

    private BigDecimal calculateDays(LeaveRequestUpdateDraftDto dto) {
        List<Holiday> holidays = holidayRepository.findByHolidayDateBetweenAndActiveTrue(
                dto.getStartDate(),
                dto.getEndDate()
        );
        Set<LocalDate> holidayDates = holidays.stream()
                .map(Holiday::getHolidayDate)
                .collect(Collectors.toSet());

        return LeaveCalculationUtil.calculateLeaveDays(
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getLeaveUnit(),
                dto.getStartSession(),
                dto.getEndSession(),
                holidayDates
        );
    }

    private void validateBeforeSubmit(LeaveRequest request) {
        if (request.getReason() == null || request.getReason().trim().length() < 10) {
            throw new BadRequestException("Lý do nghỉ phải tối thiểu 10 ký tự");
        }

        boolean overlap = leaveRequestRepository.existsOverlapExcludingCurrent(
                request.getEmployeeId(),
                List.of(LeaveStatus.PENDING, LeaveStatus.APPROVED),
                request.getStartDate(),
                request.getEndDate(),
                request.getId()
        );

        if (overlap) {
            throw new BadRequestException("Đơn nghỉ bị trùng thời gian với đơn khác");
        }

        if (request.getLeaveType().getMaxDaysPerYear() != null
                && request.getLeaveType().getMaxDaysPerYear() > 0
                && request.getTotalDays().compareTo(
                BigDecimal.valueOf(request.getLeaveType().getMaxDaysPerYear())
        ) > 0) {
            throw new BadRequestException("Số ngày nghỉ vượt quá giới hạn loại nghỉ");
        }

        Long chargeBalanceTypeId = resolveChargeBalanceTypeId(request.getLeaveType());
        if (chargeBalanceTypeId != null) {
            leaveBalanceService.validateEnoughBalance(
                    request.getEmployeeId(),
                    chargeBalanceTypeId,
                    Year.from(request.getStartDate()).getValue(),
                    request.getTotalDays()
            );
        }
    }

    private Long resolveChargeBalanceTypeId(LeaveType leaveType) {
        if (Boolean.TRUE.equals(leaveType.getDeductBalance())) {
            return leaveType.getId();
        }

        if (Boolean.TRUE.equals(leaveType.getDeductFromAnnualLeaveBalance())) {
            LeaveType annual = leaveTypeRepository.findByCode(LeaveTypeCode.ANNUAL_LEAVE.name())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ANNUAL_LEAVE"));
            return annual.getId();
        }

        return null;
    }

    private void rollbackPendingBalance(LeaveRequest request) {
        Long balanceTypeId = resolveChargeBalanceTypeId(request.getLeaveType());
        if (balanceTypeId != null) {
            leaveBalanceService.rollbackPendingDays(
                    request.getEmployeeId(),
                    balanceTypeId,
                    Year.from(request.getStartDate()).getValue(),
                    request.getTotalDays()
            );
        }
    }

    private void rollbackUsedBalance(LeaveRequest request) {
        Long balanceTypeId = resolveChargeBalanceTypeId(request.getLeaveType());
        if (balanceTypeId != null) {
            leaveBalanceService.rollbackUsedDays(
                    request.getEmployeeId(),
                    balanceTypeId,
                    Year.from(request.getStartDate()).getValue(),
                    request.getTotalDays()
            );
        }
    }

    private LeaveType getLeaveType(Long leaveTypeId) {
        return leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại nghỉ"));
    }

    private LeaveRequest getRequest(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn nghỉ"));
    }

    private LeaveRequest getRequestWithLeaveType(Long id) {
        return leaveRequestRepository.findByIdWithLeaveType(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn nghỉ"));
    }

    private Employee getEmployee(Integer employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));
    }

    private String resolveEmployeeName(Employee employee) {
        return employee.getFullName();
    }

    private Integer resolveManagerId(Employee employee) {
        RoleInDepartment role = employee.getRoleInDept();

        if (role == RoleInDepartment.HEAD) {
            return null;
        }

        Integer deptId = employee.getDeptId();
        if (deptId == null) {
            throw new BadRequestException("Nhân viên chưa được gán phòng ban");
        }

        if (role == RoleInDepartment.DEPUTY || role == RoleInDepartment.LEADER) {
            return employeeRepository
                    .findFirstByDeptIdAndRoleInDeptAndStatusAndIsDeletedFalse(
                            deptId,
                            RoleInDepartment.HEAD,
                            EmployeeStatus.ACTIVE
                    )
                    .map(Employee::getId)
                    .orElseThrow(() -> new BadRequestException("Không tìm thấy HEAD để duyệt đơn"));
        }

        if (role == RoleInDepartment.STAFF) {
            return employeeRepository
                    .findFirstByDeptIdAndRoleInDeptAndStatusAndIsDeletedFalse(
                            deptId,
                            RoleInDepartment.HEAD,
                            EmployeeStatus.ACTIVE
                    )
                    .or(() -> employeeRepository.findFirstByDeptIdAndRoleInDeptAndStatusAndIsDeletedFalse(
                            deptId,
                            RoleInDepartment.DEPUTY,
                            EmployeeStatus.ACTIVE
                    ))
                    .or(() -> employeeRepository.findFirstByDeptIdAndRoleInDeptAndStatusAndIsDeletedFalse(
                            deptId,
                            RoleInDepartment.LEADER,
                            EmployeeStatus.ACTIVE
                    ))
                    .map(Employee::getId)
                    .orElseThrow(() -> new BadRequestException("Không tìm thấy quản lý để duyệt đơn"));
        }

        throw new BadRequestException("Không xác định được vai trò để tìm người duyệt");
    }

    private void saveCancelHistory(LeaveRequest request, Integer employeeId, String reason) {
        historyRepository.save(LeaveApprovalHistory.builder()
                .leaveRequestId(request.getId())
                .actorId(employeeId)
                .actorRole(ApprovalRole.EMPLOYEE)
                .action(ApprovalAction.CANCELLED)
                .stage(request.getApprovalStage())
                .note(reason)
                .build());
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

        return LeaveRequestResponseDto.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployeeId())
                .employeeName(entity.getEmployeeName())
                .managerId(entity.getManagerId())
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