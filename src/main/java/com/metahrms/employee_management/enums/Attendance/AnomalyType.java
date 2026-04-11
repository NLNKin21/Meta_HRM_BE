package com.metahrms.employee_management.enums.Attendance;

public enum AnomalyType {
    FACE_MISMATCH,      // Khuôn mặt không khớp
    GPS_INVALID,        // GPS ngoài phạm vi
    TIME_VIOLATION,     // Chấm công ngoài giờ
    DUPLICATE,          // Chấm công trùng
    SUSPICIOUS,         // Nghi ngờ
    LOW_QUALITY,        // Ảnh chất lượng kém
    MULTIPLE_FACES      // Nhiều khuôn mặt
}