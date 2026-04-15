-- 1. shifts
ALTER TABLE shifts 
    ADD COLUMN is_deleted BIT(1) DEFAULT b'0';

-- 2. work_locations
ALTER TABLE work_locations 
    ADD COLUMN is_deleted BIT(1) DEFAULT b'0';

-- 3. employee_faces
ALTER TABLE employee_faces 
    ADD COLUMN is_deleted BIT(1) DEFAULT b'0';

-- 4. attendance_records
ALTER TABLE attendance_records 
    ADD COLUMN is_deleted BIT(1) DEFAULT b'0';

-- 5. attendance_anomalies
ALTER TABLE attendance_anomalies 
    ADD COLUMN is_deleted BIT(1) DEFAULT b'0';

-- 6. audit table
CREATE TABLE attendance_audit_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BIT(1) DEFAULT b'0',

    attendance_id INT NOT NULL,
    action VARCHAR(20) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    reason TEXT,
    performed_by INT NOT NULL,
    performed_by_name VARCHAR(150),

    INDEX idx_audit_attendance (attendance_id),
    INDEX idx_audit_action (action),
    INDEX idx_audit_performed_by (performed_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;