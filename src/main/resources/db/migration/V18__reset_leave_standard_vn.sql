-- ========================================
-- V18__reset_leave_standard_vn.sql
-- Reset leave module theo chuẩn mới
-- 4 loại nghỉ + Holiday 2026 + Demo data 2026
-- ========================================

-- ========================================
-- 1. RESET DỮ LIỆU CŨ
-- ========================================
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM leave_attachments;
DELETE FROM leave_approval_histories;
DELETE FROM leave_requests;
DELETE FROM leave_balances;
DELETE FROM leave_type_seniority_rules;
DELETE FROM leave_types;

SET FOREIGN_KEY_CHECKS = 1;

ALTER TABLE leave_attachments AUTO_INCREMENT = 1;
ALTER TABLE leave_approval_histories AUTO_INCREMENT = 1;
ALTER TABLE leave_requests AUTO_INCREMENT = 1;
ALTER TABLE leave_balances AUTO_INCREMENT = 1;
ALTER TABLE leave_type_seniority_rules AUTO_INCREMENT = 1;
ALTER TABLE leave_types AUTO_INCREMENT = 1;

-- ========================================
-- 2. SEED 4 LOẠI NGHỈ MỚI
-- ========================================
INSERT INTO leave_types (
    code, name, max_days_per_year, default_days_per_year,
    paid_leave, requires_approval, requires_document, active,
    deduct_balance, deduct_from_annual_leave_balance, auto_approve,
    allow_carry_forward, allow_encashment, counts_in_attendance,
    counts_in_company_payroll, deduct_salary, social_insurance_paid,
    increase_by_seniority
) VALUES
-- ID = 1
(
    'ANNUAL_LEAVE', 'Nghỉ phép năm', 12, 12,
    TRUE, TRUE, FALSE, TRUE,
    TRUE, FALSE, FALSE,
    TRUE, FALSE, TRUE,
    TRUE, FALSE, TRUE,
    FALSE
),
-- ID = 2
(
    'SICK_LEAVE', 'Nghỉ ốm', 3, 3,
    TRUE, TRUE, TRUE, TRUE,
    TRUE, FALSE, FALSE,
    FALSE, FALSE, TRUE,
    TRUE, FALSE, TRUE,
    FALSE
),
-- ID = 3
(
    'MATERNITY_LEAVE', 'Nghỉ thai sản', 180, 180,
    TRUE, TRUE, TRUE, TRUE,
    FALSE, FALSE, FALSE,
    FALSE, FALSE, TRUE,
    TRUE, FALSE, TRUE,
    FALSE
),
-- ID = 4
(
    'SPECIAL_LEAVE', 'Nghỉ đặc biệt', 3, 3,
    TRUE, TRUE, TRUE, TRUE,
    FALSE, FALSE, FALSE,
    FALSE, FALSE, TRUE,
    TRUE, FALSE, TRUE,
    FALSE
);

-- ========================================
-- 3. HOLIDAYS 2026
-- Giữ lại holiday 2024/2025 cũ, thêm 2026
-- ========================================
INSERT INTO holidays (holiday_date, name, active, type) VALUES
('2026-01-01', 'Tết Dương lịch', TRUE, 'NATIONAL'),
('2026-02-16', 'Tết Nguyên đán - Ngày nghỉ', TRUE, 'LUNAR_NEW_YEAR'),
('2026-02-17', 'Tết Nguyên đán - Mùng 1', TRUE, 'LUNAR_NEW_YEAR'),
('2026-02-18', 'Tết Nguyên đán - Mùng 2', TRUE, 'LUNAR_NEW_YEAR'),
('2026-02-19', 'Tết Nguyên đán - Mùng 3', TRUE, 'LUNAR_NEW_YEAR'),
('2026-02-20', 'Tết Nguyên đán - Ngày nghỉ', TRUE, 'LUNAR_NEW_YEAR'),
('2026-04-26', 'Giỗ Tổ Hùng Vương (10/3 Âm lịch)', TRUE, 'NATIONAL'),
('2026-04-27', 'Nghỉ bù Giỗ Tổ Hùng Vương', TRUE, 'COMPENSATORY'),
('2026-04-30', 'Ngày Giải phóng miền Nam', TRUE, 'NATIONAL'),
('2026-05-01', 'Ngày Quốc tế Lao động', TRUE, 'NATIONAL'),
('2026-09-01', 'Ngày nghỉ liền kề Quốc khánh', TRUE, 'COMPENSATORY'),
('2026-09-02', 'Ngày Quốc khánh', TRUE, 'NATIONAL')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    active = VALUES(active),
    type = VALUES(type);

-- ========================================
-- 4. LEAVE BALANCES 2026
-- Tất cả NV id 1..61
-- Maternity chỉ cấp cho FEMALE
-- ========================================
INSERT INTO leave_balances (
    employee_id, leave_type_id, year,
    allocated_days, used_days, pending_days,
    carry_forward_days, encashed_days
)
SELECT
    e.id,
    lt.id,
    2026,
    CASE
        WHEN lt.code = 'ANNUAL_LEAVE'   THEN 12.00
        WHEN lt.code = 'SICK_LEAVE'     THEN 3.00
        WHEN lt.code = 'SPECIAL_LEAVE'  THEN 3.00
        WHEN lt.code = 'MATERNITY_LEAVE'
             AND UPPER(COALESCE(e.gender, '')) = 'FEMALE' THEN 180.00
        WHEN lt.code = 'MATERNITY_LEAVE' THEN 0.00
    END,
    0.00, 0.00, 0.00, 0.00
FROM employees e
JOIN leave_types lt
  ON lt.code IN ('ANNUAL_LEAVE', 'SICK_LEAVE', 'MATERNITY_LEAVE', 'SPECIAL_LEAVE')
WHERE e.id BETWEEN 1 AND 61;

-- ========================================
-- 5. LEAVE REQUESTS DEMO 2026
-- HR Head: employee có dept_id=2, role_in_dept=HEAD
-- ========================================

-- Lấy HR Head ID
SET @HR_HEAD_ID := (
    SELECT e.id FROM employees e
    WHERE e.dept_id = 2
      AND UPPER(e.role_in_dept) = 'HEAD'
      AND COALESCE(e.is_deleted, 0) = 0
    LIMIT 1
);

-- R1: APPROVED - NV IT nghỉ phép năm
INSERT INTO leave_requests (
    employee_id, employee_name, manager_id, hr_id, leave_type_id,
    start_date, end_date, leave_unit, start_session, end_session,
    total_days, reason, status, approval_stage, final_approved,
    created_at, updated_at, submitted_at, approved_at
)
SELECT e.id, e.full_name, 10, @HR_HEAD_ID, lt.id,
    '2026-03-16', '2026-03-18', 'FULL_DAY', 'FULL_DAY', 'FULL_DAY',
    3.00, 'Nghỉ phép đi du lịch cùng gia đình', 'APPROVED', 'COMPLETED', TRUE,
    '2026-03-01 09:00:00', '2026-03-05 14:30:00', '2026-03-01 09:30:00', '2026-03-05 14:30:00'
FROM employees e JOIN leave_types lt ON lt.code = 'ANNUAL_LEAVE'
WHERE e.id = 17;

-- R2: WAITING_MANAGER - NV Sales nghỉ phép năm
INSERT INTO leave_requests (
    employee_id, employee_name, manager_id, hr_id, leave_type_id,
    start_date, end_date, leave_unit, start_session, end_session,
    total_days, reason, status, approval_stage, final_approved,
    created_at, updated_at, submitted_at
)
SELECT e.id, e.full_name, 33, @HR_HEAD_ID, lt.id,
    '2026-05-12', '2026-05-14', 'FULL_DAY', 'FULL_DAY', 'FULL_DAY',
    3.00, 'Nghỉ phép về quê giải quyết việc gia đình', 'PENDING', 'WAITING_MANAGER', FALSE,
    '2026-05-01 10:00:00', '2026-05-01 10:00:00', '2026-05-01 10:30:00'
FROM employees e JOIN leave_types lt ON lt.code = 'ANNUAL_LEAVE'
WHERE e.id = 38;

-- R3: WAITING_HR - NV Marketing nghỉ phép năm
INSERT INTO leave_requests (
    employee_id, employee_name, manager_id, hr_id, leave_type_id,
    start_date, end_date, leave_unit, start_session, end_session,
    total_days, reason, status, approval_stage, final_approved,
    created_at, updated_at, submitted_at
)
SELECT e.id, e.full_name, 45, @HR_HEAD_ID, lt.id,
    '2026-06-20', '2026-06-22', 'FULL_DAY', 'FULL_DAY', 'FULL_DAY',
    3.00, 'Nghỉ phép cá nhân', 'PENDING', 'WAITING_HR', FALSE,
    '2026-06-05 08:00:00', '2026-06-10 16:00:00', '2026-06-05 08:30:00'
FROM employees e JOIN leave_types lt ON lt.code = 'ANNUAL_LEAVE'
WHERE e.id = 47;

-- R4: REJECTED - NV Finance
INSERT INTO leave_requests (
    employee_id, employee_name, manager_id, hr_id, leave_type_id,
    start_date, end_date, leave_unit, start_session, end_session,
    total_days, reason, status, approval_stage, reject_reason, final_approved,
    created_at, updated_at, submitted_at
)
SELECT e.id, e.full_name, 25, @HR_HEAD_ID, lt.id,
    '2026-04-28', '2026-04-30', 'FULL_DAY', 'FULL_DAY', 'FULL_DAY',
    3.00, 'Nghỉ phép xử lý việc riêng', 'REJECTED', 'COMPLETED',
    'Cuối tháng là giai đoạn quyết toán, bộ phận chưa sắp xếp được người thay thế.', FALSE,
    '2026-04-10 11:00:00', '2026-04-12 09:00:00', '2026-04-10 11:30:00'
FROM employees e JOIN leave_types lt ON lt.code = 'ANNUAL_LEAVE'
WHERE e.id = 28;

-- R5: DRAFT - NV R&D
INSERT INTO leave_requests (
    employee_id, employee_name, manager_id, hr_id, leave_type_id,
    start_date, end_date, leave_unit, start_session, end_session,
    total_days, reason, status, approval_stage, final_approved,
    created_at, updated_at
)
SELECT e.id, e.full_name, 58, @HR_HEAD_ID, lt.id,
    '2026-07-15', '2026-07-16', 'FULL_DAY', 'FULL_DAY', 'FULL_DAY',
    2.00, 'Dự kiến nghỉ phép giữa tháng 7', 'DRAFT', 'NONE', FALSE,
    '2026-07-01 14:00:00', '2026-07-01 14:00:00'
FROM employees e JOIN leave_types lt ON lt.code = 'ANNUAL_LEAVE'
WHERE e.id = 60;

-- R6: SICK APPROVED - CEO nghỉ ốm
INSERT INTO leave_requests (
    employee_id, employee_name, manager_id, hr_id, leave_type_id,
    start_date, end_date, leave_unit, start_session, end_session,
    total_days, reason, status, approval_stage, final_approved,
    created_at, updated_at, submitted_at, approved_at
)
SELECT e.id, e.full_name, NULL, @HR_HEAD_ID, lt.id,
    '2026-02-10', '2026-02-11', 'FULL_DAY', 'FULL_DAY', 'FULL_DAY',
    2.00, 'Nghỉ ốm do sốt, có giấy khám bệnh', 'APPROVED', 'COMPLETED', TRUE,
    '2026-02-10 07:00:00', '2026-02-10 14:00:00', '2026-02-10 07:30:00', '2026-02-10 14:00:00'
FROM employees e JOIN leave_types lt ON lt.code = 'SICK_LEAVE'
WHERE e.id = 1;

-- R7: HALF_DAY APPROVED - NV HR
INSERT INTO leave_requests (
    employee_id, employee_name, manager_id, hr_id, leave_type_id,
    start_date, end_date, leave_unit, start_session, end_session,
    total_days, reason, status, approval_stage, final_approved,
    created_at, updated_at, submitted_at, approved_at
)
SELECT e.id, e.full_name, 5, @HR_HEAD_ID, lt.id,
    '2026-08-15', '2026-08-15', 'HALF_DAY', 'AFTERNOON', 'AFTERNOON',
    0.50, 'Nghỉ buổi chiều để đưa người nhà đi khám', 'APPROVED', 'COMPLETED', TRUE,
    '2026-08-10 08:00:00', '2026-08-12 09:00:00', '2026-08-10 08:15:00', '2026-08-12 09:00:00'
FROM employees e JOIN leave_types lt ON lt.code = 'ANNUAL_LEAVE'
WHERE e.id = 7;

-- R8: CANCELLED - NV Ops
INSERT INTO leave_requests (
    employee_id, employee_name, manager_id, hr_id, leave_type_id,
    start_date, end_date, leave_unit, start_session, end_session,
    total_days, reason, status, approval_stage, cancel_reason, final_approved,
    created_at, updated_at, submitted_at, cancelled_at
)
SELECT e.id, e.full_name, 51, @HR_HEAD_ID, lt.id,
    '2026-09-10', '2026-09-12', 'FULL_DAY', 'FULL_DAY', 'FULL_DAY',
    3.00, 'Nghỉ phép cá nhân', 'CANCELLED', 'NONE',
    'Đã thay đổi kế hoạch nên không cần nghỉ nữa', FALSE,
    '2026-09-01 09:00:00', '2026-09-05 08:00:00', '2026-09-01 09:30:00', '2026-09-05 08:00:00'
FROM employees e JOIN leave_types lt ON lt.code = 'ANNUAL_LEAVE'
WHERE e.id = 53;

-- R9: SPECIAL APPROVED - NV Sales đám cưới
INSERT INTO leave_requests (
    employee_id, employee_name, manager_id, hr_id, leave_type_id,
    start_date, end_date, leave_unit, start_session, end_session,
    total_days, reason, status, approval_stage, final_approved,
    created_at, updated_at, submitted_at, approved_at
)
SELECT e.id, e.full_name, 33, @HR_HEAD_ID, lt.id,
    '2026-10-15', '2026-10-17', 'FULL_DAY', 'FULL_DAY', 'FULL_DAY',
    3.00, 'Nghỉ đặc biệt để tổ chức đám cưới', 'APPROVED', 'COMPLETED', TRUE,
    '2026-10-01 10:00:00', '2026-10-05 11:00:00', '2026-10-01 10:30:00', '2026-10-05 11:00:00'
FROM employees e JOIN leave_types lt ON lt.code = 'SPECIAL_LEAVE'
WHERE e.id = 40;

-- R10: MATERNITY WAITING_HR - NV Marketing
INSERT INTO leave_requests (
    employee_id, employee_name, manager_id, hr_id, leave_type_id,
    start_date, end_date, leave_unit, start_session, end_session,
    total_days, reason, status, approval_stage, final_approved,
    created_at, updated_at, submitted_at
)
SELECT e.id, e.full_name, 45, @HR_HEAD_ID, lt.id,
    '2026-11-01', '2027-04-29', 'FULL_DAY', 'FULL_DAY', 'FULL_DAY',
    180.00, 'Nghỉ thai sản theo chế độ', 'PENDING', 'WAITING_HR', FALSE,
    '2026-10-15 09:00:00', '2026-10-20 15:30:00', '2026-10-15 09:30:00'
FROM employees e JOIN leave_types lt ON lt.code = 'MATERNITY_LEAVE'
WHERE e.id = 46;

-- ========================================
-- 6. APPROVAL HISTORIES
-- leave_request id sẽ là 1..10
-- ========================================
INSERT INTO leave_approval_histories
    (leave_request_id, actor_id, actor_role, action, stage, note, action_at)
VALUES
-- R1: APPROVED hoàn toàn
(1, 17, 'EMPLOYEE', 'CREATED_DRAFT', 'NONE', 'Tạo đơn xin nghỉ phép', '2026-03-01 09:00:00'),
(1, 17, 'EMPLOYEE', 'SUBMITTED', 'WAITING_MANAGER', 'Gửi đơn xin nghỉ phép', '2026-03-01 09:30:00'),
(1, 10, 'MANAGER', 'APPROVED', 'WAITING_HR', 'Đồng ý cho nghỉ phép, team đã sắp xếp thay thế.', '2026-03-03 10:00:00'),
(1, @HR_HEAD_ID, 'HR', 'APPROVED', 'COMPLETED', 'Xác nhận duyệt nghỉ phép.', '2026-03-05 14:30:00'),

-- R2: Đang chờ Manager
(2, 38, 'EMPLOYEE', 'CREATED_DRAFT', 'NONE', 'Tạo đơn xin nghỉ phép', '2026-05-01 10:00:00'),
(2, 38, 'EMPLOYEE', 'SUBMITTED', 'WAITING_MANAGER', 'Gửi đơn xin nghỉ phép', '2026-05-01 10:30:00'),

-- R3: Đang chờ HR
(3, 47, 'EMPLOYEE', 'CREATED_DRAFT', 'NONE', 'Tạo đơn xin nghỉ phép', '2026-06-05 08:00:00'),
(3, 47, 'EMPLOYEE', 'SUBMITTED', 'WAITING_MANAGER', 'Gửi đơn xin nghỉ phép', '2026-06-05 08:30:00'),
(3, 45, 'MANAGER', 'APPROVED', 'WAITING_HR', 'Đồng ý cho nghỉ phép.', '2026-06-10 16:00:00'),

-- R4: REJECTED
(4, 28, 'EMPLOYEE', 'CREATED_DRAFT', 'NONE', 'Tạo đơn xin nghỉ phép', '2026-04-10 11:00:00'),
(4, 28, 'EMPLOYEE', 'SUBMITTED', 'WAITING_MANAGER', 'Gửi đơn xin nghỉ phép', '2026-04-10 11:30:00'),
(4, 25, 'MANAGER', 'REJECTED', 'COMPLETED', 'Không thể sắp xếp người thay thế trong giai đoạn quyết toán.', '2026-04-12 09:00:00'),

-- R5: DRAFT
(5, 60, 'EMPLOYEE', 'CREATED_DRAFT', 'NONE', 'Tạo đơn xin nghỉ phép', '2026-07-01 14:00:00'),

-- R6: SICK APPROVED
(6, 1, 'EMPLOYEE', 'CREATED_DRAFT', 'NONE', 'Tạo đơn nghỉ ốm', '2026-02-10 07:00:00'),
(6, 1, 'EMPLOYEE', 'SUBMITTED', 'WAITING_HR', 'Gửi đơn nghỉ ốm khẩn cấp', '2026-02-10 07:30:00'),
(6, @HR_HEAD_ID, 'HR', 'APPROVED', 'COMPLETED', 'Xác nhận nghỉ ốm, đã có giấy khám bệnh.', '2026-02-10 14:00:00'),

-- R7: HALF_DAY APPROVED
(7, 7, 'EMPLOYEE', 'CREATED_DRAFT', 'NONE', 'Tạo đơn xin nghỉ nửa ngày', '2026-08-10 08:00:00'),
(7, 7, 'EMPLOYEE', 'SUBMITTED', 'WAITING_MANAGER', 'Gửi đơn xin nghỉ nửa ngày', '2026-08-10 08:15:00'),
(7, 5, 'MANAGER', 'APPROVED', 'WAITING_HR', 'Đồng ý cho nghỉ buổi chiều.', '2026-08-11 14:00:00'),
(7, @HR_HEAD_ID, 'HR', 'APPROVED', 'COMPLETED', 'Xác nhận duyệt nghỉ nửa ngày.', '2026-08-12 09:00:00'),

-- R8: CANCELLED
(8, 53, 'EMPLOYEE', 'CREATED_DRAFT', 'NONE', 'Tạo đơn xin nghỉ phép', '2026-09-01 09:00:00'),
(8, 53, 'EMPLOYEE', 'SUBMITTED', 'WAITING_MANAGER', 'Gửi đơn xin nghỉ phép', '2026-09-01 09:30:00'),
(8, 53, 'EMPLOYEE', 'CANCELLED', 'NONE', 'Kế hoạch thay đổi, không cần nghỉ phép nữa', '2026-09-05 08:00:00'),

-- R9: SPECIAL APPROVED
(9, 40, 'EMPLOYEE', 'CREATED_DRAFT', 'NONE', 'Tạo đơn xin nghỉ đặc biệt', '2026-10-01 10:00:00'),
(9, 40, 'EMPLOYEE', 'SUBMITTED', 'WAITING_MANAGER', 'Gửi đơn xin nghỉ đặc biệt', '2026-10-01 10:30:00'),
(9, 33, 'MANAGER', 'APPROVED', 'WAITING_HR', 'Đồng ý cho nghỉ.', '2026-10-03 09:00:00'),
(9, @HR_HEAD_ID, 'HR', 'APPROVED', 'COMPLETED', 'Xác nhận nghỉ đặc biệt theo chế độ.', '2026-10-05 11:00:00'),

-- R10: MATERNITY WAITING_HR
(10, 46, 'EMPLOYEE', 'CREATED_DRAFT', 'NONE', 'Tạo đơn nghỉ thai sản', '2026-10-15 09:00:00'),
(10, 46, 'EMPLOYEE', 'SUBMITTED', 'WAITING_MANAGER', 'Gửi đơn nghỉ thai sản', '2026-10-15 09:30:00'),
(10, 45, 'MANAGER', 'APPROVED', 'WAITING_HR', 'Đã xác nhận bàn giao công việc.', '2026-10-20 15:30:00');

-- ========================================
-- 7. ATTACHMENTS DEMO
-- ========================================
INSERT INTO leave_attachments (leave_request_id, file_name, file_url, file_type, file_size)
VALUES
(6, 'giay_kham_benh_20260210.pdf', '/uploads/leave/2026/02/giay_kham_benh_20260210.pdf', 'application/pdf', 385000),
(9, 'giay_chung_nhan_ket_hon.pdf', '/uploads/leave/2026/10/giay_chung_nhan_ket_hon.pdf', 'application/pdf', 542000);

-- ========================================
-- 8. CẬP NHẬT BALANCE CHO CÁC REQUEST ĐÃ APPROVED
-- ========================================

-- R1: emp 17 dùng 3 ngày Annual
UPDATE leave_balances
SET used_days = used_days + 3.00
WHERE employee_id = 17
  AND leave_type_id = (SELECT id FROM leave_types WHERE code = 'ANNUAL_LEAVE')
  AND year = 2026;

-- R6: emp 1 dùng 2 ngày Sick
UPDATE leave_balances
SET used_days = used_days + 2.00
WHERE employee_id = 1
  AND leave_type_id = (SELECT id FROM leave_types WHERE code = 'SICK_LEAVE')
  AND year = 2026;

-- R7: emp 7 dùng 0.5 ngày Annual
UPDATE leave_balances
SET used_days = used_days + 0.50
WHERE employee_id = 7
  AND leave_type_id = (SELECT id FROM leave_types WHERE code = 'ANNUAL_LEAVE')
  AND year = 2026;

-- R9: emp 40 dùng 3 ngày Special
UPDATE leave_balances
SET used_days = used_days + 3.00
WHERE employee_id = 40
  AND leave_type_id = (SELECT id FROM leave_types WHERE code = 'SPECIAL_LEAVE')
  AND year = 2026;

-- Pending: R2 emp 38 pending 3 ngày
UPDATE leave_balances
SET pending_days = pending_days + 3.00
WHERE employee_id = 38
  AND leave_type_id = (SELECT id FROM leave_types WHERE code = 'ANNUAL_LEAVE')
  AND year = 2026;

-- Pending: R3 emp 47 pending 3 ngày
UPDATE leave_balances
SET pending_days = pending_days + 3.00
WHERE employee_id = 47
  AND leave_type_id = (SELECT id FROM leave_types WHERE code = 'ANNUAL_LEAVE')
  AND year = 2026;

-- Pending: R10 emp 46 pending 180 ngày Maternity
UPDATE leave_balances
SET pending_days = pending_days + 180.00
WHERE employee_id = 46
  AND leave_type_id = (SELECT id FROM leave_types WHERE code = 'MATERNITY_LEAVE')
  AND year = 2026;

-- ========================================
-- HOÀN TẤT
-- ========================================