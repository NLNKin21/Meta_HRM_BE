-- ========================================
-- LEAVE MANAGEMENT SCHEMA
-- Version: V10
-- Author: System
-- Description: Create tables for leave management module
-- ========================================

-- ========================================
-- 1. LEAVE TYPES TABLE
-- ========================================
CREATE TABLE IF NOT EXISTS leave_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL COMMENT 'Mã loại nghỉ phép (ANNUAL_LEAVE, SICK_LEAVE...)',
    name VARCHAR(150) NOT NULL COMMENT 'Tên loại nghỉ phép',
    max_days_per_year INT NOT NULL COMMENT 'Số ngày tối đa trong năm',
    default_days_per_year INT NOT NULL COMMENT 'Số ngày mặc định',
    
    -- Cấu hình loại phép
    paid_leave BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Có hưởng lương?',
    requires_approval BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Cần phê duyệt?',
    requires_document BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Cần giấy tờ chứng minh?',
    active BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Đang hoạt động?',
    
    -- Cấu hình số dư
    deduct_balance BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Trừ số dư phép?',
    deduct_from_annual_leave_balance BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Trừ từ phép năm?',
    auto_approve BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Tự động duyệt?',
    allow_carry_forward BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Cho phép chuyển sang năm sau?',
    allow_encashment BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Cho phép quy đổi tiền?',
    
    -- Cấu hình tính toán
    counts_in_attendance BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Tính vào chấm công?',
    counts_in_company_payroll BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Tính vào bảng lương?',
    deduct_salary BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Trừ lương?',
    social_insurance_paid BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Đóng BHXH?',
    increase_by_seniority BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Tăng theo thâm niên?',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_code (code),
    INDEX idx_active (active)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Bảng loại nghỉ phép';

-- ========================================
-- 2. LEAVE TYPE SENIORITY RULES TABLE
-- ========================================
CREATE TABLE IF NOT EXISTS leave_type_seniority_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    leave_type_id BIGINT NOT NULL COMMENT 'ID loại nghỉ phép',
    min_years INT NOT NULL COMMENT 'Số năm thâm niên tối thiểu',
    extra_days INT NOT NULL COMMENT 'Số ngày phép được cộng thêm',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY unique_leave_type_years (leave_type_id, min_years),
    INDEX idx_leave_type (leave_type_id),
    INDEX idx_min_years (min_years),
    
    CONSTRAINT fk_seniority_leave_type FOREIGN KEY (leave_type_id) 
        REFERENCES leave_types(id) ON DELETE CASCADE
        
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Bảng quy tắc thâm niên cho loại nghỉ phép';

-- ========================================
-- 3. HOLIDAYS TABLE
-- ========================================
CREATE TABLE IF NOT EXISTS holidays (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    holiday_date DATE UNIQUE NOT NULL COMMENT 'Ngày nghỉ lễ',
    name VARCHAR(200) NOT NULL COMMENT 'Tên ngày lễ',
    active BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Đang hoạt động?',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_date (holiday_date),
    INDEX idx_active (active)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Bảng ngày nghỉ lễ';

-- ========================================
-- 4. LEAVE BALANCES TABLE
-- ========================================
CREATE TABLE IF NOT EXISTS leave_balances (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL COMMENT 'ID nhân viên',
    leave_type_id BIGINT NOT NULL COMMENT 'ID loại nghỉ phép',
    year INT NOT NULL COMMENT 'Năm áp dụng',
    
    allocated_days DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT 'Số ngày được cấp',
    used_days DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT 'Số ngày đã sử dụng',
    pending_days DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT 'Số ngày đang chờ duyệt',
    carry_forward_days DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT 'Số ngày chuyển sang năm sau',
    encashed_days DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT 'Số ngày đã quy đổi tiền',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY unique_employee_leave_year (employee_id, leave_type_id, year),
    INDEX idx_employee (employee_id),
    INDEX idx_leave_type (leave_type_id),
    INDEX idx_year (year),
    
    CONSTRAINT fk_balance_leave_type FOREIGN KEY (leave_type_id) 
        REFERENCES leave_types(id) ON DELETE CASCADE
        
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Bảng số dư phép của nhân viên';

-- ========================================
-- 5. LEAVE REQUESTS TABLE
-- ========================================
CREATE TABLE IF NOT EXISTS leave_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Thông tin nhân viên
    employee_id INT NOT NULL COMMENT 'ID nhân viên xin phép',
    employee_name VARCHAR(255) COMMENT 'Tên nhân viên (cache)',
    manager_id INT COMMENT 'ID người quản lý',
    hr_id INT COMMENT 'ID nhân viên HR',
    
    -- Thông tin nghỉ phép
    leave_type_id BIGINT NOT NULL COMMENT 'ID loại nghỉ phép',
    start_date DATE NOT NULL COMMENT 'Ngày bắt đầu',
    end_date DATE NOT NULL COMMENT 'Ngày kết thúc',
    leave_unit VARCHAR(20) NOT NULL DEFAULT 'FULL_DAY' COMMENT 'Đơn vị: FULL_DAY, HALF_DAY',
    start_session VARCHAR(20) NOT NULL DEFAULT 'FULL_DAY' COMMENT 'Buổi bắt đầu: MORNING, AFTERNOON, FULL_DAY',
    end_session VARCHAR(20) NOT NULL DEFAULT 'FULL_DAY' COMMENT 'Buổi kết thúc: MORNING, AFTERNOON, FULL_DAY',
    total_days DECIMAL(10,2) NOT NULL COMMENT 'Tổng số ngày nghỉ',
    reason VARCHAR(1000) NOT NULL COMMENT 'Lý do xin phép',
    
    -- Trạng thái
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT, PENDING, APPROVED, REJECTED, CANCELLED',
    approval_stage VARCHAR(30) NOT NULL DEFAULT 'NONE' COMMENT 'NONE, WAITING_MANAGER, WAITING_HR, COMPLETED',
    reject_reason TEXT COMMENT 'Lý do từ chối',
    cancel_reason TEXT COMMENT 'Lý do hủy',
    final_approved BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Đã duyệt hoàn toàn?',
    
    -- Thời gian
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,
    submitted_at TIMESTAMP NULL COMMENT 'Thời điểm gửi đơn',
    approved_at TIMESTAMP NULL COMMENT 'Thời điểm duyệt cuối',
    cancelled_at TIMESTAMP NULL COMMENT 'Thời điểm hủy',
    
    INDEX idx_employee (employee_id),
    INDEX idx_leave_type (leave_type_id),
    INDEX idx_status (status),
    INDEX idx_approval_stage (approval_stage),
    INDEX idx_dates (start_date, end_date),
    INDEX idx_manager (manager_id),
    INDEX idx_hr (hr_id),
    
    CONSTRAINT fk_request_leave_type FOREIGN KEY (leave_type_id) 
        REFERENCES leave_types(id) ON DELETE RESTRICT
        
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Bảng đơn xin nghỉ phép';

-- ========================================
-- 6. LEAVE APPROVAL HISTORIES TABLE
-- ========================================
CREATE TABLE IF NOT EXISTS leave_approval_histories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    leave_request_id BIGINT NOT NULL COMMENT 'ID đơn xin phép',
    actor_id INT NOT NULL COMMENT 'ID người thực hiện',
    actor_role VARCHAR(20) NOT NULL COMMENT 'Vai trò: EMPLOYEE, MANAGER, HR',
    action VARCHAR(30) NOT NULL COMMENT 'Hành động: CREATED_DRAFT, SUBMITTED, APPROVED, REJECTED, CANCELLED...',
    stage VARCHAR(30) NOT NULL COMMENT 'Giai đoạn: NONE, WAITING_MANAGER, WAITING_HR, COMPLETED',
    note TEXT COMMENT 'Ghi chú',
    action_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_leave_request (leave_request_id),
    INDEX idx_actor (actor_id),
    INDEX idx_action_at (action_at),
    
    CONSTRAINT fk_history_leave_request FOREIGN KEY (leave_request_id) 
        REFERENCES leave_requests(id) ON DELETE CASCADE
        
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Bảng lịch sử phê duyệt đơn nghỉ phép';

-- ========================================
-- 7. LEAVE ATTACHMENTS TABLE
-- ========================================
CREATE TABLE IF NOT EXISTS leave_attachments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    leave_request_id BIGINT NOT NULL COMMENT 'ID đơn xin phép',
    file_name VARCHAR(255) NOT NULL COMMENT 'Tên file',
    file_url VARCHAR(500) NOT NULL COMMENT 'URL file',
    file_type VARCHAR(50) NOT NULL COMMENT 'Loại file (MIME type)',
    file_size BIGINT NOT NULL COMMENT 'Kích thước file (bytes)',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_leave_request (leave_request_id),
    
    CONSTRAINT fk_attachment_leave_request FOREIGN KEY (leave_request_id) 
        REFERENCES leave_requests(id) ON DELETE CASCADE
        
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Bảng file đính kèm đơn nghỉ phép';

-- ========================================
-- COMPLETED
-- ========================================