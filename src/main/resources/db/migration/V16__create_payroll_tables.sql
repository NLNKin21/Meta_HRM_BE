-- ============================================
-- V16__create_payroll_tables.sql
--
-- Payroll Module - Bảng lương
-- 7 bảng: payroll_config, allowances, bonuses, deductions,
--          employee_tax_info, payslips, payslip_details
-- ============================================

-- ============================================
-- 1. PAYROLL_CONFIG (Cấu hình lương toàn hệ thống)
-- ============================================
CREATE TABLE payroll_config (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted      BIT(1) DEFAULT b'0',

    config_key      VARCHAR(100) NOT NULL UNIQUE COMMENT 'Key cấu hình',
    config_value    DECIMAL(15, 4) NOT NULL COMMENT 'Giá trị',
    config_group    VARCHAR(50) NOT NULL COMMENT 'Nhóm: INSURANCE, TAX, OT, PENALTY, GENERAL',
    description     VARCHAR(500) COMMENT 'Mô tả',
    is_active       BIT(1) DEFAULT b'1',
    updated_by      INT,

    INDEX idx_config_key (config_key),
    INDEX idx_config_group (config_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Cấu hình tham số tính lương';

-- ============================================
-- 2. ALLOWANCES (Phụ cấp)
-- ============================================
CREATE TABLE allowances (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted      BIT(1) DEFAULT b'0',

    employee_id     INT NOT NULL,
    allowance_type  VARCHAR(30) NOT NULL
        COMMENT 'MEAL, TRANSPORT, PHONE, HOUSING, POSITION, TOXIC, ATTENDANCE, OTHER',
    name            VARCHAR(200) NOT NULL COMMENT 'Tên phụ cấp',
    amount          DECIMAL(12, 2) NOT NULL COMMENT 'Số tiền / tháng',
    is_taxable      BIT(1) DEFAULT b'1' COMMENT 'Có chịu thuế TNCN không',
    is_insurance    BIT(1) DEFAULT b'0' COMMENT 'Có tính vào lương đóng BH không',
    effective_date  DATE NOT NULL,
    expiry_date     DATE COMMENT 'NULL = vô thời hạn',
    is_active       BIT(1) DEFAULT b'1',
    note            TEXT,
    created_by      INT,

    INDEX idx_allowance_emp (employee_id),
    INDEX idx_allowance_type (allowance_type),
    INDEX idx_allowance_active (employee_id, is_active, effective_date),
    CONSTRAINT fk_allowance_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Phụ cấp nhân viên';

-- ============================================
-- 3. BONUSES (Thưởng)
-- ============================================
CREATE TABLE bonuses (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted      BIT(1) DEFAULT b'0',

    employee_id     INT NOT NULL,
    bonus_type      VARCHAR(30) NOT NULL
        COMMENT 'KPI, PROJECT, HOLIDAY, MONTH_13, PERFORMANCE, OTHER',
    name            VARCHAR(200) NOT NULL COMMENT 'Tên khoản thưởng',
    amount          DECIMAL(12, 2) NOT NULL,
    is_taxable      BIT(1) DEFAULT b'1' COMMENT 'Có chịu thuế TNCN không',
    month           INT NOT NULL COMMENT 'Tháng áp dụng (1-12)',
    year            INT NOT NULL COMMENT 'Năm',
    reason          TEXT,
    is_approved     BIT(1) DEFAULT b'0',
    approved_by     INT,
    approved_at     DATETIME,
    created_by      INT,

    INDEX idx_bonus_emp (employee_id),
    INDEX idx_bonus_period (employee_id, year, month),
    CONSTRAINT fk_bonus_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Thưởng nhân viên';

-- ============================================
-- 4. DEDUCTIONS (Khấu trừ khác)
-- ============================================
CREATE TABLE deductions (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted      BIT(1) DEFAULT b'0',

    employee_id     INT NOT NULL,
    deduction_type  VARCHAR(30) NOT NULL
        COMMENT 'PENALTY, LATE_PENALTY, LOAN, DAMAGE, OTHER',
    name            VARCHAR(200) NOT NULL COMMENT 'Tên khoản khấu trừ',
    amount          DECIMAL(12, 2) NOT NULL,
    month           INT NOT NULL,
    year            INT NOT NULL,
    reason          TEXT,
    is_approved     BIT(1) DEFAULT b'0',
    approved_by     INT,
    created_by      INT,

    INDEX idx_deduction_emp (employee_id),
    INDEX idx_deduction_period (employee_id, year, month),
    CONSTRAINT fk_deduction_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Khấu trừ khác ngoài BH và thuế';

-- ============================================
-- 5. EMPLOYEE_TAX_INFO (Thông tin thuế NV)
-- ============================================
CREATE TABLE employee_tax_info (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted      BIT(1) DEFAULT b'0',

    employee_id         INT NOT NULL UNIQUE,
    tax_code            VARCHAR(20) COMMENT 'Mã số thuế cá nhân',
    number_of_dependents INT DEFAULT 0 COMMENT 'Số người phụ thuộc',
    social_insurance_no VARCHAR(30) COMMENT 'Số sổ BHXH',
    social_insurance_salary DECIMAL(12, 2) COMMENT 'Mức lương đóng BH (NULL = tự tính)',

    -- Thông tin ngân hàng
    bank_name           VARCHAR(100) COMMENT 'Tên ngân hàng',
    bank_branch         VARCHAR(200) COMMENT 'Chi nhánh',
    bank_account_number VARCHAR(30) COMMENT 'Số tài khoản',
    bank_account_holder VARCHAR(150) COMMENT 'Tên chủ TK',

    note                TEXT,
    updated_by          INT,

    INDEX idx_tax_emp (employee_id),
    CONSTRAINT fk_tax_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Thông tin thuế và ngân hàng nhân viên';

-- ============================================
-- 6. PAYSLIPS (Phiếu lương tháng)
-- ============================================
CREATE TABLE payslips (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted      BIT(1) DEFAULT b'0',

    employee_id     INT NOT NULL,
    month           INT NOT NULL COMMENT 'Tháng lương (1-12)',
    year            INT NOT NULL COMMENT 'Năm',
    status          VARCHAR(20) NOT NULL DEFAULT 'DRAFT'
        COMMENT 'DRAFT, CALCULATED, APPROVED, PAID, REJECTED',

    -- ====== NGÀY CÔNG ======
    standard_work_days      INT DEFAULT 22 COMMENT 'Ngày công chuẩn trong tháng',
    actual_work_days        INT DEFAULT 0 COMMENT 'Ngày đi làm thực tế',
    paid_leave_days         DECIMAL(4, 1) DEFAULT 0 COMMENT 'Nghỉ phép có lương',
    unpaid_leave_days       DECIMAL(4, 1) DEFAULT 0 COMMENT 'Nghỉ không lương',
    absent_days             INT DEFAULT 0 COMMENT 'Ngày vắng không phép',
    overtime_hours_weekday  DECIMAL(5, 2) DEFAULT 0 COMMENT 'Giờ OT ngày thường',
    overtime_hours_weekend  DECIMAL(5, 2) DEFAULT 0 COMMENT 'Giờ OT cuối tuần',
    overtime_hours_holiday  DECIMAL(5, 2) DEFAULT 0 COMMENT 'Giờ OT ngày lễ',
    total_overtime_hours    DECIMAL(5, 2) DEFAULT 0 COMMENT 'Tổng giờ OT',
    total_late_times        INT DEFAULT 0 COMMENT 'Số lần đi trễ',
    total_late_minutes      INT DEFAULT 0 COMMENT 'Tổng phút trễ',

    -- ====== THU NHẬP ======
    basic_salary            DECIMAL(12, 2) DEFAULT 0 COMMENT 'Lương cơ bản (gốc theo HĐLĐ)',
    actual_basic_salary     DECIMAL(12, 2) DEFAULT 0 COMMENT 'Lương CB thực tế theo ngày công',
    total_allowances        DECIMAL(12, 2) DEFAULT 0 COMMENT 'Tổng phụ cấp',
    overtime_pay            DECIMAL(12, 2) DEFAULT 0 COMMENT 'Lương làm thêm',
    total_bonus             DECIMAL(12, 2) DEFAULT 0 COMMENT 'Tổng thưởng',
    gross_salary            DECIMAL(12, 2) DEFAULT 0 COMMENT 'TỔNG THU NHẬP',

    -- ====== BẢO HIỂM (NV đóng) ======
    insurance_salary        DECIMAL(12, 2) DEFAULT 0 COMMENT 'Mức lương đóng BH',
    social_insurance        DECIMAL(12, 2) DEFAULT 0 COMMENT 'BHXH NV (8%)',
    health_insurance        DECIMAL(12, 2) DEFAULT 0 COMMENT 'BHYT NV (1.5%)',
    unemployment_insurance  DECIMAL(12, 2) DEFAULT 0 COMMENT 'BHTN NV (1%)',
    total_insurance         DECIMAL(12, 2) DEFAULT 0 COMMENT 'Tổng BH NV đóng',

    -- ====== THUẾ ======
    pre_tax_income          DECIMAL(12, 2) DEFAULT 0 COMMENT 'Thu nhập trước thuế',
    personal_deduction      DECIMAL(12, 2) DEFAULT 0 COMMENT 'Giảm trừ bản thân',
    dependent_deduction     DECIMAL(12, 2) DEFAULT 0 COMMENT 'Giảm trừ người phụ thuộc',
    taxable_income          DECIMAL(12, 2) DEFAULT 0 COMMENT 'Thu nhập chịu thuế',
    personal_income_tax     DECIMAL(12, 2) DEFAULT 0 COMMENT 'Thuế TNCN',

    -- ====== KHẤU TRỪ KHÁC ======
    late_penalty            DECIMAL(12, 2) DEFAULT 0 COMMENT 'Phạt đi trễ',
    other_deductions        DECIMAL(12, 2) DEFAULT 0 COMMENT 'Khấu trừ khác',
    total_deduction_amount  DECIMAL(12, 2) DEFAULT 0 COMMENT 'TỔNG KHẤU TRỪ',

    -- ====== KẾT QUẢ ======
    net_salary              DECIMAL(12, 2) DEFAULT 0 COMMENT 'LƯƠNG THỰC NHẬN',

    -- ====== CHI PHÍ CÔNG TY ======
    company_social_insurance  DECIMAL(12, 2) DEFAULT 0 COMMENT 'BHXH công ty đóng (17.5%)',
    company_health_insurance  DECIMAL(12, 2) DEFAULT 0 COMMENT 'BHYT công ty (3%)',
    company_unemployment      DECIMAL(12, 2) DEFAULT 0 COMMENT 'BHTN công ty (1%)',
    total_company_cost        DECIMAL(12, 2) DEFAULT 0 COMMENT 'Tổng chi phí công ty',

    -- ====== META ======
    note                    TEXT,
    calculated_by           INT,
    calculated_at           DATETIME,
    approved_by             INT,
    approved_at             DATETIME,
    rejected_by             INT,
    rejected_at             DATETIME,
    reject_reason           TEXT,
    paid_at                 DATETIME,
    payment_method          VARCHAR(20) DEFAULT 'BANK_TRANSFER'
        COMMENT 'BANK_TRANSFER, CASH',

    UNIQUE KEY uk_payslip_emp_period (employee_id, month, year),
    INDEX idx_payslip_period (year, month),
    INDEX idx_payslip_status (status),
    INDEX idx_payslip_emp (employee_id),
    CONSTRAINT fk_payslip_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Phiếu lương tháng';

-- ============================================
-- 7. PAYSLIP_DETAILS (Chi tiết từng khoản)
-- ============================================
CREATE TABLE payslip_details (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted      BIT(1) DEFAULT b'0',

    payslip_id      INT NOT NULL,
    item_type       VARCHAR(20) NOT NULL COMMENT 'EARNING hoặc DEDUCTION',
    item_code       VARCHAR(50) NOT NULL COMMENT 'Mã khoản mục',
    item_name       VARCHAR(200) NOT NULL COMMENT 'Tên khoản mục',
    amount          DECIMAL(12, 2) NOT NULL DEFAULT 0,
    quantity        DECIMAL(8, 2) COMMENT 'Số lượng (giờ OT, ngày...)',
    rate            DECIMAL(8, 4) COMMENT 'Hệ số / tỷ lệ',
    note            TEXT,
    sort_order      INT DEFAULT 0 COMMENT 'Thứ tự hiển thị',

    INDEX idx_detail_payslip (payslip_id),
    INDEX idx_detail_type (item_type),
    CONSTRAINT fk_detail_payslip FOREIGN KEY (payslip_id) REFERENCES payslips(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Chi tiết từng khoản thu nhập / khấu trừ';


-- ============================================
-- ============================================
--              SEED DATA
-- ============================================
-- ============================================


-- ============================================
-- PAYROLL_CONFIG - Cấu hình hệ thống
-- ============================================
INSERT INTO payroll_config (config_key, config_value, config_group, description, is_active, updated_by) VALUES

-- Nhóm: BẢO HIỂM (tỷ lệ NV đóng)
('BHXH_EMPLOYEE_RATE',       0.0800, 'INSURANCE', 'Tỷ lệ BHXH nhân viên đóng (8%)', b'1', 1),
('BHYT_EMPLOYEE_RATE',       0.0150, 'INSURANCE', 'Tỷ lệ BHYT nhân viên đóng (1.5%)', b'1', 1),
('BHTN_EMPLOYEE_RATE',       0.0100, 'INSURANCE', 'Tỷ lệ BHTN nhân viên đóng (1%)', b'1', 1),

-- Nhóm: BẢO HIỂM (tỷ lệ CÔNG TY đóng)
('BHXH_COMPANY_RATE',        0.1750, 'INSURANCE', 'Tỷ lệ BHXH công ty đóng (17.5%)', b'1', 1),
('BHYT_COMPANY_RATE',        0.0300, 'INSURANCE', 'Tỷ lệ BHYT công ty đóng (3%)', b'1', 1),
('BHTN_COMPANY_RATE',        0.0100, 'INSURANCE', 'Tỷ lệ BHTN công ty đóng (1%)', b'1', 1),

-- Nhóm: BẢO HIỂM (mức trần)
('MINIMUM_WAGE_REGION_1',    4960000.0000, 'INSURANCE', 'Lương tối thiểu vùng I (từ 1/7/2024)', b'1', 1),
('MAX_INSURANCE_MULTIPLIER', 20.0000, 'INSURANCE', 'Hệ số nhân lương tối thiểu để tính trần BH', b'1', 1),
-- → Trần BH = 4,960,000 × 20 = 99,200,000 (thực tế hiện hành)
-- Nếu muốn fix cứng: dùng MAX_INSURANCE_BASE thay thế
('MAX_INSURANCE_BASE',       46800000.0000, 'INSURANCE', 'Mức trần lương đóng BHXH (nếu fix cứng, 0=dùng công thức)', b'1', 1),

-- Nhóm: THUẾ TNCN
('PERSONAL_DEDUCTION',       11000000.0000, 'TAX', 'Giảm trừ gia cảnh bản thân (11 triệu/tháng)', b'1', 1),
('DEPENDENT_DEDUCTION',      4400000.0000, 'TAX', 'Giảm trừ mỗi người phụ thuộc (4.4 triệu/tháng)', b'1', 1),

-- Nhóm: LÀM THÊM GIỜ (OT)
('OT_RATE_WEEKDAY',          1.5000, 'OT', 'Hệ số OT ngày thường (150%)', b'1', 1),
('OT_RATE_WEEKEND',          2.0000, 'OT', 'Hệ số OT cuối tuần (200%)', b'1', 1),
('OT_RATE_HOLIDAY',          3.0000, 'OT', 'Hệ số OT ngày lễ (300%)', b'1', 1),

-- Nhóm: PHẠT
('LATE_PENALTY_PER_TIME',    50000.0000, 'PENALTY', 'Mức phạt mỗi lần đi trễ (VNĐ)', b'1', 1),
('LATE_THRESHOLD_MINUTES',   15.0000, 'PENALTY', 'Phút trễ tối thiểu mới tính phạt', b'1', 1),

-- Nhóm: CHUNG
('STANDARD_WORK_DAYS',       22.0000, 'GENERAL', 'Số ngày công chuẩn / tháng', b'1', 1),
('STANDARD_WORK_HOURS',      8.0000, 'GENERAL', 'Số giờ làm chuẩn / ngày', b'1', 1);


-- ============================================
-- EMPLOYEE_TAX_INFO - Thông tin thuế + ngân hàng
-- Tạo cho 61 NV active (emp 1-61)
-- ============================================
INSERT INTO employee_tax_info
(employee_id, tax_code, number_of_dependents, social_insurance_no,
 social_insurance_salary, bank_name, bank_branch,
 bank_account_number, bank_account_holder, updated_by) VALUES

-- Ban Giám đốc (lương cao, nhiều người phụ thuộc)
(1,  '0301234001', 2, 'BH-2015-000001', 46800000.00, 'Techcombank', 'CN Quận 1',    '19031234560001', 'NGUYEN DINH HUNG',     1),
(2,  '0301234002', 1, 'BH-2016-000002', 46800000.00, 'Techcombank', 'CN Quận 1',    '19031234560002', 'TRAN QUOC MINH',       1),
(3,  '0301234003', 2, 'BH-2017-000003', 46800000.00, 'Techcombank', 'CN Quận 1',    '19031234560003', 'LE THI LAN',           1),

-- Phòng HR
(4,  '0301234004', 1, 'BH-2017-000004', 46800000.00, 'Techcombank', 'CN Quận 10',   '19031234560004', 'PHAM THI MAI',         1),
(5,  '0301234005', 0, 'BH-2018-000005', 38000000.00, 'Techcombank', 'CN Quận 5',    '19031234560005', 'NGUYEN THI HOA',       1),
(6,  '0301234006', 0, 'BH-2020-000006', 22000000.00, 'Techcombank', 'CN Quận 3',    '19031234560006', 'LE THI THAO',          1),
(7,  '0301234007', 0, 'BH-2021-000007', 20000000.00, 'Techcombank', 'CN Quận 3',    '19031234560007', 'TRAN THI LINH',        1),
(8,  '0301234008', 1, 'BH-2019-000008', 24000000.00, 'Techcombank', 'CN Bình Thạnh','19031234560008', 'HOANG THI TRANG',      1),
(9,  '0301234009', 0, 'BH-2022-000009', 15000000.00, 'Techcombank', 'CN Quận 3',    '19031234560009', 'DO VAN NAM',           1),

-- Phòng IT
(10, '0301234010', 2, 'BH-2016-000010', 46800000.00, 'Techcombank', 'CN Quận 7',    '19031234560010', 'VO VAN TUAN',          1),
(11, '0301234011', 1, 'BH-2017-000011', 46800000.00, 'Techcombank', 'CN Quận 7',    '19031234560011', 'NGUYEN VAN DUC',       1),
(12, '0301234012', 1, 'BH-2018-000012', 45000000.00, 'Techcombank', 'CN Quận 1',    '19031234560012', 'TRAN VAN LONG',        1),
(13, '0301234013', 0, 'BH-2019-000013', 42000000.00, 'Techcombank', 'CN Quận 3',    '19031234560013', 'PHAM MINH HIEU',       1),
(14, '0301234014', 0, 'BH-2019-000014', 38000000.00, 'Techcombank', 'CN Phú Nhuận', '19031234560014', 'LE HOANG PHONG',       1),
(15, '0301234015', 1, 'BH-2019-000015', 40000000.00, 'Techcombank', 'CN Tân Bình',  '19031234560015', 'NGUYEN DUC KHANH',     1),
(16, '0301234016', 0, 'BH-2020-000016', 36000000.00, 'Techcombank', 'CN Tân Bình',  '19031234560016', 'TRAN MINH HAI',        1),
(17, '0301234017', 0, 'BH-2021-000017', 28000000.00, 'Techcombank', 'CN Tân Bình',  '19031234560017', 'LY MINH HOANG',        1),
(18, '0301234018', 0, 'BH-2022-000018', 26000000.00, 'Techcombank', 'CN Tân Phú',   '19031234560018', 'DANG THI QUYNH',       1),
(19, '0301234019', 0, 'BH-2023-000019', 18000000.00, 'Techcombank', 'CN Tân Phú',   '19031234560019', 'NGO VAN AN',           1),
(20, '0301234020', 0, 'BH-2023-000020', 16000000.00, 'Techcombank', 'CN Tân Phú',   '19031234560020', 'BUI THANH BINH',       1),
(21, '0301234021', 1, 'BH-2019-000021', 42000000.00, 'Techcombank', 'CN Quận 6',    '19031234560021', 'MAI VAN CUONG',        1),
(22, '0301234022', 0, 'BH-2020-000022', 38000000.00, 'Techcombank', 'CN Quận 5',    '19031234560022', 'TRINH VAN DUNG',       1),
(23, '0301234023', 0, 'BH-2021-000023', 24000000.00, 'Techcombank', 'CN Quận 5',    '19031234560023', 'NGUYEN THI THU',       1),
(24, '0301234024', 0, 'BH-2024-000024',  8000000.00, 'Techcombank', 'CN Quận 5',    '19031234560024', 'LE VAN DAT',           1),

-- Phòng Tài chính
(25, '0301234025', 2, 'BH-2016-000025', 46800000.00, 'Techcombank', 'CN Quận 10',   '19031234560025', 'NGUYEN THI HUONG',     1),
(26, '0301234026', 1, 'BH-2017-000026', 40000000.00, 'Techcombank', 'CN Quận 10',   '19031234560026', 'TRAN THI PHUONG',      1),
(27, '0301234027', 1, 'BH-2017-000027', 45000000.00, 'Techcombank', 'CN Quận 10',   '19031234560027', 'LE THI NGA',           1),
(28, '0301234028', 0, 'BH-2020-000028', 24000000.00, 'Techcombank', 'CN Quận 10',   '19031234560028', 'PHAM THI VAN',         1),
(29, '0301234029', 0, 'BH-2021-000029', 22000000.00, 'Techcombank', 'CN Quận 10',   '19031234560029', 'HOANG THI YEN',        1),
(30, '0301234030', 0, 'BH-2022-000030', 20000000.00, 'Techcombank', 'CN Quận 10',   '19031234560030', 'VU THI HANH',          1),
(31, '0301234031', 1, 'BH-2019-000031', 28000000.00, 'Techcombank', 'CN Quận 10',   '19031234560031', 'DO THI THUY',          1),
(32, '0301234032', 0, 'BH-2023-000032', 16000000.00, 'Techcombank', 'CN Quận 10',   '19031234560032', 'BUI THI LOAN',         1),

-- Phòng Kinh doanh
(33, '0301234033', 2, 'BH-2016-000033', 46800000.00, 'Techcombank', 'CN Quận 3',    '19031234560033', 'TRAN VAN THANH',       1),
(34, '0301234034', 1, 'BH-2017-000034', 45000000.00, 'Techcombank', 'CN Quận 3',    '19031234560034', 'NGUYEN VAN SON',       1),
(35, '0301234035', 1, 'BH-2018-000035', 38000000.00, 'Techcombank', 'CN Quận 1',    '19031234560035', 'LE VAN HUNG',          1),
(36, '0301234036', 0, 'BH-2019-000036', 36000000.00, 'Techcombank', 'CN Quận 3',    '19031234560036', 'PHAM VAN TIEN',        1),
(37, '0301234037', 0, 'BH-2020-000037', 28000000.00, 'Techcombank', 'CN Quận 3',    '19031234560037', 'HOANG VAN DUY',        1),
(38, '0301234038', 0, 'BH-2021-000038', 26000000.00, 'Techcombank', 'CN Quận 3',    '19031234560038', 'NGUYEN THI GIANG',     1),
(39, '0301234039', 0, 'BH-2021-000039', 25000000.00, 'Techcombank', 'CN Phú Nhuận', '19031234560039', 'TRAN VAN KHOI',        1),
(40, '0301234040', 0, 'BH-2022-000040', 24000000.00, 'Techcombank', 'CN Phú Nhuận', '19031234560040', 'LE THI LY',            1),
(41, '0301234041', 0, 'BH-2022-000041', 22000000.00, 'Techcombank', 'CN Phú Nhuận', '19031234560041', 'NGO THI MY',           1),
(42, '0301234042', 0, 'BH-2023-000042', 20000000.00, 'Techcombank', 'CN Bình Thạnh','19031234560042', 'BUI THI NHI',          1),
(43, '0301234043', 1, 'BH-2019-000043', 30000000.00, 'Techcombank', 'CN Bình Thạnh','19031234560043', 'DANG THI OANH',        1),
(44, '0301234044', 0, 'BH-2023-000044', 16000000.00, 'Techcombank', 'CN Bình Thạnh','19031234560044', 'LE VAN PHAT',          1),

-- Phòng Marketing
(45, '0301234045', 1, 'BH-2017-000045', 46800000.00, 'Techcombank', 'CN Quận 1',    '19031234560045', 'NGUYEN THI QUYEN',     1),
(46, '0301234046', 0, 'BH-2021-000046', 26000000.00, 'Techcombank', 'CN Quận 1',    '19031234560046', 'TRAN HONG ROSE',       1),
(47, '0301234047', 0, 'BH-2022-000047', 22000000.00, 'Techcombank', 'CN Bình Thạnh','19031234560047', 'LE THI SUONG',         1),
(48, '0301234048', 0, 'BH-2021-000048', 24000000.00, 'Techcombank', 'CN Bình Thạnh','19031234560048', 'PHAM VAN TAM',         1),
(49, '0301234049', 0, 'BH-2022-000049', 20000000.00, 'Techcombank', 'CN Bình Thạnh','19031234560049', 'HOANG THI UYEN',       1),
(50, '0301234050', 0, 'BH-2023-000050', 18000000.00, 'Techcombank', 'CN Quận 3',    '19031234560050', 'VU THI VY',            1),

-- Phòng Vận hành
(51, '0301234051', 2, 'BH-2017-000051', 42000000.00, 'Techcombank', 'CN Quận 10',   '19031234560051', 'NGUYEN VAN XUAN',      1),
(52, '0301234052', 1, 'BH-2018-000052', 35000000.00, 'Techcombank', 'CN Quận 10',   '19031234560052', 'TRAN THI YEN',         1),
(53, '0301234053', 0, 'BH-2021-000053', 18000000.00, 'Techcombank', 'CN Quận 10',   '19031234560053', 'LE VAN BACH',          1),
(54, '0301234054', 0, 'BH-2022-000054', 16000000.00, 'Techcombank', 'CN Quận 10',   '19031234560054', 'NGUYEN THI CHI',       1),
(55, '0301234055', 0, 'BH-2022-000055', 15000000.00, 'Techcombank', 'CN Quận 11',   '19031234560055', 'PHAM VAN DAO',         1),
(56, '0301234056', 0, 'BH-2023-000056', 14000000.00, 'Techcombank', 'CN Quận 11',   '19031234560056', 'TRAN THI EM',          1),
(57, '0301234057', 1, 'BH-2020-000057', 15000000.00, 'Techcombank', 'CN Quận 11',   '19031234560057', 'NGUYEN VAN GIAI',      1),

-- Phòng R&D
(58, '0301234058', 2, 'BH-2016-000058', 46800000.00, 'Techcombank', 'CN Thủ Đức',   '19031234560058', 'TRAN VAN HAO',         1),
(59, '0301234059', 0, 'BH-2019-000059', 38000000.00, 'Techcombank', 'CN Thủ Đức',   '19031234560059', 'LE VAN ICH',           1),
(60, '0301234060', 0, 'BH-2020-000060', 35000000.00, 'Techcombank', 'CN Thủ Đức',   '19031234560060', 'NGUYEN VAN KHANG',     1),
(61, '0301234061', 0, 'BH-2021-000061', 32000000.00, 'Techcombank', 'CN Quận 9',    '19031234560061', 'PHAM VAN LAM',         1);


-- ============================================
-- ALLOWANCES - Phụ cấp mẫu
-- Áp dụng từ 01/01/2026, không hết hạn
-- ============================================

-- Phụ cấp ĂN TRƯA - tất cả NV đều có (730,000/tháng ~ 33k/ngày)
INSERT INTO allowances (employee_id, allowance_type, name, amount, is_taxable, is_insurance, effective_date, is_active, created_by)
SELECT id, 'MEAL', 'Phụ cấp ăn trưa', 730000.00, b'0', b'0', '2026-01-01', b'1', 1
FROM employees WHERE id BETWEEN 1 AND 61 AND is_deleted = b'0';

-- Phụ cấp XE - tất cả NV (500,000/tháng)
INSERT INTO allowances (employee_id, allowance_type, name, amount, is_taxable, is_insurance, effective_date, is_active, created_by)
SELECT id, 'TRANSPORT', 'Phụ cấp xăng xe', 500000.00, b'0', b'0', '2026-01-01', b'1', 1
FROM employees WHERE id BETWEEN 1 AND 61 AND is_deleted = b'0';

-- Phụ cấp ĐIỆN THOẠI - quản lý trở lên (500,000/tháng)
INSERT INTO allowances (employee_id, allowance_type, name, amount, is_taxable, is_insurance, effective_date, is_active, created_by)
SELECT id, 'PHONE', 'Phụ cấp điện thoại', 500000.00, b'1', b'0', '2026-01-01', b'1', 1
FROM employees
WHERE id IN (1,2,3,4,5,10,11,12,13,22,25,26,27,33,34,35,36,45,51,52,58)
AND is_deleted = b'0';

-- Phụ cấp CHỨC VỤ - HEAD/DEPUTY/LEADER
INSERT INTO allowances (employee_id, allowance_type, name, amount, is_taxable, is_insurance, effective_date, is_active, created_by) VALUES
-- Ban GĐ
(1, 'POSITION', 'Phụ cấp chức vụ CEO', 15000000.00, b'1', b'1', '2026-01-01', b'1', 1),
(2, 'POSITION', 'Phụ cấp chức vụ CTO', 10000000.00, b'1', b'1', '2026-01-01', b'1', 1),
(3, 'POSITION', 'Phụ cấp chức vụ CFO', 10000000.00, b'1', b'1', '2026-01-01', b'1', 1),
-- Trưởng phòng (HEAD)
(4,  'POSITION', 'Phụ cấp trưởng phòng', 5000000.00, b'1', b'1', '2026-01-01', b'1', 1),
(10, 'POSITION', 'Phụ cấp trưởng phòng', 5000000.00, b'1', b'1', '2026-01-01', b'1', 1),
(25, 'POSITION', 'Phụ cấp trưởng phòng', 5000000.00, b'1', b'1', '2026-01-01', b'1', 1),
(33, 'POSITION', 'Phụ cấp trưởng phòng', 5000000.00, b'1', b'1', '2026-01-01', b'1', 1),
(45, 'POSITION', 'Phụ cấp trưởng phòng', 5000000.00, b'1', b'1', '2026-01-01', b'1', 1),
(51, 'POSITION', 'Phụ cấp trưởng phòng', 5000000.00, b'1', b'1', '2026-01-01', b'1', 1),
(58, 'POSITION', 'Phụ cấp trưởng phòng', 5000000.00, b'1', b'1', '2026-01-01', b'1', 1),
-- Phó phòng (DEPUTY)
(5,  'POSITION', 'Phụ cấp phó phòng', 3000000.00, b'1', b'1', '2026-01-01', b'1', 1),
(11, 'POSITION', 'Phụ cấp phó phòng', 3000000.00, b'1', b'1', '2026-01-01', b'1', 1),
(26, 'POSITION', 'Phụ cấp phó phòng', 3000000.00, b'1', b'1', '2026-01-01', b'1', 1),
(34, 'POSITION', 'Phụ cấp phó phòng', 3000000.00, b'1', b'1', '2026-01-01', b'1', 1),
(52, 'POSITION', 'Phụ cấp phó phòng', 3000000.00, b'1', b'1', '2026-01-01', b'1', 1),
-- Leader
(12, 'POSITION', 'Phụ cấp trưởng nhóm', 2000000.00, b'1', b'1', '2026-01-01', b'1', 1),
(13, 'POSITION', 'Phụ cấp trưởng nhóm', 2000000.00, b'1', b'1', '2026-01-01', b'1', 1),
(22, 'POSITION', 'Phụ cấp trưởng nhóm', 2000000.00, b'1', b'1', '2026-01-01', b'1', 1),
(27, 'POSITION', 'Phụ cấp trưởng nhóm', 2000000.00, b'1', b'1', '2026-01-01', b'1', 1),
(35, 'POSITION', 'Phụ cấp trưởng nhóm', 2000000.00, b'1', b'1', '2026-01-01', b'1', 1),
(36, 'POSITION', 'Phụ cấp trưởng nhóm', 2000000.00, b'1', b'1', '2026-01-01', b'1', 1);


-- ============================================
-- BONUSES - Thưởng tháng 3/2026 (mẫu)
-- ============================================
INSERT INTO bonuses (employee_id, bonus_type, name, amount, is_taxable, month, year, reason, is_approved, approved_by, approved_at, created_by) VALUES
-- Thưởng KPI Q1/2026
(1,  'KPI', 'Thưởng KPI Q1/2026', 30000000.00, b'1', 3, 2026, 'Hoàn thành xuất sắc KPI Q1', b'1', 1, '2026-03-28 10:00:00', 1),
(2,  'KPI', 'Thưởng KPI Q1/2026', 20000000.00, b'1', 3, 2026, 'Hoàn thành KPI Q1', b'1', 1, '2026-03-28 10:00:00', 1),
(10, 'KPI', 'Thưởng KPI Q1/2026', 15000000.00, b'1', 3, 2026, 'IT team delivery đúng hạn', b'1', 1, '2026-03-28 10:00:00', 1),
(12, 'KPI', 'Thưởng KPI Q1/2026', 10000000.00, b'1', 3, 2026, 'Lead backend hoàn thành tốt', b'1', 1, '2026-03-28 10:00:00', 1),
(33, 'KPI', 'Thưởng KPI Q1/2026', 12000000.00, b'1', 3, 2026, 'Sales vượt target 120%', b'1', 1, '2026-03-28 10:00:00', 1),

-- Thưởng dự án
(14, 'PROJECT', 'Thưởng dự án MetaHRM', 5000000.00, b'1', 3, 2026, 'Hoàn thành module Attendance', b'1', 10, '2026-03-25 14:00:00', 10),
(15, 'PROJECT', 'Thưởng dự án MetaHRM', 5000000.00, b'1', 3, 2026, 'Hoàn thành module Attendance', b'1', 10, '2026-03-25 14:00:00', 10),
(21, 'PROJECT', 'Thưởng dự án MetaHRM', 3000000.00, b'1', 3, 2026, 'DevOps setup CI/CD', b'1', 10, '2026-03-25 14:00:00', 10);


-- ============================================
-- DEDUCTIONS - Khấu trừ tháng 3/2026 (mẫu)
-- ============================================
INSERT INTO deductions (employee_id, deduction_type, name, amount, month, year, reason, is_approved, approved_by, created_by) VALUES
-- Phạt vi phạm nội quy
(37, 'PENALTY', 'Phạt vi phạm trang phục', 200000.00, 3, 2026, 'Vi phạm quy định trang phục công sở 2 lần', b'1', 34, 34),
-- Bồi thường thiết bị
(20, 'DAMAGE', 'Bồi thường hỏng keyboard', 500000.00, 3, 2026, 'Làm hỏng bàn phím cơ công ty', b'1', 11, 11);


-- ============================================
-- SUMMARY
-- ============================================
-- payroll_config:     18 records (BH, thuế, OT, phạt, chung)
-- employee_tax_info:  61 records (mỗi NV 1 record)
-- allowances:         ~150 records
--   ├── Ăn trưa:      61 NV × 730k
--   ├── Xăng xe:       61 NV × 500k
--   ├── Điện thoại:    ~21 NV × 500k
--   └── Chức vụ:       ~21 NV × 2-15 triệu
-- bonuses:            8 records (KPI + project tháng 3)
-- deductions:         2 records (phạt + bồi thường tháng 3)
-- payslips:           0 (sẽ tạo khi tính lương)
-- payslip_details:    0 (sẽ tạo khi tính lương)