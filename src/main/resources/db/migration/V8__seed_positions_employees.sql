-- ============================================================
-- V8: SEED POSITIONS & EMPLOYEES
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- positions
-- Cấu trúc cây theo 6 level:
--   1 = CEO
--   2 = C-Level (CTO, CFO, GĐ chức năng)
--   3 = Phó phòng / Trưởng nhóm cấp phòng
--   4 = Trưởng nhóm chuyên môn / Chuyên viên cao cấp
--   5 = Nhân viên / Junior
--   6 = Thực tập sinh
--
-- Lưu ý: min_salary/max_salary để NULL — sẽ cấu hình riêng
--         theo chính sách lương từng công ty
-- ------------------------------------------------------------
INSERT INTO positions
    (id, position_code, position_name, description,
     min_salary, max_salary, is_active,
     department_id, parent_position_id, level_order, sort_order,
     created_at, updated_at)
VALUES
-- ── Level 1: CEO ─────────────────────────────────────────────
(1,  'CEO',          'Tổng Giám đốc',
     'Người đứng đầu công ty, chịu trách nhiệm toàn bộ hoạt động kinh doanh.',
     NULL, NULL, TRUE, 1, NULL, 1, 1,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),

-- ── Level 2: C-Level (báo cáo lên CEO) ───────────────────────
(2,  'CTO',          'Giám đốc Công nghệ',
     'Phụ trách chiến lược công nghệ, kiến trúc hệ thống và định hướng kỹ thuật toàn công ty.',
     NULL, NULL, TRUE, 1, 1, 2, 1,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(3,  'CFO',          'Giám đốc Tài chính',
     'Phụ trách tài chính, kế toán, kiểm soát chi phí và báo cáo tài chính toàn công ty.',
     NULL, NULL, TRUE, 1, 1, 2, 2,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(4,  'HR_DIR',       'Giám đốc Nhân sự',
     'Phụ trách chiến lược nhân sự, tuyển dụng, đào tạo và phát triển nhân lực.',
     NULL, NULL, TRUE, 1, 1, 2, 3,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(5,  'SALES_DIR',    'Giám đốc Kinh doanh',
     'Phụ trách chiến lược kinh doanh, phát triển thị trường và doanh thu.',
     NULL, NULL, TRUE, 1, 1, 2, 4,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(6,  'MKT_DIR',      'Giám đốc Marketing',
     'Phụ trách chiến lược marketing, thương hiệu và truyền thông.',
     NULL, NULL, TRUE, 1, 1, 2, 5,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(7,  'OPS_DIR',      'Giám đốc Vận hành',
     'Phụ trách hoạt động vận hành nội bộ, hành chính và hạ tầng.',
     NULL, NULL, TRUE, 1, 1, 2, 6,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(8,  'RD_DIR',       'Giám đốc R&D',
     'Phụ trách nghiên cứu & phát triển sản phẩm, công nghệ mới.',
     NULL, NULL, TRUE, 1, 1, 2, 7,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),

-- ── Level 3: GĐ/Phó phòng chức năng (báo cáo lên GĐ tương ứng) ──

-- Phòng CNTT
(9,  'IT_DIR',       'Giám đốc CNTT',
     'Quản lý toàn bộ hoạt động phòng CNTT, báo cáo trực tiếp lên CTO.',
     NULL, NULL, TRUE, 3, 2, 3, 1,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(10, 'IT_DEP',       'Phó phòng Công nghệ thông tin',
     'Hỗ trợ GĐ CNTT điều hành, kiêm quản lý hạ tầng kỹ thuật.',
     NULL, NULL, TRUE, 3, 9, 3, 2,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),

-- Phòng Nhân sự
(11, 'HR_DEP',       'Phó phòng Nhân sự',
     'Hỗ trợ GĐ Nhân sự, điều phối tuyển dụng và C&B.',
     NULL, NULL, TRUE, 2, 4, 3, 1,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),

-- Phòng Tài chính
(12, 'FIN_DEP',      'Phó phòng Tài chính - Kế toán',
     'Hỗ trợ CFO trong điều hành tài chính hàng ngày.',
     NULL, NULL, TRUE, 4, 3, 3, 1,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),

-- Phòng Kinh doanh
(13, 'SALES_DEP',    'Phó phòng Kinh doanh',
     'Hỗ trợ GĐ Kinh doanh, quản lý các nhóm sales.',
     NULL, NULL, TRUE, 5, 5, 3, 1,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),

-- Phòng Marketing (Trưởng nhóm — đang tuyển, chưa có người)
(14, 'MKT_DEP',      'Trưởng nhóm Marketing',
     'Quản lý trực tiếp nhóm triển khai marketing: digital, content, design, SEO, event.',
     NULL, NULL, TRUE, 6, 6, 3, 1,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),

-- Phòng Vận hành
(15, 'OPS_DEP',      'Phó phòng Vận hành',
     'Hỗ trợ GĐ Vận hành, quản lý nhân viên hành chính và dịch vụ nội bộ.',
     NULL, NULL, TRUE, 7, 7, 3, 1,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),

-- ── Level 4: Trưởng nhóm / Chuyên viên cao cấp ───────────────

-- CNTT
(16, 'TECH_LEAD_BE', 'Backend Lead',
     'Trưởng nhóm Backend, dẫn dắt thiết kế API, kiến trúc service và code review.',
     NULL, NULL, TRUE, 3, 10, 4, 1,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(17, 'TECH_LEAD_FE', 'Frontend Lead',
     'Trưởng nhóm Frontend, dẫn dắt UI/UX implementation, component library và performance.',
     NULL, NULL, TRUE, 3, 10, 4, 2,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(18, 'DEVOPS',       'DevOps Engineer',
     'Quản lý CI/CD, hạ tầng cloud, monitoring và bảo mật hệ thống.',
     NULL, NULL, TRUE, 3, 10, 4, 3,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(19, 'QA_LEAD',      'QA Lead',
     'Trưởng nhóm QA, xây dựng quy trình kiểm thử và chiến lược chất lượng.',
     NULL, NULL, TRUE, 3, 10, 4, 4,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),

-- Nhân sự
(20, 'HR_REC',       'Chuyên viên Tuyển dụng',
     'Phụ trách toàn bộ quy trình tuyển dụng từ JD đến onboarding.',
     NULL, NULL, TRUE, 2, 11, 4, 1,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(21, 'HR_TRN',       'Chuyên viên Đào tạo & Phát triển',
     'Xây dựng và triển khai chương trình đào tạo nội bộ.',
     NULL, NULL, TRUE, 2, 11, 4, 2,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(22, 'HR_PAY',       'Chuyên viên Tiền lương & C&B',
     'Tính lương, BHXH, thuế TNCN và các chế độ phúc lợi.',
     NULL, NULL, TRUE, 2, 11, 4, 3,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(23, 'HR_ADM',       'Chuyên viên Hành chính Nhân sự',
     'Quản lý hồ sơ nhân sự, hợp đồng lao động và thủ tục hành chính.',
     NULL, NULL, TRUE, 2, 11, 4, 4,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),

-- Tài chính
(24, 'CHIEF_ACC',    'Kế toán trưởng',
     'Phụ trách kế toán tổng hợp, lập báo cáo tài chính và kiểm soát nội bộ.',
     NULL, NULL, TRUE, 4, 12, 4, 1,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),

-- Kinh doanh
(25, 'SALES_LEAD',   'Trưởng nhóm Kinh doanh',
     'Quản lý nhóm sales, đặt KPI và hỗ trợ chốt hợp đồng lớn.',
     NULL, NULL, TRUE, 5, 13, 4, 1,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(26, 'PRESALES',     'Pre-sales Specialist',
     'Tư vấn giải pháp kỹ thuật cho khách hàng, hỗ trợ đội sales trong giai đoạn đề xuất.',
     NULL, NULL, TRUE, 5, 13, 4, 2,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(27, 'SALES_SUP',    'Chuyên viên Hỗ trợ Kinh doanh',
     'Xử lý hợp đồng, báo giá, quản lý dữ liệu khách hàng (CRM).',
     NULL, NULL, TRUE, 5, 13, 4, 3,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),

-- Marketing
(28, 'DIGITAL_MKT',  'Digital Marketing Specialist',
     'Quản lý quảng cáo trực tuyến (Google Ads, Meta Ads) và performance marketing.',
     NULL, NULL, TRUE, 6, 14, 4, 1,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(29, 'CONTENT_MKT',  'Content Marketing Specialist',
     'Sản xuất nội dung blog, mạng xã hội và tài liệu marketing.',
     NULL, NULL, TRUE, 6, 14, 4, 2,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(30, 'DESIGNER',     'Graphic Designer',
     'Thiết kế visual identity, ấn phẩm truyền thông và tài nguyên số.',
     NULL, NULL, TRUE, 6, 14, 4, 3,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(31, 'SEO_SPEC',     'SEO Specialist',
     'Tối ưu hóa công cụ tìm kiếm, phân tích từ khóa và xây dựng backlink.',
     NULL, NULL, TRUE, 6, 14, 4, 4,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(32, 'EVENT_COORD',  'Event Coordinator',
     'Lên kế hoạch và tổ chức sự kiện nội bộ, hội thảo và hoạt động thương hiệu.',
     NULL, NULL, TRUE, 6, 14, 4, 5,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),

-- Vận hành
(33, 'OPS_STAFF',    'Nhân viên Vận hành',
     'Hỗ trợ các hoạt động hành chính, văn phòng phẩm và dịch vụ nội bộ.',
     NULL, NULL, TRUE, 7, 15, 4, 1,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(34, 'RECEPTION',    'Lễ tân',
     'Tiếp nhận khách, điện thoại tổng đài và hỗ trợ hành chính cơ bản.',
     NULL, NULL, TRUE, 7, 15, 4, 2,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(35, 'DRIVER',       'Tài xế',
     'Lái xe phục vụ Ban Giám đốc và công tác đối ngoại.',
     NULL, NULL, TRUE, 7, 15, 4, 3,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(36, 'SECURITY',     'Bảo vệ',
     'Đảm bảo an ninh, kiểm soát ra vào và tài sản công ty.',
     NULL, NULL, TRUE, 7, 15, 4, 4,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),

-- R&D
(37, 'RESEARCHER',   'Nghiên cứu viên',
     'Nghiên cứu công nghệ mới, thử nghiệm giải pháp và viết báo cáo kỹ thuật.',
     NULL, NULL, TRUE, 8, 8, 4, 1,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),

-- ── Level 5: Nhân viên ───────────────────────────────────────

-- CNTT
(38, 'SEN_DEV',      'Senior Developer',
     'Lập trình viên cao cấp, có khả năng tự thiết kế module và mentor junior.',
     NULL, NULL, TRUE, 3, 16, 5, 1,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(39, 'MID_DEV',      'Mid-level Developer',
     'Lập trình viên trung cấp, thực hiện các tính năng độc lập.',
     NULL, NULL, TRUE, 3, 16, 5, 2,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(40, 'JUN_DEV',      'Junior Developer',
     'Lập trình viên mới, làm việc dưới sự hướng dẫn của Senior/Lead.',
     NULL, NULL, TRUE, 3, 16, 5, 3,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(41, 'QA_ENG',       'QA Engineer',
     'Kiểm thử phần mềm, viết test case và báo cáo bug.',
     NULL, NULL, TRUE, 3, 19, 5, 1,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),

-- Tài chính
(42, 'ACCOUNTANT',   'Kế toán viên',
     'Hạch toán thu chi, đối soát ngân hàng và lập báo cáo kế toán định kỳ.',
     NULL, NULL, TRUE, 4, 24, 5, 1,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(43, 'TAX_SPEC',     'Chuyên viên Thuế',
     'Kê khai thuế GTGT, thuế TNCN, quyết toán thuế năm và làm việc với cơ quan thuế.',
     NULL, NULL, TRUE, 4, 24, 5, 2,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(44, 'CASHIER',      'Thủ quỹ',
     'Quản lý quỹ tiền mặt, thu chi và đối chiếu sổ quỹ hàng ngày.',
     NULL, NULL, TRUE, 4, 24, 5, 3,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),

-- Kinh doanh
(45, 'SALES_EXEC',   'Nhân viên Kinh doanh',
     'Tìm kiếm, tư vấn và chốt hợp đồng với khách hàng mới và hiện có.',
     NULL, NULL, TRUE, 5, 25, 5, 1,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),

-- R&D
(46, 'LAB_STAFF',    'Nhân viên thí nghiệm',
     'Hỗ trợ nghiên cứu viên trong thực nghiệm, ghi chép dữ liệu và bảo quản thiết bị.',
     NULL, NULL, TRUE, 8, 37, 5, 1,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),

-- Vận hành
(47, 'DRIVER_ASS',   'Phụ xe',
     'Hỗ trợ tài xế và xử lý công việc vận chuyển nội bộ.',
     NULL, NULL, TRUE, 7, 35, 5, 1,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00'),

-- ── Level 6: Thực tập sinh ────────────────────────────────────
(48, 'IT_INTERN',    'Intern IT',
     'Thực tập sinh CNTT, hỗ trợ nhóm phát triển dưới sự hướng dẫn của Lead.',
     NULL, NULL, TRUE, 3, 16, 6, 1,
     '2024-01-01 08:00:00', '2024-01-01 08:00:00');

-- ------------------------------------------------------------
-- employees
-- Mapping đầy đủ user_id → emp_id → dept → position → shift
-- role_in_dept: HEAD | DEPUTY | LEADER | STAFF
-- ------------------------------------------------------------
INSERT INTO employees
    (id, user_id, dept_id, position_id, shift_id,
     full_name, gender, dob, phone_number,
     profile_pic_image, address, hire_date,
     basic_salary, status, role_in_dept,
     created_at, updated_at)
VALUES
-- ── Ban Giám đốc (dept=1) ─────────────────────────────────────
(1,  2,  1, 1,  1, 'Nguyễn Đình Hùng', 'MALE',   '1975-05-15', '0901000001',
     NULL, '1 Lê Duẩn, Quận 1, TP.HCM',          '2015-01-05', 120000000, 'ACTIVE', 'HEAD',
     '2024-01-03 08:00:00', '2024-01-03 08:00:00'),
(2,  3,  1, 2,  1, 'Trần Quốc Minh',   'MALE',   '1978-08-22', '0901000002',
     NULL, '25 Nguyễn Huệ, Quận 1, TP.HCM',       '2016-03-01',  95000000, 'ACTIVE', 'DEPUTY',
     '2024-01-03 08:10:00', '2024-01-03 08:10:00'),
(3,  4,  1, 3,  1, 'Lê Thị Lan',       'FEMALE', '1980-11-08', '0901000003',
     NULL, '50 Đồng Khởi, Quận 1, TP.HCM',        '2017-06-01',  90000000, 'ACTIVE', 'DEPUTY',
     '2024-01-03 08:20:00', '2024-01-03 08:20:00'),

-- ── Phòng Nhân sự (dept=2) ───────────────────────────────────
(4,  5,  2, 4,  1, 'Phạm Thị Mai',     'FEMALE', '1982-03-20', '0901000004',
     NULL, '123 Lý Thường Kiệt, Quận 10, TP.HCM', '2017-09-15',  48000000, 'ACTIVE', 'HEAD',
     '2024-01-04 08:00:00', '2024-01-04 08:00:00'),
(5,  6,  2, 11, 1, 'Nguyễn Thị Hoa',   'FEMALE', '1985-07-12', '0901000005',
     NULL, '456 Trần Hưng Đạo, Quận 5, TP.HCM',   '2018-02-01',  38000000, 'ACTIVE', 'DEPUTY',
     '2024-01-04 08:10:00', '2024-01-04 08:10:00'),
(6,  7,  2, 20, 1, 'Lê Thị Thảo',      'FEMALE', '1990-12-05', '0901000006',
     NULL, '789 Cách Mạng Tháng 8, Quận 3, TP.HCM','2020-05-15', 22000000, 'ACTIVE', 'STAFF',
     '2024-01-04 08:20:00', '2024-01-04 08:20:00'),
(7,  8,  2, 21, 1, 'Trần Thị Linh',    'FEMALE', '1992-04-18', '0901000007',
     NULL, '321 Nguyễn Thị Minh Khai, Q3, TP.HCM', '2021-01-10', 20000000, 'ACTIVE', 'STAFF',
     '2024-01-04 08:30:00', '2024-01-04 08:30:00'),
(8,  9,  2, 22, 1, 'Hoàng Thị Trang',  'FEMALE', '1988-09-25', '0901000008',
     NULL, '654 Điện Biên Phủ, Q.Bình Thạnh',      '2019-08-01', 24000000, 'ACTIVE', 'STAFF',
     '2024-01-04 08:40:00', '2024-01-04 08:40:00'),
(9,  10, 2, 23, 1, 'Đỗ Văn Nam',       'MALE',   '1995-01-30', '0901000009',
     NULL, '987 Võ Văn Tần, Quận 3, TP.HCM',       '2022-06-01', 15000000, 'ACTIVE', 'STAFF',
     '2024-01-04 08:50:00', '2024-01-04 08:50:00'),

-- ── Phòng CNTT (dept=3) ──────────────────────────────────────
(10, 11, 3, 9,  2, 'Võ Văn Tuấn',      'MALE',   '1980-06-10', '0358640777',
     'https://res.cloudinary.com/dyjfpbj5e/image/upload/v1775564516/MetaHRM/avatars/emp_10_avatar.png',
     '32 Cầu Bươu, Tân Triều, Hà Nội',            '2016-08-01', 65000000, 'ACTIVE', 'HEAD',
     '2024-01-05 08:00:00', '2024-01-05 08:00:00'),
(11, 12, 3, 10, 2, 'Nguyễn Văn Đức',   'MALE',   '1983-02-28', '0901000011',
     'https://res.cloudinary.com/dyjfpbj5e/image/upload/v1775564754/MetaHRM/avatars/emp_11_avatar.jpg',
     '200 Phú Mỹ Hưng, Quận 7, TP.HCM',           '2017-03-15', 52000000, 'ACTIVE', 'DEPUTY',
     '2024-01-05 08:10:00', '2024-01-05 08:10:00'),
(12, 13, 3, 16, 2, 'Trần Văn Long',    'MALE',   '1986-09-14', '0901000012',
     NULL, '55 Nguyễn Trãi, Quận 1, TP.HCM',       '2018-06-01', 45000000, 'ACTIVE', 'LEADER',
     '2024-01-05 08:20:00', '2024-01-05 08:20:00'),
(13, 14, 3, 17, 2, 'Phạm Minh Hiếu',   'MALE',   '1988-11-22', '0901000013',
     NULL, '78 Lê Văn Sỹ, Quận 3, TP.HCM',         '2019-01-15', 42000000, 'ACTIVE', 'LEADER',
     '2024-01-05 08:30:00', '2024-01-05 08:30:00'),
(14, 15, 3, 38, 2, 'Lê Hoàng Phong',   'MALE',   '1989-04-05', '0901000014',
     'https://res.cloudinary.com/dyjfpbj5e/image/upload/v1775661144/MetaHRM/avatars/emp_14_avatar.jpg',
     '90 Phan Xích Long, Q.Phú Nhuận',             '2019-09-01', 38000000, 'ACTIVE', 'STAFF',
     '2024-01-05 08:40:00', '2024-01-05 08:40:00'),
(15, 16, 3, 38, 2, 'Nguyễn Đức Khánh', 'MALE',   '1987-07-18', '0901000015',
     NULL, '120 Hoàng Văn Thụ, Q.Tân Bình',        '2019-05-15', 40000000, 'ACTIVE', 'STAFF',
     '2024-01-05 08:50:00', '2024-01-05 08:50:00'),
(16, 17, 3, 38, 2, 'Trần Minh Hải',    'MALE',   '1990-10-30', '0901000016',
     NULL, '150 Cộng Hòa, Q.Tân Bình',             '2020-02-01', 36000000, 'ACTIVE', 'STAFF',
     '2024-01-05 09:00:00', '2024-01-05 09:00:00'),
(17, 18, 3, 39, 2, 'Lý Minh Hoàng',    'MALE',   '1993-05-12', '0901000017',
     NULL, '180 Trường Chinh, Q.Tân Bình',          '2021-08-01', 28000000, 'ACTIVE', 'STAFF',
     '2024-01-05 09:10:00', '2024-01-05 09:10:00'),
(18, 19, 3, 39, 2, 'Đặng Thị Quỳnh',   'FEMALE', '1994-08-25', '0901000018',
     NULL, '210 Âu Cơ, Q.Tân Phú',                 '2022-01-15', 26000000, 'ACTIVE', 'STAFF',
     '2024-01-05 09:20:00', '2024-01-05 09:20:00'),
(19, 20, 3, 40, 2, 'Ngô Văn An',       'MALE',   '1997-12-08', '0901000019',
     NULL, '240 Lũy Bán Bích, Q.Tân Phú',           '2023-03-01', 18000000, 'ACTIVE', 'STAFF',
     '2024-01-05 09:30:00', '2024-01-05 09:30:00'),
(20, 21, 3, 40, 2, 'Bùi Thanh Bình',   'MALE',   '1998-03-15', '0901000020',
     NULL, '270 Tân Sơn Nhì, Q.Tân Phú',            '2023-07-01', 16000000, 'ACTIVE', 'STAFF',
     '2024-01-05 09:40:00', '2024-01-05 09:40:00'),
(21, 22, 3, 18, 2, 'Mai Văn Cường',     'MALE',   '1988-01-20', '0901000021',
     NULL, '300 Hậu Giang, Quận 6',                 '2019-04-01', 42000000, 'ACTIVE', 'STAFF',
     '2024-01-05 09:50:00', '2024-01-05 09:50:00'),
(22, 23, 3, 19, 2, 'Trịnh Văn Dũng',   'MALE',   '1989-06-28', '0901000022',
     NULL, '330 An Dương Vương, Quận 5',             '2020-01-15', 38000000, 'ACTIVE', 'LEADER',
     '2024-01-05 10:00:00', '2024-01-05 10:00:00'),
(23, 24, 3, 41, 2, 'Nguyễn Thị Thu',   'FEMALE', '1992-09-15', '0901000023',
     NULL, '360 Nguyễn Chí Thanh, Quận 5',          '2021-06-01', 24000000, 'ACTIVE', 'STAFF',
     '2024-01-05 10:10:00', '2024-01-05 10:10:00'),
(24, 25, 3, 48, 2, 'Lê Văn Đạt',       'MALE',   '2001-11-25', '0901000024',
     NULL, '390 Hùng Vương, Quận 5',                '2024-09-01',  8000000, 'ACTIVE', 'STAFF',
     '2024-01-05 10:20:00', '2024-01-05 10:20:00'),

-- ── Phòng Tài chính (dept=4) ─────────────────────────────────
(25, 26, 4, 3,  1, 'Nguyễn Thị Hương', 'FEMALE', '1979-04-12', '0901000025',
     NULL, '420 Lê Hồng Phong, Quận 10',            '2016-05-01', 52000000, 'ACTIVE', 'HEAD',
     '2024-01-06 08:00:00', '2024-01-06 08:00:00'),
(26, 27, 4, 12, 1, 'Trần Thị Phương',  'FEMALE', '1982-08-30', '0901000026',
     NULL, '450 Ba Tháng Hai, Quận 10',              '2017-08-15', 40000000, 'ACTIVE', 'DEPUTY',
     '2024-01-06 08:10:00', '2024-01-06 08:10:00'),
(27, 28, 4, 24, 1, 'Lê Thị Nga',       'FEMALE', '1981-12-18', '0901000027',
     NULL, '480 Tô Hiến Thành, Quận 10',             '2017-02-01', 45000000, 'ACTIVE', 'LEADER',
     '2024-01-06 08:20:00', '2024-01-06 08:20:00'),
(28, 29, 4, 42, 1, 'Phạm Thị Vân',     'FEMALE', '1990-02-14', '0901000028',
     NULL, '510 Sư Vạn Hạnh, Quận 10',              '2020-04-01', 24000000, 'ACTIVE', 'STAFF',
     '2024-01-06 08:30:00', '2024-01-06 08:30:00'),
(29, 30, 4, 42, 1, 'Hoàng Thị Yến',    'FEMALE', '1991-06-22', '0901000029',
     NULL, '540 Nguyễn Tri Phương, Quận 10',         '2021-01-15', 22000000, 'ACTIVE', 'STAFF',
     '2024-01-06 08:40:00', '2024-01-06 08:40:00'),
(30, 31, 4, 42, 1, 'Vũ Thị Hạnh',      'FEMALE', '1993-10-05', '0901000030',
     NULL, '570 Thành Thái, Quận 10',                '2022-03-01', 20000000, 'ACTIVE', 'STAFF',
     '2024-01-06 08:50:00', '2024-01-06 08:50:00'),
(31, 32, 4, 43, 1, 'Đỗ Thị Thúy',      'FEMALE', '1988-03-28', '0901000031',
     NULL, '600 Ngô Gia Tự, Quận 10',                '2019-11-01', 28000000, 'ACTIVE', 'STAFF',
     '2024-01-06 09:00:00', '2024-01-06 09:00:00'),
(32, 33, 4, 44, 1, 'Bùi Thị Loan',     'FEMALE', '1995-07-15', '0901000032',
     NULL, '630 Lý Thái Tổ, Quận 10',                '2023-02-01', 16000000, 'ACTIVE', 'STAFF',
     '2024-01-06 09:10:00', '2024-01-06 09:10:00'),

-- ── Phòng Kinh doanh (dept=5) ────────────────────────────────
(33, 34, 5, 5,  1, 'Trần Văn Thành',   'MALE',   '1978-09-08', '0901000033',
     NULL, '660 Nguyễn Đình Chiểu, Quận 3',         '2016-04-15', 55000000, 'ACTIVE', 'HEAD',
     '2024-01-07 08:00:00', '2024-01-07 08:00:00'),
(34, 35, 5, 13, 1, 'Nguyễn Văn Sơn',   'MALE',   '1981-12-25', '0901000034',
     NULL, '690 Võ Thị Sáu, Quận 3',                '2017-07-01', 45000000, 'ACTIVE', 'DEPUTY',
     '2024-01-07 08:10:00', '2024-01-07 08:10:00'),
(35, 36, 5, 25, 1, 'Lê Văn Hùng',      'MALE',   '1984-05-18', '0901000035',
     NULL, '720 Hai Bà Trưng, Quận 1',               '2018-10-01', 38000000, 'ACTIVE', 'LEADER',
     '2024-01-07 08:20:00', '2024-01-07 08:20:00'),
(36, 37, 5, 25, 1, 'Phạm Văn Tiến',    'MALE',   '1985-08-10', '0901000036',
     NULL, '750 Pasteur, Quận 3',                    '2019-02-15', 36000000, 'ACTIVE', 'LEADER',
     '2024-01-07 08:30:00', '2024-01-07 08:30:00'),
(37, 38, 5, 45, 1, 'Hoàng Văn Duy',    'MALE',   '1989-11-30', '0901000037',
     NULL, '780 Nam Kỳ Khởi Nghĩa, Quận 3',         '2020-06-01', 28000000, 'ACTIVE', 'STAFF',
     '2024-01-07 08:40:00', '2024-01-07 08:40:00'),
(38, 39, 5, 45, 1, 'Nguyễn Thị Giang', 'FEMALE', '1991-02-22', '0901000038',
     NULL, '810 Trần Cao Vân, Quận 3',               '2021-03-15', 26000000, 'ACTIVE', 'STAFF',
     '2024-01-07 08:50:00', '2024-01-07 08:50:00'),
(39, 40, 5, 45, 1, 'Trần Văn Khôi',    'MALE',   '1992-05-14', '0901000039',
     NULL, '840 Lê Văn Sỹ, Q.Phú Nhuận',            '2021-08-01', 25000000, 'ACTIVE', 'STAFF',
     '2024-01-07 09:00:00', '2024-01-07 09:00:00'),
(40, 41, 5, 45, 1, 'Lê Thị Lý',        'FEMALE', '1993-08-28', '0901000040',
     NULL, '870 Nguyễn Kiệm, Q.Phú Nhuận',          '2022-01-10', 24000000, 'ACTIVE', 'STAFF',
     '2024-01-07 09:10:00', '2024-01-07 09:10:00'),
(41, 42, 5, 45, 1, 'Ngô Thị Mỹ',       'FEMALE', '1994-10-12', '0901000041',
     NULL, '900 Phan Đăng Lưu, Q.Phú Nhuận',        '2022-06-01', 22000000, 'ACTIVE', 'STAFF',
     '2024-01-07 09:20:00', '2024-01-07 09:20:00'),
(42, 43, 5, 45, 1, 'Bùi Thị Nhi',      'FEMALE', '1995-12-05', '0901000042',
     NULL, '930 Nguyễn Văn Đậu, Q.Bình Thạnh',      '2023-01-15', 20000000, 'ACTIVE', 'STAFF',
     '2024-01-07 09:30:00', '2024-01-07 09:30:00'),
(43, 44, 5, 26, 1, 'Đặng Thị Oanh',    'FEMALE', '1988-04-20', '0901000043',
     NULL, '960 Xô Viết Nghệ Tĩnh, Q.Bình Thạnh',  '2019-12-01', 30000000, 'ACTIVE', 'STAFF',
     '2024-01-07 09:40:00', '2024-01-07 09:40:00'),
(44, 45, 5, 27, 1, 'Lê Văn Phát',      'MALE',   '1996-06-18', '0901000044',
     NULL, '990 Bạch Đằng, Q.Bình Thạnh',           '2023-05-01', 16000000, 'ACTIVE', 'STAFF',
     '2024-01-07 09:50:00', '2024-01-07 09:50:00'),

-- ── Phòng Marketing (dept=6) ─────────────────────────────────
(45, 46, 6, 6,  1, 'Nguyễn Thị Quyên', 'FEMALE', '1983-07-25', '0901000045',
     NULL, '1020 Đinh Tiên Hoàng, Quận 1',          '2017-11-15', 48000000, 'ACTIVE', 'HEAD',
     '2024-01-08 08:00:00', '2024-01-08 08:00:00'),
-- MKT_DEP (Trưởng nhóm) đang tuyển — không có employee
(46, 47, 6, 28, 1, 'Trần Hồng Rose',   'FEMALE', '1991-10-08', '0901000046',
     NULL, '1050 Trần Quang Khải, Quận 1',          '2021-04-01', 26000000, 'ACTIVE', 'STAFF',
     '2024-01-08 08:10:00', '2024-01-08 08:10:00'),
(47, 48, 6, 29, 1, 'Lê Thị Sương',     'FEMALE', '1993-01-15', '0901000047',
     NULL, '1080 Nguyễn Hữu Cảnh, Q.Bình Thạnh',   '2022-02-15', 22000000, 'ACTIVE', 'STAFF',
     '2024-01-08 08:20:00', '2024-01-08 08:20:00'),
(48, 49, 6, 30, 1, 'Phạm Văn Tâm',     'MALE',   '1992-04-22', '0901000048',
     NULL, '1110 Nguyễn Xí, Q.Bình Thạnh',          '2021-09-01', 24000000, 'ACTIVE', 'STAFF',
     '2024-01-08 08:30:00', '2024-01-08 08:30:00'),
(49, 50, 6, 31, 1, 'Hoàng Thị Uyên',   'FEMALE', '1994-07-30', '0901000049',
     NULL, '1140 Ung Văn Khiêm, Q.Bình Thạnh',     '2022-07-01', 20000000, 'ACTIVE', 'STAFF',
     '2024-01-08 08:40:00', '2024-01-08 08:40:00'),
(50, 51, 6, 32, 1, 'Vũ Thị Vy',        'FEMALE', '1995-09-12', '0901000050',
     NULL, '1170 Điện Biên Phủ, Quận 3',            '2023-03-01', 18000000, 'ACTIVE', 'STAFF',
     '2024-01-08 08:50:00', '2024-01-08 08:50:00'),

-- ── Phòng Vận hành (dept=7) ──────────────────────────────────
(51, 52, 7, 7,  1, 'Nguyễn Văn Xuân',  'MALE',   '1980-11-18', '0901000051',
     NULL, '1200 Cách Mạng Tháng 8, Quận 10',      '2017-05-01', 42000000, 'ACTIVE', 'HEAD',
     '2024-01-09 08:00:00', '2024-01-09 08:00:00'),
(52, 53, 7, 15, 1, 'Trần Thị Yến',     'FEMALE', '1983-02-25', '0901000052',
     NULL, '1230 Hòa Hảo, Quận 10',                 '2018-08-15', 35000000, 'ON_LEAVE', 'DEPUTY',
     '2024-01-09 08:10:00', '2024-01-09 08:10:00'),
(53, 54, 7, 33, 1, 'Lê Văn Bách',      'MALE',   '1992-05-10', '0901000053',
     NULL, '1260 Nguyễn Tiểu La, Quận 10',          '2021-05-01', 18000000, 'ACTIVE', 'STAFF',
     '2024-01-09 08:20:00', '2024-01-09 08:20:00'),
(54, 55, 7, 33, 1, 'Nguyễn Thị Chi',   'FEMALE', '1993-08-15', '0901000054',
     NULL, '1290 Vĩnh Viễn, Quận 10',               '2022-01-10', 16000000, 'ACTIVE', 'STAFF',
     '2024-01-09 08:30:00', '2024-01-09 08:30:00'),
(55, 56, 7, 33, 1, 'Phạm Văn Đào',     'MALE',   '1994-11-22', '0901000055',
     NULL, '1320 Lý Thường Kiệt, Quận 11',          '2022-08-01', 15000000, 'ACTIVE', 'STAFF',
     '2024-01-09 08:40:00', '2024-01-09 08:40:00'),
(56, 57, 7, 34, 1, 'Trần Thị Em',      'FEMALE', '1997-03-08', '0901000056',
     NULL, '1350 Lạc Long Quân, Quận 11',           '2023-06-01', 14000000, 'ACTIVE', 'STAFF',
     '2024-01-09 08:50:00', '2024-01-09 08:50:00'),
(57, 58, 7, 35, 1, 'Nguyễn Văn Giải',  'MALE',   '1985-06-28', '0901000057',
     NULL, '1380 Bình Thới, Quận 11',               '2020-03-01', 15000000, 'ACTIVE', 'STAFF',
     '2024-01-09 09:00:00', '2024-01-09 09:00:00'),

-- ── Phòng R&D (dept=8) ───────────────────────────────────────
(58, 59, 8, 8,  2, 'Trần Văn Hảo',     'MALE',   '1977-08-15', '0901000058',
     NULL, '1410 Kha Vạn Cân, Q.Thủ Đức',           '2016-06-01', 60000000, 'ACTIVE', 'HEAD',
     '2024-01-10 08:00:00', '2024-01-10 08:00:00'),
-- Các researcher (position cũ id=43 rác → map về RESEARCHER id=37)
(59, 60, 8, 37, 2, 'Lê Văn Ích',       'MALE',   '1987-11-20', '0901000059',
     NULL, '1440 Võ Văn Ngân, Q.Thủ Đức',           '2019-09-01', 38000000, 'ACTIVE', 'STAFF',
     '2024-01-10 08:10:00', '2024-01-10 08:10:00'),
(60, 61, 8, 37, 2, 'Nguyễn Văn Khang', 'MALE',   '1989-04-12', '0901000060',
     NULL, '1470 Đặng Văn Bi, Q.Thủ Đức',           '2020-05-15', 35000000, 'ACTIVE', 'STAFF',
     '2024-01-10 08:20:00', '2024-01-10 08:20:00'),
(61, 62, 8, 37, 2, 'Phạm Văn Lâm',     'MALE',   '1990-07-25', '0901000061',
     NULL, '1500 Lê Văn Việt, Quận 9',               '2021-01-10', 32000000, 'ACTIVE', 'STAFF',
     '2024-01-10 08:30:00', '2024-01-10 08:30:00'),

-- ── INACTIVE (đã nghỉ việc) ──────────────────────────────────
(62, 63, 3, 38, 2, 'Hoàng Văn Minh',   'MALE',   '1991-09-18', '0901000062',
     NULL, '1530 Tăng Nhơn Phú, Quận 9',            '2020-01-15', 25000000, 'INACTIVE', 'STAFF',
     '2024-01-11 08:00:00', '2024-06-15 17:00:00'),
(63, 64, 5, 45, 1, 'Trần Thị Nhung',   'FEMALE', '1993-12-05', '0901000063',
     NULL, '1560 Man Thiện, Quận 9',                 '2021-06-01', 22000000, 'INACTIVE', 'STAFF',
     '2024-01-11 08:10:00', '2024-08-30 17:00:00');

SET FOREIGN_KEY_CHECKS = 1;
