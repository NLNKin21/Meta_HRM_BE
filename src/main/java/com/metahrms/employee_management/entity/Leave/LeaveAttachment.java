package com.metahrms.employee_management.entity.Leave;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "leave_request_id", nullable = false)
    private Long leaveRequestId;

    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(nullable = false, length = 500)
    private String fileUrl;

    @Column(nullable = false, length = 50)
    private String fileType;

    @Column(nullable = false)
    private Long fileSize;
}