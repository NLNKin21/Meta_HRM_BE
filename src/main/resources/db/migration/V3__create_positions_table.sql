-- v3_create_position_table.sql (CẬP NHẬT)
CREATE TABLE positions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    position_code VARCHAR(20) UNIQUE NOT NULL,
    position_name VARCHAR(100) NOT NULL,
    description TEXT,
    min_salary DECIMAL(15, 2),
    max_salary DECIMAL(15, 2),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    department_id INT,
    
    -- ===== THÊM CÁC CỘT MỚI CHO CÂY TỔ CHỨC =====
    parent_position_id INT NULL,
    level_order INT DEFAULT 6 COMMENT 'Cấp bậc: 1=CEO, 2=Director, 3=Manager, 4=Team Lead, 5=Senior, 6=Staff',
    sort_order INT DEFAULT 1 COMMENT 'Thứ tự sắp xếp trong cùng cấp',
    
    created_at DATETIME,
    updated_at DATETIME,
    is_deleted BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (department_id) REFERENCES departments(id),
    
    -- ===== THÊM FOREIGN KEY TỰ THAM CHIẾU =====
    FOREIGN KEY (parent_position_id) REFERENCES positions(id) ON DELETE SET NULL,
    
    -- ===== INDEXES =====
    INDEX idx_parent_position (parent_position_id),
    INDEX idx_department (department_id),
    INDEX idx_level_order (level_order),
    INDEX idx_is_active (is_active),
    INDEX idx_is_deleted (is_deleted)
);