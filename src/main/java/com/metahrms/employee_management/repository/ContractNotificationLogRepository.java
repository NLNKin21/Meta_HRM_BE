package com.metahrms.employee_management.repository;

import com.metahrms.employee_management.entity.ContractNotificationLog;
import com.metahrms.employee_management.enums.ContractNotificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractNotificationLogRepository extends JpaRepository<ContractNotificationLog, Integer> {

    boolean existsByContractIdAndRecipientIdAndNotificationCodeAndIsDeletedFalse(
            Integer contractId,
            Integer recipientId,
            ContractNotificationCode notificationCode
    );
}