package com.metahrms.employee_management.enums;

public enum CandidateStatus {
    NEW,                    // Vừa nộp đơn
    REVIEWING,              // HR đang xem xét
    INTERVIEW_SCHEDULED,    // Đã lên lịch phỏng vấn
    INTERVIEWED,            // Đã phỏng vấn xong
    APPROVED,               // Đã duyệt - chờ tạo User
    ONBOARDED,              // Đã tạo User thành công
    REJECTED                // Từ chối
}