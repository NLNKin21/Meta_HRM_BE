package com.metahrms.employee_management.enums;

public enum LeaveType {
    ANNUAL_LEAVE,       // Nghỉ phép năm
    SICK_LEAVE,         // Nghỉ ốm (Có giấy BHXH)
    MATERNITY_LEAVE,    // Thai sản
    UNPAID_LEAVE,       // Nghỉ không lương
    COMPENSATORY_LEAVE, // Nghỉ bù (Quan trọng: Dùng khi OT ngày chủ nhật -> được nghỉ bù)
    MARRIAGE_LEAVE,     // Nghỉ cưới hỏi (Theo luật được hưởng nguyên lương)
    BEREAVEMENT_LEAVE,  // Nghỉ tang chế (Hiếu)
    OTHER
}
