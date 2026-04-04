-- ========================================
-- SEED DATA FOR LEAVE MANAGEMENT
-- Version: V11
-- Author: System
-- Description: Insert initial data for leave management module
-- ========================================

-- ========================================
-- 1. LEAVE TYPES - Các loại nghỉ phép
-- ========================================

INSERT INTO leave_types (
    code, name, max_days_per_year, default_days_per_year,
    paid_leave, requires_approval, requires_document, active,
    deduct_balance, deduct_from_annual_leave_balance, auto_approve,
    allow_carry_forward, allow_encashment, counts_in_attendance,
    counts_in_company_payroll, deduct_salary, social_insurance_paid,
    increase_by_seniority
) VALUES
-- Nghỉ phép năm (Annual Leave)
(
    'ANNUAL_LEAVE', 'Nghỉ phép năm', 20, 12,
    TRUE, TRUE, FALSE, TRUE,
    TRUE, FALSE, FALSE,
    TRUE, TRUE, TRUE,
    TRUE, FALSE, TRUE,
    TRUE
),
-- Nghỉ ốm (Sick Leave)
(
    'SICK_LEAVE', 'Nghỉ ốm', 30, 30,
    TRUE, TRUE, TRUE, TRUE,
    TRUE, FALSE, FALSE,
    FALSE, FALSE, TRUE,
    TRUE, FALSE, TRUE,
    FALSE
),
-- Nghỉ không lương (Unpaid Leave)
(
    'UNPAID_LEAVE', 'Nghỉ không lương', 365, 0,
    FALSE, TRUE, FALSE, TRUE,
    FALSE, FALSE, FALSE,
    FALSE, FALSE, TRUE,
    FALSE, TRUE, FALSE,
    FALSE
),
-- Nghỉ lễ (Holiday Leave)
(
    'HOLIDAY_LEAVE', 'Nghỉ lễ', 15, 15,
    TRUE, FALSE, FALSE, TRUE,
    FALSE, FALSE, TRUE,
    FALSE, FALSE, TRUE,
    TRUE, FALSE, TRUE,
    FALSE
),
-- Nghỉ thai sản (Maternity Leave)
(
    'MATERNITY_LEAVE', 'Nghỉ thai sản', 180, 180,
    TRUE, TRUE, TRUE, TRUE,
    FALSE, FALSE, FALSE,
    FALSE, FALSE, TRUE,
    TRUE, FALSE, TRUE,
    FALSE
),
-- Nghỉ đặc biệt (Special Leave) - Cưới, tang, v.v.
(
    'SPECIAL_LEAVE', 'Nghỉ đặc biệt', 10, 10,
    TRUE, TRUE, TRUE, TRUE,
    FALSE, FALSE, FALSE,
    FALSE, FALSE, TRUE,
    TRUE, FALSE, TRUE,
    FALSE
),
-- Nghỉ phép cha (Paternity Leave)
(
    'PATERNITY_LEAVE', 'Nghỉ phép cha', 7, 7,
    TRUE, TRUE, TRUE, TRUE,
    FALSE, FALSE, FALSE,
    FALSE, FALSE, TRUE,
    TRUE, FALSE, TRUE,
    FALSE
),
-- Nghỉ bù (Compensatory Leave)
(
    'COMPENSATORY_LEAVE', 'Nghỉ bù', 30, 0,
    TRUE, TRUE, FALSE, TRUE,
    TRUE, FALSE, FALSE,
    FALSE, FALSE, TRUE,
    TRUE, FALSE, TRUE,
    FALSE
),
-- Nghỉ học tập (Study Leave)
(
    'STUDY_LEAVE', 'Nghỉ học tập', 15, 0,
    TRUE, TRUE, TRUE, TRUE,
    TRUE, FALSE, FALSE,
    FALSE, FALSE, TRUE,
    TRUE, FALSE, TRUE,
    FALSE
),
-- Nghỉ công tác (Business Leave)
(
    'BUSINESS_LEAVE', 'Nghỉ công tác', 60, 0,
    TRUE, TRUE, FALSE, TRUE,
    FALSE, FALSE, FALSE,
    FALSE, FALSE, TRUE,
    TRUE, FALSE, TRUE,
    FALSE
);

-- ========================================
-- 2. LEAVE TYPE SENIORITY RULES
-- ========================================

-- Quy tắc thâm niên cho Nghỉ phép năm (ANNUAL_LEAVE)
INSERT INTO leave_type_seniority_rules (leave_type_id, min_years, extra_days)
SELECT id, 0, 0 FROM leave_types WHERE code = 'ANNUAL_LEAVE'
UNION ALL
SELECT id, 3, 1 FROM leave_types WHERE code = 'ANNUAL_LEAVE'
UNION ALL
SELECT id, 5, 2 FROM leave_types WHERE code = 'ANNUAL_LEAVE'
UNION ALL
SELECT id, 10, 3 FROM leave_types WHERE code = 'ANNUAL_LEAVE'
UNION ALL
SELECT id, 15, 5 FROM leave_types WHERE code = 'ANNUAL_LEAVE'
UNION ALL
SELECT id, 20, 7 FROM leave_types WHERE code = 'ANNUAL_LEAVE';

-- Quy tắc thâm niên cho Nghỉ bù (COMPENSATORY_LEAVE)
INSERT INTO leave_type_seniority_rules (leave_type_id, min_years, extra_days)
SELECT id, 0, 0 FROM leave_types WHERE code = 'COMPENSATORY_LEAVE'
UNION ALL
SELECT id, 5, 2 FROM leave_types WHERE code = 'COMPENSATORY_LEAVE'
UNION ALL
SELECT id, 10, 5 FROM leave_types WHERE code = 'COMPENSATORY_LEAVE';

-- Quy tắc thâm niên cho Nghỉ học tập (STUDY_LEAVE)
INSERT INTO leave_type_seniority_rules (leave_type_id, min_years, extra_days)
SELECT id, 0, 0 FROM leave_types WHERE code = 'STUDY_LEAVE'
UNION ALL
SELECT id, 3, 3 FROM leave_types WHERE code = 'STUDY_LEAVE'
UNION ALL
SELECT id, 5, 5 FROM leave_types WHERE code = 'STUDY_LEAVE';

-- ========================================
-- 3. HOLIDAYS - Ngày lễ năm 2024
-- ========================================

INSERT INTO holidays (holiday_date, name, active) VALUES
-- Tết Dương lịch
('2024-01-01', 'Tết Dương lịch', TRUE),

-- Tết Nguyên đán 2024 (Giáp Thìn)
('2024-02-08', 'Tết Nguyên đán - Ngày 29 tháng Chạp', TRUE),
('2024-02-09', 'Tết Nguyên đán - Giao thừa (30 tháng Chạp)', TRUE),
('2024-02-10', 'Tết Nguyên đán - Mùng 1', TRUE),
('2024-02-11', 'Tết Nguyên đán - Mùng 2', TRUE),
('2024-02-12', 'Tết Nguyên đán - Mùng 3', TRUE),
('2024-02-13', 'Tết Nguyên đán - Mùng 4', TRUE),
('2024-02-14', 'Tết Nguyên đán - Mùng 5', TRUE),

-- Giỗ Tổ Hùng Vương
('2024-04-18', 'Giỗ Tổ Hùng Vương (10/3 Âm lịch)', TRUE),

-- Ngày Giải phóng miền Nam
('2024-04-30', 'Ngày Giải phóng miền Nam thống nhất đất nước', TRUE),

-- Ngày Quốc tế Lao động
('2024-05-01', 'Ngày Quốc tế Lao động', TRUE),

-- Ngày Quốc khánh
('2024-09-02', 'Ngày Quốc khánh nước CHXHCN Việt Nam', TRUE),
('2024-09-03', 'Nghỉ bù Ngày Quốc khánh', TRUE);

-- ========================================
-- 4. HOLIDAYS - Ngày lễ năm 2025
-- ========================================

INSERT INTO holidays (holiday_date, name, active) VALUES
-- Tết Dương lịch
('2025-01-01', 'Tết Dương lịch', TRUE),

-- Tết Nguyên đán 2025 (Ất Tỵ)
('2025-01-27', 'Tết Nguyên đán - Ngày 28 tháng Chạp', TRUE),
('2025-01-28', 'Tết Nguyên đán - Ngày 29 tháng Chạp', TRUE),
('2025-01-29', 'Tết Nguyên đán - Giao thừa (30 tháng Chạp)', TRUE),
('2025-01-30', 'Tết Nguyên đán - Mùng 1', TRUE),
('2025-01-31', 'Tết Nguyên đán - Mùng 2', TRUE),
('2025-02-01', 'Tết Nguyên đán - Mùng 3', TRUE),
('2025-02-02', 'Tết Nguyên đán - Mùng 4', TRUE),
('2025-02-03', 'Tết Nguyên đán - Mùng 5', TRUE),

-- Giỗ Tổ Hùng Vương
('2025-04-07', 'Giỗ Tổ Hùng Vương (10/3 Âm lịch)', TRUE),

-- Ngày Giải phóng miền Nam
('2025-04-30', 'Ngày Giải phóng miền Nam thống nhất đất nước', TRUE),

-- Ngày Quốc tế Lao động
('2025-05-01', 'Ngày Quốc tế Lao động', TRUE),
('2025-05-02', 'Nghỉ bù Ngày Quốc tế Lao động', TRUE),

-- Ngày Quốc khánh
('2025-09-01', 'Nghỉ bù Ngày Quốc khánh', TRUE),
('2025-09-02', 'Ngày Quốc khánh nước CHXHCN Việt Nam', TRUE);

-- ========================================
-- 5. LEAVE BALANCES - Số dư phép năm 2024
-- ========================================

-- Annual Leave cho tất cả nhân viên đang làm việc (employee_id 1-61)
INSERT INTO leave_balances (employee_id, leave_type_id, year, allocated_days, used_days, pending_days, carry_forward_days, encashed_days)
SELECT 1, id, 2024, 19.00, 8.00, 0.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE'  -- CEO (thâm niên 9 năm = 12+2)
UNION ALL SELECT 2, id, 2024, 17.00, 6.00, 2.00, 1.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE'  -- CTO (8 năm)
UNION ALL SELECT 3, id, 2024, 17.00, 4.00, 0.00, 2.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE'  -- CFO (7 năm)
UNION ALL SELECT 4, id, 2024, 17.00, 5.00, 1.00, 1.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE'  -- HR Director (7 năm)
UNION ALL SELECT 5, id, 2024, 15.00, 3.00, 0.00, 2.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE'  -- HR Deputy (6 năm)
UNION ALL SELECT 6, id, 2024, 12.00, 2.00, 1.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE'  -- HR Staff
UNION ALL SELECT 7, id, 2024, 12.00, 4.00, 0.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE'  -- HR Staff
UNION ALL SELECT 8, id, 2024, 13.00, 6.00, 0.50, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE'  -- HR Staff (5 năm)
UNION ALL SELECT 9, id, 2024, 12.00, 1.00, 0.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE'  -- HR Staff
UNION ALL SELECT 10, id, 2024, 17.00, 7.00, 0.00, 2.00, 1.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- IT Director (8 năm)
UNION ALL SELECT 11, id, 2024, 16.00, 5.00, 1.00, 1.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- IT Deputy (7 năm)
UNION ALL SELECT 12, id, 2024, 15.00, 3.00, 0.00, 2.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Tech Lead (6 năm)
UNION ALL SELECT 13, id, 2024, 14.00, 4.00, 1.00, 1.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Tech Lead (5 năm)
UNION ALL SELECT 14, id, 2024, 13.00, 2.00, 0.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Senior Dev (5 năm)
UNION ALL SELECT 15, id, 2024, 13.00, 6.00, 0.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Senior Dev (5 năm)
UNION ALL SELECT 16, id, 2024, 12.00, 3.00, 2.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Senior Dev
UNION ALL SELECT 17, id, 2024, 13.00, 5.00, 0.00, 1.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Mid Dev (3 năm)
UNION ALL SELECT 18, id, 2024, 12.00, 4.00, 1.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Mid Dev
UNION ALL SELECT 19, id, 2024, 12.00, 2.00, 0.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Junior Dev
UNION ALL SELECT 20, id, 2024, 12.00, 1.00, 0.50, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Junior Dev
UNION ALL SELECT 21, id, 2024, 13.00, 4.00, 0.00, 1.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- DevOps (5 năm)
UNION ALL SELECT 22, id, 2024, 12.00, 3.00, 1.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- QA Lead
UNION ALL SELECT 23, id, 2024, 13.00, 2.00, 0.00, 1.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- QA (3 năm)
UNION ALL SELECT 24, id, 2024, 0.00, 0.00, 0.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE'  -- Intern (chưa có phép)
UNION ALL SELECT 25, id, 2024, 17.00, 5.00, 0.00, 2.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Finance Dir (8 năm)
UNION ALL SELECT 26, id, 2024, 16.00, 4.00, 1.00, 1.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Finance Deputy (7 năm)
UNION ALL SELECT 27, id, 2024, 16.00, 6.00, 0.00, 1.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Chief Acc (7 năm)
UNION ALL SELECT 28, id, 2024, 12.00, 3.00, 0.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Accountant
UNION ALL SELECT 29, id, 2024, 13.00, 2.00, 1.00, 1.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Accountant (3 năm)
UNION ALL SELECT 30, id, 2024, 12.00, 4.00, 0.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Accountant
UNION ALL SELECT 31, id, 2024, 13.00, 5.00, 0.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Tax (5 năm)
UNION ALL SELECT 32, id, 2024, 12.00, 1.00, 0.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Cashier
UNION ALL SELECT 33, id, 2024, 17.00, 6.00, 0.00, 2.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Sales Dir (8 năm)
UNION ALL SELECT 34, id, 2024, 16.00, 4.00, 1.00, 1.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Sales Deputy (7 năm)
UNION ALL SELECT 35, id, 2024, 15.00, 3.00, 0.00, 2.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Sales Lead (6 năm)
UNION ALL SELECT 36, id, 2024, 14.00, 5.00, 0.00, 1.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Sales Lead (5 năm)
UNION ALL SELECT 37, id, 2024, 12.00, 2.00, 1.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Sales
UNION ALL SELECT 38, id, 2024, 13.00, 4.00, 0.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Sales (3 năm)
UNION ALL SELECT 39, id, 2024, 13.00, 3.00, 0.00, 1.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Sales (3 năm)
UNION ALL SELECT 40, id, 2024, 12.00, 2.00, 1.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Sales
UNION ALL SELECT 41, id, 2024, 12.00, 5.00, 0.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Sales
UNION ALL SELECT 42, id, 2024, 12.00, 1.00, 0.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Sales
UNION ALL SELECT 43, id, 2024, 13.00, 3.00, 1.00, 1.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Presales (5 năm)
UNION ALL SELECT 44, id, 2024, 12.00, 2.00, 0.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Sales Support
UNION ALL SELECT 45, id, 2024, 16.00, 4.00, 0.00, 2.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Marketing Dir (7 năm)
UNION ALL SELECT 46, id, 2024, 13.00, 3.00, 1.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Digital Mkt (3 năm)
UNION ALL SELECT 47, id, 2024, 12.00, 2.00, 0.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Content Mkt
UNION ALL SELECT 48, id, 2024, 13.00, 4.00, 0.00, 1.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Designer (3 năm)
UNION ALL SELECT 49, id, 2024, 12.00, 2.00, 1.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- SEO
UNION ALL SELECT 50, id, 2024, 12.00, 3.00, 0.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Event
UNION ALL SELECT 51, id, 2024, 16.00, 5.00, 0.00, 2.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Ops Dir (7 năm)
UNION ALL SELECT 52, id, 2024, 15.00, 4.00, 1.00, 1.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Ops Deputy (6 năm)
UNION ALL SELECT 53, id, 2024, 13.00, 2.00, 0.00, 1.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Ops (3 năm)
UNION ALL SELECT 54, id, 2024, 12.00, 3.00, 0.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Ops
UNION ALL SELECT 55, id, 2024, 12.00, 1.00, 1.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Ops
UNION ALL SELECT 56, id, 2024, 12.00, 2.00, 0.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Reception
UNION ALL SELECT 57, id, 2024, 12.00, 4.00, 0.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Driver
UNION ALL SELECT 58, id, 2024, 17.00, 6.00, 0.00, 2.00, 1.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- R&D Dir (8 năm)
UNION ALL SELECT 59, id, 2024, 13.00, 3.00, 1.00, 1.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Researcher (5 năm)
UNION ALL SELECT 60, id, 2024, 12.00, 2.00, 0.00, 0.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE' -- Researcher
UNION ALL SELECT 61, id, 2024, 13.00, 4.00, 0.00, 1.00, 0.00 FROM leave_types WHERE code = 'ANNUAL_LEAVE'; -- Researcher (3 năm)

-- Sick Leave cho tất cả nhân viên
INSERT INTO leave_balances (employee_id, leave_type_id, year, allocated_days, used_days, pending_days, carry_forward_days, encashed_days)
SELECT e.id, lt.id, 2024, 30.00, 
    CASE 
        WHEN e.id <= 10 THEN ROUND(RAND() * 5, 1)
        WHEN e.id <= 30 THEN ROUND(RAND() * 3, 1)
        ELSE ROUND(RAND() * 2, 1)
    END, 
    0.00, 0.00, 0.00
FROM employees e
CROSS JOIN leave_types lt
WHERE e.id BETWEEN 1 AND 61
  AND lt.code = 'SICK_LEAVE';

-- Compensatory Leave (chỉ cho nhân viên có overtime)
INSERT INTO leave_balances (employee_id, leave_type_id, year, allocated_days, used_days, pending_days, carry_forward_days, encashed_days)
SELECT e.id, lt.id, 2024,
    CASE 
        WHEN e.dept_id = 3 THEN 8.00  -- IT có nhiều OT
        WHEN e.dept_id = 5 THEN 6.00  -- Sales
        WHEN e.dept_id IN (6, 8) THEN 4.00  -- Marketing, R&D
        ELSE 2.00
    END,
    ROUND(RAND() * 3, 1), 0.00, 0.00, 0.00
FROM employees e
CROSS JOIN leave_types lt
WHERE e.id BETWEEN 1 AND 61
  AND lt.code = 'COMPENSATORY_LEAVE';

-- ========================================
-- 6. LEAVE REQUESTS - Đơn xin nghỉ phép
-- ========================================

-- Mapping manager_id dựa trên department
-- Dept 1 (Ban Giám đốc): manager = CEO (emp 1)
-- Dept 2 (HR): manager = HR Dir (emp 4)
-- Dept 3 (IT): manager = IT Dir (emp 10)
-- Dept 4 (Finance): manager = Fin Dir (emp 25)
-- Dept 5 (Sales): manager = Sales Dir (emp 33)
-- Dept 6 (Marketing): manager = Mkt Dir (emp 45)
-- Dept 7 (Ops): manager = Ops Dir (emp 51)
-- Dept 8 (R&D): manager = R&D Dir (emp 58)

-- HR staff (user_id = 5,6 tương ứng emp 4,5)

-- Đơn 1: Nhân viên IT - Đã duyệt hoàn toàn
INSERT INTO leave_requests (
    employee_id, employee_name, manager_id, hr_id, leave_type_id,
    start_date, end_date, leave_unit, start_session, end_session,
    total_days, reason, status, approval_stage, final_approved,
    created_at, updated_at, submitted_at, approved_at
)
SELECT 
    17, 'Lý Minh Hoàng', 10, 4, id,
    '2024-03-15', '2024-03-17', 'FULL_DAY', 'FULL_DAY', 'FULL_DAY',
    3.00, 'Nghỉ phép để đi du lịch cùng gia đình', 'APPROVED', 'COMPLETED', TRUE,
    '2024-03-01 09:00:00', '2024-03-05 14:30:00', '2024-03-01 09:30:00', '2024-03-05 14:30:00'
FROM leave_types WHERE code = 'ANNUAL_LEAVE';

-- Đơn 2: Nhân viên Sales - Đang chờ Manager duyệt
INSERT INTO leave_requests (
    employee_id, employee_name, manager_id, hr_id, leave_type_id,
    start_date, end_date, leave_unit, start_session, end_session,
    total_days, reason, status, approval_stage, final_approved,
    created_at, updated_at, submitted_at
)
SELECT 
    38, 'Nguyễn Thị Giang', 33, 4, id,
    '2024-12-20', '2024-12-24', 'FULL_DAY', 'FULL_DAY', 'FULL_DAY',
    5.00, 'Nghỉ phép cuối năm để về quê', 'PENDING', 'WAITING_MANAGER', FALSE,
    '2024-12-10 10:00:00', '2024-12-10 10:00:00', '2024-12-10 10:30:00'
FROM leave_types WHERE code = 'ANNUAL_LEAVE';

-- Đơn 3: Nhân viên Marketing - Đang chờ HR duyệt
INSERT INTO leave_requests (
    employee_id, employee_name, manager_id, hr_id, leave_type_id,
    start_date, end_date, leave_unit, start_session, end_session,
    total_days, reason, status, approval_stage, final_approved,
    created_at, updated_at, submitted_at
)
SELECT 
    47, 'Lê Thị Sương', 45, 5, id,
    '2024-12-25', '2024-12-27', 'FULL_DAY', 'FULL_DAY', 'FULL_DAY',
    3.00, 'Nghỉ lễ Noel và năm mới', 'PENDING', 'WAITING_HR', FALSE,
    '2024-12-05 08:00:00', '2024-12-15 16:00:00', '2024-12-05 08:30:00'
FROM leave_types WHERE code = 'ANNUAL_LEAVE';

-- Đơn 4: Nhân viên Finance - Bị từ chối
INSERT INTO leave_requests (
    employee_id, employee_name, manager_id, hr_id, leave_type_id,
    start_date, end_date, leave_unit, start_session, end_session,
    total_days, reason, status, approval_stage, reject_reason, final_approved,
    created_at, updated_at, submitted_at
)
SELECT 
    28, 'Phạm Thị Vân', 25, 4, id,
    '2024-12-28', '2024-12-31', 'FULL_DAY', 'FULL_DAY', 'FULL_DAY',
    4.00, 'Nghỉ phép cuối năm', 'REJECTED', 'COMPLETED',
    'Cuối năm là thời điểm chốt sổ sách, cần tất cả nhân viên kế toán có mặt.', FALSE,
    '2024-12-01 11:00:00', '2024-12-08 09:00:00', '2024-12-01 11:30:00'
FROM leave_types WHERE code = 'ANNUAL_LEAVE';

-- Đơn 5: Nhân viên R&D - Đơn nháp
INSERT INTO leave_requests (
    employee_id, employee_name, manager_id, hr_id, leave_type_id,
    start_date, end_date, leave_unit, start_session, end_session,
    total_days, reason, status, approval_stage, final_approved,
    created_at, updated_at
)
SELECT 
    60, 'Nguyễn Văn Khang', 58, 5, id,
    '2025-01-15', '2025-01-17', 'FULL_DAY', 'FULL_DAY', 'FULL_DAY',
    3.00, 'Dự định nghỉ phép đầu năm', 'DRAFT', 'NONE', FALSE,
    '2024-12-18 14:00:00', '2024-12-18 14:00:00'
FROM leave_types WHERE code = 'ANNUAL_LEAVE';

-- Đơn 6: CEO - Nghỉ ốm đã duyệt
INSERT INTO leave_requests (
    employee_id, employee_name, manager_id, hr_id, leave_type_id,
    start_date, end_date, leave_unit, start_session, end_session,
    total_days, reason, status, approval_stage, final_approved,
    created_at, updated_at, submitted_at, approved_at
)
SELECT 
    1, 'Nguyễn Đình Hùng', NULL, 4, id,
    '2024-06-10', '2024-06-11', 'FULL_DAY', 'FULL_DAY', 'FULL_DAY',
    2.00, 'Nghỉ ốm do bị cảm cúm, có giấy khám bệnh', 'APPROVED', 'COMPLETED', TRUE,
    '2024-06-10 07:00:00', '2024-06-10 14:00:00', '2024-06-10 07:30:00', '2024-06-10 14:00:00'
FROM leave_types WHERE code = 'SICK_LEAVE';

-- Đơn 7: Nhân viên HR - Nghỉ nửa ngày đã duyệt
INSERT INTO leave_requests (
    employee_id, employee_name, manager_id, hr_id, leave_type_id,
    start_date, end_date, leave_unit, start_session, end_session,
    total_days, reason, status, approval_stage, final_approved,
    created_at, updated_at, submitted_at, approved_at
)
SELECT 
    6, 'Lê Thị Thảo', 4, 5, id,
    '2024-08-15', '2024-08-15', 'HALF_DAY', 'AFTERNOON', 'AFTERNOON',
    0.50, 'Nghỉ buổi chiều để đưa con đi khám bệnh', 'APPROVED', 'COMPLETED', TRUE,
    '2024-08-10 08:00:00', '2024-08-12 09:00:00', '2024-08-10 08:15:00', '2024-08-12 09:00:00'
FROM leave_types WHERE code = 'ANNUAL_LEAVE';

-- Đơn 8: Nhân viên Ops - Đã hủy
INSERT INTO leave_requests (
    employee_id, employee_name, manager_id, hr_id, leave_type_id,
    start_date, end_date, leave_unit, start_session, end_session,
    total_days, reason, status, approval_stage, cancel_reason, final_approved,
    created_at, updated_at, submitted_at, cancelled_at
)
SELECT 
    53, 'Lê Văn Bách', 51, 4, id,
    '2024-09-10', '2024-09-12', 'FULL_DAY', 'FULL_DAY', 'FULL_DAY',
    3.00, 'Nghỉ phép riêng', 'CANCELLED', 'NONE',
    'Kế hoạch thay đổi, không cần nghỉ phép nữa', FALSE,
    '2024-09-01 09:00:00', '2024-09-05 08:00:00', '2024-09-01 09:30:00', '2024-09-05 08:00:00'
FROM leave_types WHERE code = 'ANNUAL_LEAVE';

-- Đơn 9: Nhân viên Sales - Nghỉ đặc biệt (cưới) đã duyệt
INSERT INTO leave_requests (
    employee_id, employee_name, manager_id, hr_id, leave_type_id,
    start_date, end_date, leave_unit, start_session, end_session,
    total_days, reason, status, approval_stage, final_approved,
    created_at, updated_at, submitted_at, approved_at
)
SELECT 
    40, 'Lê Thị Lý', 33, 4, id,
    '2024-10-15', '2024-10-17', 'FULL_DAY', 'FULL_DAY', 'FULL_DAY',
    3.00, 'Nghỉ phép để tổ chức đám cưới', 'APPROVED', 'COMPLETED', TRUE,
    '2024-10-01 10:00:00', '2024-10-05 11:00:00', '2024-10-01 10:30:00', '2024-10-05 11:00:00'
FROM leave_types WHERE code = 'SPECIAL_LEAVE';

-- Đơn 10: Nhân viên IT - Nghỉ không lương đang chờ
INSERT INTO leave_requests (
    employee_id, employee_name, manager_id, hr_id, leave_type_id,
    start_date, end_date, leave_unit, start_session, end_session,
    total_days, reason, status, approval_stage, final_approved,
    created_at, updated_at, submitted_at
)
SELECT 
    19, 'Ngô Văn An', 10, 5, id,
    '2025-02-01', '2025-02-05', 'FULL_DAY', 'FULL_DAY', 'FULL_DAY',
    5.00, 'Nghỉ không lương để xử lý công việc gia đình', 'PENDING', 'WAITING_MANAGER', FALSE,
    '2024-12-20 09:00:00', '2024-12-20 09:00:00', '2024-12-20 09:30:00'
FROM leave_types WHERE code = 'UNPAID_LEAVE';

-- ========================================
-- 7. LEAVE APPROVAL HISTORIES
-- ========================================

-- Lịch sử cho đơn 1 (APPROVED hoàn toàn)
INSERT INTO leave_approval_histories (leave_request_id, actor_id, actor_role, action, stage, note, action_at)
VALUES
(1, 17, 'EMPLOYEE', 'CREATED_DRAFT', 'NONE', 'Tạo đơn xin nghỉ phép', '2024-03-01 09:00:00'),
(1, 17, 'EMPLOYEE', 'SUBMITTED', 'WAITING_MANAGER', 'Gửi đơn xin nghỉ phép', '2024-03-01 09:30:00'),
(1, 10, 'MANAGER', 'APPROVED', 'WAITING_HR', 'Đồng ý cho nghỉ phép. Team đã sắp xếp người thay thế.', '2024-03-03 10:00:00'),
(1, 4, 'HR', 'APPROVED', 'COMPLETED', 'Xác nhận duyệt nghỉ phép. Còn đủ số ngày phép.', '2024-03-05 14:30:00');

-- Lịch sử cho đơn 2 (Đang chờ Manager)
INSERT INTO leave_approval_histories (leave_request_id, actor_id, actor_role, action, stage, note, action_at)
VALUES
(2, 38, 'EMPLOYEE', 'CREATED_DRAFT', 'NONE', 'Tạo đơn xin nghỉ phép', '2024-12-10 10:00:00'),
(2, 38, 'EMPLOYEE', 'SUBMITTED', 'WAITING_MANAGER', 'Gửi đơn xin nghỉ phép', '2024-12-10 10:30:00');

-- Lịch sử cho đơn 3 (Đang chờ HR)
INSERT INTO leave_approval_histories (leave_request_id, actor_id, actor_role, action, stage, note, action_at)
VALUES
(3, 47, 'EMPLOYEE', 'CREATED_DRAFT', 'NONE', 'Tạo đơn xin nghỉ phép', '2024-12-05 08:00:00'),
(3, 47, 'EMPLOYEE', 'SUBMITTED', 'WAITING_MANAGER', 'Gửi đơn xin nghỉ phép', '2024-12-05 08:30:00'),
(3, 45, 'MANAGER', 'APPROVED', 'WAITING_HR', 'Đồng ý. Nhân viên có thể nghỉ trong thời gian này.', '2024-12-15 16:00:00');

-- Lịch sử cho đơn 4 (REJECTED)
INSERT INTO leave_approval_histories (leave_request_id, actor_id, actor_role, action, stage, note, action_at)
VALUES
(4, 28, 'EMPLOYEE', 'CREATED_DRAFT', 'NONE', 'Tạo đơn xin nghỉ phép', '2024-12-01 11:00:00'),
(4, 28, 'EMPLOYEE', 'SUBMITTED', 'WAITING_MANAGER', 'Gửi đơn xin nghỉ phép', '2024-12-01 11:30:00'),
(4, 25, 'MANAGER', 'REJECTED', 'COMPLETED', 'Cuối năm là thời điểm chốt sổ sách, cần tất cả nhân viên kế toán có mặt.', '2024-12-08 09:00:00');

-- Lịch sử cho đơn 5 (DRAFT)
INSERT INTO leave_approval_histories (leave_request_id, actor_id, actor_role, action, stage, note, action_at)
VALUES
(5, 60, 'EMPLOYEE', 'CREATED_DRAFT', 'NONE', 'Tạo đơn xin nghỉ phép', '2024-12-18 14:00:00');

-- Lịch sử cho đơn 6 (Sick leave - APPROVED)
INSERT INTO leave_approval_histories (leave_request_id, actor_id, actor_role, action, stage, note, action_at)
VALUES
(6, 1, 'EMPLOYEE', 'CREATED_DRAFT', 'NONE', 'Tạo đơn nghỉ ốm', '2024-06-10 07:00:00'),
(6, 1, 'EMPLOYEE', 'SUBMITTED', 'WAITING_HR', 'Gửi đơn nghỉ ốm khẩn cấp', '2024-06-10 07:30:00'),
(6, 4, 'HR', 'APPROVED', 'COMPLETED', 'Xác nhận nghỉ ốm. Đã có giấy khám bệnh. Chúc mau khỏe.', '2024-06-10 14:00:00');

-- Lịch sử cho đơn 7 (Half day - APPROVED)
INSERT INTO leave_approval_histories (leave_request_id, actor_id, actor_role, action, stage, note, action_at)
VALUES
(7, 6, 'EMPLOYEE', 'CREATED_DRAFT', 'NONE', 'Tạo đơn xin nghỉ nửa ngày', '2024-08-10 08:00:00'),
(7, 6, 'EMPLOYEE', 'SUBMITTED', 'WAITING_MANAGER', 'Gửi đơn xin nghỉ nửa ngày', '2024-08-10 08:15:00'),
(7, 4, 'MANAGER', 'APPROVED', 'WAITING_HR', 'Đồng ý cho nghỉ buổi chiều.', '2024-08-11 14:00:00'),
(7, 5, 'HR', 'APPROVED', 'COMPLETED', 'Xác nhận duyệt nghỉ nửa ngày.', '2024-08-12 09:00:00');

-- Lịch sử cho đơn 8 (CANCELLED)
INSERT INTO leave_approval_histories (leave_request_id, actor_id, actor_role, action, stage, note, action_at)
VALUES
(8, 53, 'EMPLOYEE', 'CREATED_DRAFT', 'NONE', 'Tạo đơn xin nghỉ phép', '2024-09-01 09:00:00'),
(8, 53, 'EMPLOYEE', 'SUBMITTED', 'WAITING_MANAGER', 'Gửi đơn xin nghỉ phép', '2024-09-01 09:30:00'),
(8, 53, 'EMPLOYEE', 'CANCELLED', 'NONE', 'Kế hoạch thay đổi, không cần nghỉ phép nữa', '2024-09-05 08:00:00');

-- Lịch sử cho đơn 9 (Special leave - APPROVED)
INSERT INTO leave_approval_histories (leave_request_id, actor_id, actor_role, action, stage, note, action_at)
VALUES
(9, 40, 'EMPLOYEE', 'CREATED_DRAFT', 'NONE', 'Tạo đơn xin nghỉ đặc biệt (cưới)', '2024-10-01 10:00:00'),
(9, 40, 'EMPLOYEE', 'SUBMITTED', 'WAITING_MANAGER', 'Gửi đơn xin nghỉ đặc biệt', '2024-10-01 10:30:00'),
(9, 33, 'MANAGER', 'APPROVED', 'WAITING_HR', 'Chúc mừng nhân viên. Đồng ý cho nghỉ.', '2024-10-03 09:00:00'),
(9, 4, 'HR', 'APPROVED', 'COMPLETED', 'Xác nhận nghỉ đặc biệt theo chế độ. Chúc hạnh phúc!', '2024-10-05 11:00:00');

-- Lịch sử cho đơn 10 (Unpaid leave - PENDING)
INSERT INTO leave_approval_histories (leave_request_id, actor_id, actor_role, action, stage, note, action_at)
VALUES
(10, 19, 'EMPLOYEE', 'CREATED_DRAFT', 'NONE', 'Tạo đơn xin nghỉ không lương', '2024-12-20 09:00:00'),
(10, 19, 'EMPLOYEE', 'SUBMITTED', 'WAITING_MANAGER', 'Gửi đơn xin nghỉ không lương', '2024-12-20 09:30:00');

-- ========================================
-- 8. LEAVE ATTACHMENTS
-- ========================================

INSERT INTO leave_attachments (leave_request_id, file_name, file_url, file_type, file_size)
VALUES
-- Đính kèm cho đơn nghỉ ốm của CEO (đơn 6)
(6, 'giay_kham_benh_ceo_20240610.pdf', '/uploads/leave/2024/06/giay_kham_benh_ceo_20240610.pdf', 'application/pdf', 385000),

-- Đính kèm cho đơn nghỉ đặc biệt (đơn 9)
(9, 'giay_chung_nhan_ket_hon.pdf', '/uploads/leave/2024/10/giay_chung_nhan_ket_hon.pdf', 'application/pdf', 542000),
(9, 'thiep_moi_dam_cuoi.jpg', '/uploads/leave/2024/10/thiep_moi_dam_cuoi.jpg', 'image/jpeg', 892000);

-- ========================================
-- 9. LEAVE BALANCES năm 2025
-- ========================================

-- Annual Leave cho năm 2025 (carry forward từ 2024)
INSERT INTO leave_balances (employee_id, leave_type_id, year, allocated_days, used_days, pending_days, carry_forward_days, encashed_days)
SELECT e.id, lt.id, 2025, 
    CASE 
        WHEN YEAR(e.hire_date) <= 2015 THEN 19.00  -- >= 10 năm
        WHEN YEAR(e.hire_date) <= 2017 THEN 17.00  -- >= 7 năm
        WHEN YEAR(e.hire_date) <= 2019 THEN 14.00  -- >= 5 năm
        WHEN YEAR(e.hire_date) <= 2021 THEN 13.00  -- >= 3 năm
        ELSE 12.00
    END,
    0.00, 0.00, 
    ROUND(RAND() * 3, 1),  -- Carry forward ngẫu nhiên 0-3 ngày
    0.00
FROM employees e
CROSS JOIN leave_types lt
WHERE e.id BETWEEN 1 AND 61
  AND lt.code = 'ANNUAL_LEAVE';

-- Sick Leave cho năm 2025
INSERT INTO leave_balances (employee_id, leave_type_id, year, allocated_days, used_days, pending_days, carry_forward_days, encashed_days)
SELECT e.id, lt.id, 2025, 30.00, 0.00, 0.00, 0.00, 0.00
FROM employees e
CROSS JOIN leave_types lt
WHERE e.id BETWEEN 1 AND 61
  AND lt.code = 'SICK_LEAVE';

-- ========================================
-- END OF SEED DATA FOR LEAVE MANAGEMENT
-- ========================================