-- ============================================================
-- V3: CONTRACT SCHEMA
-- Tables: contract_types, contracts, contract_notification_logs
-- Depends on: V2 (employees)
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- contract_types
-- Danh mục loại hợp đồng — do HR cấu hình
-- duration_unit: MONTH | YEAR | INDEFINITE
-- ------------------------------------------------------------
CREATE TABLE contract_types (
    id              INT          AUTO_INCREMENT PRIMARY KEY,
    type_code       VARCHAR(50)  NOT NULL UNIQUE,
    type_name       VARCHAR(255) NOT NULL,
    description     TEXT,
    notes           TEXT,
    duration_unit   VARCHAR(20)  NOT NULL COMMENT 'MONTH | YEAR | INDEFINITE',
    duration_value  INT          COMMENT 'NULL khi INDEFINITE',
    require_file    BOOLEAN      NOT NULL DEFAULT TRUE  COMMENT 'Bắt buộc đính kèm file scan?',
    clause_template TEXT         COMMENT 'Mẫu điều khoản mặc định',
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_by      INT,
    updated_by      INT,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted      BOOLEAN      NOT NULL DEFAULT FALSE,
    INDEX idx_contract_types_code (type_code)
);

-- ------------------------------------------------------------
-- contracts
-- Hợp đồng lao động của nhân viên
-- status: DRAFT | ACTIVE | EXPIRED | TERMINATED
-- ------------------------------------------------------------
CREATE TABLE contracts (
    id               INT          AUTO_INCREMENT PRIMARY KEY,
    emp_id           INT          NOT NULL,
    contract_type_id INT          NOT NULL,
    start_date       DATE         NOT NULL,
    end_date         DATE         COMMENT 'NULL = vô thời hạn (INDEFINITE)',
    file_url         VARCHAR(500) COMMENT 'Cloudinary URL',
    file_key         VARCHAR(255) COMMENT 'Cloudinary public_id',
    preview_url      VARCHAR(500),
    file_format      VARCHAR(20)  COMMENT 'pdf | docx | jpg',
    previewable      BOOLEAN      NOT NULL DEFAULT FALSE,
    status           VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'DRAFT | ACTIVE | EXPIRED | TERMINATED',
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted       BOOLEAN      NOT NULL DEFAULT FALSE,
    INDEX idx_contracts_emp        (emp_id),
    INDEX idx_contracts_status     (status),
    INDEX idx_contracts_end_date   (end_date),
    FOREIGN KEY (emp_id)           REFERENCES employees(id)      ON DELETE RESTRICT,
    FOREIGN KEY (contract_type_id) REFERENCES contract_types(id) ON DELETE RESTRICT
);

-- ------------------------------------------------------------
-- contract_notification_logs
-- Log mỗi lần hệ thống gửi thông báo liên quan hợp đồng
-- notification_code: EXPIRY_90D | EXPIRY_30D | EXPIRY_7D | CREATED | TERMINATED
-- ------------------------------------------------------------
CREATE TABLE contract_notification_logs (
    id                INT         AUTO_INCREMENT PRIMARY KEY,
    contract_id       INT         NOT NULL,
    recipient_id      INT         NOT NULL COMMENT 'Employee ID người nhận',
    notification_code VARCHAR(50) NOT NULL COMMENT 'EXPIRY_90D | EXPIRY_30D | EXPIRY_7D | CREATED | TERMINATED',
    sent_at           DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted        BOOLEAN     NOT NULL DEFAULT FALSE,
    INDEX idx_contract_notif_contract   (contract_id),
    INDEX idx_contract_notif_recipient  (recipient_id),
    FOREIGN KEY (contract_id)  REFERENCES contracts(id)  ON DELETE CASCADE,
    FOREIGN KEY (recipient_id) REFERENCES employees(id)  ON DELETE CASCADE
);

SET FOREIGN_KEY_CHECKS = 1;
