-- V16: Recreate payslips and payslip_details tables
-- Tables were accidentally dropped, recreating from entity definitions

CREATE TABLE IF NOT EXISTS payslips (
    id                          INT             NOT NULL AUTO_INCREMENT,
    employee_id                 INT             NOT NULL,
    month                       INT             NOT NULL,
    year                        INT             NOT NULL,
    status                      VARCHAR(20)     NOT NULL DEFAULT 'DRAFT',

    -- Ngày công
    standard_work_days          INT             DEFAULT 22,
    actual_work_days            INT             DEFAULT 0,
    paid_leave_days             DECIMAL(4,1)    DEFAULT 0.0,
    unpaid_leave_days           DECIMAL(4,1)    DEFAULT 0.0,
    absent_days                 INT             DEFAULT 0,
    overtime_hours_weekday      DECIMAL(5,2)    DEFAULT 0.00,
    overtime_hours_weekend      DECIMAL(5,2)    DEFAULT 0.00,
    overtime_hours_holiday      DECIMAL(5,2)    DEFAULT 0.00,
    total_overtime_hours        DECIMAL(5,2)    DEFAULT 0.00,
    total_late_times            INT             DEFAULT 0,
    total_late_minutes          INT             DEFAULT 0,

    -- Thu nhập
    basic_salary                DECIMAL(12,2)   DEFAULT 0.00,
    actual_basic_salary         DECIMAL(12,2)   DEFAULT 0.00,
    total_allowances            DECIMAL(12,2)   DEFAULT 0.00,
    overtime_pay                DECIMAL(12,2)   DEFAULT 0.00,
    total_bonus                 DECIMAL(12,2)   DEFAULT 0.00,
    gross_salary                DECIMAL(12,2)   DEFAULT 0.00,

    -- Bảo hiểm
    insurance_salary            DECIMAL(12,2)   DEFAULT 0.00,
    social_insurance            DECIMAL(12,2)   DEFAULT 0.00,
    health_insurance            DECIMAL(12,2)   DEFAULT 0.00,
    unemployment_insurance      DECIMAL(12,2)   DEFAULT 0.00,
    total_insurance             DECIMAL(12,2)   DEFAULT 0.00,

    -- Thuế
    pre_tax_income              DECIMAL(12,2)   DEFAULT 0.00,
    personal_deduction          DECIMAL(12,2)   DEFAULT 0.00,
    dependent_deduction         DECIMAL(12,2)   DEFAULT 0.00,
    taxable_income              DECIMAL(12,2)   DEFAULT 0.00,
    personal_income_tax         DECIMAL(12,2)   DEFAULT 0.00,

    -- Khấu trừ
    late_penalty                DECIMAL(12,2)   DEFAULT 0.00,
    other_deductions            DECIMAL(12,2)   DEFAULT 0.00,
    total_deduction_amount      DECIMAL(12,2)   DEFAULT 0.00,

    -- Kết quả
    net_salary                  DECIMAL(12,2)   DEFAULT 0.00,

    -- Chi phí công ty
    company_social_insurance    DECIMAL(12,2)   DEFAULT 0.00,
    company_health_insurance    DECIMAL(12,2)   DEFAULT 0.00,
    company_unemployment        DECIMAL(12,2)   DEFAULT 0.00,
    total_company_cost          DECIMAL(12,2)   DEFAULT 0.00,

    -- Meta
    note                        TEXT,
    calculated_by               INT,
    calculated_at               DATETIME,
    approved_by                 INT,
    approved_at                 DATETIME,
    rejected_by                 INT,
    rejected_at                 DATETIME,
    reject_reason               TEXT,
    paid_at                     DATETIME,
    payment_method              VARCHAR(20)     DEFAULT 'BANK_TRANSFER',

    -- BaseEntity
    created_at                  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted                  TINYINT(1)      NOT NULL DEFAULT 0,

    PRIMARY KEY (id),
    UNIQUE KEY uq_payslip_emp_month_year (employee_id, month, year),
    KEY idx_payslip_employee_id (employee_id),
    KEY idx_payslip_status (status),
    KEY idx_payslip_month_year (month, year),
    CONSTRAINT fk_payslip_employee FOREIGN KEY (employee_id) REFERENCES employees (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS payslip_details (
    id          INT             NOT NULL AUTO_INCREMENT,
    payslip_id  INT             NOT NULL,
    item_type   VARCHAR(20)     NOT NULL,
    item_code   VARCHAR(50)     NOT NULL,
    item_name   VARCHAR(200)    NOT NULL,
    amount      DECIMAL(12,2)   NOT NULL DEFAULT 0.00,
    quantity    DECIMAL(8,2),
    rate        DECIMAL(15,4),
    note        TEXT,
    sort_order  INT             DEFAULT 0,

    -- BaseEntity
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted  TINYINT(1)      NOT NULL DEFAULT 0,

    PRIMARY KEY (id),
    KEY idx_payslip_detail_payslip_id (payslip_id),
    KEY idx_payslip_detail_item_type (item_type),
    CONSTRAINT fk_payslip_detail_payslip FOREIGN KEY (payslip_id) REFERENCES payslips (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;