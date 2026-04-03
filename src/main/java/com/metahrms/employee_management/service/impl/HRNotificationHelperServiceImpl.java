package com.metahrms.employee_management.service.impl;

import com.metahrms.employee_management.dto.request.HRNotificationCreateDto;
import com.metahrms.employee_management.enums.HRNotificationPriority;
import com.metahrms.employee_management.enums.HRNotificationType;
import com.metahrms.employee_management.enums.HRRelatedEntityType;
import com.metahrms.employee_management.service.HRNotificationHelperService;
import com.metahrms.employee_management.service.HRNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HRNotificationHelperServiceImpl implements HRNotificationHelperService {

    private final HRNotificationService hrNotificationService;

    @Override
    public void notifyEmployeeSubmittedLeave(
            Integer employeeId,
            Long leaveRequestId,
            String startDate,
            String endDate
    ) {
        hrNotificationService.create(
                HRNotificationCreateDto.builder()
                        .recipientId(employeeId)
                        .title("Gửi đơn nghỉ phép thành công")
                        .content("Bạn đã gửi đơn nghỉ phép từ ngày " + startDate + " đến ngày " + endDate + " thành công.")
                        .type(HRNotificationType.LEAVE_SUBMITTED)
                        .relatedEntityType(HRRelatedEntityType.LEAVE_REQUEST)
                        .relatedEntityId(leaveRequestId)
                        .priority(HRNotificationPriority.NORMAL)
                        .createdBySystem(true)
                        .build()
        );
    }

    @Override
    public void notifyManagerNewLeaveRequest(
            Integer managerId,
            Long leaveRequestId,
            String employeeName
    ) {
        hrNotificationService.create(
                HRNotificationCreateDto.builder()
                        .recipientId(managerId)
                        .title("Có đơn nghỉ phép mới cần phê duyệt")
                        .content("Bạn có 1 đơn nghỉ phép mới từ nhân viên " + employeeName + " cần phê duyệt.")
                        .type(HRNotificationType.TASK_PENDING)
                        .relatedEntityType(HRRelatedEntityType.LEAVE_REQUEST)
                        .relatedEntityId(leaveRequestId)
                        .priority(HRNotificationPriority.HIGH)
                        .createdBySystem(true)
                        .build()
        );
    }

    @Override
    public void notifyEmployeeLeaveApprovedByManager(
            Integer employeeId,
            Long leaveRequestId
    ) {
        hrNotificationService.create(
                HRNotificationCreateDto.builder()
                        .recipientId(employeeId)
                        .title("Đơn nghỉ phép đã được trưởng phòng phê duyệt")
                        .content("Đơn nghỉ phép của bạn đã được trưởng phòng phê duyệt và chuyển đến HR.")
                        .type(HRNotificationType.LEAVE_APPROVED)
                        .relatedEntityType(HRRelatedEntityType.LEAVE_REQUEST)
                        .relatedEntityId(leaveRequestId)
                        .priority(HRNotificationPriority.NORMAL)
                        .createdBySystem(true)
                        .build()
        );
    }

    @Override
    public void notifyHrLeaveWaitingForApproval(
            Integer hrId,
            Long leaveRequestId,
            String employeeName
    ) {
        hrNotificationService.create(
                HRNotificationCreateDto.builder()
                        .recipientId(hrId)
                        .title("Có đơn nghỉ phép cần HR xử lý")
                        .content("Bạn có 1 đơn nghỉ phép từ nhân viên " + employeeName + " đang chờ xử lý.")
                        .type(HRNotificationType.TASK_PENDING)
                        .relatedEntityType(HRRelatedEntityType.LEAVE_REQUEST)
                        .relatedEntityId(leaveRequestId)
                        .priority(HRNotificationPriority.HIGH)
                        .createdBySystem(true)
                        .build()
        );
    }

    @Override
    public void notifyEmployeeLeaveApprovedFinal(
            Integer employeeId,
            Long leaveRequestId
    ) {
        hrNotificationService.create(
                HRNotificationCreateDto.builder()
                        .recipientId(employeeId)
                        .title("Đơn nghỉ phép đã được phê duyệt")
                        .content("Đơn nghỉ phép của bạn đã được phê duyệt hoàn tất.")
                        .type(HRNotificationType.LEAVE_APPROVED)
                        .relatedEntityType(HRRelatedEntityType.LEAVE_REQUEST)
                        .relatedEntityId(leaveRequestId)
                        .priority(HRNotificationPriority.NORMAL)
                        .createdBySystem(true)
                        .build()
        );
    }

    @Override
    public void notifyEmployeeLeaveRejected(
            Integer employeeId,
            Long leaveRequestId,
            String reason
    ) {
        hrNotificationService.create(
                HRNotificationCreateDto.builder()
                        .recipientId(employeeId)
                        .title("Đơn nghỉ phép bị từ chối")
                        .content("Đơn nghỉ phép của bạn đã bị từ chối. Lý do: " + reason)
                        .type(HRNotificationType.LEAVE_REJECTED)
                        .relatedEntityType(HRRelatedEntityType.LEAVE_REQUEST)
                        .relatedEntityId(leaveRequestId)
                        .priority(HRNotificationPriority.HIGH)
                        .createdBySystem(true)
                        .build()
        );
    }

    @Override
    public void notifyManagerLeaveCancelled(
            Integer managerId,
            Long leaveRequestId,
            String employeeName
    ) {
        hrNotificationService.create(
                HRNotificationCreateDto.builder()
                        .recipientId(managerId)
                        .title("Đơn nghỉ phép đã bị hủy")
                        .content("Nhân viên " + employeeName + " đã hủy đơn nghỉ phép đã gửi trước đó.")
                        .type(HRNotificationType.LEAVE_CANCELLED)
                        .relatedEntityType(HRRelatedEntityType.LEAVE_REQUEST)
                        .relatedEntityId(leaveRequestId)
                        .priority(HRNotificationPriority.NORMAL)
                        .createdBySystem(true)
                        .build()
        );
    }
}