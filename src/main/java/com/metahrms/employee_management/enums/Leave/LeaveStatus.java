package com.metahrms.employee_management.enums.Leave;

public enum LeaveStatus {
    DRAFT,          //đơn nháp, mới tạo, chưa gửi
    PENDING,        //đã gửi, đang chờ duyệt
    APPROVED,       //đã được duyệt 
    REJECTED,       //bị từ chối
    CANCELLED       //bị hủy
}