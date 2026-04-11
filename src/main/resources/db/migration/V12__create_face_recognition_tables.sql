-- ================================================
-- ATTENDANCE MODULE - CREATE TABLES
-- Version: V12
-- Description: Tables for face recognition attendance system
-- Author: MetaHRM Team
-- ================================================

-- ================================================
-- TABLE 1: shifts (Ca làm việc)
-- Phải tạo TRƯỚC vì các bảng khác reference đến
-- ================================================
CREATE TABLE shifts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT 'Tên ca (VD: Ca sáng, Ca chiều)',
    code VARCHAR(50) UNIQUE COMMENT 'Mã ca (VD: MORNING, AFTERNOON)',
    
    start_time TIME NOT NULL COMMENT 'Giờ bắt đầu ca',
    end_time TIME NOT NULL COMMENT 'Giờ kết thúc ca',
    
    late_threshold INT DEFAULT 15 COMMENT 'Số phút được phép đi muộn',
    early_leave_threshold INT DEFAULT 15 COMMENT 'Số phút được phép về sớm',
    check_in_start_before INT DEFAULT 30 COMMENT 'Được phép check-in sớm (phút)',
    check_in_end_after INT DEFAULT 120 COMMENT 'Được phép check-in muộn (phút)',
    
    work_days JSON COMMENT 'Các ngày trong tuần áp dụng ca này [1,2,3,4,5]',
    break_duration INT DEFAULT 60 COMMENT 'Thời gian nghỉ trưa (phút)',
    
    description TEXT COMMENT 'Mô tả ca làm việc',
    color VARCHAR(20) COMMENT 'Màu hiển thị trên UI (VD: #FF5733)',
    
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT,
    updated_by INT,
    
    INDEX idx_code (code),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Thông tin ca làm việc';

-- ================================================
-- TABLE 2: work_locations (Địa điểm làm việc)
-- ================================================
CREATE TABLE work_locations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL COMMENT 'Tên địa điểm',
    code VARCHAR(50) UNIQUE COMMENT 'Mã địa điểm (VD: HQ-HN)',
    address TEXT COMMENT 'Địa chỉ đầy đủ',
    
    latitude DECIMAL(10, 8) NOT NULL COMMENT 'Vĩ độ (Latitude)',
    longitude DECIMAL(11, 8) NOT NULL COMMENT 'Kinh độ (Longitude)',
    radius INT DEFAULT 100 COMMENT 'Bán kính cho phép (mét)',
    
    description TEXT COMMENT 'Mô tả địa điểm',
    contact_person VARCHAR(100) COMMENT 'Người liên hệ',
    contact_phone VARCHAR(20) COMMENT 'Số điện thoại',
    
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT,
    updated_by INT,
    
    INDEX idx_code (code),
    INDEX idx_coordinates (latitude, longitude),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Địa điểm làm việc với GPS geofencing';

-- ================================================
-- TABLE 3: employee_faces (Khuôn mặt nhân viên)
-- ================================================
CREATE TABLE employee_faces (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    
    face_image_url VARCHAR(500) NOT NULL COMMENT 'Cloudinary URL của ảnh khuôn mặt',
    face_encoding TEXT NOT NULL COMMENT 'JSON array chứa 512-dim embedding vector',
    
    confidence_score DECIMAL(5,2) COMMENT 'Độ tin cậy khi detect face (0-100)',
    is_primary BOOLEAN DEFAULT FALSE COMMENT 'Ảnh chính dùng để nhận diện',
    is_active BOOLEAN DEFAULT TRUE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT,
    updated_by INT,
    
    CONSTRAINT fk_face_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    
    INDEX idx_employee_id (employee_id),
    INDEX idx_employee_primary (employee_id, is_primary),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Lưu trữ thông tin khuôn mặt của nhân viên';

-- ================================================
-- TABLE 4: attendance_records (Bảng chấm công)
-- ================================================
CREATE TABLE attendance_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    date DATE NOT NULL COMMENT 'Ngày chấm công',
    shift_id INT COMMENT 'Ca làm việc',
    
    -- CHECK-IN DATA
    check_in_time DATETIME COMMENT 'Thời gian check-in',
    check_in_location_id INT COMMENT 'Địa điểm check-in',
    check_in_lat DECIMAL(10, 8) COMMENT 'Vĩ độ khi check-in',
    check_in_lng DECIMAL(11, 8) COMMENT 'Kinh độ khi check-in',
    check_in_photo_url VARCHAR(500) COMMENT 'URL ảnh chấm công vào',
    check_in_face_match_score DECIMAL(5,2) COMMENT 'Điểm khớp khuôn mặt (%)',
    check_in_device_info JSON COMMENT 'Thông tin thiết bị',
    check_in_note TEXT COMMENT 'Ghi chú khi check-in',
    
    -- CHECK-OUT DATA
    check_out_time DATETIME COMMENT 'Thời gian check-out',
    check_out_location_id INT COMMENT 'Địa điểm check-out',
    check_out_lat DECIMAL(10, 8) COMMENT 'Vĩ độ khi check-out',
    check_out_lng DECIMAL(11, 8) COMMENT 'Kinh độ khi check-out',
    check_out_photo_url VARCHAR(500) COMMENT 'URL ảnh chấm công ra',
    check_out_face_match_score DECIMAL(5,2) COMMENT 'Điểm khớp khuôn mặt (%)',
    check_out_device_info JSON COMMENT 'Thông tin thiết bị',
    check_out_note TEXT COMMENT 'Ghi chú khi check-out',
    
    -- CALCULATED FIELDS
    status ENUM('PRESENT', 'LATE', 'EARLY_LEAVE', 'ABSENT', 'LEAVE', 'NOT_CHECKED') DEFAULT 'NOT_CHECKED',
    work_hours DECIMAL(4,2) DEFAULT 0 COMMENT 'Số giờ làm việc thực tế',
    overtime_hours DECIMAL(4,2) DEFAULT 0 COMMENT 'Số giờ làm thêm',
    late_minutes INT DEFAULT 0 COMMENT 'Số phút đi muộn',
    early_leave_minutes INT DEFAULT 0 COMMENT 'Số phút về sớm',
    
    -- VERIFICATION
    is_verified BOOLEAN DEFAULT FALSE,
    verified_by INT,
    verified_at DATETIME,
    verification_note TEXT,
    
    -- APPROVAL
    is_approved BOOLEAN DEFAULT NULL,
    approved_by INT,
    approved_at DATETIME,
    approval_note TEXT,
    
    note TEXT COMMENT 'Ghi chú chung',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign keys
    CONSTRAINT fk_attendance_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    CONSTRAINT fk_attendance_shift FOREIGN KEY (shift_id) REFERENCES shifts(id),
    CONSTRAINT fk_attendance_checkin_location FOREIGN KEY (check_in_location_id) REFERENCES work_locations(id),
    CONSTRAINT fk_attendance_checkout_location FOREIGN KEY (check_out_location_id) REFERENCES work_locations(id),
    
    -- Indexes
    UNIQUE KEY unique_employee_date (employee_id, date),
    INDEX idx_date (date),
    INDEX idx_status (status),
    INDEX idx_employee_date (employee_id, date DESC),
    INDEX idx_check_in_time (check_in_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Bảng chấm công hàng ngày';

-- ================================================
-- TABLE 5: attendance_anomalies (Bất thường)
-- ================================================
CREATE TABLE attendance_anomalies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    attendance_id INT NOT NULL,
    
    anomaly_type ENUM(
        'FACE_MISMATCH',
        'GPS_INVALID',
        'TIME_VIOLATION',
        'DUPLICATE',
        'SUSPICIOUS',
        'LOW_QUALITY',
        'MULTIPLE_FACES'
    ) NOT NULL,
    
    description TEXT COMMENT 'Mô tả chi tiết',
    severity ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') DEFAULT 'MEDIUM',
    metadata JSON COMMENT 'Dữ liệu bổ sung',
    
    resolved BOOLEAN DEFAULT FALSE,
    resolved_by INT,
    resolved_at DATETIME,
    resolution_note TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_anomaly_attendance FOREIGN KEY (attendance_id) REFERENCES attendance_records(id) ON DELETE CASCADE,
    
    INDEX idx_attendance (attendance_id),
    INDEX idx_type (anomaly_type),
    INDEX idx_severity (severity),
    INDEX idx_resolved (resolved)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Theo dõi các trường hợp chấm công bất thường';

-- ================================================
-- ALTER TABLE: Add shift_id to employees
-- ================================================
ALTER TABLE employees ADD COLUMN shift_id INT COMMENT 'Ca làm việc mặc định';
ALTER TABLE employees ADD CONSTRAINT fk_employees_shift FOREIGN KEY (shift_id) REFERENCES shifts(id);
ALTER TABLE employees ADD INDEX idx_shift_id (shift_id);