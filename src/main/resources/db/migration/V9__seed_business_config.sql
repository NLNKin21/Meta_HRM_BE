-- ============================================================
-- V9: SEED BUSINESS CONFIG DATA
-- contract_types, contracts (mẫu), task_statuses,
-- task_status_transitions, payroll_config,
-- leave_types, leave_type_seniority_rules, holidays
-- ============================================================

-- ------------------------------------------------------------
-- contract_types (giữ nguyên 7 loại từ data gốc)
-- ------------------------------------------------------------
INSERT INTO contract_types
    (id, type_code, type_name, description, notes,
     duration_unit, duration_value, require_file, clause_template,
     is_active, created_by, updated_by, created_at, updated_at)
VALUES
(1, 'PERMANENT', 'Hợp đồng lao động vô thời hạn',
    'Hợp đồng không xác định thời hạn, áp dụng cho nhân viên chính thức lâu năm, đã qua ít nhất 2 lần ký hợp đồng có thời hạn.',
    NULL, 'INDEFINITE', NULL, TRUE,
    'Điều 1: Loại hợp đồng - Hợp đồng lao động không xác định thời hạn\nĐiều 2: Công việc - Theo mô tả công việc đính kèm\nĐiều 3: Thời gian làm việc - 8 giờ/ngày, 5 ngày/tuần\nĐiều 4: Chế độ - Theo quy chế công ty và pháp luật lao động hiện hành',
    TRUE, 1, 1, '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(2, 'DEFINITE', 'Hợp đồng lao động có thời hạn',
    'Hợp đồng xác định thời hạn từ 12 đến 36 tháng, áp dụng cho nhân viên chính thức sau thử việc.',
    NULL, 'YEAR', 3, TRUE,
    'Điều 1: Loại hợp đồng - Hợp đồng lao động xác định thời hạn\nĐiều 2: Thời hạn - Theo thời hạn ghi trên hợp đồng\nĐiều 3: Công việc - Theo mô tả công việc đính kèm\nĐiều 4: Thời gian làm việc - 8 giờ/ngày, 5 ngày/tuần\nĐiều 5: Gia hạn - Hợp đồng có thể gia hạn theo thỏa thuận hai bên',
    TRUE, 1, 1, '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(3, 'PROBATION', 'Hợp đồng thử việc',
    'Hợp đồng trong thời gian thử việc, từ 1 đến 6 tháng tùy vị trí. Lương thử việc tối thiểu 85% lương chính thức.',
    NULL, 'MONTH', 2, TRUE,
    'Điều 1: Loại hợp đồng - Hợp đồng thử việc\nĐiều 2: Thời gian thử việc - Theo thời hạn ghi trên hợp đồng\nĐiều 3: Mức lương thử việc - 85% lương chính thức\nĐiều 4: Đánh giá - Kết thúc thử việc sẽ được đánh giá để ký hợp đồng chính thức\nĐiều 5: Chấm dứt - Hai bên có quyền chấm dứt mà không cần báo trước',
    TRUE, 1, 1, '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(4, 'SEASONAL', 'Hợp đồng thời vụ',
    'Hợp đồng ngắn hạn theo mùa vụ hoặc dự án cụ thể, thời hạn dưới 12 tháng.',
    NULL, 'MONTH', 6, TRUE,
    'Điều 1: Loại hợp đồng - Hợp đồng lao động theo mùa vụ\nĐiều 2: Thời hạn - Theo thời hạn ghi trên hợp đồng\nĐiều 3: Công việc - Theo yêu cầu dự án/mùa vụ cụ thể\nĐiều 4: Thanh toán - Theo thỏa thuận trên hợp đồng',
    TRUE, 1, 1, '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(5, 'PART_TIME', 'Hợp đồng bán thời gian',
    'Hợp đồng dành cho nhân viên làm việc bán thời gian, dưới 8 giờ/ngày hoặc dưới 5 ngày/tuần.',
    NULL, 'MONTH', 12, TRUE,
    'Điều 1: Loại hợp đồng - Hợp đồng lao động bán thời gian\nĐiều 2: Thời gian làm việc - Theo lịch thỏa thuận, không quá 4 giờ/ngày\nĐiều 3: Lương - Tính theo giờ làm việc thực tế\nĐiều 4: Chế độ - Theo quy định pháp luật về lao động bán thời gian',
    TRUE, 1, 1, '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(6, 'INTERNSHIP', 'Hợp đồng thực tập',
    'Hợp đồng dành cho sinh viên thực tập, có thể có hoặc không có lương. Thời hạn từ 1 đến 6 tháng.',
    NULL, 'MONTH', 6, FALSE,
    'Điều 1: Loại hợp đồng - Hợp đồng thực tập sinh\nĐiều 2: Thời hạn thực tập - Theo thời hạn ghi trên hợp đồng\nĐiều 3: Phụ cấp - Theo chính sách công ty\nĐiều 4: Người hướng dẫn - Được phân công người hướng dẫn\nĐiều 5: Đánh giá - Được đánh giá cuối kỳ thực tập',
    TRUE, 1, 1, '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(7, 'FULL_TIME', 'Hợp đồng lao động toàn thời gian',
    'Hợp đồng toàn thời gian cho nhân viên chính thức, làm việc 8 giờ/ngày, 5 ngày/tuần.',
    NULL, 'YEAR', 3, TRUE,
    'Điều 1: Loại hợp đồng - Hợp đồng lao động toàn thời gian\nĐiều 2: Thời hạn - Theo thời hạn ghi trên hợp đồng\nĐiều 3: Thời gian làm việc - 8 giờ/ngày, 5 ngày/tuần\nĐiều 4: Lương và phụ cấp - Theo thỏa thuận trên hợp đồng\nĐiều 5: Chế độ BHXH, BHYT, BHTN - Theo quy định pháp luật hiện hành',
    TRUE, 1, 1, '2024-01-01 08:00:00', '2024-01-01 08:00:00');

-- ------------------------------------------------------------
-- task_statuses (5 trạng thái mặc định toàn cục)
-- ------------------------------------------------------------
INSERT INTO task_statuses
    (id, status_name, status_name_en, order_index, color, icon,
     is_completed, is_default, department_id, is_active,
     created_at, updated_at)
VALUES
(1, 'Chờ thực hiện', 'To Do',       1, '#2196F3', 'Assignment',   FALSE, TRUE,  NULL, TRUE, '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(2, 'Đang thực hiện','In Progress', 2, '#FF9800', 'PlayCircle',   FALSE, FALSE, NULL, TRUE, '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(3, 'Chờ đánh giá',  'In Review',   3, '#9C27B0', 'RateReview',   FALSE, FALSE, NULL, TRUE, '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(4, 'Hoàn thành',    'Done',        4, '#4CAF50', 'CheckCircle',  TRUE,  FALSE, NULL, TRUE, '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(5, 'Đã hủy',        'Cancelled',   5, '#F44336', 'Cancel',       TRUE,  FALSE, NULL, TRUE, '2024-01-01 08:00:00', '2024-01-01 08:00:00');

-- ------------------------------------------------------------
-- task_status_transitions
-- Workflow chuẩn: staff chuyển tiến; leader/head duyệt và hủy
-- ------------------------------------------------------------
INSERT INTO task_status_transitions
    (from_status_id, to_status_id, department_id, allowed_roles, is_active)
VALUES
-- Tiến theo luồng chính
(1, 2, NULL, '["HEAD","DEPUTY","LEADER","STAFF"]', TRUE), -- To Do → In Progress
(2, 3, NULL, '["HEAD","DEPUTY","LEADER","STAFF"]', TRUE), -- In Progress → In Review
(3, 4, NULL, '["HEAD","DEPUTY","LEADER"]',         TRUE), -- In Review → Done (chỉ lead+)
(3, 2, NULL, '["HEAD","DEPUTY","LEADER"]',         TRUE), -- In Review → In Progress (trả lại)
-- Hủy (chỉ head/deputy)
(1, 5, NULL, '["HEAD","DEPUTY"]', TRUE),
(2, 5, NULL, '["HEAD","DEPUTY"]', TRUE),
(3, 5, NULL, '["HEAD","DEPUTY"]', TRUE),
-- Mở lại task đã done (chỉ head)
(4, 2, NULL, '["HEAD"]', TRUE);

-- ------------------------------------------------------------
-- payroll_config (căn cứ pháp lý Việt Nam 2024-2025)
-- ------------------------------------------------------------
INSERT INTO payroll_config
    (config_key, config_value, config_group, description, is_active)
VALUES
-- Bảo hiểm xã hội (NLĐ)
('BHXH_EMP_RATE',          0.0800, 'INSURANCE', 'BHXH nhân viên 8% — Điều 85 Luật BHXH 2014',                          TRUE),
('BHYT_EMP_RATE',          0.0150, 'INSURANCE', 'BHYT nhân viên 1.5% — Luật BHYT 2014 sửa đổi',                        TRUE),
('BHTN_EMP_RATE',          0.0100, 'INSURANCE', 'BHTN nhân viên 1% — Điều 57 Luật Việc làm 2013',                      TRUE),
-- Bảo hiểm xã hội (Công ty)
('BHXH_EM_RATE',           0.1750, 'INSURANCE', 'BHXH công ty 17.5%',                                                  TRUE),
('BHYT_EM_RATE',           0.0300, 'INSURANCE', 'BHYT công ty 3%',                                                     TRUE),
('BHTN_EM_RATE',           0.0100, 'INSURANCE', 'BHTN công ty 1%',                                                     TRUE),
-- Trần lương đóng BH
('INSURANCE_SALARY_CAP',   46800000.0000, 'INSURANCE', 'Trần lương đóng BH = 20 × lương TT vùng 1 (NĐ 74/2024)',      TRUE),
('MIN_REGIONAL_WAGE',       4960000.0000, 'INSURANCE', 'Lương tối thiểu vùng 1 HCM/HN — NĐ 74/2024/NĐ-CP',           TRUE),
-- Thuế TNCN — giảm trừ gia cảnh
('PERSONAL_DEDUCTION',     11000000.0000, 'TAX',       'Giảm trừ bản thân 11tr/tháng — NQ 954/2020/UBTVQH14',         TRUE),
('DEPENDENT_DEDUCTION',     4400000.0000, 'TAX',       'Giảm trừ người phụ thuộc 4.4tr/người/tháng',                  TRUE),
-- Thuế TNCN — ngưỡng bậc lũy tiến
('TAX_BRACKET_1_MAX',       5000000.0000, 'TAX',       'Bậc 1: ≤ 5 triệu',                                             TRUE),
('TAX_BRACKET_2_MAX',      10000000.0000, 'TAX',       'Bậc 2: 5–10 triệu',                                            TRUE),
('TAX_BRACKET_3_MAX',      18000000.0000, 'TAX',       'Bậc 3: 10–18 triệu',                                           TRUE),
('TAX_BRACKET_4_MAX',      32000000.0000, 'TAX',       'Bậc 4: 18–32 triệu',                                           TRUE),
('TAX_BRACKET_5_MAX',      52000000.0000, 'TAX',       'Bậc 5: 32–52 triệu',                                           TRUE),
('TAX_BRACKET_6_MAX',      80000000.0000, 'TAX',       'Bậc 6: 52–80 triệu',                                           TRUE),
-- Thuế TNCN — tỷ lệ từng bậc
('TAX_RATE_BRACKET_1',     0.0500, 'TAX',       '5% — bậc 1',                                                         TRUE),
('TAX_RATE_BRACKET_2',     0.1000, 'TAX',       '10% — bậc 2',                                                        TRUE),
('TAX_RATE_BRACKET_3',     0.1500, 'TAX',       '15% — bậc 3',                                                        TRUE),
('TAX_RATE_BRACKET_4',     0.2000, 'TAX',       '20% — bậc 4',                                                        TRUE),
('TAX_RATE_BRACKET_5',     0.2500, 'TAX',       '25% — bậc 5',                                                        TRUE),
('TAX_RATE_BRACKET_6',     0.3000, 'TAX',       '30% — bậc 6',                                                        TRUE),
('TAX_RATE_BRACKET_7',     0.3500, 'TAX',       '35% — bậc 7 (> 80 triệu)',                                           TRUE),
-- OT — hệ số theo Điều 98 BLLĐ 2019
('OT_RATE_WEEKDAY',        1.5000, 'OT',        'OT ngày thường: 150% — Điều 98 BLLĐ 2019',                           TRUE),
('OT_RATE_WEEKEND',        2.0000, 'OT',        'OT cuối tuần T7/CN: 200%',                                           TRUE),
('OT_RATE_HOLIDAY',        3.0000, 'OT',        'OT ngày lễ: 300%',                                                   TRUE),
('MAX_OT_HOURS_MONTH',     40.0000, 'OT',       'OT tối đa 40 giờ/tháng — Điều 107 BLLĐ',                            TRUE),
('MAX_OT_HOURS_YEAR',     300.0000, 'OT',       'OT tối đa 300 giờ/năm — Điều 107 BLLĐ',                             TRUE),
-- Phạt & khấu trừ
('LATE_PENALTY_AMOUNT',   100000.0000, 'PENALTY', 'Phạt đi muộn 1 lần: 100,000đ (nội quy công ty)',                   TRUE),
('LATE_PENALTY_THRESHOLD',    15.0000, 'PENALTY', 'Ngưỡng phút trễ bắt đầu tính phạt: 15 phút',                      TRUE),
('ABSENT_PENALTY_RATE',        1.0000, 'PENALTY', 'Trừ 1 ngày lương khi vắng không phép',                             TRUE),
-- Cấu hình chung
('STANDARD_WORK_DAYS',        26.0000, 'GENERAL', 'Ngày công chuẩn/tháng',                                            TRUE),
('STANDARD_WORK_HOURS',        8.0000, 'GENERAL', 'Giờ làm việc chuẩn/ngày',                                         TRUE),
('MEAL_ALLOWANCE_EXEMPT',  730000.0000, 'GENERAL', 'Phụ cấp ăn ca miễn thuế/tháng — TT 11/2018/TT-BTC',             TRUE),
('TRANSPORT_ALLOWANCE_EXEMPT', 1000000.0000, 'GENERAL', 'Phụ cấp đi lại miễn thuế/tháng — TT 111/2013',             TRUE),
('PROBATION_SALARY_RATE',      0.8500, 'GENERAL', 'Lương thử việc = 85% lương CB — Điều 26 BLLĐ',                   TRUE),
('ADVANCE_PAYMENT_RATE',       0.5000, 'GENERAL', 'Tạm ứng lương tối đa 50%',                                        TRUE);

-- ------------------------------------------------------------
-- leave_types (7 loại theo pháp luật lao động VN)
-- ------------------------------------------------------------
INSERT INTO leave_types
    (id, code, name,
     max_days_per_year, default_days_per_year,
     paid_leave, requires_approval, requires_document, active,
     deduct_balance, deduct_from_annual_leave_balance,
     auto_approve, allow_carry_forward, allow_encashment,
     counts_in_attendance, counts_in_company_payroll,
     deduct_salary, social_insurance_paid, increase_by_seniority)
VALUES
-- Nghỉ phép năm (Điều 113 BLLĐ: 12 ngày, +1 ngày/5 năm)
(1, 'ANNUAL_LEAVE',      'Nghỉ phép năm',
    12, 12, TRUE, TRUE,  FALSE, TRUE,
    TRUE,  FALSE, FALSE, TRUE, TRUE,
    TRUE, TRUE, FALSE, FALSE, TRUE),
-- Nghỉ ốm (BHXH trả 75%)
(2, 'SICK_LEAVE',        'Nghỉ ốm',
    30, 30, TRUE, TRUE,  TRUE,  TRUE,
    FALSE, FALSE, FALSE, FALSE, FALSE,
    TRUE, TRUE, FALSE, TRUE, FALSE),
-- Nghỉ thai sản nữ (Điều 139 BLLĐ: 6 tháng = 180 ngày)
(3, 'MATERNITY_LEAVE',   'Nghỉ thai sản',
    180, 180, TRUE, TRUE, TRUE, TRUE,
    FALSE, FALSE, FALSE, FALSE, FALSE,
    TRUE, TRUE, FALSE, TRUE, FALSE),
-- Nghỉ thai sản nam (Điều 34 Luật BHXH: 5-14 ngày tùy ca sinh)
(4, 'PATERNITY_LEAVE',   'Nghỉ thai sản (nam)',
    14, 5, TRUE, TRUE, TRUE, TRUE,
    FALSE, FALSE, FALSE, FALSE, FALSE,
    TRUE, TRUE, FALSE, TRUE, FALSE),
-- Nghỉ đặc biệt (cưới hỏi, tang chế — Điều 115 BLLĐ)
(5, 'SPECIAL_LEAVE',     'Nghỉ việc riêng',
    3, 3, TRUE, TRUE, TRUE, TRUE,
    FALSE, FALSE, FALSE, FALSE, FALSE,
    TRUE, TRUE, FALSE, FALSE, FALSE),
-- Nghỉ không lương (theo thỏa thuận)
(6, 'UNPAID_LEAVE',      'Nghỉ không lương',
    30, 0, FALSE, TRUE, FALSE, TRUE,
    FALSE, FALSE, FALSE, FALSE, FALSE,
    TRUE, FALSE, TRUE, FALSE, FALSE),
-- Nghỉ bù (sau khi làm OT ngày lễ/cuối tuần)
(7, 'COMPENSATORY_LEAVE','Nghỉ bù',
    12, 0, TRUE, FALSE, FALSE, TRUE,
    TRUE, FALSE, TRUE, FALSE, FALSE,
    TRUE, TRUE, FALSE, FALSE, FALSE);

-- leave_type_seniority_rules (chỉ áp dụng cho ANNUAL_LEAVE)
-- Điều 113 BLLĐ: cứ 5 năm thâm niên được cộng thêm 1 ngày
INSERT INTO leave_type_seniority_rules (leave_type_id, min_years, extra_days) VALUES
(1,  5,  1),
(1, 10,  2),
(1, 15,  3),
(1, 20,  4);

-- ------------------------------------------------------------
-- holidays (ngày lễ chuẩn Việt Nam 2024, 2025, 2026)
-- type: NATIONAL | COMPENSATORY | LUNAR_NEW_YEAR
-- ------------------------------------------------------------
INSERT INTO holidays (holiday_date, name, type, active) VALUES
-- 2024
('2024-01-01', 'Tết Dương lịch',                              'NATIONAL',       TRUE),
('2024-02-08', 'Tết Nguyên đán — Ngày 29 tháng Chạp',        'LUNAR_NEW_YEAR', TRUE),
('2024-02-09', 'Tết Nguyên đán — Giao thừa (30 tháng Chạp)', 'LUNAR_NEW_YEAR', TRUE),
('2024-02-10', 'Tết Nguyên đán — Mùng 1',                    'LUNAR_NEW_YEAR', TRUE),
('2024-02-11', 'Tết Nguyên đán — Mùng 2',                    'LUNAR_NEW_YEAR', TRUE),
('2024-02-12', 'Tết Nguyên đán — Mùng 3',                    'LUNAR_NEW_YEAR', TRUE),
('2024-02-13', 'Tết Nguyên đán — Mùng 4',                    'LUNAR_NEW_YEAR', TRUE),
('2024-02-14', 'Tết Nguyên đán — Mùng 5',                    'LUNAR_NEW_YEAR', TRUE),
('2024-04-18', 'Giỗ Tổ Hùng Vương (10/3 Âm lịch)',           'NATIONAL',       TRUE),
('2024-04-30', 'Ngày Giải phóng miền Nam',                    'NATIONAL',       TRUE),
('2024-05-01', 'Ngày Quốc tế Lao động',                       'NATIONAL',       TRUE),
('2024-09-02', 'Ngày Quốc khánh',                             'NATIONAL',       TRUE),
('2024-09-03', 'Nghỉ bù Ngày Quốc khánh',                    'COMPENSATORY',   TRUE),
-- 2025
('2025-01-01', 'Tết Dương lịch',                              'NATIONAL',       TRUE),
('2025-01-27', 'Tết Nguyên đán — Ngày 28 tháng Chạp',        'LUNAR_NEW_YEAR', TRUE),
('2025-01-28', 'Tết Nguyên đán — Ngày 29 tháng Chạp',        'LUNAR_NEW_YEAR', TRUE),
('2025-01-29', 'Tết Nguyên đán — Giao thừa',                  'LUNAR_NEW_YEAR', TRUE),
('2025-01-30', 'Tết Nguyên đán — Mùng 1',                    'LUNAR_NEW_YEAR', TRUE),
('2025-01-31', 'Tết Nguyên đán — Mùng 2',                    'LUNAR_NEW_YEAR', TRUE),
('2025-02-01', 'Tết Nguyên đán — Mùng 3',                    'LUNAR_NEW_YEAR', TRUE),
('2025-02-02', 'Tết Nguyên đán — Mùng 4',                    'LUNAR_NEW_YEAR', TRUE),
('2025-02-03', 'Tết Nguyên đán — Mùng 5',                    'LUNAR_NEW_YEAR', TRUE),
('2025-04-07', 'Giỗ Tổ Hùng Vương (10/3 Âm lịch)',           'NATIONAL',       TRUE),
('2025-04-30', 'Ngày Giải phóng miền Nam',                    'NATIONAL',       TRUE),
('2025-05-01', 'Ngày Quốc tế Lao động',                       'NATIONAL',       TRUE),
('2025-09-02', 'Ngày Quốc khánh',                             'NATIONAL',       TRUE),
-- 2026
('2026-01-01', 'Tết Dương lịch',                              'NATIONAL',       TRUE),
('2026-01-27', 'Tết Nguyên đán — Ngày 28 tháng Chạp',        'LUNAR_NEW_YEAR', TRUE),
('2026-01-28', 'Tết Nguyên đán — Ngày 29 tháng Chạp',        'LUNAR_NEW_YEAR', TRUE),
('2026-01-29', 'Tết Nguyên đán — Giao thừa',                  'LUNAR_NEW_YEAR', TRUE),
('2026-01-30', 'Tết Nguyên đán — Mùng 1',                    'LUNAR_NEW_YEAR', TRUE),
('2026-01-31', 'Tết Nguyên đán — Mùng 2',                    'LUNAR_NEW_YEAR', TRUE),
('2026-02-01', 'Tết Nguyên đán — Mùng 3',                    'LUNAR_NEW_YEAR', TRUE),
('2026-02-02', 'Tết Nguyên đán — Mùng 4',                    'LUNAR_NEW_YEAR', TRUE),
('2026-02-03', 'Tết Nguyên đán — Mùng 5',                    'LUNAR_NEW_YEAR', TRUE),
('2026-04-26', 'Giỗ Tổ Hùng Vương (10/3 Âm lịch)',           'NATIONAL',       TRUE),
('2026-04-27', 'Nghỉ bù Giỗ Tổ Hùng Vương',                  'COMPENSATORY',   TRUE),
('2026-04-30', 'Ngày Giải phóng miền Nam',                    'NATIONAL',       TRUE),
('2026-05-01', 'Ngày Quốc tế Lao động',                       'NATIONAL',       TRUE),
('2026-05-02', 'Nghỉ bù Ngày Quốc tế Lao động',              'COMPENSATORY',   TRUE),
('2026-09-01', 'Nghỉ liền kề Ngày Quốc khánh',               'COMPENSATORY',   TRUE),
('2026-09-02', 'Ngày Quốc khánh',                             'NATIONAL',       TRUE);
