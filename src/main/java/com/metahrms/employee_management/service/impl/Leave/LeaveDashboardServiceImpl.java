package com.metahrms.employee_management.service.impl.Leave;

import com.metahrms.employee_management.dto.response.Leave.ManagerLeaveSummaryDto;
import com.metahrms.employee_management.enums.Leave.LeaveApprovalStage;
import com.metahrms.employee_management.enums.Leave.LeaveStatus;
import com.metahrms.employee_management.repository.LeaveRequestRepository;
import com.metahrms.employee_management.service.Leave.LeaveDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeaveDashboardServiceImpl implements LeaveDashboardService {

    private final LeaveRequestRepository leaveRequestRepository;

    @Override
    @Transactional(readOnly = true)
    public ManagerLeaveSummaryDto getManagerSummary(Integer managerId) {
        long pending = leaveRequestRepository.countByManagerIdAndStatusAndApprovalStageCustom(
                managerId,
                LeaveStatus.PENDING,
                LeaveApprovalStage.WAITING_MANAGER
        );

        long approved = leaveRequestRepository.countByManagerIdAndStatusCustom(
                managerId,
                LeaveStatus.APPROVED
        );

        long rejected = leaveRequestRepository.countByManagerIdAndStatusCustom(
                managerId,
                LeaveStatus.REJECTED
        );

        return ManagerLeaveSummaryDto.builder()
                .pending(pending)
                .approved(approved)
                .rejected(rejected)
                .build();
    }
}