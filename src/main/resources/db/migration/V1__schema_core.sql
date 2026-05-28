-- ============================================================
-- V1: CORE SCHEMA
-- Tables: users, role_permissions, departments, shifts, work_locations
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- users
-- role:   0=ADMIN | 1=HR | 2=ACCOUNTANT | 3=EMPLOYEE
-- status: 0=ACTIVE | 1=INACTIVE | 2=LOCKED
-- ------------------------------------------------------------
CREATE TABLE users (
    id         INT          AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    role       TINYINT      NOT NULL DEFAULT 3 COMMENT '0=ADMIN 1=HR 2=ACCOUNTANT 3=EMPLOYEE',
    status     TINYINT      NOT NULL DEFAULT 0 COMMENT '0=ACTIVE 1=INACTIVE 2=LOCKED',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BOOLEAN      NOT NULL DEFAULT FALSE,
    INDEX idx_users_email  (email),
    INDEX idx_users_role   (role),
    INDEX idx_users_status (status)
);

-- ------------------------------------------------------------
-- role_permissions
-- Kiểm soát module nào role nào được truy cập
-- ------------------------------------------------------------
CREATE TABLE role_permissions (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    role        VARCHAR(30)  NOT NULL COMMENT 'HR | ACCOUNTANT | ADMIN',
    module_key  VARCHAR(50)  NOT NULL COMMENT 'dashboard | users | employees | ...',
    module_name VARCHAR(100) NOT NULL COMMENT 'Tên hiển thị trên menu',
    enabled     BOOLEAN      NOT NULL DEFAULT FALSE,
    sort_order  INT          NOT NULL DEFAULT 0,
    UNIQUE KEY uq_role_module (role, module_key)
);

-- ------------------------------------------------------------
-- departments
-- Manager xác định qua employees.role_in_dept = HEAD
-- ------------------------------------------------------------
CREATE TABLE departments (
    id         INT          AUTO_INCREMENT PRIMARY KEY,
    dept_name  VARCHAR(100) NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BOOLEAN      NOT NULL DEFAULT FALSE
);

-- ------------------------------------------------------------
-- shifts
-- work_days: JSON array [1=T2, 2=T3, 3=T4, 4=T5, 5=T6, 6=T7, 7=CN]
-- ------------------------------------------------------------
CREATE TABLE shifts (
    id                    INT          AUTO_INCREMENT PRIMARY KEY,
    name                  VARCHAR(100) NOT NULL,
    code                  VARCHAR(50)  NOT NULL UNIQUE,
    start_time            TIME         NOT NULL,
    end_time              TIME         NOT NULL,
    late_threshold        INT          NOT NULL DEFAULT 15  COMMENT 'Phút trễ tối đa không bị phạt',
    early_leave_threshold INT          NOT NULL DEFAULT 15  COMMENT 'Phút về sớm tối đa không bị phạt',
    check_in_start_before INT          NOT NULL DEFAULT 30  COMMENT 'Mở check-in trước X phút',
    check_in_end_after    INT          NOT NULL DEFAULT 120 COMMENT 'Đóng check-in sau X phút',
    work_days             JSON         NOT NULL             COMMENT '[1,2,3,4,5] = T2-T6',
    break_duration        INT          NOT NULL DEFAULT 60  COMMENT 'Nghỉ trưa (phút)',
    description           TEXT,
    color                 VARCHAR(20),
    is_active             BOOLEAN      NOT NULL DEFAULT TRUE,
    created_by            INT,
    updated_by            INT,
    created_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted            BOOLEAN      NOT NULL DEFAULT FALSE,
    INDEX idx_shifts_code (code)
);

-- ------------------------------------------------------------
-- work_locations
-- Địa điểm chấm công với geofence GPS
-- ------------------------------------------------------------
CREATE TABLE work_locations (
    id             INT           AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(200)  NOT NULL,
    code           VARCHAR(50)   NOT NULL UNIQUE,
    address        TEXT,
    latitude       DECIMAL(10,8) NOT NULL,
    longitude      DECIMAL(11,8) NOT NULL,
    radius         INT           NOT NULL DEFAULT 100 COMMENT 'Bán kính geofence (mét)',
    description    TEXT,
    contact_person VARCHAR(100),
    contact_phone  VARCHAR(20),
    is_active      BOOLEAN       NOT NULL DEFAULT TRUE,
    created_by     INT,
    updated_by     INT,
    created_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted     BOOLEAN       NOT NULL DEFAULT FALSE,
    INDEX idx_work_locations_code (code)
);

SET FOREIGN_KEY_CHECKS = 1;
