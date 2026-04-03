package com.metahrms.employee_management.entity;

import com.metahrms.employee_management.enums.ContractNotificationCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "contract_notification_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractNotificationLog extends BaseEntity {

    @Column(name = "contract_id", nullable = false)
    private Integer contractId;

    @Column(name = "recipient_id", nullable = false)
    private Integer recipientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_code", nullable = false, length = 50)
    private ContractNotificationCode notificationCode;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;
}