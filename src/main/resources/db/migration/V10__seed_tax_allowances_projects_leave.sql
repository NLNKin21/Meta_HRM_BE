-- ============================================================
-- V10: SEED OPERATIONAL DATA
-- employee_tax_info, allowances, projects,
-- leave_balances, attendance_records,
-- attendance_anomalies, attendance_audit_logs
-- ============================================================

-- ============================================================
-- EMPLOYEE TAX INFO
-- social_insurance_salary: cap 46,800,000 cho C-level/Director
-- bank_account_number: 10 số chuẩn VN
-- number_of_dependents: theo data gốc
-- ============================================================
INSERT INTO employee_tax_info
    (employee_id, tax_code, number_of_dependents,
     social_insurance_no, social_insurance_salary,
     bank_name, bank_branch, bank_account_number, bank_account_holder,
     note, updated_by)
VALUES
-- Ban Giám đốc
(1,  '0123456789', 2, '7901234500001', 46800000, 'Vietcombank',   'CN Quận 1 TP.HCM',          '0011456789', 'Nguyễn Đình Hùng',  NULL, 1),
(2,  '0123456790', 2, '7801234500002', 46800000, 'Techcombank',   'CN Hội sở HCM',              '1903678901', 'Trần Quốc Minh',    NULL, 1),
(3,  '0123456791', 1, '8001234500003', 46800000, 'BIDV',          'CN Quận 1 TP.HCM',           '1200456789', 'Lê Thị Lan',        NULL, 1),
-- Nhân sự
(4,  '0123456792', 2, '8201234500004', 46800000, 'Vietcombank',   'CN Quận 10 TP.HCM',          '0011456790', 'Phạm Thị Mai',      NULL, 1),
(5,  '0123456793', 1, '8501234500005', 38000000, 'Agribank',      'CN Quận 5 TP.HCM',           '1400456789', 'Nguyễn Thị Hoa',    NULL, 1),
(6,  '0123456794', 0, '9001234500006', 22000000, 'Vietcombank',   'CN Quận 3 TP.HCM',           '0011456791', 'Lê Thị Thảo',       NULL, 1),
(7,  '0123456795', 1, '9201234500007', 20000000, 'BIDV',          'CN Quận 3 TP.HCM',           '1200456790', 'Trần Thị Linh',     NULL, 1),
(8,  '0123456796', 0, '8801234500008', 24000000, 'Techcombank',   'CN Bình Thạnh TP.HCM',       '1903678902', 'Hoàng Thị Trang',   NULL, 1),
(9,  '0123456797', 0, '9501234500009', 15000000, 'Vietcombank',   'CN Quận 3 TP.HCM',           '0011456792', 'Đỗ Văn Nam',        NULL, 1),
-- CNTT
(10, '0123456798', 2, '8001234500010', 46800000, 'Vietcombank',   'CN Quận 7 TP.HCM',           '0011456793', 'Võ Văn Tuấn',       NULL, 1),
(11, '0123456799', 1, '8301234500011', 46800000, 'Techcombank',   'CN Phú Mỹ Hưng Q7',          '1903678903', 'Nguyễn Văn Đức',    NULL, 1),
(12, '0123456800', 2, '8601234500012', 45000000, 'BIDV',          'CN Quận 1 TP.HCM',           '1200456791', 'Trần Văn Long',     NULL, 1),
(13, '0123456801', 1, '8801234500013', 42000000, 'Vietcombank',   'CN Quận 3 TP.HCM',           '0011456794', 'Phạm Minh Hiếu',    NULL, 1),
(14, '0123456802', 0, '8901234500014', 38000000, 'Agribank',      'CN Phú Nhuận TP.HCM',        '1400456790', 'Lê Hoàng Phong',    NULL, 1),
(15, '0123456803', 1, '8701234500015', 40000000, 'Techcombank',   'CN Tân Bình TP.HCM',         '1903678904', 'Nguyễn Đức Khánh',  NULL, 1),
(16, '0123456804', 0, '9001234500016', 36000000, 'BIDV',          'CN Tân Bình TP.HCM',         '1200456792', 'Trần Minh Hải',     NULL, 1),
(17, '0123456805', 0, '9301234500017', 28000000, 'Vietcombank',   'CN Tân Bình TP.HCM',         '0011456795', 'Lý Minh Hoàng',     NULL, 1),
(18, '0123456806', 0, '9401234500018', 26000000, 'Agribank',      'CN Tân Phú TP.HCM',          '1400456791', 'Đặng Thị Quỳnh',    NULL, 1),
(19, '0123456807', 0, '9701234500019', 18000000, 'Vietcombank',   'CN Tân Phú TP.HCM',          '0011456796', 'Ngô Văn An',        NULL, 1),
(20, '0123456808', 0, '9801234500020', 16000000, 'BIDV',          'CN Tân Phú TP.HCM',          '1200456793', 'Bùi Thanh Bình',    NULL, 1),
(21, '0123456809', 1, '8801234500021', 42000000, 'Techcombank',   'CN Quận 6 TP.HCM',           '1903678905', 'Mai Văn Cường',     NULL, 1),
(22, '0123456810', 1, '8901234500022', 38000000, 'Vietcombank',   'CN Quận 5 TP.HCM',           '0011456797', 'Trịnh Văn Dũng',    NULL, 1),
(23, '0123456811', 0, '9201234500023', 24000000, 'Agribank',      'CN Quận 5 TP.HCM',           '1400456792', 'Nguyễn Thị Thu',    NULL, 1),
(24, '0123456812', 0, '0101234500024',  8000000, 'Vietcombank',   'CN Quận 5 TP.HCM',           '0011456798', 'Lê Văn Đạt',        'Thực tập sinh', 1),
-- Tài chính
(25, '0123456813', 2, '7901234500025', 46800000, 'Vietcombank',   'CN Quận 10 TP.HCM',          '0011456799', 'Nguyễn Thị Hương',  NULL, 1),
(26, '0123456814', 1, '8201234500026', 40000000, 'BIDV',          'CN Quận 10 TP.HCM',          '1200456794', 'Trần Thị Phương',   NULL, 1),
(27, '0123456815', 2, '8101234500027', 45000000, 'Techcombank',   'CN Quận 10 TP.HCM',          '1903678906', 'Lê Thị Nga',        NULL, 1),
(28, '0123456816', 1, '9001234500028', 24000000, 'Agribank',      'CN Quận 10 TP.HCM',          '1400456793', 'Phạm Thị Vân',      NULL, 1),
(29, '0123456817', 0, '9101234500029', 22000000, 'Vietcombank',   'CN Quận 10 TP.HCM',          '0011456800', 'Hoàng Thị Yến',     NULL, 1),
(30, '0123456818', 1, '9301234500030', 20000000, 'BIDV',          'CN Quận 10 TP.HCM',          '1200456795', 'Vũ Thị Hạnh',       NULL, 1),
(31, '0123456819', 0, '8801234500031', 28000000, 'Techcombank',   'CN Quận 10 TP.HCM',          '1903678907', 'Đỗ Thị Thúy',       NULL, 1),
(32, '0123456820', 0, '9501234500032', 16000000, 'Agribank',      'CN Quận 10 TP.HCM',          '1400456794', 'Bùi Thị Loan',      NULL, 1),
-- Kinh doanh
(33, '0123456821', 2, '7801234500033', 46800000, 'Vietcombank',   'CN Quận 3 TP.HCM',           '0011456801', 'Trần Văn Thành',    NULL, 1),
(34, '0123456822', 1, '8101234500034', 45000000, 'BIDV',          'CN Quận 3 TP.HCM',           '1200456796', 'Nguyễn Văn Sơn',    NULL, 1),
(35, '0123456823', 2, '8401234500035', 38000000, 'Techcombank',   'CN Quận 1 TP.HCM',           '1903678908', 'Lê Văn Hùng',       NULL, 1),
(36, '0123456824', 0, '8501234500036', 36000000, 'Vietcombank',   'CN Quận 3 TP.HCM',           '0011456802', 'Phạm Văn Tiến',     NULL, 1),
(37, '0123456825', 1, '8901234500037', 28000000, 'Agribank',      'CN Quận 3 TP.HCM',           '1400456795', 'Hoàng Văn Duy',     NULL, 1),
(38, '0123456826', 0, '9101234500038', 26000000, 'BIDV',          'CN Quận 3 TP.HCM',           '1200456797', 'Nguyễn Thị Giang',  NULL, 1),
(39, '0123456827', 1, '9201234500039', 25000000, 'Vietcombank',   'CN Phú Nhuận TP.HCM',        '0011456803', 'Trần Văn Khôi',     NULL, 1),
(40, '0123456828', 0, '9301234500040', 24000000, 'Techcombank',   'CN Phú Nhuận TP.HCM',        '1903678909', 'Lê Thị Lý',         NULL, 1),
(41, '0123456829', 0, '9401234500041', 22000000, 'Agribank',      'CN Phú Nhuận TP.HCM',        '1400456796', 'Ngô Thị Mỹ',        NULL, 1),
(42, '0123456830', 0, '9501234500042', 20000000, 'Vietcombank',   'CN Bình Thạnh TP.HCM',       '0011456804', 'Bùi Thị Nhi',       NULL, 1),
(43, '0123456831', 1, '8801234500043', 30000000, 'BIDV',          'CN Bình Thạnh TP.HCM',       '1200456798', 'Đặng Thị Oanh',     NULL, 1),
(44, '0123456832', 0, '9601234500044', 16000000, 'Techcombank',   'CN Bình Thạnh TP.HCM',       '1903678910', 'Lê Văn Phát',       NULL, 1),
-- Marketing
(45, '0123456833', 2, '8301234500045', 46800000, 'Vietcombank',   'CN Quận 1 TP.HCM',           '0011456805', 'Nguyễn Thị Quyên',  NULL, 1),
(46, '0123456834', 0, '9101234500046', 26000000, 'Agribank',      'CN Quận 1 TP.HCM',           '1400456797', 'Trần Hồng Rose',    NULL, 1),
(47, '0123456835', 1, '9301234500047', 22000000, 'BIDV',          'CN Bình Thạnh TP.HCM',       '1200456799', 'Lê Thị Sương',      NULL, 1),
(48, '0123456836', 0, '9201234500048', 24000000, 'Vietcombank',   'CN Bình Thạnh TP.HCM',       '0011456806', 'Phạm Văn Tâm',      NULL, 1),
(49, '0123456837', 1, '9401234500049', 20000000, 'Techcombank',   'CN Bình Thạnh TP.HCM',       '1903678911', 'Hoàng Thị Uyên',    NULL, 1),
(50, '0123456838', 0, '9501234500050', 18000000, 'Agribank',      'CN Quận 3 TP.HCM',           '1400456798', 'Vũ Thị Vy',         NULL, 1),
-- Vận hành
(51, '0123456839', 2, '8001234500051', 42000000, 'Vietcombank',   'CN Quận 10 TP.HCM',          '0011456807', 'Nguyễn Văn Xuân',   NULL, 1),
(52, '0123456840', 1, '8301234500052', 35000000, 'BIDV',          'CN Quận 10 TP.HCM',          '1200456800', 'Trần Thị Yến',      'Đang nghỉ thai sản', 1),
(53, '0123456841', 0, '9201234500053', 18000000, 'Techcombank',   'CN Quận 10 TP.HCM',          '1903678912', 'Lê Văn Bách',       NULL, 1),
(54, '0123456842', 0, '9301234500054', 16000000, 'Vietcombank',   'CN Quận 10 TP.HCM',          '0011456808', 'Nguyễn Thị Chi',    NULL, 1),
(55, '0123456843', 1, '9401234500055', 15000000, 'Agribank',      'CN Quận 11 TP.HCM',          '1400456799', 'Phạm Văn Đào',      NULL, 1),
(56, '0123456844', 0, '9701234500056', 14000000, 'BIDV',          'CN Quận 11 TP.HCM',          '1200456801', 'Trần Thị Em',       NULL, 1),
(57, '0123456845', 1, '8501234500057', 15000000, 'Vietcombank',   'CN Quận 11 TP.HCM',          '0011456809', 'Nguyễn Văn Giải',   NULL, 1),
-- R&D
(58, '0123456846', 2, '7701234500058', 46800000, 'Vietcombank',   'CN Thủ Đức TP.HCM',          '0011456810', 'Trần Văn Hảo',      NULL, 1),
(59, '0123456847', 0, '8701234500059', 38000000, 'Techcombank',   'CN Thủ Đức TP.HCM',          '1903678913', 'Lê Văn Ích',        NULL, 1),
(60, '0123456848', 1, '8901234500060', 35000000, 'BIDV',          'CN Thủ Đức TP.HCM',          '1200456802', 'Nguyễn Văn Khang',  NULL, 1),
(61, '0123456849', 0, '9001234500061', 32000000, 'Agribank',      'CN Quận 9 TP.HCM',           '1400456800', 'Phạm Văn Lâm',      NULL, 1);

-- ============================================================
-- ALLOWANCES
-- Phụ cấp theo chức vụ và vị trí - hiệu lực từ 2026-01-01
-- is_taxable=0: phụ cấp chức vụ và ăn ca/đi lại trong ngưỡng miễn thuế
-- ============================================================
INSERT INTO allowances
    (employee_id, allowance_type, name, amount,
     is_taxable, is_insurance, effective_date, expiry_date,
     is_active, note, created_by)
VALUES
-- ── CEO (emp=1) ─────────────────────────────────────────────
(1, 'RESPONSIBILITY', 'Phụ cấp chức vụ Tổng Giám đốc', 5000000, FALSE, FALSE, '2026-01-01', NULL, TRUE, 'Cấp theo quyết định HĐQT', 1),
(1, 'MEAL',           'Phụ cấp ăn ca',                   730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, 'Miễn thuế theo TT 11/2018', 1),
(1, 'TRANSPORT',      'Phụ cấp đi lại',                 2000000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(1, 'PHONE',          'Phụ cấp điện thoại',              700000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── CTO (emp=2) ─────────────────────────────────────────────
(2, 'RESPONSIBILITY', 'Phụ cấp chức vụ Giám đốc Công nghệ', 3500000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(2, 'MEAL',           'Phụ cấp ăn ca',   730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(2, 'TRANSPORT',      'Phụ cấp đi lại', 1500000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(2, 'PHONE',          'Phụ cấp điện thoại', 500000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── CFO (emp=3) ─────────────────────────────────────────────
(3, 'RESPONSIBILITY', 'Phụ cấp chức vụ Giám đốc Tài chính', 3500000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(3, 'MEAL',           'Phụ cấp ăn ca',   730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(3, 'TRANSPORT',      'Phụ cấp đi lại', 1500000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(3, 'PHONE',          'Phụ cấp điện thoại', 500000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── GĐ Nhân sự (emp=4) ──────────────────────────────────────
(4, 'RESPONSIBILITY', 'Phụ cấp chức vụ Giám đốc Nhân sự', 3000000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(4, 'MEAL',           'Phụ cấp ăn ca',   730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(4, 'TRANSPORT',      'Phụ cấp đi lại', 1200000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(4, 'PHONE',          'Phụ cấp điện thoại', 400000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── Phó phòng NS (emp=5) ────────────────────────────────────
(5, 'RESPONSIBILITY', 'Phụ cấp chức vụ Phó phòng Nhân sự', 2000000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(5, 'MEAL',           'Phụ cấp ăn ca',  730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(5, 'TRANSPORT',      'Phụ cấp đi lại', 800000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── NS Staff (emp=6,7,8,9) ──────────────────────────────────
(6,  'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(6,  'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(7,  'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(7,  'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(8,  'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(8,  'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(9,  'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── GĐ CNTT (emp=10) ────────────────────────────────────────
(10, 'RESPONSIBILITY', 'Phụ cấp chức vụ Giám đốc CNTT', 3000000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(10, 'MEAL',           'Phụ cấp ăn ca',   730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(10, 'TRANSPORT',      'Phụ cấp đi lại', 1200000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(10, 'PHONE',          'Phụ cấp điện thoại', 400000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── Phó phòng IT (emp=11) ───────────────────────────────────
(11, 'RESPONSIBILITY', 'Phụ cấp chức vụ Phó phòng CNTT', 2000000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(11, 'MEAL',           'Phụ cấp ăn ca',  730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(11, 'TRANSPORT',      'Phụ cấp đi lại', 800000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── Backend Lead (emp=12) ────────────────────────────────────
(12, 'RESPONSIBILITY', 'Phụ cấp chức vụ Backend Lead', 2000000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(12, 'MEAL',           'Phụ cấp ăn ca',  730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(12, 'TRANSPORT',      'Phụ cấp đi lại', 800000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── Frontend Lead (emp=13) ───────────────────────────────────
(13, 'RESPONSIBILITY', 'Phụ cấp chức vụ Frontend Lead', 2000000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(13, 'MEAL',           'Phụ cấp ăn ca',  730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(13, 'TRANSPORT',      'Phụ cấp đi lại', 800000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── Senior Dev (emp=14,15,16) ────────────────────────────────
(14, 'RESPONSIBILITY', 'Phụ cấp chức vụ Senior Developer', 1500000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(14, 'MEAL',           'Phụ cấp ăn ca',  730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(14, 'TRANSPORT',      'Phụ cấp đi lại', 500000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(15, 'RESPONSIBILITY', 'Phụ cấp chức vụ Senior Developer', 1500000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(15, 'MEAL',           'Phụ cấp ăn ca',  730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(15, 'TRANSPORT',      'Phụ cấp đi lại', 500000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(16, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(16, 'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── Mid/Junior Dev (emp=17,18,19,20) ─────────────────────────
(17, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(17, 'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(18, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(18, 'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(19, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(19, 'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(20, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── DevOps (emp=21) ──────────────────────────────────────────
(21, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(21, 'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── QA Lead (emp=22) ─────────────────────────────────────────
(22, 'RESPONSIBILITY', 'Phụ cấp chức vụ QA Lead', 1500000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(22, 'MEAL',           'Phụ cấp ăn ca',  730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(22, 'TRANSPORT',      'Phụ cấp đi lại', 500000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── QA Eng + Intern (emp=23,24) ──────────────────────────────
(23, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(23, 'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(24, 'MEAL', 'Phụ cấp ăn ca thực tập sinh', 500000, FALSE, FALSE, '2026-01-01', '2026-12-31', TRUE, 'Phụ cấp thực tập', 1),
-- ── GĐ Tài chính (emp=25) ────────────────────────────────────
(25, 'RESPONSIBILITY', 'Phụ cấp chức vụ Giám đốc Tài chính', 3000000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(25, 'MEAL',           'Phụ cấp ăn ca',   730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(25, 'TRANSPORT',      'Phụ cấp đi lại', 1200000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(25, 'PHONE',          'Phụ cấp điện thoại', 400000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── Phó phòng TC (emp=26) ────────────────────────────────────
(26, 'RESPONSIBILITY', 'Phụ cấp chức vụ Phó phòng Tài chính', 2000000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(26, 'MEAL',           'Phụ cấp ăn ca',  730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(26, 'TRANSPORT',      'Phụ cấp đi lại', 800000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── Kế toán trưởng (emp=27) ──────────────────────────────────
(27, 'RESPONSIBILITY', 'Phụ cấp chức vụ Kế toán trưởng', 2000000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(27, 'MEAL',           'Phụ cấp ăn ca',  730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(27, 'TRANSPORT',      'Phụ cấp đi lại', 800000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── TC Staff (emp=28,29,30,31,32) ────────────────────────────
(28, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(28, 'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(29, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(29, 'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(30, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(30, 'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(31, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(31, 'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(32, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── GĐ KD (emp=33) ───────────────────────────────────────────
(33, 'RESPONSIBILITY', 'Phụ cấp chức vụ Giám đốc Kinh doanh', 3000000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(33, 'MEAL',           'Phụ cấp ăn ca',   730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(33, 'TRANSPORT',      'Phụ cấp đi lại', 1200000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(33, 'PHONE',          'Phụ cấp điện thoại', 400000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── Phó phòng KD (emp=34) ────────────────────────────────────
(34, 'RESPONSIBILITY', 'Phụ cấp chức vụ Phó phòng Kinh doanh', 2000000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(34, 'MEAL',           'Phụ cấp ăn ca',  730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(34, 'TRANSPORT',      'Phụ cấp đi lại', 800000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── Trưởng nhóm KD (emp=35,36) ───────────────────────────────
(35, 'RESPONSIBILITY', 'Phụ cấp chức vụ Trưởng nhóm Kinh doanh', 1500000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(35, 'MEAL',           'Phụ cấp ăn ca',  730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(35, 'TRANSPORT',      'Phụ cấp đi lại', 500000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(36, 'RESPONSIBILITY', 'Phụ cấp chức vụ Trưởng nhóm Kinh doanh', 1500000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(36, 'MEAL',           'Phụ cấp ăn ca',  730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(36, 'TRANSPORT',      'Phụ cấp đi lại', 500000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── KD Staff (emp=37..44) ────────────────────────────────────
(37, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(37, 'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(38, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(38, 'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(39, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(39, 'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(40, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(40, 'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(41, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(41, 'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(42, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(42, 'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── Pre-sales (emp=43) ───────────────────────────────────────
(43, 'RESPONSIBILITY', 'Phụ cấp chức vụ Pre-sales Specialist', 1000000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(43, 'MEAL',           'Phụ cấp ăn ca',  730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(43, 'TRANSPORT',      'Phụ cấp đi lại', 500000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── Sales Support (emp=44) ───────────────────────────────────
(44, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(44, 'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── GĐ Marketing (emp=45) ────────────────────────────────────
(45, 'RESPONSIBILITY', 'Phụ cấp chức vụ Giám đốc Marketing', 3000000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(45, 'MEAL',           'Phụ cấp ăn ca',   730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(45, 'TRANSPORT',      'Phụ cấp đi lại', 1200000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(45, 'PHONE',          'Phụ cấp điện thoại', 400000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── MKT Staff (emp=46..50) ───────────────────────────────────
(46, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(46, 'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(47, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(47, 'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(48, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(48, 'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(49, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(49, 'TRANSPORT', 'Phụ cấp đi lại', 300000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(50, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── GĐ Vận hành (emp=51) ─────────────────────────────────────
(51, 'RESPONSIBILITY', 'Phụ cấp chức vụ Giám đốc Vận hành', 3000000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(51, 'MEAL',           'Phụ cấp ăn ca',   730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(51, 'TRANSPORT',      'Phụ cấp đi lại', 1200000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(51, 'PHONE',          'Phụ cấp điện thoại', 400000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── Phó phòng VH đang nghỉ thai sản (emp=52) ────────────────
(52, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', '2026-12-31', TRUE, 'Duy trì trong thời gian nghỉ thai sản', 1),
-- ── VH Staff (emp=53..57) ────────────────────────────────────
(53, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(54, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(55, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(56, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(57, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── GĐ R&D (emp=58) ──────────────────────────────────────────
(58, 'RESPONSIBILITY', 'Phụ cấp chức vụ Giám đốc R&D', 3000000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(58, 'MEAL',           'Phụ cấp ăn ca',   730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(58, 'TRANSPORT',      'Phụ cấp đi lại', 1200000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(58, 'PHONE',          'Phụ cấp điện thoại', 400000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
-- ── Nghiên cứu viên (emp=59,60,61) ───────────────────────────
(59, 'RESPONSIBILITY', 'Phụ cấp chức vụ Nghiên cứu viên', 1000000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(59, 'MEAL',           'Phụ cấp ăn ca',  730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(59, 'TRANSPORT',      'Phụ cấp đi lại', 500000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(60, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(60, 'TRANSPORT', 'Phụ cấp đi lại', 500000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(61, 'MEAL', 'Phụ cấp ăn ca', 730000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1),
(61, 'TRANSPORT', 'Phụ cấp đi lại', 500000, FALSE, FALSE, '2026-01-01', NULL, TRUE, NULL, 1);

-- ============================================================
-- PROJECTS (chuẩn hóa theo quý 2026 toàn công ty)
-- ============================================================
INSERT INTO projects
    (id, project_code, project_name, description,
     department_id, manager_id, start_date, end_date,
     status, is_active, created_at, updated_at)
VALUES
-- Q1 2026
(1, 'PRJ-2026-Q1-001', 'Nâng cấp hệ thống MetaHRM v2.0',
   'Phát triển và nâng cấp toàn diện hệ thống HRM: tích hợp chấm công Face ID, cải tiến module lương, dashboard analytics.',
   3, 10, '2026-01-05', '2026-03-31', 'COMPLETED', TRUE,
   '2025-12-20 08:00:00', '2026-04-01 08:00:00'),
(2, 'PRJ-2026-Q1-002', 'Chiến dịch Marketing Tết 2026',
   'Lên kế hoạch và triển khai chiến dịch marketing dịp Tết Nguyên đán: digital ads, event, content.',
   6, 45, '2026-01-02', '2026-02-28', 'COMPLETED', TRUE,
   '2025-12-15 08:00:00', '2026-03-01 08:00:00'),
(3, 'PRJ-2026-Q1-003', 'Chuẩn hóa quy trình Vận hành nội bộ',
   'Rà soát và chuẩn hóa toàn bộ quy trình hành chính vận hành: quản lý tài sản, phòng họp, xe công ty.',
   7, 51, '2026-01-10', '2026-03-31', 'COMPLETED', TRUE,
   '2026-01-05 08:00:00', '2026-04-02 08:00:00'),
-- Q2 2026
(4, 'PRJ-2026-Q2-001', 'Phát triển tính năng MetaHRM v2.1',
   'Sprint Q2: module tuyển dụng CV pipeline, thông báo hợp đồng tự động, báo cáo HR nâng cao.',
   3, 10, '2026-04-01', '2026-06-30', 'IN_PROGRESS', TRUE,
   '2026-03-25 08:00:00', '2026-04-01 08:00:00'),
(5, 'PRJ-2026-Q2-002', 'Chiến dịch Marketing Q2 - Brand Awareness',
   'Tăng nhận diện thương hiệu TechCorp: SEO/SEM, LinkedIn ads, webinar series, case study.',
   6, 45, '2026-04-01', '2026-06-30', 'IN_PROGRESS', TRUE,
   '2026-03-20 08:00:00', '2026-04-01 08:00:00'),
(6, 'PRJ-2026-Q2-003', 'Nghiên cứu AI ứng dụng trong HR',
   'Nghiên cứu và đề xuất ứng dụng AI/ML trong tuyển dụng, dự báo nghỉ việc, đánh giá hiệu suất.',
   8, 58, '2026-04-15', '2026-06-30', 'IN_PROGRESS', TRUE,
   '2026-04-10 08:00:00', '2026-04-15 08:00:00'),
-- Q3 2026 (planning)
(7, 'PRJ-2026-Q3-001', 'Tích hợp chấm công sinh trắc học toàn hệ thống',
   'Mở rộng chấm công Face ID + vân tay cho tất cả chi nhánh, tích hợp với phần cứng thiết bị.',
   3, 10, '2026-07-01', '2026-09-30', 'PLANNING', TRUE,
   '2026-05-01 08:00:00', '2026-05-01 08:00:00'),
(8, 'PRJ-2026-Q3-002', 'Mở rộng thị trường B2B miền Bắc',
   'Phát triển mạng lưới đối tác và khách hàng doanh nghiệp khu vực Hà Nội, Hải Phòng, Hải Dương.',
   5, 33, '2026-07-01', '2026-09-30', 'PLANNING', TRUE,
   '2026-05-01 08:00:00', '2026-05-01 08:00:00'),
-- Q4 2026 (planning)
(9, 'PRJ-2026-Q4-001', 'Nâng cấp bảo mật và kiểm toán hệ thống',
   'Pentest, vá lỗ hổng bảo mật, triển khai 2FA toàn hệ thống, audit log nâng cao.',
   3, 10, '2026-10-01', '2026-12-31', 'PLANNING', TRUE,
   '2026-05-15 08:00:00', '2026-05-15 08:00:00'),
(10, 'PRJ-2026-Q4-002', 'Tổng kết năm 2026 & Kế hoạch chiến lược 2027',
   'Đánh giá toàn bộ hoạt động năm 2026, lập kế hoạch OKR và ngân sách cho năm 2027.',
   1, 1, '2026-11-01', '2026-12-31', 'PLANNING', TRUE,
   '2026-05-15 08:00:00', '2026-05-15 08:00:00');

-- ============================================================
-- LEAVE BALANCES 2026
-- Dựa trên leave_types: 1=ANNUAL(12), 2=SICK(30), 3=MATERNITY(180),
-- 4=PATERNITY(14), 5=SPECIAL(3), 6=UNPAID(0), 7=COMPENSATORY(0)
-- used_days: dựa theo leave_requests thực tế
-- ============================================================
INSERT INTO leave_balances
    (employee_id, leave_type_id, year,
     allocated_days, used_days, pending_days, carry_forward_days, encashed_days)
VALUES
-- emp 1 (CEO - Nguyễn Đình Hùng)
(1,1,2026, 12,0,0,0,0),(1,2,2026, 30,2,0,0,0),(1,3,2026, 0,0,0,0,0),(1,4,2026, 0,0,0,0,0),(1,5,2026, 3,0,0,0,0),
-- emp 2 (CTO)
(2,1,2026, 12,0,0,0,0),(2,2,2026, 30,0,0,0,0),(2,5,2026, 3,0,0,0,0),
-- emp 3 (CFO)
(3,1,2026, 12,0,0,0,0),(3,2,2026, 30,0,0,0,0),(3,5,2026, 3,0,0,0,0),
-- emp 4 (GĐ NS)
(4,1,2026, 12,0,0,0,0),(4,2,2026, 30,0,0,0,0),(4,5,2026, 3,0,0,0,0),
-- emp 5 (Phó phòng NS)
(5,1,2026, 12,0,0,0,0),(5,2,2026, 30,0,0,0,0),(5,5,2026, 3,0,0,0,0),
-- emp 6 (HR Tuyển dụng)
(6,1,2026, 12,0,0,0,0),(6,2,2026, 30,0,0,0,0),(6,5,2026, 3,0,0,0,0),
-- emp 7 (HR Đào tạo)
(7,1,2026, 12,0,0,0,0),(7,2,2026, 30,0,0,0,0),(7,5,2026, 3,0,0,0,0),
-- emp 8 (HR Lương)
(8,1,2026, 12,0.5,0,0,0),(8,2,2026, 30,0,0,0,0),(8,5,2026, 3,0,0,0,0),
-- emp 9 (HR HC)
(9,1,2026, 12,0,0,0,0),(9,2,2026, 30,0,0,0,0),(9,5,2026, 3,0,0,0,0),
-- emp 10 (GĐ CNTT)
(10,1,2026, 12,0,0,0,0),(10,2,2026, 30,0,0,0,0),(10,5,2026, 3,0,0,0,0),
-- emp 11 (Phó phòng IT)
(11,1,2026, 12,0,0,0,0),(11,2,2026, 30,0,0,0,0),(11,5,2026, 3,0,0,0,0),
-- emp 12 (Backend Lead)
(12,1,2026, 12,0,0,0,0),(12,2,2026, 30,0,0,0,0),(12,5,2026, 3,0,0,0,0),
-- emp 13 (Frontend Lead)
(13,1,2026, 12,0,0,0,0),(13,2,2026, 30,0,0,0,0),(13,5,2026, 3,0,0,0,0),
-- emp 14 (Senior Dev - có leave request thực tế: 3 ngày sick đã dùng)
(14,1,2026, 12,0,0,0,0),(14,2,2026, 30,3,0,0,0),(14,5,2026, 3,0,0,0,0),
-- emp 15 (Senior Dev - có 7 ngày annual đã dùng)
(15,1,2026, 12,7,0,0,0),(15,2,2026, 30,0,0,0,0),(15,5,2026, 3,0,0,0,0),
-- emp 16 (Senior Dev)
(16,1,2026, 12,0,0,0,0),(16,2,2026, 30,0,0,0,0),(16,5,2026, 3,0,0,0,0),
-- emp 17 (Mid Dev - 3 ngày annual đã dùng)
(17,1,2026, 12,3,0,0,0),(17,2,2026, 30,0,0,0,0),(17,5,2026, 3,0,0,0,0),
-- emp 18 (Mid Dev)
(18,1,2026, 12,0,0,0,0),(18,2,2026, 30,0,0,0,0),(18,3,2026, 180,0,0,0,0),(18,5,2026, 3,0,0,0,0),
-- emp 19 (Junior Dev)
(19,1,2026, 12,0,0,0,0),(19,2,2026, 30,0,0,0,0),(19,3,2026, 180,0,0,0,0),(19,5,2026, 3,0,0,0,0),
-- emp 20 (Junior Dev)
(20,1,2026, 12,0,0,0,0),(20,2,2026, 30,0,0,0,0),(20,5,2026, 3,0,0,0,0),
-- emp 21 (DevOps)
(21,1,2026, 12,0,0,0,0),(21,2,2026, 30,0,0,0,0),(21,5,2026, 3,0,0,0,0),
-- emp 22 (QA Lead)
(22,1,2026, 12,0,0,0,0),(22,2,2026, 30,0,0,0,0),(22,5,2026, 3,0,0,0,0),
-- emp 23 (QA Eng)
(23,1,2026, 12,0,0,0,0),(23,2,2026, 30,0,0,0,0),(23,3,2026, 180,0,0,0,0),(23,5,2026, 3,0,0,0,0),
-- emp 24 (Intern - chỉ 6 tháng, không có phép năm đầy đủ)
(24,1,2026, 6,0,0,0,0),(24,2,2026, 30,0,0,0,0),
-- emp 25 (GĐ TC)
(25,1,2026, 12,0,0,0,0),(25,2,2026, 30,0,0,0,0),(25,3,2026, 180,0,0,0,0),(25,5,2026, 3,0,0,0,0),
-- emp 26 (Phó TC)
(26,1,2026, 12,0,0,0,0),(26,2,2026, 30,0,0,0,0),(26,3,2026, 180,0,0,0,0),(26,5,2026, 3,0,0,0,0),
-- emp 27 (KT Trưởng)
(27,1,2026, 12,0,0,0,0),(27,2,2026, 30,0,0,0,0),(27,3,2026, 180,0,0,0,0),(27,5,2026, 3,0,0,0,0),
-- emp 28 (KT Viên)
(28,1,2026, 12,3,0,0,0),(28,2,2026, 30,0,0,0,0),(28,3,2026, 180,0,0,0,0),(28,5,2026, 3,0,0,0,0),
-- emp 29..32
(29,1,2026, 12,0,0,0,0),(29,2,2026, 30,0,0,0,0),(29,3,2026, 180,0,0,0,0),(29,5,2026, 3,0,0,0,0),
(30,1,2026, 12,0,0,0,0),(30,2,2026, 30,0,0,0,0),(30,3,2026, 180,0,0,0,0),(30,5,2026, 3,0,0,0,0),
(31,1,2026, 12,0,0,0,0),(31,2,2026, 30,0,0,0,0),(31,3,2026, 180,0,0,0,0),(31,5,2026, 3,0,0,0,0),
(32,1,2026, 12,0,0,0,0),(32,2,2026, 30,0,0,0,0),(32,3,2026, 180,0,0,0,0),(32,5,2026, 3,0,0,0,0),
-- emp 33 (GĐ KD)
(33,1,2026, 12,0,0,0,0),(33,2,2026, 30,0,0,0,0),(33,5,2026, 3,0,0,0,0),
-- emp 34..36
(34,1,2026, 12,0,0,0,0),(34,2,2026, 30,0,0,0,0),(34,5,2026, 3,0,0,0,0),
(35,1,2026, 12,0,0,0,0),(35,2,2026, 30,0,0,0,0),(35,5,2026, 3,0,0,0,0),
(36,1,2026, 12,0,0,0,0),(36,2,2026, 30,0,0,0,0),(36,5,2026, 3,0,0,0,0),
-- emp 37..44
(37,1,2026, 12,0,0,0,0),(37,2,2026, 30,0,0,0,0),(37,5,2026, 3,0,0,0,0),
(38,1,2026, 12,0,3,0,0),(38,2,2026, 30,0,0,0,0),(38,5,2026, 3,0,0,0,0),
(39,1,2026, 12,0,0,0,0),(39,2,2026, 30,0,0,0,0),(39,5,2026, 3,0,0,0,0),
(40,1,2026, 12,0,0,0,0),(40,2,2026, 30,0,0,0,0),(40,5,2026, 3,3,0,0,0),
(41,1,2026, 12,0,0,0,0),(41,2,2026, 30,0,0,0,0),(41,3,2026, 180,0,0,0,0),(41,5,2026, 3,0,0,0,0),
(42,1,2026, 12,0,0,0,0),(42,2,2026, 30,0,0,0,0),(42,3,2026, 180,0,0,0,0),(42,5,2026, 3,0,0,0,0),
(43,1,2026, 12,0,0,0,0),(43,2,2026, 30,0,0,0,0),(43,3,2026, 180,0,0,0,0),(43,5,2026, 3,0,0,0,0),
(44,1,2026, 12,0,0,0,0),(44,2,2026, 30,0,0,0,0),(44,5,2026, 3,0,0,0,0),
-- emp 45 (GĐ MKT)
(45,1,2026, 12,0,0,0,0),(45,2,2026, 30,0,0,0,0),(45,3,2026, 180,0,0,0,0),(45,5,2026, 3,0,0,0,0),
-- emp 46 (MKT - nghỉ thai sản 180 ngày từ 01/11/2026)
(46,1,2026, 12,0,0,0,0),(46,2,2026, 30,0,0,0,0),(46,3,2026, 180,0,180,0,0),(46,5,2026, 3,0,0,0,0),
-- emp 47 (MKT - pending 3 ngày annual)
(47,1,2026, 12,0,3,0,0),(47,2,2026, 30,0,0,0,0),(47,3,2026, 180,0,0,0,0),(47,5,2026, 3,0,0,0,0),
-- emp 48..50
(48,1,2026, 12,0,0,0,0),(48,2,2026, 30,0,0,0,0),(48,5,2026, 3,0,0,0,0),
(49,1,2026, 12,0,0,0,0),(49,2,2026, 30,0,0,0,0),(49,3,2026, 180,0,0,0,0),(49,5,2026, 3,0,0,0,0),
(50,1,2026, 12,0,0,0,0),(50,2,2026, 30,0,0,0,0),(50,3,2026, 180,0,0,0,0),(50,5,2026, 3,0,0,0,0),
-- emp 51 (GĐ VH)
(51,1,2026, 12,0,0,0,0),(51,2,2026, 30,0,0,0,0),(51,5,2026, 3,0,0,0,0),
-- emp 52 (ON_LEAVE thai sản - 180 ngày đã dùng)
(52,1,2026, 12,0,0,0,0),(52,3,2026, 180,0,0,0,0),
-- emp 53..57
(53,1,2026, 12,0,3,0,0),(53,2,2026, 30,0,0,0,0),(53,5,2026, 3,0,0,0,0),
(54,1,2026, 12,0,0,0,0),(54,2,2026, 30,0,0,0,0),(54,3,2026, 180,0,0,0,0),(54,5,2026, 3,0,0,0,0),
(55,1,2026, 12,0,0,0,0),(55,2,2026, 30,0,0,0,0),(55,5,2026, 3,0,0,0,0),
(56,1,2026, 12,0,0,0,0),(56,2,2026, 30,0,0,0,0),(56,3,2026, 180,0,0,0,0),(56,5,2026, 3,0,0,0,0),
(57,1,2026, 12,0,0,0,0),(57,2,2026, 30,0,0,0,0),(57,5,2026, 3,0,0,0,0),
-- emp 58 (GĐ R&D)
(58,1,2026, 12,0,0,0,0),(58,2,2026, 30,0,0,0,0),(58,5,2026, 3,0,0,0,0),
-- emp 59..61
(59,1,2026, 12,0,0,0,0),(59,2,2026, 30,0,0,0,0),(59,5,2026, 3,0,0,0,0),
(60,1,2026, 12,0,0,0,0),(60,2,2026, 30,0,0,0,0),(60,5,2026, 3,0,0,0,0),
(61,1,2026, 12,0,0,0,0),(61,2,2026, 30,0,0,0,0),(61,5,2026, 3,0,0,0,0);
