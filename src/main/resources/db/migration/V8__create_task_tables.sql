-- ========================================
-- TASK MANAGEMENT SCHEMA
-- Version: V8
-- Author: System
-- Description: Create tables for task management module
-- ========================================

-- ========================================
-- 1. PROJECTS TABLE
-- ========================================
CREATE TABLE IF NOT EXISTS projects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    project_code VARCHAR(50) UNIQUE NOT NULL COMMENT 'Mã dự án (PRJ-2024-001)',
    project_name VARCHAR(200) NOT NULL COMMENT 'Tên dự án',
    description TEXT COMMENT 'Mô tả dự án',
    department_id INT COMMENT 'Phòng ban phụ trách',
    manager_id INT COMMENT 'Người quản lý dự án',
    start_date DATE COMMENT 'Ngày bắt đầu',
    end_date DATE COMMENT 'Ngày kết thúc',
    status VARCHAR(20) DEFAULT 'PLANNING' COMMENT 'Trạng thái: PLANNING, ACTIVE, ON_HOLD, COMPLETED, CANCELLED',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Còn hoạt động?',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT,
    updated_by INT,
    
    INDEX idx_project_code (project_code),
    INDEX idx_department (department_id),
    INDEX idx_manager (manager_id),
    INDEX idx_status (status),
    
    CONSTRAINT fk_project_department FOREIGN KEY (department_id) 
        REFERENCES departments(id) ON DELETE SET NULL,
    CONSTRAINT fk_project_manager FOREIGN KEY (manager_id) 
        REFERENCES employees(id) ON DELETE SET NULL
        
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Bảng quản lý dự án';

-- ========================================
-- 2. TASK_STATUSES TABLE
-- ========================================
CREATE TABLE IF NOT EXISTS task_statuses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    status_name VARCHAR(100) NOT NULL COMMENT 'Tên trạng thái (tiếng Việt)',
    status_name_en VARCHAR(100) COMMENT 'Tên trạng thái (tiếng Anh)',
    order_index INT NOT NULL DEFAULT 0 COMMENT 'Thứ tự hiển thị',
    color VARCHAR(7) DEFAULT '#1976d2' COMMENT 'Màu sắc (hex)',
    icon VARCHAR(50) COMMENT 'Tên icon MUI',
    is_completed BOOLEAN DEFAULT FALSE COMMENT 'Là trạng thái hoàn thành?',
    is_default BOOLEAN DEFAULT FALSE COMMENT 'Là trạng thái mặc định?',
    department_id INT COMMENT 'Phòng ban (null = áp dụng chung)',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Còn hoạt động?',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT,
    updated_by INT,
    
    INDEX idx_department (department_id),
    INDEX idx_order (order_index),
    INDEX idx_active (is_active),
    
    CONSTRAINT fk_status_department FOREIGN KEY (department_id) 
        REFERENCES departments(id) ON DELETE CASCADE
        
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Bảng trạng thái task (dynamic)';

-- ========================================
-- 3. TASKS TABLE (CORE)
-- ========================================
CREATE TABLE IF NOT EXISTS tasks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    task_code VARCHAR(50) UNIQUE NOT NULL COMMENT 'Mã task (TSK-20240115-001)',
    title VARCHAR(255) NOT NULL COMMENT 'Tiêu đề task',
    description TEXT COMMENT 'Mô tả chi tiết',
    
    -- Phân loại
    task_type VARCHAR(20) DEFAULT 'TASK' COMMENT 'Loại: TASK, BUG, FEATURE, IMPROVEMENT',
    priority VARCHAR(20) DEFAULT 'MEDIUM' COMMENT 'Ưu tiên: LOW, MEDIUM, HIGH, URGENT',
    
    -- Trạng thái
    status_id INT NOT NULL COMMENT 'Trạng thái hiện tại',
    
    -- Người liên quan
    reporter_id INT NOT NULL COMMENT 'Người tạo task',
    assignee_id INT NOT NULL COMMENT 'Người thực hiện',
    approver_id INT COMMENT 'Người duyệt',
    
    -- Phân nhóm
    department_id INT NOT NULL COMMENT 'Phòng ban',
    project_id INT COMMENT 'Dự án',
    
    -- Thời gian
    estimated_hours DECIMAL(8,2) COMMENT 'Số giờ ước lượng',
    actual_hours DECIMAL(8,2) DEFAULT 0 COMMENT 'Số giờ thực tế',
    start_date DATE COMMENT 'Ngày bắt đầu',
    due_date DATE COMMENT 'Ngày hết hạn',
    completed_at TIMESTAMP NULL COMMENT 'Thời điểm hoàn thành',
    
    -- Tiến độ & KPI
    completion_rate INT DEFAULT 0 COMMENT 'Tỉ lệ hoàn thành (%)',
    is_late BOOLEAN DEFAULT FALSE COMMENT 'Trễ deadline?',
    is_urgent BOOLEAN DEFAULT FALSE COMMENT 'Gấp?',
    
    -- Metadata
    is_deleted BOOLEAN DEFAULT FALSE COMMENT 'Đã xóa? (soft delete)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT,
    updated_by INT,
    
    INDEX idx_task_code (task_code),
    INDEX idx_status (status_id),
    INDEX idx_assignee (assignee_id),
    INDEX idx_reporter (reporter_id),
    INDEX idx_department (department_id),
    INDEX idx_project (project_id),
    INDEX idx_due_date (due_date),
    INDEX idx_priority (priority),
    INDEX idx_is_late (is_late),
    INDEX idx_is_deleted (is_deleted),
    
    CONSTRAINT fk_task_status FOREIGN KEY (status_id) 
        REFERENCES task_statuses(id),
    CONSTRAINT fk_task_reporter FOREIGN KEY (reporter_id) 
        REFERENCES employees(id),
    CONSTRAINT fk_task_assignee FOREIGN KEY (assignee_id) 
        REFERENCES employees(id),
    CONSTRAINT fk_task_approver FOREIGN KEY (approver_id) 
        REFERENCES employees(id) ON DELETE SET NULL,
    CONSTRAINT fk_task_department FOREIGN KEY (department_id) 
        REFERENCES departments(id),
    CONSTRAINT fk_task_project FOREIGN KEY (project_id) 
        REFERENCES projects(id) ON DELETE SET NULL,
        
    CONSTRAINT chk_completion_rate CHECK (completion_rate BETWEEN 0 AND 100)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Bảng quản lý task';

-- ========================================
-- 4. TASK_STATUS_TRANSITIONS TABLE (OPTIONAL)
-- ========================================
CREATE TABLE IF NOT EXISTS task_status_transitions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    from_status_id INT NOT NULL COMMENT 'Từ trạng thái',
    to_status_id INT NOT NULL COMMENT 'Đến trạng thái',
    department_id INT COMMENT 'Phòng ban (null = áp dụng chung)',
    allowed_roles TEXT COMMENT 'Roles được phép chuyển (JSON)',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Còn hiệu lực?',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT,
    updated_by INT,
    
    UNIQUE KEY unique_transition (from_status_id, to_status_id),
    INDEX idx_from_status (from_status_id),
    INDEX idx_to_status (to_status_id),
    
    CONSTRAINT fk_transition_from FOREIGN KEY (from_status_id) 
        REFERENCES task_statuses(id) ON DELETE CASCADE,
    CONSTRAINT fk_transition_to FOREIGN KEY (to_status_id) 
        REFERENCES task_statuses(id) ON DELETE CASCADE,
    CONSTRAINT fk_transition_department FOREIGN KEY (department_id) 
        REFERENCES departments(id) ON DELETE CASCADE
        
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Bảng quy tắc chuyển trạng thái';

-- ========================================
-- 5. TASK_COMMENTS TABLE
-- ========================================
CREATE TABLE IF NOT EXISTS task_comments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT NOT NULL COMMENT 'Task được comment',
    user_id INT NOT NULL COMMENT 'Người comment',
    content TEXT NOT NULL COMMENT 'Nội dung comment',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT 'Đã xóa?',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT,
    updated_by INT,
    
    INDEX idx_task (task_id),
    INDEX idx_user (user_id),
    INDEX idx_created (created_at),
    
    CONSTRAINT fk_comment_task FOREIGN KEY (task_id) 
        REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) 
        REFERENCES employees(id)
        
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Bảng comment trong task';

-- ========================================
-- 6. TASK_HISTORIES TABLE (AUDIT LOG)
-- ========================================
CREATE TABLE IF NOT EXISTS task_histories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT NOT NULL COMMENT 'Task bị thay đổi',
    user_id INT NOT NULL COMMENT 'Người thực hiện',
    field_name VARCHAR(50) COMMENT 'Tên trường thay đổi',
    old_value TEXT COMMENT 'Giá trị cũ',
    new_value TEXT COMMENT 'Giá trị mới',
    action_type VARCHAR(50) COMMENT 'Loại hành động: CREATE, UPDATE, DELETE, COMMENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_task (task_id),
    INDEX idx_user (user_id),
    INDEX idx_created (created_at),
    INDEX idx_action (action_type),
    
    CONSTRAINT fk_history_task FOREIGN KEY (task_id) 
        REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_history_user FOREIGN KEY (user_id) 
        REFERENCES employees(id)
        
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Bảng lịch sử thay đổi task';

-- ========================================
-- 7. NOTIFICATIONS TABLE
-- ========================================
CREATE TABLE IF NOT EXISTS notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL COMMENT 'Người nhận thông báo',
    notification_type VARCHAR(50) COMMENT 'Loại thông báo',
    reference_id INT COMMENT 'ID tham chiếu (task_id, comment_id...)',
    title VARCHAR(255) COMMENT 'Tiêu đề',
    message TEXT COMMENT 'Nội dung',
    link VARCHAR(500) COMMENT 'Link đến trang chi tiết',
    is_read BOOLEAN DEFAULT FALSE COMMENT 'Đã đọc?',
    read_at TIMESTAMP NULL COMMENT 'Thời điểm đọc',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created (created_at),
    INDEX idx_type (notification_type),
    
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) 
        REFERENCES employees(id) ON DELETE CASCADE
        
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Bảng thông báo';

-- ========================================
-- 8. TASK_REMINDERS TABLE
-- ========================================
CREATE TABLE IF NOT EXISTS task_reminders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT NOT NULL COMMENT 'Task cần nhắc nhở',
    remind_before_hours INT NOT NULL COMMENT 'Nhắc trước bao nhiêu giờ',
    remind_at TIMESTAMP NULL COMMENT 'Thời điểm nhắc',
    is_sent BOOLEAN DEFAULT FALSE COMMENT 'Đã gửi?',
    sent_at TIMESTAMP NULL COMMENT 'Thời điểm đã gửi',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT,
    updated_by INT,
    
    INDEX idx_task (task_id),
    INDEX idx_remind_at (remind_at),
    INDEX idx_is_sent (is_sent),
    
    CONSTRAINT fk_reminder_task FOREIGN KEY (task_id) 
        REFERENCES tasks(id) ON DELETE CASCADE
        
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Bảng nhắc nhở deadline';

-- ========================================
-- COMPLETED
-- ========================================