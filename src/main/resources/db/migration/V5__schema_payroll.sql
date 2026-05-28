-- ============================================================
-- V5: PAYROLL SCHEMA
-- Tables: payroll_config, allowances, bonuses, deductions,
--         payslips, payslip_details
-- Depends on: V2 (employees)
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- payroll_config
-- Tham số tính lương — HR/Kế toán cấu hình
-- config_group: INSURANCE | TAX | OT | PENALTY | GENERAL
-- ------------------------------------------------------------
CREATE TABLE payroll_config (
    id           INT           AUTO_INCREMENT PRIMARY KEY,
    config_key   VARCHAR(100)  NOT NULL UNIQUE COMMENT 'BHXH_EMP_RATE | PERSONAL_DEDUCTION | OT_RATE_WEEKDAY...',
    config_value DECIMAL(15,4) NOT NULL,
    config_group VARCHAR(50)   NOT NULL COMMENT 'INSURANCE | TAX | OT | PENALTY | GENERAL',
    description  VARCHAR(500)  COMMENT 'Căn cứ pháp lý',
    is_active    BOOLEAN       NOT NULL DEFAULT TRUE,
    updated_by   INT,
    created_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted   BOOLEAN       NOT NULL DEFAULT FALSE,
    INDEX idx_payroll_config_group (config_group)
);

-- ------------------------------------------------------------
-- allowances
-- Phụ cấp định kỳ của nhân viên (ăn trưa, xăng xe, điện thoại...)
-- allowance_type: MEAL | TRANSPORT | PHONE | HOUSING | RESPONSIBILITY | OTHER
-- ------------------------------------------------------------
CREATE TABLE allowances (
    id             INT           AUTO_INCREMENT PRIMARY KEY,
    employee_id    INT           NOT NULL,
    allowance_type VARCHAR(30)   NOT NULL COMMENT 'MEAL | TRANSPORT | PHONE | HOUSING | RESPONSIBILITY | OTHER',
    name           VARCHAR(200)  NOT NULL,
    amount         DECIMAL(12,2) NOT NULL,
    is_taxable     BOOLEAN       NOT NULL DEFAULT TRUE  COMMENT 'Tính vào thu nhập chịu thuế TNCN?',
    is_insurance   BOOLEAN       NOT NULL DEFAULT FALSE COMMENT 'Tính vào lương đóng BHXH?',
    effective_date DATE          NOT NULL,
    expiry_date    DATE          COMMENT 'NULL = không hết hạn',
    is_active      BOOLEAN       NOT NULL DEFAULT TRUE,
    note           TEXT,
    created_by     INT,
    created_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted     BOOLEAN       NOT NULL DEFAULT FALSE,
    INDEX idx_allowances_emp    (employee_id),
    INDEX idx_allowances_active (is_active),
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- bonuses
-- Thưởng theo tháng/năm — cần phê duyệt trước khi tính lương
-- bonus_type: KPI | HOLIDAY | PERFORMANCE | RETENTION | OTHER
-- ------------------------------------------------------------
CREATE TABLE bonuses (
    id          INT           AUTO_INCREMENT PRIMARY KEY,
    employee_id INT           NOT NULL,
    bonus_type  VARCHAR(30)   NOT NULL COMMENT 'KPI | HOLIDAY | PERFORMANCE | RETENTION | OTHER',
    name        VARCHAR(200)  NOT NULL,
    amount      DECIMAL(12,2) NOT NULL,
    is_taxable  BOOLEAN       NOT NULL DEFAULT TRUE,
    month       INT           NOT NULL COMMENT '1-12',
    year        INT           NOT NULL,
    reason      TEXT,
    is_approved BOOLEAN       NOT NULL DEFAULT FALSE,
    approved_by INT,
    approved_at DATETIME,
    created_by  INT,
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted  BOOLEAN       NOT NULL DEFAULT FALSE,
    INDEX idx_bonuses_emp        (employee_id),
    INDEX idx_bonuses_month_year (year, month),
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- deductions
-- Khấu trừ bổ sung (phạt trễ, tạm ứng lương, bồi thường...)
-- deduction_type: LATE_PENALTY | ADVANCE | DAMAGE | ABSENCE | OTHER
-- ------------------------------------------------------------
CREATE TABLE deductions (
    id             INT           AUTO_INCREMENT PRIMARY KEY,
    employee_id    INT           NOT NULL,
    deduction_type VARCHAR(30)   NOT NULL COMMENT 'LATE_PENALTY | ADVANCE | DAMAGE | ABSENCE | OTHER',
    name           VARCHAR(200)  NOT NULL,
    amount         DECIMAL(12,2) NOT NULL,
    month          INT           NOT NULL,
    year           INT           NOT NULL,
    reason         TEXT,
    is_approved    BOOLEAN       NOT NULL DEFAULT FALSE,
    approved_by    INT,
    created_by     INT,
    created_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted     BOOLEAN       NOT NULL DEFAULT FALSE,
    INDEX idx_deductions_emp        (employee_id),
    INDEX idx_deductions_month_year (year, month),
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- payslips
-- Phiếu lương tháng — Unique (employee_id, month, year)
-- status: DRAFT → CALCULATED → PENDING_APPROVAL → APPROVED → PAID | REJECTED
-- ------------------------------------------------------------
CREATE TABLE payslips (
    id                       INT           AUTO_INCREMENT PRIMARY KEY,
    employee_id              INT           NOT NULL,
    month                    INT           NOT NULL,
    year                     INT           NOT NULL,
    status                   VARCHAR(30)   NOT NULL DEFAULT 'DRAFT'
                             COMMENT 'DRAFT | CALCULATED | PENDING_APPROVAL | APPROVED | REJECTED | PAID',
    -- ── Ngày công ──────────────────────────────────────────
    standard_work_days       INT           NOT NULL DEFAULT 26,
    actual_work_days         INT           NOT NULL DEFAULT 0,
    paid_leave_days          DECIMAL(4,1)  NOT NULL DEFAULT 0,
    unpaid_leave_days        DECIMAL(4,1)  NOT NULL DEFAULT 0,
    absent_days              INT           NOT NULL DEFAULT 0,
    overtime_hours_weekday   DECIMAL(5,2)  NOT NULL DEFAULT 0,
    overtime_hours_weekend   DECIMAL(5,2)  NOT NULL DEFAULT 0,
    overtime_hours_holiday   DECIMAL(5,2)  NOT NULL DEFAULT 0,
    total_overtime_hours     DECIMAL(5,2)  NOT NULL DEFAULT 0,
    total_late_times         INT           NOT NULL DEFAULT 0,
    total_late_minutes       INT           NOT NULL DEFAULT 0,
    -- ── Thu nhập ───────────────────────────────────────────
    basic_salary             DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT 'Snapshot lương CB tại thời điểm tính',
    actual_basic_salary      DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT 'Lương CB × (ngày công / ngày chuẩn)',
    total_allowances         DECIMAL(12,2) NOT NULL DEFAULT 0,
    overtime_pay             DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_bonus              DECIMAL(12,2) NOT NULL DEFAULT 0,
    gross_salary             DECIMAL(12,2) NOT NULL DEFAULT 0,
    -- ── Bảo hiểm (phần NLĐ đóng) ──────────────────────────
    insurance_salary         DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT 'Lương làm căn cứ đóng BH',
    social_insurance         DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT 'BHXH 8%',
    health_insurance         DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT 'BHYT 1.5%',
    unemployment_insurance   DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT 'BHTN 1%',
    total_insurance          DECIMAL(12,2) NOT NULL DEFAULT 0,
    -- ── Thuế TNCN ──────────────────────────────────────────
    pre_tax_income           DECIMAL(12,2) NOT NULL DEFAULT 0,
    personal_deduction       DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '11tr/tháng',
    dependent_deduction      DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '4.4tr/người/tháng',
    taxable_income           DECIMAL(12,2) NOT NULL DEFAULT 0,
    personal_income_tax      DECIMAL(12,2) NOT NULL DEFAULT 0,
    -- ── Khấu trừ khác ──────────────────────────────────────
    late_penalty             DECIMAL(12,2) NOT NULL DEFAULT 0,
    other_deductions         DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_deduction_amount   DECIMAL(12,2) NOT NULL DEFAULT 0,
    -- ── Kết quả ────────────────────────────────────────────
    net_salary               DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT 'Lương thực lĩnh',
    -- ── Chi phí công ty (BHXH 17.5% + BHYT 3% + BHTN 1%) ──
    company_social_insurance DECIMAL(12,2) NOT NULL DEFAULT 0,
    company_health_insurance DECIMAL(12,2) NOT NULL DEFAULT 0,
    company_unemployment     DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_company_cost       DECIMAL(12,2) NOT NULL DEFAULT 0,
    -- ── Meta ───────────────────────────────────────────────
    note                     TEXT,
    calculated_by            INT,
    calculated_at            DATETIME,
    approved_by              INT,
    approved_at              DATETIME,
    rejected_by              INT,
    rejected_at              DATETIME,
    reject_reason            TEXT,
    paid_at                  DATETIME,
    payment_method           VARCHAR(20)   NOT NULL DEFAULT 'BANK_TRANSFER' COMMENT 'BANK_TRANSFER | CASH',
    created_at               DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted               BOOLEAN       NOT NULL DEFAULT FALSE,
    UNIQUE KEY uq_payslip (employee_id, month, year),
    INDEX idx_payslips_status     (status),
    INDEX idx_payslips_month_year (year, month),
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE RESTRICT
);

-- ------------------------------------------------------------
-- payslip_details
-- Dòng chi tiết phiếu lương
-- item_type: EARNING | DEDUCTION
-- ------------------------------------------------------------
CREATE TABLE payslip_details (
    id         INT           AUTO_INCREMENT PRIMARY KEY,
    payslip_id INT           NOT NULL,
    item_type  VARCHAR(20)   NOT NULL COMMENT 'EARNING | DEDUCTION',
    item_code  VARCHAR(50)   NOT NULL COMMENT 'BASIC_SALARY | MEAL_ALLOWANCE | BHXH | PIT...',
    item_name  VARCHAR(200)  NOT NULL,
    amount     DECIMAL(12,2) NOT NULL DEFAULT 0,
    quantity   DECIMAL(8,2)  COMMENT 'Số giờ OT, số ngày...',
    rate       DECIMAL(15,4) COMMENT 'Đơn giá',
    note       TEXT,
    sort_order INT           NOT NULL DEFAULT 0,
    created_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BOOLEAN       NOT NULL DEFAULT FALSE,
    INDEX idx_payslip_details_payslip (payslip_id),
    INDEX idx_payslip_details_type    (item_type),
    FOREIGN KEY (payslip_id) REFERENCES payslips(id) ON DELETE CASCADE
);

SET FOREIGN_KEY_CHECKS = 1;
