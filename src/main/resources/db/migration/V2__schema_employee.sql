-- ============================================================
-- V2: EMPLOYEE SCHEMA
-- Tables: positions, employees, employee_documents,
--         employee_faces, employee_tax_info, hr_notifications
-- Depends on: V1 (departments, users, shifts)
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- positions
-- Cây phân cấp chức vụ (self-reference)
-- level_order: 1=CEO, 2=C-Level, 3=Phó phòng/Trưởng nhóm,
--              4=Chuyên viên cao cấp, 5=Nhân viên, 6=Thực tập
-- ------------------------------------------------------------
CREATE TABLE positions (
    id                 INT           AUTO_INCREMENT PRIMARY KEY,
    position_code      VARCHAR(30)   NOT NULL UNIQUE,
    position_name      VARCHAR(150)  NOT NULL,
    description        TEXT,
    min_salary         DECIMAL(15,2),
    max_salary         DECIMAL(15,2),
    is_active          BOOLEAN       NOT NULL DEFAULT TRUE,
    department_id      INT           COMMENT 'NULL = áp dụng liên phòng',
    parent_position_id INT           COMMENT 'Self-reference: cấp trên trực tiếp',
    level_order        INT           NOT NULL DEFAULT 5 COMMENT '1=CEO 2=C-Level 3=Deputy/Lead 4=Senior 5=Staff 6=Intern',
    sort_order         INT           NOT NULL DEFAULT 1 COMMENT 'Thứ tự hiển thị trong cùng level',
    created_at         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted         BOOLEAN       NOT NULL DEFAULT FALSE,
    INDEX idx_positions_code   (position_code),
    INDEX idx_positions_dept   (department_id),
    INDEX idx_positions_level  (level_order),
    INDEX idx_positions_parent (parent_position_id),
    FOREIGN KEY (department_id)      REFERENCES departments(id) ON DELETE SET NULL,
    FOREIGN KEY (parent_position_id) REFERENCES positions(id)   ON DELETE SET NULL
);

-- ------------------------------------------------------------
-- employees
-- Bảng trung tâm — liên kết hầu hết các module khác
-- status:      ACTIVE | INACTIVE | RESIGNED | ON_LEAVE
-- role_in_dept: HEAD | DEPUTY | LEADER | STAFF
-- ------------------------------------------------------------
CREATE TABLE employees (
    id                INT           AUTO_INCREMENT PRIMARY KEY,
    user_id           INT           UNIQUE COMMENT '1-1 với users',
    dept_id           INT           NOT NULL,
    position_id       INT,
    shift_id          INT,
    full_name         VARCHAR(150)  NOT NULL,
    gender            VARCHAR(10)   COMMENT 'MALE | FEMALE | OTHER',
    dob               DATE,
    phone_number      VARCHAR(20),
    profile_pic_image VARCHAR(500)  COMMENT 'Cloudinary URL',
    address           VARCHAR(255),
    hire_date         DATE          NOT NULL,
    basic_salary      DECIMAL(12,2) NOT NULL DEFAULT 0,
    status            VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE | INACTIVE | RESIGNED | ON_LEAVE',
    role_in_dept      VARCHAR(20)   NOT NULL DEFAULT 'STAFF'  COMMENT 'HEAD | DEPUTY | LEADER | STAFF',
    created_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted        BOOLEAN       NOT NULL DEFAULT FALSE,
    INDEX idx_employees_dept     (dept_id),
    INDEX idx_employees_position (position_id),
    INDEX idx_employees_status   (status),
    INDEX idx_employees_phone    (phone_number),
    FOREIGN KEY (user_id)     REFERENCES users(id)       ON DELETE SET NULL,
    FOREIGN KEY (dept_id)     REFERENCES departments(id) ON DELETE RESTRICT,
    FOREIGN KEY (position_id) REFERENCES positions(id)   ON DELETE SET NULL,
    FOREIGN KEY (shift_id)    REFERENCES shifts(id)      ON DELETE SET NULL
);

-- ------------------------------------------------------------
-- employee_documents
-- Tài liệu nhân sự: CCCD, bằng cấp, hợp đồng scan...
-- ------------------------------------------------------------
CREATE TABLE employee_documents (
    id            INT          AUTO_INCREMENT PRIMARY KEY,
    emp_id        INT          NOT NULL,
    doc_type      VARCHAR(50)  NOT NULL COMMENT 'CCCD | DEGREE | CONTRACT | CERTIFICATE | OTHER',
    file_url      VARCHAR(500) COMMENT 'Cloudinary URL',
    file_key      VARCHAR(255) COMMENT 'Cloudinary public_id',
    original_name VARCHAR(255),
    file_size     BIGINT       COMMENT 'Bytes',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted    BOOLEAN      NOT NULL DEFAULT FALSE,
    INDEX idx_emp_docs_emp  (emp_id),
    INDEX idx_emp_docs_type (doc_type),
    FOREIGN KEY (emp_id) REFERENCES employees(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- employee_faces
-- Vector khuôn mặt cho Face ID chấm công
-- face_encoding: JSON float[] 512-dim embedding
-- ------------------------------------------------------------
CREATE TABLE employee_faces (
    id               INT          AUTO_INCREMENT PRIMARY KEY,
    employee_id      INT          NOT NULL,
    face_image_url   VARCHAR(500) NOT NULL,
    face_encoding    TEXT         NOT NULL COMMENT 'JSON float[] 512-dim',
    confidence_score DECIMAL(5,2) COMMENT '0.00-100.00',
    is_primary       BOOLEAN      NOT NULL DEFAULT FALSE,
    is_active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_by       INT,
    updated_by       INT,
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted       BOOLEAN      NOT NULL DEFAULT FALSE,
    INDEX idx_emp_faces_emp     (employee_id),
    INDEX idx_emp_faces_primary (is_primary),
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- employee_tax_info
-- Thông tin thuế TNCN, BHXH và tài khoản ngân hàng
-- Quan hệ 1-1 với employees
-- ------------------------------------------------------------
CREATE TABLE employee_tax_info (
    id                       INT          AUTO_INCREMENT PRIMARY KEY,
    employee_id              INT          NOT NULL UNIQUE,
    tax_code                 VARCHAR(20)  COMMENT 'Mã số thuế cá nhân',
    number_of_dependents     INT          NOT NULL DEFAULT 0,
    social_insurance_no      VARCHAR(30)  COMMENT 'Số sổ BHXH',
    social_insurance_salary  DECIMAL(12,2) COMMENT 'Lương đóng BHXH (nếu khác lương CB)',
    bank_name                VARCHAR(100),
    bank_branch              VARCHAR(200),
    bank_account_number      VARCHAR(30),
    bank_account_holder      VARCHAR(150),
    note                     TEXT,
    updated_by               INT,
    created_at               DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted               BOOLEAN      NOT NULL DEFAULT FALSE,
    INDEX idx_tax_info_emp     (employee_id),
    INDEX idx_tax_info_account (bank_account_number),
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- hr_notifications
-- Thông báo HR nội bộ: hợp đồng hết hạn, sinh nhật, yêu cầu...
-- priority: LOW | NORMAL | HIGH | URGENT
-- ------------------------------------------------------------
CREATE TABLE hr_notifications (
    id                  BIGINT       AUTO_INCREMENT PRIMARY KEY,
    recipient_id        INT          NOT NULL,
    title               VARCHAR(255) NOT NULL,
    content             TEXT         NOT NULL,
    type                VARCHAR(50)  NOT NULL COMMENT 'CONTRACT_EXPIRY | BIRTHDAY | DOCUMENT_REQUEST | SYSTEM',
    related_entity_type VARCHAR(50)  COMMENT 'CONTRACT | EMPLOYEE | LEAVE | PAYSLIP',
    related_entity_id   BIGINT,
    is_read             BOOLEAN      NOT NULL DEFAULT FALSE,
    priority            VARCHAR(20)  NOT NULL DEFAULT 'NORMAL' COMMENT 'LOW | NORMAL | HIGH | URGENT',
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at             DATETIME,
    created_by_system   BOOLEAN      NOT NULL DEFAULT TRUE,
    INDEX idx_hr_notif_recipient (recipient_id),
    INDEX idx_hr_notif_read      (is_read),
    INDEX idx_hr_notif_type      (type),
    FOREIGN KEY (recipient_id) REFERENCES employees(id) ON DELETE CASCADE
);

SET FOREIGN_KEY_CHECKS = 1;
