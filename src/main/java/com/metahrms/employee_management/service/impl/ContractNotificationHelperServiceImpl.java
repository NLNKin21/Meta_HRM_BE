package com.metahrms.employee_management.service.impl;

import com.metahrms.employee_management.dto.request.HRNotificationCreateDto;
import com.metahrms.employee_management.entity.ContractNotificationLog;
import com.metahrms.employee_management.enums.ContractNotificationCode;
import com.metahrms.employee_management.enums.HRNotificationType;
import com.metahrms.employee_management.enums.HRRelatedEntityType;
import com.metahrms.employee_management.repository.ContractNotificationLogRepository;
import com.metahrms.employee_management.service.ContractNotificationHelperService;
import com.metahrms.employee_management.service.HRNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContractNotificationHelperServiceImpl implements ContractNotificationHelperService {

    private final HRNotificationService hrNotificationService;
    private final ContractNotificationLogRepository contractNotificationLogRepository;

    private void createNotificationWithLog(
            Integer recipientId,
            String title,
            String content,
            Integer contractId,
            ContractNotificationCode code,
            HRNotificationType notificationType
    ) {
        boolean exists = contractNotificationLogRepository
                .existsByContractIdAndRecipientIdAndNotificationCodeAndIsDeletedFalse(
                        contractId,
                        recipientId,
                        code
                );

        if (exists) {
            return;
        }

        HRNotificationCreateDto dto = new HRNotificationCreateDto();
        dto.setRecipientId(recipientId);
        dto.setTitle(title);
        dto.setContent(content);

        // ✅ FIX CHUẨN ENUM
        dto.setType(notificationType);
        dto.setRelatedEntityType(HRRelatedEntityType.CONTRACT);
        dto.setRelatedEntityId(Long.valueOf(contractId));

        dto.setCreatedBySystem(true);

        hrNotificationService.create(dto);

        ContractNotificationLog log = ContractNotificationLog.builder()
                .contractId(contractId)
                .recipientId(recipientId)
                .notificationCode(code)
                .sentAt(LocalDateTime.now())
                .build();

        contractNotificationLogRepository.save(log);
    }

    private ContractNotificationCode resolveExpiringCode(long daysLeft) {
        return switch ((int) daysLeft) {
            case 30 -> ContractNotificationCode.CONTRACT_EXPIRING_30;
            case 15 -> ContractNotificationCode.CONTRACT_EXPIRING_15;
            case 7 -> ContractNotificationCode.CONTRACT_EXPIRING_7;
            case 3 -> ContractNotificationCode.CONTRACT_EXPIRING_3;
            case 1 -> ContractNotificationCode.CONTRACT_EXPIRING_1;
            default -> null;
        };
    }

    @Override
    public void notifyHrContractExpiring(
            Integer hrId,
            Integer employeeId,
            Integer contractId,
            String employeeName,
            String endDate,
            long daysLeft
    ) {
        ContractNotificationCode code = resolveExpiringCode(daysLeft);
        if (code == null) return;

        createNotificationWithLog(
                hrId,
                "HĐ của " + employeeName + " sắp hết hạn",
                "HĐ của " + employeeName + " sẽ hết hạn sau " + daysLeft + " ngày (" + endDate + ").",
                contractId,
                code,
                HRNotificationType.CONTRACT_EXPIRING
        );
    }

    @Override
    public void notifyEmployeeContractExpiring(
            Integer employeeId,
            Integer contractId,
            String endDate,
            long daysLeft
    ) {
        ContractNotificationCode code = resolveExpiringCode(daysLeft);
        if (code == null) return;

        createNotificationWithLog(
                employeeId,
                "HĐ lao động sắp hết hạn",
                "HĐ lao động của bạn sẽ hết hạn sau " + daysLeft + " ngày (" + endDate + ").",
                contractId,
                code,
                HRNotificationType.CONTRACT_EXPIRING
        );
    }

    @Override
    public void notifyHrContractExpired(
            Integer hrId,
            Integer employeeId,
            Integer contractId,
            String employeeName,
            String endDate
    ) {
        createNotificationWithLog(
                hrId,
                "HĐ của " + employeeName + " đã hết hạn",
                "HĐ của " + employeeName + " đã hết hạn từ ngày " + endDate + ".",
                contractId,
                ContractNotificationCode.CONTRACT_EXPIRED,
                HRNotificationType.CONTRACT_EXPIRED
        );
    }

    @Override
    public void notifyEmployeeContractExpired(
            Integer employeeId,
            Integer contractId,
            String endDate
    ) {
        createNotificationWithLog(
                employeeId,
                "HĐ lao động đã hết hiệu lực",
                "HĐ lao động của bạn đã hết hiệu lực từ ngày " + endDate + ".",
                contractId,
                ContractNotificationCode.CONTRACT_EXPIRED,
                HRNotificationType.CONTRACT_EXPIRED
        );
    }

    @Override
    public void notifyEmployeeContractRenewed(
            Integer employeeId,
            Integer contractId,
            String newEndDate
    ) {
        createNotificationWithLog(
                employeeId,
                "Gia hạn hợp đồng",
                "HĐ lao động của bạn đã được gia hạn đến ngày " + newEndDate + ".",
                contractId,
                ContractNotificationCode.CONTRACT_RENEWED,
                HRNotificationType.CONTRACT_UPDATED
        );
    }

    @Override
    public void notifyEmployeeContractCreated(
            Integer employeeId,
            Integer contractId,
            String contractType,
            String startDate,
            String endDate
    ) {
        createNotificationWithLog(
                employeeId,
                "HĐ lao động mới",
                "Một HĐ mới (" + contractType + ") từ " + startDate + " đến " + endDate + ".",
                contractId,
                ContractNotificationCode.CONTRACT_CREATED,
                HRNotificationType.CONTRACT_UPDATED
        );
    }

    @Override
    public void notifyEmployeeContractUpdated(
            Integer employeeId,
            Integer contractId
    ) {
        createNotificationWithLog(
                employeeId,
                "HĐ đã cập nhật",
                "HĐ lao động của bạn vừa được cập nhật.",
                contractId,
                ContractNotificationCode.CONTRACT_UPDATED,
                HRNotificationType.CONTRACT_UPDATED
        );
    }

    @Override
    public void notifyEmployeeContractTerminated(
            Integer employeeId,
            Integer contractId,
            String effectiveDate
    ) {
        createNotificationWithLog(
                employeeId,
                "HĐ đã chấm dứt",
                "HĐ lao động của bạn đã chấm dứt từ ngày " + effectiveDate + ".",
                contractId,
                ContractNotificationCode.CONTRACT_TERMINATED,
                HRNotificationType.CONTRACT_UPDATED
        );
    }
}