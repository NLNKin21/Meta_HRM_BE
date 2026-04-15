-- ============================================
-- V15__seed_attendance_data_full.sql
--
-- Seed data cho Attendance System
-- Phù hợp với 61 nhân viên active (emp 1-61)
-- Ngày hiện tại: 12/04/2026 (Chủ Nhật)
-- 
-- Ngày làm việc tháng 4/2026:
--   01(Wed) 02(Thu) 03(Fri)
--   06(Mon) 07(Tue) 08(Wed) 09(Thu) 10(Fri)
--   11(Sat) - chỉ sales ca KD
--   12(Sun) - hôm nay, vài IT hotfix
-- ============================================

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- CLEAR OLD DATA (V13)
-- ============================================
DELETE FROM attendance_anomalies;
DELETE FROM attendance_records;
DELETE FROM employee_faces;
DELETE FROM work_locations;
DELETE FROM shifts;

-- Reset shift_id trên employees
UPDATE employees SET shift_id = NULL;

-- ============================================
-- SHIFTS (5 ca)
-- ============================================
INSERT INTO shifts (id, name, code, start_time, end_time, 
    late_threshold, early_leave_threshold, check_in_start_before, check_in_end_after,
    work_days, break_duration, description, color, is_active, is_deleted, created_by,
    created_at, updated_at) VALUES

(1, 'Ca Hành Chính', 'HC', '08:00:00', '17:00:00',
    15, 15, 30, 120,
    '[1,2,3,4,5]', 60,
    'Ca hành chính tiêu chuẩn 8h-17h. Áp dụng: HR, Finance, Marketing.',
    '#2196F3', TRUE, b'0', 1,
    '2026-01-02 08:00:00', '2026-01-02 08:00:00'),

(2, 'Ca Sáng Sớm', 'CS', '07:00:00', '16:00:00',
    10, 15, 30, 120,
    '[1,2,3,4,5]', 60,
    'Ca sáng sớm cho Vận hành: lễ tân, tài xế, bảo vệ.',
    '#4CAF50', TRUE, b'0', 1,
    '2026-01-02 08:05:00', '2026-01-02 08:05:00'),

(3, 'Ca Linh Hoạt IT', 'FLEX', '09:00:00', '18:00:00',
    30, 15, 60, 180,
    '[1,2,3,4,5]', 60,
    'Ca linh hoạt IT/R&D: check-in 8h-10h, trễ 30p vẫn ok.',
    '#FF9800', TRUE, b'0', 1,
    '2026-01-02 08:10:00', '2026-01-02 08:10:00'),

(4, 'Ca Kinh Doanh', 'KD', '08:30:00', '17:30:00',
    15, 30, 30, 120,
    '[1,2,3,4,5,6]', 60,
    'Ca kinh doanh: làm thêm thứ 7 luân phiên.',
    '#E91E63', TRUE, b'0', 1,
    '2026-01-02 08:15:00', '2026-01-02 08:15:00'),

(5, 'Ca Ban Giám Đốc', 'BGD', '08:00:00', '17:00:00',
    30, 30, 60, 180,
    '[1,2,3,4,5]', 60,
    'Ca BGĐ, linh hoạt hơn: trễ 30p, về sớm 30p vẫn ok.',
    '#9C27B0', TRUE, b'0', 1,
    '2026-01-02 08:20:00', '2026-01-02 08:20:00');


-- ============================================
-- WORK LOCATIONS (3 địa điểm TP.HCM)
-- ============================================
INSERT INTO work_locations (id, name, code, address, latitude, longitude, radius,
    description, contact_person, contact_phone, is_active, is_deleted, created_by,
    created_at, updated_at) VALUES

(1, 'Trụ sở chính TechCorp', 'HQ',
    'Tầng 10-12, Tòa nhà Viettel Complex, 285 Cách Mạng Tháng 8, P.12, Q.10, TP.HCM',
    10.77291500, 106.66017800, 150,
    'Trụ sở chính 3 tầng. T10: Sales/Marketing/Ops. T11: IT/R&D. T12: BGĐ/HR/Finance.',
    'Nguyễn Văn Xuân', '0901000051', TRUE, b'0', 1,
    '2026-01-02 08:00:00', '2026-01-02 08:00:00'),

(2, 'Chi nhánh Quận 7', 'Q7',
    'Lầu 5, Crescent Plaza, 105 Tôn Dật Tiên, Phú Mỹ Hưng, Q.7, TP.HCM',
    10.72920100, 106.72188400, 100,
    'Chi nhánh phía Nam - Sales khu vực Nam.',
    'Phạm Văn Tiến', '0901000036', TRUE, b'0', 1,
    '2026-01-02 08:05:00', '2026-01-02 08:05:00'),

(3, 'Văn phòng R&D Thủ Đức', 'TD',
    'Lô E2a-7, Đường D1, Khu CNC, TP.Thủ Đức, TP.HCM',
    10.85587600, 106.78603800, 200,
    'Văn phòng R&D tại Khu Công nghệ cao.',
    'Trần Văn Hảo', '0901000058', TRUE, b'0', 1,
    '2026-01-02 08:10:00', '2026-01-02 08:10:00');


-- ============================================
-- GÁN CA CHO NHÂN VIÊN
-- ============================================
-- Ban Giám đốc (1-3) → Ca BGĐ (5)
UPDATE employees SET shift_id = 5 WHERE id IN (1, 2, 3);

-- Phòng HR (4-9) → Ca HC (1)
UPDATE employees SET shift_id = 1 WHERE id IN (4, 5, 6, 7, 8, 9);

-- Phòng IT (10-24) → Ca Linh Hoạt IT (3)
UPDATE employees SET shift_id = 3 WHERE id BETWEEN 10 AND 24;

-- Phòng Tài chính (25-32) → Ca HC (1)
UPDATE employees SET shift_id = 1 WHERE id BETWEEN 25 AND 32;

-- Phòng Kinh doanh (33-44) → Ca KD (4)
UPDATE employees SET shift_id = 4 WHERE id BETWEEN 33 AND 44;

-- Phòng Marketing (45-50) → Ca HC (1)
UPDATE employees SET shift_id = 1 WHERE id BETWEEN 45 AND 50;

-- Phòng Vận hành (51-57) → Ca Sáng Sớm (2)
UPDATE employees SET shift_id = 2 WHERE id BETWEEN 51 AND 57;

-- Phòng R&D (58-61) → Ca Linh Hoạt IT (3)
UPDATE employees SET shift_id = 3 WHERE id BETWEEN 58 AND 61;

-- Nhân viên nghỉ việc (62-63) → không gán


-- ============================================
-- ATTENDANCE RECORDS: NGÀY 01/04/2026 (THỨ 4)
-- Base day - insert chi tiết từng người
-- 
-- Convention:
--   Location 1 = HQ (hầu hết)
--   Location 2 = Q7 (sales lead south)
--   Location 3 = R&D Thủ Đức
--   
-- Status phân bổ realistic:
--   ~80% PRESENT, ~10% LATE, ~3% EARLY_LEAVE
--   ~5% ABSENT (không insert), ~2% LEAVE
-- ============================================
INSERT INTO attendance_records 
(employee_id, date, shift_id,
 check_in_time, check_out_time,
 check_in_location_id, check_out_location_id,
 check_in_lat, check_in_lng, check_out_lat, check_out_lng,
 check_in_face_match_score, check_out_face_match_score,
 status, work_hours, overtime_hours, late_minutes, early_leave_minutes,
 is_verified, is_approved, approved_by, approved_at,
 is_deleted, created_at, updated_at) VALUES

-- ========== BAN GIÁM ĐỐC (shift 5: 08:00-17:00, threshold 30p) ==========
-- emp 1 - CEO - đi sớm, về muộn
(1, '2026-04-01', 5,
 '2026-04-01 07:55:00', '2026-04-01 17:10:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 98.50, 97.80,
 'PRESENT', 8.25, 0.25, 0, 0,
 b'1', b'1', 1, '2026-04-01 17:15:00',
 b'0', '2026-04-01 07:55:00', '2026-04-01 17:10:00'),

-- emp 2 - CTO - đi hơi muộn (5p, vẫn trong threshold 30p)
(2, '2026-04-01', 5,
 '2026-04-01 08:05:00', '2026-04-01 17:30:00',
 1, 1, 10.77290, 106.66018, 10.77291, 106.66017,
 97.20, 96.50,
 'PRESENT', 8.42, 0.42, 5, 0,
 b'1', b'1', 1, '2026-04-01 17:35:00',
 b'0', '2026-04-01 08:05:00', '2026-04-01 17:30:00'),

-- emp 3 - CFO
(3, '2026-04-01', 5,
 '2026-04-01 08:10:00', '2026-04-01 17:05:00',
 1, 1, 10.77292, 106.66016, 10.77291, 106.66017,
 99.10, 98.30,
 'PRESENT', 7.92, 0.00, 10, 0,
 b'1', b'1', 1, '2026-04-01 17:10:00',
 b'0', '2026-04-01 08:10:00', '2026-04-01 17:05:00'),

-- ========== PHÒNG HR (shift 1: 08:00-17:00, threshold 15p) ==========
-- emp 4 - HR Director - HEAD - luôn đúng giờ
(4, '2026-04-01', 1,
 '2026-04-01 07:50:00', '2026-04-01 17:05:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 98.80, 97.90,
 'PRESENT', 8.25, 0.25, 0, 0,
 b'1', b'1', 5, '2026-04-01 17:10:00',
 b'0', '2026-04-01 07:50:00', '2026-04-01 17:05:00'),

-- emp 5 - HR Deputy
(5, '2026-04-01', 1,
 '2026-04-01 07:58:00', '2026-04-01 17:02:00',
 1, 1, 10.77290, 106.66018, 10.77292, 106.66017,
 97.50, 96.80,
 'PRESENT', 8.07, 0.07, 0, 0,
 b'1', b'1', 5, '2026-04-01 17:05:00',
 b'0', '2026-04-01 07:58:00', '2026-04-01 17:02:00'),

-- emp 6 - HR Recruit - LATE 20p
(6, '2026-04-01', 1,
 '2026-04-01 08:20:00', '2026-04-01 17:00:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 96.30, 95.80,
 'LATE', 7.67, 0.00, 20, 0,
 b'1', b'1', 5, '2026-04-01 17:05:00',
 b'0', '2026-04-01 08:20:00', '2026-04-01 17:00:00'),

-- emp 7 - HR Training - đúng giờ
(7, '2026-04-01', 1,
 '2026-04-01 08:02:00', '2026-04-01 17:03:00',
 1, 1, 10.77292, 106.66016, 10.77291, 106.66018,
 98.10, 97.20,
 'PRESENT', 8.02, 0.02, 2, 0,
 b'1', b'1', 5, '2026-04-01 17:05:00',
 b'0', '2026-04-01 08:02:00', '2026-04-01 17:03:00'),

-- emp 8 - HR Payroll
(8, '2026-04-01', 1,
 '2026-04-01 07:55:00', '2026-04-01 17:00:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 99.00, 98.50,
 'PRESENT', 8.08, 0.08, 0, 0,
 b'1', b'1', 5, '2026-04-01 17:05:00',
 b'0', '2026-04-01 07:55:00', '2026-04-01 17:00:00'),

-- emp 9 - HR Admin - EARLY_LEAVE 15p
(9, '2026-04-01', 1,
 '2026-04-01 08:00:00', '2026-04-01 16:45:00',
 1, 1, 10.77290, 106.66018, 10.77291, 106.66017,
 97.80, 96.90,
 'EARLY_LEAVE', 7.75, 0.00, 0, 15,
 b'1', b'1', 5, '2026-04-01 16:50:00',
 b'0', '2026-04-01 08:00:00', '2026-04-01 16:45:00'),

-- ========== PHÒNG IT (shift 3: 09:00-18:00, threshold 30p) ==========
-- emp 10 - IT Director HEAD
(10, '2026-04-01', 3,
 '2026-04-01 08:50:00', '2026-04-01 18:05:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 98.90, 98.20,
 'PRESENT', 8.25, 0.25, 0, 0,
 b'1', b'1', 11, '2026-04-01 18:10:00',
 b'0', '2026-04-01 08:50:00', '2026-04-01 18:05:00'),

-- emp 11 - IT Deputy
(11, '2026-04-01', 3,
 '2026-04-01 08:55:00', '2026-04-01 18:10:00',
 1, 1, 10.77290, 106.66018, 10.77291, 106.66017,
 97.60, 97.10,
 'PRESENT', 8.25, 0.25, 0, 0,
 b'1', b'1', 11, '2026-04-01 18:15:00',
 b'0', '2026-04-01 08:55:00', '2026-04-01 18:10:00'),

-- emp 12 - Backend Lead
(12, '2026-04-01', 3,
 '2026-04-01 09:00:00', '2026-04-01 18:00:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 99.20, 98.80,
 'PRESENT', 8.00, 0.00, 0, 0,
 b'1', b'1', 11, '2026-04-01 18:05:00',
 b'0', '2026-04-01 09:00:00', '2026-04-01 18:00:00'),

-- emp 13 - Frontend Lead
(13, '2026-04-01', 3,
 '2026-04-01 09:05:00', '2026-04-01 18:15:00',
 1, 1, 10.77292, 106.66016, 10.77291, 106.66018,
 96.80, 96.20,
 'PRESENT', 8.17, 0.17, 5, 0,
 b'1', b'1', 11, '2026-04-01 18:20:00',
 b'0', '2026-04-01 09:05:00', '2026-04-01 18:15:00'),

-- emp 14 - Senior Dev 1 - LATE 35p (quá threshold 30p)
(14, '2026-04-01', 3,
 '2026-04-01 09:35:00', '2026-04-01 18:30:00',
 1, 1, 10.77291, 106.66017, 10.77290, 106.66018,
 97.40, 97.00,
 'LATE', 7.92, 0.00, 35, 0,
 b'1', b'1', 11, '2026-04-01 18:35:00',
 b'0', '2026-04-01 09:35:00', '2026-04-01 18:30:00'),

-- emp 15 - Senior Dev 2 - sớm
(15, '2026-04-01', 3,
 '2026-04-01 08:45:00', '2026-04-01 18:00:00',
 1, 1, 10.77290, 106.66018, 10.77291, 106.66017,
 98.30, 97.70,
 'PRESENT', 8.25, 0.25, 0, 0,
 b'1', b'1', 11, '2026-04-01 18:05:00',
 b'0', '2026-04-01 08:45:00', '2026-04-01 18:00:00'),

-- emp 16 - Senior Dev 3
(16, '2026-04-01', 3,
 '2026-04-01 09:10:00', '2026-04-01 18:20:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 97.90, 97.30,
 'PRESENT', 8.17, 0.17, 10, 0,
 b'1', b'1', 11, '2026-04-01 18:25:00',
 b'0', '2026-04-01 09:10:00', '2026-04-01 18:20:00'),

-- emp 17 - Mid Dev 1
(17, '2026-04-01', 3,
 '2026-04-01 09:02:00', '2026-04-01 18:05:00',
 1, 1, 10.77292, 106.66016, 10.77291, 106.66017,
 98.60, 98.10,
 'PRESENT', 8.05, 0.05, 2, 0,
 b'1', b'1', 11, '2026-04-01 18:10:00',
 b'0', '2026-04-01 09:02:00', '2026-04-01 18:05:00'),

-- emp 18 - Mid Dev 2
(18, '2026-04-01', 3,
 '2026-04-01 08:58:00', '2026-04-01 18:00:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 96.50, 96.00,
 'PRESENT', 8.03, 0.03, 0, 0,
 b'1', b'1', 11, '2026-04-01 18:05:00',
 b'0', '2026-04-01 08:58:00', '2026-04-01 18:00:00'),

-- emp 19 - Junior Dev 1 - ABSENT (không insert record)

-- emp 20 - Junior Dev 2 - EARLY_LEAVE
(20, '2026-04-01', 3,
 '2026-04-01 09:15:00', '2026-04-01 17:50:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 97.10, 96.50,
 'EARLY_LEAVE', 7.58, 0.00, 15, 10,
 b'1', b'1', 11, '2026-04-01 17:55:00',
 b'0', '2026-04-01 09:15:00', '2026-04-01 17:50:00'),

-- emp 21 - DevOps - OT
(21, '2026-04-01', 3,
 '2026-04-01 08:55:00', '2026-04-01 18:30:00',
 1, 1, 10.77290, 106.66018, 10.77291, 106.66017,
 98.70, 98.30,
 'PRESENT', 8.58, 0.58, 0, 0,
 b'1', b'1', 11, '2026-04-01 18:35:00',
 b'0', '2026-04-01 08:55:00', '2026-04-01 18:30:00'),

-- emp 22 - QA Lead
(22, '2026-04-01', 3,
 '2026-04-01 09:00:00', '2026-04-01 18:10:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 98.40, 97.90,
 'PRESENT', 8.17, 0.17, 0, 0,
 b'1', b'1', 11, '2026-04-01 18:15:00',
 b'0', '2026-04-01 09:00:00', '2026-04-01 18:10:00'),

-- emp 23 - QA Engineer
(23, '2026-04-01', 3,
 '2026-04-01 09:08:00', '2026-04-01 18:00:00',
 1, 1, 10.77292, 106.66016, 10.77291, 106.66018,
 97.00, 96.40,
 'PRESENT', 7.87, 0.00, 8, 0,
 b'1', b'1', 11, '2026-04-01 18:05:00',
 b'0', '2026-04-01 09:08:00', '2026-04-01 18:00:00'),

-- emp 24 - IT Intern - về sớm 1h (lịch học)
(24, '2026-04-01', 3,
 '2026-04-01 09:00:00', '2026-04-01 17:00:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 95.80, 95.20,
 'EARLY_LEAVE', 7.00, 0.00, 0, 60,
 b'1', b'1', 11, '2026-04-01 17:05:00',
 b'0', '2026-04-01 09:00:00', '2026-04-01 17:00:00'),

-- ========== PHÒNG TÀI CHÍNH (shift 1: 08:00-17:00, threshold 15p) ==========
-- emp 25 - Finance Director HEAD
(25, '2026-04-01', 1,
 '2026-04-01 07:52:00', '2026-04-01 17:05:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 98.90, 98.30,
 'PRESENT', 8.22, 0.22, 0, 0,
 b'1', b'1', 26, '2026-04-01 17:10:00',
 b'0', '2026-04-01 07:52:00', '2026-04-01 17:05:00'),

-- emp 26 - Finance Deputy
(26, '2026-04-01', 1,
 '2026-04-01 07:55:00', '2026-04-01 17:00:00',
 1, 1, 10.77290, 106.66018, 10.77291, 106.66017,
 97.80, 97.20,
 'PRESENT', 8.08, 0.08, 0, 0,
 b'1', b'1', 26, '2026-04-01 17:05:00',
 b'0', '2026-04-01 07:55:00', '2026-04-01 17:00:00'),

-- emp 27 - Chief Accountant
(27, '2026-04-01', 1,
 '2026-04-01 08:00:00', '2026-04-01 17:10:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 99.30, 98.70,
 'PRESENT', 8.17, 0.17, 0, 0,
 b'1', b'1', 26, '2026-04-01 17:15:00',
 b'0', '2026-04-01 08:00:00', '2026-04-01 17:10:00'),

-- emp 28-29 - Accountants
(28, '2026-04-01', 1,
 '2026-04-01 08:05:00', '2026-04-01 17:00:00',
 1, 1, 10.77292, 106.66016, 10.77291, 106.66017,
 96.90, 96.30,
 'PRESENT', 7.92, 0.00, 5, 0,
 b'1', b'1', 26, '2026-04-01 17:05:00',
 b'0', '2026-04-01 08:05:00', '2026-04-01 17:00:00'),

(29, '2026-04-01', 1,
 '2026-04-01 07:58:00', '2026-04-01 17:05:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 98.20, 97.60,
 'PRESENT', 8.12, 0.12, 0, 0,
 b'1', b'1', 26, '2026-04-01 17:10:00',
 b'0', '2026-04-01 07:58:00', '2026-04-01 17:05:00'),

-- emp 30 - Accountant 3 - LATE 25p
(30, '2026-04-01', 1,
 '2026-04-01 08:25:00', '2026-04-01 17:00:00',
 1, 1, 10.77290, 106.66018, 10.77291, 106.66017,
 97.40, 96.80,
 'LATE', 7.58, 0.00, 25, 0,
 b'1', b'1', 26, '2026-04-01 17:05:00',
 b'0', '2026-04-01 08:25:00', '2026-04-01 17:00:00'),

-- emp 31 - Tax Specialist
(31, '2026-04-01', 1,
 '2026-04-01 08:00:00', '2026-04-01 17:00:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 98.60, 98.00,
 'PRESENT', 8.00, 0.00, 0, 0,
 b'1', b'1', 26, '2026-04-01 17:05:00',
 b'0', '2026-04-01 08:00:00', '2026-04-01 17:00:00'),

-- emp 32 - Cashier
(32, '2026-04-01', 1,
 '2026-04-01 08:03:00', '2026-04-01 17:02:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 97.10, 96.50,
 'PRESENT', 7.98, 0.00, 3, 0,
 b'1', b'1', 26, '2026-04-01 17:05:00',
 b'0', '2026-04-01 08:03:00', '2026-04-01 17:02:00'),

-- ========== PHÒNG KINH DOANH (shift 4: 08:30-17:30, threshold 15p) ==========
-- emp 33 - Sales Director HEAD
(33, '2026-04-01', 4,
 '2026-04-01 08:25:00', '2026-04-01 17:35:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 98.50, 97.90,
 'PRESENT', 8.17, 0.17, 0, 0,
 b'1', b'1', 34, '2026-04-01 17:40:00',
 b'0', '2026-04-01 08:25:00', '2026-04-01 17:35:00'),

-- emp 34 - Sales Deputy
(34, '2026-04-01', 4,
 '2026-04-01 08:28:00', '2026-04-01 17:30:00',
 1, 1, 10.77290, 106.66018, 10.77291, 106.66017,
 97.30, 96.80,
 'PRESENT', 8.03, 0.03, 0, 0,
 b'1', b'1', 34, '2026-04-01 17:35:00',
 b'0', '2026-04-01 08:28:00', '2026-04-01 17:30:00'),

-- emp 35 - Sales Lead North (làm tại Q7)
(35, '2026-04-01', 4,
 '2026-04-01 08:30:00', '2026-04-01 17:40:00',
 2, 2, 10.72920, 106.72188, 10.72920, 106.72188,
 98.80, 98.20,
 'PRESENT', 8.17, 0.17, 0, 0,
 b'1', b'1', 34, '2026-04-01 17:45:00',
 b'0', '2026-04-01 08:30:00', '2026-04-01 17:40:00'),

-- emp 36 - Sales Lead South (Q7)
(36, '2026-04-01', 4,
 '2026-04-01 08:35:00', '2026-04-01 17:30:00',
 2, 2, 10.72921, 106.72187, 10.72920, 106.72188,
 97.60, 97.00,
 'PRESENT', 7.92, 0.00, 5, 0,
 b'1', b'1', 34, '2026-04-01 17:35:00',
 b'0', '2026-04-01 08:35:00', '2026-04-01 17:30:00'),

-- emp 37 - Sales 1 - LATE 20p
(37, '2026-04-01', 4,
 '2026-04-01 08:50:00', '2026-04-01 17:35:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 96.90, 96.30,
 'LATE', 7.75, 0.00, 20, 0,
 b'1', b'1', 34, '2026-04-01 17:40:00',
 b'0', '2026-04-01 08:50:00', '2026-04-01 17:35:00'),

-- emp 38-40 - Sales
(38, '2026-04-01', 4,
 '2026-04-01 08:30:00', '2026-04-01 17:30:00',
 1, 1, 10.77290, 106.66018, 10.77291, 106.66017,
 98.10, 97.50,
 'PRESENT', 8.00, 0.00, 0, 0,
 b'1', b'1', 34, '2026-04-01 17:35:00',
 b'0', '2026-04-01 08:30:00', '2026-04-01 17:30:00'),

(39, '2026-04-01', 4,
 '2026-04-01 08:28:00', '2026-04-01 17:30:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 97.70, 97.10,
 'PRESENT', 8.03, 0.03, 0, 0,
 b'1', b'1', 34, '2026-04-01 17:35:00',
 b'0', '2026-04-01 08:28:00', '2026-04-01 17:30:00'),

(40, '2026-04-01', 4,
 '2026-04-01 08:32:00', '2026-04-01 17:32:00',
 1, 1, 10.77292, 106.66016, 10.77291, 106.66017,
 98.40, 97.80,
 'PRESENT', 8.00, 0.00, 2, 0,
 b'1', b'1', 34, '2026-04-01 17:37:00',
 b'0', '2026-04-01 08:32:00', '2026-04-01 17:32:00'),

-- emp 41 - Sales 5 - LEAVE (nghỉ phép)
(41, '2026-04-01', 4,
 NULL, NULL,
 NULL, NULL, NULL, NULL, NULL, NULL,
 NULL, NULL,
 'LEAVE', 0.00, 0.00, 0, 0,
 b'0', b'1', 34, '2026-04-01 08:00:00',
 b'0', '2026-04-01 08:00:00', '2026-04-01 08:00:00'),

-- emp 42-44
(42, '2026-04-01', 4,
 '2026-04-01 08:30:00', '2026-04-01 17:30:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 96.50, 95.90,
 'PRESENT', 8.00, 0.00, 0, 0,
 b'1', b'1', 34, '2026-04-01 17:35:00',
 b'0', '2026-04-01 08:30:00', '2026-04-01 17:30:00'),

(43, '2026-04-01', 4,
 '2026-04-01 08:25:00', '2026-04-01 17:30:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 97.90, 97.30,
 'PRESENT', 8.08, 0.08, 0, 0,
 b'1', b'1', 34, '2026-04-01 17:35:00',
 b'0', '2026-04-01 08:25:00', '2026-04-01 17:30:00'),

(44, '2026-04-01', 4,
 '2026-04-01 08:30:00', '2026-04-01 17:35:00',
 1, 1, 10.77290, 106.66018, 10.77291, 106.66017,
 98.20, 97.60,
 'PRESENT', 8.08, 0.08, 0, 0,
 b'1', b'1', 34, '2026-04-01 17:40:00',
 b'0', '2026-04-01 08:30:00', '2026-04-01 17:35:00'),

-- ========== PHÒNG MARKETING (shift 1: 08:00-17:00) ==========
(45, '2026-04-01', 1,
 '2026-04-01 07:55:00', '2026-04-01 17:10:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 98.70, 98.10,
 'PRESENT', 8.25, 0.25, 0, 0,
 b'1', b'1', 46, '2026-04-01 17:15:00',
 b'0', '2026-04-01 07:55:00', '2026-04-01 17:10:00'),

(46, '2026-04-01', 1,
 '2026-04-01 08:00:00', '2026-04-01 17:00:00',
 1, 1, 10.77290, 106.66018, 10.77291, 106.66017,
 97.40, 96.80,
 'PRESENT', 8.00, 0.00, 0, 0,
 b'1', b'1', 46, '2026-04-01 17:05:00',
 b'0', '2026-04-01 08:00:00', '2026-04-01 17:00:00'),

(47, '2026-04-01', 1,
 '2026-04-01 08:10:00', '2026-04-01 17:05:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 96.80, 96.20,
 'PRESENT', 7.92, 0.00, 10, 0,
 b'1', b'1', 46, '2026-04-01 17:10:00',
 b'0', '2026-04-01 08:10:00', '2026-04-01 17:05:00'),

(48, '2026-04-01', 1,
 '2026-04-01 08:05:00', '2026-04-01 17:00:00',
 1, 1, 10.77292, 106.66016, 10.77291, 106.66017,
 98.30, 97.70,
 'PRESENT', 7.92, 0.00, 5, 0,
 b'1', b'1', 46, '2026-04-01 17:05:00',
 b'0', '2026-04-01 08:05:00', '2026-04-01 17:00:00'),

-- emp 49 - SEO - LATE 18p
(49, '2026-04-01', 1,
 '2026-04-01 08:18:00', '2026-04-01 17:02:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 97.60, 97.00,
 'LATE', 7.73, 0.00, 18, 0,
 b'1', b'1', 46, '2026-04-01 17:05:00',
 b'0', '2026-04-01 08:18:00', '2026-04-01 17:02:00'),

(50, '2026-04-01', 1,
 '2026-04-01 07:58:00', '2026-04-01 17:00:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 98.50, 98.00,
 'PRESENT', 8.03, 0.03, 0, 0,
 b'1', b'1', 46, '2026-04-01 17:05:00',
 b'0', '2026-04-01 07:58:00', '2026-04-01 17:00:00'),

-- ========== PHÒNG VẬN HÀNH (shift 2: 07:00-16:00) ==========
-- emp 51 - Ops Director HEAD
(51, '2026-04-01', 2,
 '2026-04-01 06:55:00', '2026-04-01 16:05:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 98.40, 97.80,
 'PRESENT', 8.17, 0.17, 0, 0,
 b'1', b'1', 52, '2026-04-01 16:10:00',
 b'0', '2026-04-01 06:55:00', '2026-04-01 16:05:00'),

-- emp 52 - Ops Deputy - ON_LEAVE → không có record (status INACTIVE/ON_LEAVE)

-- emp 53 - Ops Staff 1
(53, '2026-04-01', 2,
 '2026-04-01 06:58:00', '2026-04-01 16:00:00',
 1, 1, 10.77290, 106.66018, 10.77291, 106.66017,
 97.20, 96.60,
 'PRESENT', 8.03, 0.03, 0, 0,
 b'1', b'1', 52, '2026-04-01 16:05:00',
 b'0', '2026-04-01 06:58:00', '2026-04-01 16:00:00'),

(54, '2026-04-01', 2,
 '2026-04-01 07:00:00', '2026-04-01 16:05:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 96.90, 96.30,
 'PRESENT', 8.08, 0.08, 0, 0,
 b'1', b'1', 52, '2026-04-01 16:10:00',
 b'0', '2026-04-01 07:00:00', '2026-04-01 16:05:00'),

-- emp 55 - LATE 20p
(55, '2026-04-01', 2,
 '2026-04-01 07:20:00', '2026-04-01 16:00:00',
 1, 1, 10.77292, 106.66016, 10.77291, 106.66017,
 97.50, 96.90,
 'LATE', 7.67, 0.00, 20, 0,
 b'1', b'1', 52, '2026-04-01 16:05:00',
 b'0', '2026-04-01 07:20:00', '2026-04-01 16:00:00'),

-- emp 56 - Receptionist
(56, '2026-04-01', 2,
 '2026-04-01 06:50:00', '2026-04-01 16:00:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 98.80, 98.20,
 'PRESENT', 8.17, 0.17, 0, 0,
 b'1', b'1', 52, '2026-04-01 16:05:00',
 b'0', '2026-04-01 06:50:00', '2026-04-01 16:00:00'),

-- emp 57 - Driver
(57, '2026-04-01', 2,
 '2026-04-01 07:05:00', '2026-04-01 16:00:00',
 1, 1, 10.77291, 106.66017, 10.77291, 106.66017,
 97.00, 96.40,
 'PRESENT', 7.92, 0.00, 5, 0,
 b'1', b'1', 52, '2026-04-01 16:05:00',
 b'0', '2026-04-01 07:05:00', '2026-04-01 16:00:00'),

-- ========== PHÒNG R&D (shift 3: 09:00-18:00) - Làm tại Thủ Đức ==========
-- emp 58 - R&D Director HEAD
(58, '2026-04-01', 3,
 '2026-04-01 08:50:00', '2026-04-01 18:20:00',
 3, 3, 10.85587, 106.78603, 10.85587, 106.78603,
 98.60, 98.00,
 'PRESENT', 8.50, 0.50, 0, 0,
 b'1', b'1', 59, '2026-04-01 18:25:00',
 b'0', '2026-04-01 08:50:00', '2026-04-01 18:20:00'),

-- emp 59 - Researcher 1
(59, '2026-04-01', 3,
 '2026-04-01 09:00:00', '2026-04-01 18:10:00',
 3, 3, 10.85588, 106.78602, 10.85587, 106.78603,
 97.30, 96.70,
 'PRESENT', 8.17, 0.17, 0, 0,
 b'1', b'1', 59, '2026-04-01 18:15:00',
 b'0', '2026-04-01 09:00:00', '2026-04-01 18:10:00'),

-- emp 60 - Researcher 2
(60, '2026-04-01', 3,
 '2026-04-01 09:05:00', '2026-04-01 18:00:00',
 3, 3, 10.85587, 106.78603, 10.85587, 106.78603,
 98.90, 98.30,
 'PRESENT', 7.92, 0.00, 5, 0,
 b'1', b'1', 59, '2026-04-01 18:05:00',
 b'0', '2026-04-01 09:05:00', '2026-04-01 18:00:00'),

-- emp 61 - Researcher 3 - LATE 40p
(61, '2026-04-01', 3,
 '2026-04-01 09:40:00', '2026-04-01 18:30:00',
 3, 3, 10.85586, 106.78604, 10.85587, 106.78603,
 96.70, 96.10,
 'LATE', 7.83, 0.00, 40, 0,
 b'1', b'1', 59, '2026-04-01 18:35:00',
 b'0', '2026-04-01 09:40:00', '2026-04-01 18:30:00');


-- ============================================
-- CLONE NGÀY 02/04 → 10/04 (tự động)
-- Sử dụng INSERT ... SELECT clone từ ngày trước
-- ============================================

-- 02/04 (Thu) ← từ 01/04
INSERT INTO attendance_records
(employee_id, date, shift_id, check_in_time, check_out_time,
 check_in_location_id, check_out_location_id,
 check_in_lat, check_in_lng, check_out_lat, check_out_lng,
 check_in_face_match_score, check_out_face_match_score,
 status, work_hours, overtime_hours, late_minutes, early_leave_minutes,
 is_verified, is_approved, approved_by, approved_at,
 is_deleted, created_at, updated_at)
SELECT
    employee_id, '2026-04-02', shift_id,
    CASE WHEN check_in_time IS NOT NULL
         THEN DATE_ADD(DATE_ADD(check_in_time, INTERVAL 1 DAY), INTERVAL FLOOR(RAND()*10)-5 MINUTE)
         ELSE NULL END,
    CASE WHEN check_out_time IS NOT NULL
         THEN DATE_ADD(DATE_ADD(check_out_time, INTERVAL 1 DAY), INTERVAL FLOOR(RAND()*10)-5 MINUTE)
         ELSE NULL END,
    check_in_location_id, check_out_location_id,
    check_in_lat, check_in_lng, check_out_lat, check_out_lng,
    ROUND(GREATEST(90, LEAST(100, check_in_face_match_score + (RAND()-0.5)*2)), 2),
    ROUND(GREATEST(90, LEAST(100, check_out_face_match_score + (RAND()-0.5)*2)), 2),
    status, work_hours, overtime_hours, late_minutes, early_leave_minutes,
    is_verified, is_approved, approved_by,
    CASE WHEN approved_at IS NOT NULL THEN DATE_ADD(approved_at, INTERVAL 1 DAY) ELSE NULL END,
    b'0',
    DATE_ADD(created_at, INTERVAL 1 DAY),
    DATE_ADD(updated_at, INTERVAL 1 DAY)
FROM attendance_records WHERE date = '2026-04-01';

-- 03/04 (Fri) ← từ 02/04
INSERT INTO attendance_records
(employee_id, date, shift_id, check_in_time, check_out_time,
 check_in_location_id, check_out_location_id,
 check_in_lat, check_in_lng, check_out_lat, check_out_lng,
 check_in_face_match_score, check_out_face_match_score,
 status, work_hours, overtime_hours, late_minutes, early_leave_minutes,
 is_verified, is_approved, approved_by, approved_at,
 is_deleted, created_at, updated_at)
SELECT
    employee_id, '2026-04-03', shift_id,
    CASE WHEN check_in_time IS NOT NULL
         THEN DATE_ADD(DATE_ADD(check_in_time, INTERVAL 1 DAY), INTERVAL FLOOR(RAND()*10)-5 MINUTE)
         ELSE NULL END,
    CASE WHEN check_out_time IS NOT NULL
         THEN DATE_ADD(DATE_ADD(check_out_time, INTERVAL 1 DAY), INTERVAL FLOOR(RAND()*10)-5 MINUTE)
         ELSE NULL END,
    check_in_location_id, check_out_location_id,
    check_in_lat, check_in_lng, check_out_lat, check_out_lng,
    ROUND(GREATEST(90, LEAST(100, check_in_face_match_score + (RAND()-0.5)*2)), 2),
    ROUND(GREATEST(90, LEAST(100, check_out_face_match_score + (RAND()-0.5)*2)), 2),
    status, work_hours, overtime_hours, late_minutes, early_leave_minutes,
    is_verified, is_approved, approved_by,
    CASE WHEN approved_at IS NOT NULL THEN DATE_ADD(approved_at, INTERVAL 1 DAY) ELSE NULL END,
    b'0',
    DATE_ADD(created_at, INTERVAL 1 DAY),
    DATE_ADD(updated_at, INTERVAL 1 DAY)
FROM attendance_records WHERE date = '2026-04-02';

-- 06/04 (Mon) ← từ 03/04 + 3 ngày (qua weekend)
INSERT INTO attendance_records
(employee_id, date, shift_id, check_in_time, check_out_time,
 check_in_location_id, check_out_location_id,
 check_in_lat, check_in_lng, check_out_lat, check_out_lng,
 check_in_face_match_score, check_out_face_match_score,
 status, work_hours, overtime_hours, late_minutes, early_leave_minutes,
 is_verified, is_approved, approved_by, approved_at,
 is_deleted, created_at, updated_at)
SELECT
    employee_id, '2026-04-06', shift_id,
    CASE WHEN check_in_time IS NOT NULL
         THEN DATE_ADD(DATE_ADD(check_in_time, INTERVAL 3 DAY), INTERVAL FLOOR(RAND()*10)-5 MINUTE)
         ELSE NULL END,
    CASE WHEN check_out_time IS NOT NULL
         THEN DATE_ADD(DATE_ADD(check_out_time, INTERVAL 3 DAY), INTERVAL FLOOR(RAND()*10)-5 MINUTE)
         ELSE NULL END,
    check_in_location_id, check_out_location_id,
    check_in_lat, check_in_lng, check_out_lat, check_out_lng,
    ROUND(GREATEST(90, LEAST(100, check_in_face_match_score + (RAND()-0.5)*2)), 2),
    ROUND(GREATEST(90, LEAST(100, check_out_face_match_score + (RAND()-0.5)*2)), 2),
    status, work_hours, overtime_hours, late_minutes, early_leave_minutes,
    is_verified, is_approved, approved_by,
    CASE WHEN approved_at IS NOT NULL THEN DATE_ADD(approved_at, INTERVAL 3 DAY) ELSE NULL END,
    b'0',
    DATE_ADD(created_at, INTERVAL 3 DAY),
    DATE_ADD(updated_at, INTERVAL 3 DAY)
FROM attendance_records WHERE date = '2026-04-03';

-- 07/04 (Tue) ← từ 06/04
INSERT INTO attendance_records
(employee_id, date, shift_id, check_in_time, check_out_time,
 check_in_location_id, check_out_location_id,
 check_in_lat, check_in_lng, check_out_lat, check_out_lng,
 check_in_face_match_score, check_out_face_match_score,
 status, work_hours, overtime_hours, late_minutes, early_leave_minutes,
 is_verified, is_approved, approved_by, approved_at,
 is_deleted, created_at, updated_at)
SELECT
    employee_id, '2026-04-07', shift_id,
    CASE WHEN check_in_time IS NOT NULL
         THEN DATE_ADD(DATE_ADD(check_in_time, INTERVAL 1 DAY), INTERVAL FLOOR(RAND()*10)-5 MINUTE)
         ELSE NULL END,
    CASE WHEN check_out_time IS NOT NULL
         THEN DATE_ADD(DATE_ADD(check_out_time, INTERVAL 1 DAY), INTERVAL FLOOR(RAND()*10)-5 MINUTE)
         ELSE NULL END,
    check_in_location_id, check_out_location_id,
    check_in_lat, check_in_lng, check_out_lat, check_out_lng,
    ROUND(GREATEST(90, LEAST(100, check_in_face_match_score + (RAND()-0.5)*2)), 2),
    ROUND(GREATEST(90, LEAST(100, check_out_face_match_score + (RAND()-0.5)*2)), 2),
    status, work_hours, overtime_hours, late_minutes, early_leave_minutes,
    is_verified, is_approved, approved_by,
    CASE WHEN approved_at IS NOT NULL THEN DATE_ADD(approved_at, INTERVAL 1 DAY) ELSE NULL END,
    b'0',
    DATE_ADD(created_at, INTERVAL 1 DAY),
    DATE_ADD(updated_at, INTERVAL 1 DAY)
FROM attendance_records WHERE date = '2026-04-06';

-- 08/04 (Wed) ← từ 07/04
INSERT INTO attendance_records
(employee_id, date, shift_id, check_in_time, check_out_time,
 check_in_location_id, check_out_location_id,
 check_in_lat, check_in_lng, check_out_lat, check_out_lng,
 check_in_face_match_score, check_out_face_match_score,
 status, work_hours, overtime_hours, late_minutes, early_leave_minutes,
 is_verified, is_approved, approved_by, approved_at,
 is_deleted, created_at, updated_at)
SELECT
    employee_id, '2026-04-08', shift_id,
    CASE WHEN check_in_time IS NOT NULL
         THEN DATE_ADD(DATE_ADD(check_in_time, INTERVAL 1 DAY), INTERVAL FLOOR(RAND()*10)-5 MINUTE)
         ELSE NULL END,
    CASE WHEN check_out_time IS NOT NULL
         THEN DATE_ADD(DATE_ADD(check_out_time, INTERVAL 1 DAY), INTERVAL FLOOR(RAND()*10)-5 MINUTE)
         ELSE NULL END,
    check_in_location_id, check_out_location_id,
    check_in_lat, check_in_lng, check_out_lat, check_out_lng,
    ROUND(GREATEST(90, LEAST(100, check_in_face_match_score + (RAND()-0.5)*2)), 2),
    ROUND(GREATEST(90, LEAST(100, check_out_face_match_score + (RAND()-0.5)*2)), 2),
    status, work_hours, overtime_hours, late_minutes, early_leave_minutes,
    is_verified, is_approved, approved_by,
    CASE WHEN approved_at IS NOT NULL THEN DATE_ADD(approved_at, INTERVAL 1 DAY) ELSE NULL END,
    b'0',
    DATE_ADD(created_at, INTERVAL 1 DAY),
    DATE_ADD(updated_at, INTERVAL 1 DAY)
FROM attendance_records WHERE date = '2026-04-07';

-- 09/04 (Thu) ← từ 08/04
INSERT INTO attendance_records
(employee_id, date, shift_id, check_in_time, check_out_time,
 check_in_location_id, check_out_location_id,
 check_in_lat, check_in_lng, check_out_lat, check_out_lng,
 check_in_face_match_score, check_out_face_match_score,
 status, work_hours, overtime_hours, late_minutes, early_leave_minutes,
 is_verified, is_approved, approved_by, approved_at,
 is_deleted, created_at, updated_at)
SELECT
    employee_id, '2026-04-09', shift_id,
    CASE WHEN check_in_time IS NOT NULL
         THEN DATE_ADD(DATE_ADD(check_in_time, INTERVAL 1 DAY), INTERVAL FLOOR(RAND()*10)-5 MINUTE)
         ELSE NULL END,
    CASE WHEN check_out_time IS NOT NULL
         THEN DATE_ADD(DATE_ADD(check_out_time, INTERVAL 1 DAY), INTERVAL FLOOR(RAND()*10)-5 MINUTE)
         ELSE NULL END,
    check_in_location_id, check_out_location_id,
    check_in_lat, check_in_lng, check_out_lat, check_out_lng,
    ROUND(GREATEST(90, LEAST(100, check_in_face_match_score + (RAND()-0.5)*2)), 2),
    ROUND(GREATEST(90, LEAST(100, check_out_face_match_score + (RAND()-0.5)*2)), 2),
    status, work_hours, overtime_hours, late_minutes, early_leave_minutes,
    is_verified, is_approved, approved_by,
    CASE WHEN approved_at IS NOT NULL THEN DATE_ADD(approved_at, INTERVAL 1 DAY) ELSE NULL END,
    b'0',
    DATE_ADD(created_at, INTERVAL 1 DAY),
    DATE_ADD(updated_at, INTERVAL 1 DAY)
FROM attendance_records WHERE date = '2026-04-08';

-- 10/04 (Fri) ← từ 09/04
INSERT INTO attendance_records
(employee_id, date, shift_id, check_in_time, check_out_time,
 check_in_location_id, check_out_location_id,
 check_in_lat, check_in_lng, check_out_lat, check_out_lng,
 check_in_face_match_score, check_out_face_match_score,
 status, work_hours, overtime_hours, late_minutes, early_leave_minutes,
 is_verified, is_approved, approved_by, approved_at,
 is_deleted, created_at, updated_at)
SELECT
    employee_id, '2026-04-10', shift_id,
    CASE WHEN check_in_time IS NOT NULL
         THEN DATE_ADD(DATE_ADD(check_in_time, INTERVAL 1 DAY), INTERVAL FLOOR(RAND()*10)-5 MINUTE)
         ELSE NULL END,
    CASE WHEN check_out_time IS NOT NULL
         THEN DATE_ADD(DATE_ADD(check_out_time, INTERVAL 1 DAY), INTERVAL FLOOR(RAND()*10)-5 MINUTE)
         ELSE NULL END,
    check_in_location_id, check_out_location_id,
    check_in_lat, check_in_lng, check_out_lat, check_out_lng,
    ROUND(GREATEST(90, LEAST(100, check_in_face_match_score + (RAND()-0.5)*2)), 2),
    ROUND(GREATEST(90, LEAST(100, check_out_face_match_score + (RAND()-0.5)*2)), 2),
    status, work_hours, overtime_hours, late_minutes, early_leave_minutes,
    is_verified, is_approved, approved_by,
    CASE WHEN approved_at IS NOT NULL THEN DATE_ADD(approved_at, INTERVAL 1 DAY) ELSE NULL END,
    b'0',
    DATE_ADD(created_at, INTERVAL 1 DAY),
    DATE_ADD(updated_at, INTERVAL 1 DAY)
FROM attendance_records WHERE date = '2026-04-09';


-- ============================================
-- 12/04/2026 (CHỦ NHẬT) - HÔM NAY
-- Vài dev IT hotfix cuối tuần (chỉ check-in, chưa check-out)
-- ============================================
INSERT INTO attendance_records
(employee_id, date, shift_id,
 check_in_time, check_out_time,
 check_in_location_id, check_out_location_id,
 check_in_lat, check_in_lng, check_out_lat, check_out_lng,
 check_in_face_match_score, check_out_face_match_score,
 status, work_hours, overtime_hours, late_minutes, early_leave_minutes,
 is_verified, is_approved,
 is_deleted, created_at, updated_at) VALUES

-- Backend lead hotfix
(12, '2026-04-12', 3,
 '2026-04-12 09:30:00', NULL,
 1, NULL, 10.77291, 106.66017, NULL, NULL,
 98.20, NULL,
 'PRESENT', 0.00, 0.00, 0, 0,
 b'1', NULL,
 b'0', '2026-04-12 09:30:00', '2026-04-12 09:30:00'),

-- Senior dev hotfix
(14, '2026-04-12', 3,
 '2026-04-12 10:00:00', NULL,
 1, NULL, 10.77290, 106.66018, NULL, NULL,
 97.50, NULL,
 'PRESENT', 0.00, 0.00, 0, 0,
 b'1', NULL,
 b'0', '2026-04-12 10:00:00', '2026-04-12 10:00:00'),

-- DevOps hotfix
(21, '2026-04-12', 3,
 '2026-04-12 09:15:00', NULL,
 1, NULL, 10.77291, 106.66017, NULL, NULL,
 98.70, NULL,
 'PRESENT', 0.00, 0.00, 0, 0,
 b'1', NULL,
 b'0', '2026-04-12 09:15:00', '2026-04-12 09:15:00');


-- ============================================
-- ATTENDANCE ANOMALIES (mẫu)
-- ============================================
INSERT INTO attendance_anomalies
(attendance_id, anomaly_type, description, severity, resolved,
 resolved_by, resolved_at, resolution_note,
 is_deleted, created_at, updated_at)

SELECT id, 'TIME_VIOLATION', 'Check-in trễ 35 phút so với ca', 'MEDIUM',
    TRUE, 5, '2026-04-01 10:00:00', 'Đã xác nhận lý do: kẹt xe',
    b'0', '2026-04-01 09:35:00', '2026-04-01 10:00:00'
FROM attendance_records WHERE employee_id = 14 AND date = '2026-04-01'
UNION ALL
SELECT id, 'TIME_VIOLATION', 'Check-in trễ 40 phút so với ca', 'HIGH',
    FALSE, NULL, NULL, NULL,
    b'0', '2026-04-01 09:40:00', '2026-04-01 09:40:00'
FROM attendance_records WHERE employee_id = 61 AND date = '2026-04-01'
UNION ALL
SELECT id, 'TIME_VIOLATION', 'Check-out sớm 60 phút (intern lịch học)', 'MEDIUM',
    TRUE, 5, '2026-04-02 09:00:00', 'Intern được phép về sớm do lịch học',
    b'0', '2026-04-01 17:00:00', '2026-04-02 09:00:00'
FROM attendance_records WHERE employee_id = 24 AND date = '2026-04-01'
UNION ALL
SELECT id, 'TIME_VIOLATION', 'Check-in trễ 25 phút', 'LOW',
    TRUE, 5, '2026-04-01 09:00:00', 'Nhân viên giải trình: xe hỏng',
    b'0', '2026-04-01 08:25:00', '2026-04-01 09:00:00'
FROM attendance_records WHERE employee_id = 30 AND date = '2026-04-01'
UNION ALL
SELECT id, 'FACE_MISMATCH', 'Face match score thấp: 89.5% - cần kiểm tra', 'HIGH',
    FALSE, NULL, NULL, NULL,
    b'0', '2026-04-06 08:50:00', '2026-04-06 08:50:00'
FROM attendance_records WHERE employee_id = 37 AND date = '2026-04-06'
UNION ALL
SELECT id, 'GPS_INVALID', 'Check-in ngoài bán kính cho phép (250m)', 'HIGH',
    FALSE, NULL, NULL, NULL,
    b'0', '2026-04-07 07:20:00', '2026-04-07 07:20:00'
FROM attendance_records WHERE employee_id = 55 AND date = '2026-04-07';


-- ============================================
-- ATTENDANCE AUDIT LOGS (mẫu)
-- ============================================
INSERT INTO attendance_audit_logs
(attendance_id, action, old_value, new_value, reason,
 performed_by, performed_by_name,
 is_deleted, created_at, updated_at)

SELECT id, 'APPROVE',
    '{"isApproved":null}',
    '{"isApproved":true}',
    NULL,
    5, 'Phạm Thị Mai',
    b'0', '2026-04-01 17:30:00', '2026-04-01 17:30:00'
FROM attendance_records WHERE employee_id = 6 AND date = '2026-04-01'
UNION ALL
SELECT id, 'EDIT',
    '{"checkOutTime":"17:00","status":"EARLY_LEAVE","earlyLeaveMinutes":60}',
    '{"checkOutTime":"18:00","status":"PRESENT","earlyLeaveMinutes":0}',
    'Intern được phép về sớm do lịch học, đã bổ sung giờ ngày hôm sau',
    5, 'Phạm Thị Mai',
    b'0', '2026-04-02 09:00:00', '2026-04-02 09:00:00'
FROM attendance_records WHERE employee_id = 24 AND date = '2026-04-01';


SET FOREIGN_KEY_CHECKS = 1;


-- ============================================
-- DATA SUMMARY
-- ============================================
-- shifts: 5 ca (HC, CS, FLEX, KD, BGĐ)
-- work_locations: 3 (HQ Q.10, Q7, Thủ Đức)
-- employees.shift_id: 61 NV active đã gán ca
--
-- attendance_records:
--   01/04: 58 records (base day, chi tiết)
--   02/04 → 10/04: ~58 records/ngày (clone + random variation)
--   12/04: 3 records (IT hotfix, chỉ check-in)
--   TỔNG: ~58×8 + 3 = ~467 records
--
-- attendance_anomalies: 6 records
-- attendance_audit_logs: 2 records
--
-- Phân bổ status mỗi ngày (58 records):
--   PRESENT:      ~46 (79%)
--   LATE:         ~6  (10%)
--   EARLY_LEAVE:  ~3  (5%)
--   LEAVE:        ~1  (2%)
--   ABSENT:       ~2  (3%, không có record: emp 19, 52)
--
-- Phân bổ ca:
--   Ca HC (1):        HR(6) + Finance(8) + Marketing(6) = 20
--   Ca Sáng Sớm (2):  Ops(6, trừ emp 52 ON_LEAVE) = 6
--   Ca Linh Hoạt (3):  IT(14, trừ emp 19 absent) + R&D(4) = 18
--   Ca KD (4):         Sales(12) = 12
--   Ca BGĐ (5):        BGĐ(3) = 3
--   Không gán:          emp 52(ON_LEAVE), emp 62-63(nghỉ việc)
-- ============================================