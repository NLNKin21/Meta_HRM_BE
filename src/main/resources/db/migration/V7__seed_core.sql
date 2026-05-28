-- ============================================================
-- V7: SEED CORE DATA
-- users, role_permissions, departments, shifts, work_locations
-- Password hash: $2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa
-- (BCrypt của chuỗi "Password@123" - chỉ dùng cho môi trường dev)
-- ============================================================

-- ------------------------------------------------------------
-- users
-- role: 0=ADMIN | 1=HR | 2=ACCOUNTANT | 3=EMPLOYEE
-- Chú ý: CEO/CTO/CFO và tất cả non-HR/non-Accountant → role=3
-- ------------------------------------------------------------
INSERT INTO users (id, username, password, email, role, status, created_at, updated_at) VALUES
-- Admin hệ thống
(1,  'admin',               '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'admin@techcorp.vn',            0, 0, '2024-01-02 08:00:00', '2024-01-02 08:00:00'),
-- Ban Giám đốc → EMPLOYEE
(2,  'ceo_hung',            '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'ceo@techcorp.vn',              3, 0, '2024-01-03 08:00:00', '2024-01-03 08:00:00'),
(3,  'cto_minh',            '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'cto@techcorp.vn',              3, 0, '2024-01-03 08:10:00', '2024-01-03 08:10:00'),
(4,  'cfo_lan',             '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'cfo@techcorp.vn',              3, 0, '2024-01-03 08:20:00', '2024-01-03 08:20:00'),
-- HR team → role=1
(5,  'hr_director_mai',     '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'hr.director@techcorp.vn',      1, 0, '2024-01-04 08:00:00', '2024-01-04 08:00:00'),
(6,  'hr_deputy_hoa',       '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'hr.deputy@techcorp.vn',        1, 0, '2024-01-04 08:10:00', '2024-01-04 08:10:00'),
(7,  'hr_thao',             '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'hr.recruit@techcorp.vn',       1, 0, '2024-01-04 08:20:00', '2024-01-04 08:20:00'),
(8,  'hr_linh',             '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'hr.training@techcorp.vn',      1, 0, '2024-01-04 08:30:00', '2024-01-04 08:30:00'),
(9,  'hr_trang',            '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'hr.payroll@techcorp.vn',       1, 0, '2024-01-04 08:40:00', '2024-01-04 08:40:00'),
(10, 'hr_nam',              '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'hr.admin@techcorp.vn',         1, 0, '2024-01-04 08:50:00', '2024-01-04 08:50:00'),
-- CNTT → EMPLOYEE
(11, 'it_director_tuan',    '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'it.director@techcorp.vn',      3, 0, '2024-01-05 08:00:00', '2024-01-05 08:00:00'),
(12, 'it_deputy_duc',       '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'it.deputy@techcorp.vn',        3, 0, '2024-01-05 08:10:00', '2024-01-05 08:10:00'),
(13, 'backend_lead_long',   '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'it.lead.backend@techcorp.vn',  3, 0, '2024-01-05 08:20:00', '2024-01-05 08:20:00'),
(14, 'frontend_lead_hieu',  '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'it.lead.frontend@techcorp.vn', 3, 0, '2024-01-05 08:30:00', '2024-01-05 08:30:00'),
(15, 'dev_senior_phong',    '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'it.senior1@techcorp.vn',       3, 0, '2024-01-05 08:40:00', '2024-01-05 08:40:00'),
(16, 'dev_senior_khanh',    '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'it.senior2@techcorp.vn',       3, 0, '2024-01-05 08:50:00', '2024-01-05 08:50:00'),
(17, 'dev_senior_hai',      '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'it.senior3@techcorp.vn',       3, 0, '2024-01-05 09:00:00', '2024-01-05 09:00:00'),
(18, 'dev_mid_hoang',       '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'it.mid1@techcorp.vn',          3, 0, '2024-01-05 09:10:00', '2024-01-05 09:10:00'),
(19, 'dev_mid_quynh',       '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'it.mid2@techcorp.vn',          3, 0, '2024-01-05 09:20:00', '2024-01-05 09:20:00'),
(20, 'dev_junior_an',       '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'it.junior1@techcorp.vn',       3, 0, '2024-01-05 09:30:00', '2024-01-05 09:30:00'),
(21, 'dev_junior_binh',     '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'it.junior2@techcorp.vn',       3, 0, '2024-01-05 09:40:00', '2024-01-05 09:40:00'),
(22, 'devops_cuong',        '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'devops@techcorp.vn',           3, 0, '2024-01-05 09:50:00', '2024-01-05 09:50:00'),
(23, 'qa_lead_dung',        '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'qa.lead@techcorp.vn',          3, 0, '2024-01-05 10:00:00', '2024-01-05 10:00:00'),
(24, 'qa_thu',              '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'qa1@techcorp.vn',              3, 0, '2024-01-05 10:10:00', '2024-01-05 10:10:00'),
(25, 'intern_it_dat',       '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'it.intern@techcorp.vn',        3, 0, '2024-01-05 10:20:00', '2024-01-05 10:20:00'),
-- Finance → ACCOUNTANT (role=2)
(26, 'fin_director_huong',  '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'finance.director@techcorp.vn', 2, 0, '2024-01-06 08:00:00', '2024-01-06 08:00:00'),
(27, 'fin_deputy_phuong',   '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'finance.deputy@techcorp.vn',   2, 0, '2024-01-06 08:10:00', '2024-01-06 08:10:00'),
(28, 'chief_accountant_nga','$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'accountant.chief@techcorp.vn', 2, 0, '2024-01-06 08:20:00', '2024-01-06 08:20:00'),
(29, 'accountant_van',      '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'accountant1@techcorp.vn',      2, 0, '2024-01-06 08:30:00', '2024-01-06 08:30:00'),
(30, 'accountant_yen',      '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'accountant2@techcorp.vn',      2, 0, '2024-01-06 08:40:00', '2024-01-06 08:40:00'),
(31, 'accountant_hanh',     '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'accountant3@techcorp.vn',      2, 0, '2024-01-06 08:50:00', '2024-01-06 08:50:00'),
(32, 'tax_thuy',            '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'tax@techcorp.vn',              2, 0, '2024-01-06 09:00:00', '2024-01-06 09:00:00'),
(33, 'cashier_loan',        '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'cashier@techcorp.vn',          2, 0, '2024-01-06 09:10:00', '2024-01-06 09:10:00'),
-- Sales → EMPLOYEE
(34, 'sales_director_thanh','$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'sales.director@techcorp.vn',   3, 0, '2024-01-07 08:00:00', '2024-01-07 08:00:00'),
(35, 'sales_deputy_son',    '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'sales.deputy@techcorp.vn',     3, 0, '2024-01-07 08:10:00', '2024-01-07 08:10:00'),
(36, 'sales_lead_hung',     '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'sales.lead.north@techcorp.vn', 3, 0, '2024-01-07 08:20:00', '2024-01-07 08:20:00'),
(37, 'sales_lead_tien',     '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'sales.lead.south@techcorp.vn', 3, 0, '2024-01-07 08:30:00', '2024-01-07 08:30:00'),
(38, 'sales_duy',           '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'sales1@techcorp.vn',           3, 0, '2024-01-07 08:40:00', '2024-01-07 08:40:00'),
(39, 'sales_giang',         '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'sales2@techcorp.vn',           3, 0, '2024-01-07 08:50:00', '2024-01-07 08:50:00'),
(40, 'sales_khoi',          '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'sales3@techcorp.vn',           3, 0, '2024-01-07 09:00:00', '2024-01-07 09:00:00'),
(41, 'sales_ly',            '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'sales4@techcorp.vn',           3, 0, '2024-01-07 09:10:00', '2024-01-07 09:10:00'),
(42, 'sales_my',            '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'sales5@techcorp.vn',           3, 0, '2024-01-07 09:20:00', '2024-01-07 09:20:00'),
(43, 'sales_nhi',           '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'sales6@techcorp.vn',           3, 0, '2024-01-07 09:30:00', '2024-01-07 09:30:00'),
(44, 'presales_oanh',       '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'presales@techcorp.vn',         3, 0, '2024-01-07 09:40:00', '2024-01-07 09:40:00'),
(45, 'sales_support_phat',  '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'sales.support@techcorp.vn',    3, 0, '2024-01-07 09:50:00', '2024-01-07 09:50:00'),
-- Marketing → EMPLOYEE
(46, 'mkt_director_quyen',  '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'marketing.director@techcorp.vn',3, 0, '2024-01-08 08:00:00', '2024-01-08 08:00:00'),
(47, 'mkt_digital_rose',    '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'marketing.digital@techcorp.vn', 3, 0, '2024-01-08 08:10:00', '2024-01-08 08:10:00'),
(48, 'mkt_content_suong',   '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'marketing.content@techcorp.vn', 3, 0, '2024-01-08 08:20:00', '2024-01-08 08:20:00'),
(49, 'mkt_design_tam',      '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'marketing.design@techcorp.vn',  3, 0, '2024-01-08 08:30:00', '2024-01-08 08:30:00'),
(50, 'mkt_seo_uyen',        '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'marketing.seo@techcorp.vn',     3, 0, '2024-01-08 08:40:00', '2024-01-08 08:40:00'),
(51, 'mkt_event_vy',        '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'marketing.event@techcorp.vn',   3, 0, '2024-01-08 08:50:00', '2024-01-08 08:50:00'),
-- Ops → EMPLOYEE
(52, 'ops_director_xuan',   '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'ops.director@techcorp.vn',      3, 0, '2024-01-09 08:00:00', '2024-01-09 08:00:00'),
(53, 'ops_deputy_yen',      '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'ops.deputy@techcorp.vn',        3, 0, '2024-01-09 08:10:00', '2024-01-09 08:10:00'),
(54, 'ops_bach',            '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'ops1@techcorp.vn',              3, 0, '2024-01-09 08:20:00', '2024-01-09 08:20:00'),
(55, 'ops_chi',             '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'ops2@techcorp.vn',              3, 0, '2024-01-09 08:30:00', '2024-01-09 08:30:00'),
(56, 'ops_dao',             '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'ops3@techcorp.vn',              3, 0, '2024-01-09 08:40:00', '2024-01-09 08:40:00'),
(57, 'reception_em',        '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'receptionist@techcorp.vn',      3, 0, '2024-01-09 08:50:00', '2024-01-09 08:50:00'),
(58, 'driver_giai',         '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'driver@techcorp.vn',            3, 0, '2024-01-09 09:00:00', '2024-01-09 09:00:00'),
-- R&D → EMPLOYEE
(59, 'rd_director_hao',     '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'rd.director@techcorp.vn',       3, 0, '2024-01-10 08:00:00', '2024-01-10 08:00:00'),
(60, 'researcher_ich',      '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'researcher1@techcorp.vn',       3, 0, '2024-01-10 08:10:00', '2024-01-10 08:10:00'),
(61, 'researcher_khang',    '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'researcher2@techcorp.vn',       3, 0, '2024-01-10 08:20:00', '2024-01-10 08:20:00'),
(62, 'researcher_lam',      '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'researcher3@techcorp.vn',       3, 0, '2024-01-10 08:30:00', '2024-01-10 08:30:00'),
-- Resigned (INACTIVE)
(63, 'resigned_minh',       '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'resigned1@techcorp.vn',         3, 1, '2024-01-11 08:00:00', '2024-06-15 17:00:00'),
(64, 'resigned_nhung',      '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 'resigned2@techcorp.vn',         3, 1, '2024-01-11 08:10:00', '2024-08-30 17:00:00');

-- ------------------------------------------------------------
-- role_permissions
-- HR: hầu hết quyền trừ payroll
-- ACCOUNTANT: dashboard + payroll
-- ------------------------------------------------------------
INSERT INTO role_permissions (id, enabled, module_key, module_name, role, sort_order) VALUES
-- HR
(1,  TRUE,  'dashboard',       'Dashboard',              'HR', 1),
(2,  TRUE,  'users',           'Quản lý tài khoản',      'HR', 2),
(3,  TRUE,  'employees',       'Danh sách nhân viên',    'HR', 3),
(4,  TRUE,  'departments',     'Phòng ban',              'HR', 4),
(5,  TRUE,  'positions',       'Chức vụ',                'HR', 5),
(6,  TRUE,  'contracts',       'Hợp đồng lao động',      'HR', 6),
(7,  TRUE,  'leave-management','Quản lý nghỉ phép',      'HR', 7),
(8,  TRUE,  'attendance',      'Quản lý chấm công',      'HR', 8),
(9,  FALSE, 'payroll',         'Bảng lương',             'HR', 9),
(10, TRUE,  'recruitment',     'Tuyển dụng',             'HR', 10),
(11, TRUE,  'help',            'Trợ giúp',               'HR', 99),
-- ACCOUNTANT
(12, TRUE,  'dashboard',       'Dashboard',              'ACCOUNTANT', 1),
(13, FALSE, 'users',           'Quản lý tài khoản',      'ACCOUNTANT', 2),
(14, FALSE, 'employees',       'Danh sách nhân viên',    'ACCOUNTANT', 3),
(15, FALSE, 'departments',     'Phòng ban',              'ACCOUNTANT', 4),
(16, FALSE, 'positions',       'Chức vụ',                'ACCOUNTANT', 5),
(17, FALSE, 'contracts',       'Hợp đồng lao động',      'ACCOUNTANT', 6),
(18, FALSE, 'leave-management','Quản lý nghỉ phép',      'ACCOUNTANT', 7),
(19, FALSE, 'attendance',      'Quản lý chấm công',      'ACCOUNTANT', 8),
(20, TRUE,  'payroll',         'Bảng lương',             'ACCOUNTANT', 9),
(21, FALSE, 'recruitment',     'Tuyển dụng',             'ACCOUNTANT', 10),
(22, TRUE,  'help',            'Trợ giúp',               'ACCOUNTANT', 99);

-- ------------------------------------------------------------
-- departments (bỏ id=9 Phòng Dự án đã giải thể)
-- ------------------------------------------------------------
INSERT INTO departments (id, dept_name, created_at, updated_at) VALUES
(1, 'Ban Giám đốc',                 '2024-01-01 08:00:00', '2024-01-01 08:00:00'),
(2, 'Phòng Nhân sự',                '2024-01-01 08:05:00', '2024-01-01 08:05:00'),
(3, 'Phòng Công nghệ thông tin',    '2024-01-01 08:10:00', '2024-01-01 08:10:00'),
(4, 'Phòng Tài chính - Kế toán',    '2024-01-01 08:15:00', '2024-01-01 08:15:00'),
(5, 'Phòng Kinh doanh',             '2024-01-01 08:20:00', '2024-01-01 08:20:00'),
(6, 'Phòng Marketing',              '2024-01-01 08:25:00', '2024-01-01 08:25:00'),
(7, 'Phòng Vận hành',               '2024-01-01 08:30:00', '2024-01-01 08:30:00'),
(8, 'Phòng Nghiên cứu & Phát triển','2024-01-01 08:35:00', '2024-01-01 08:35:00');

-- ------------------------------------------------------------
-- shifts
-- ------------------------------------------------------------
INSERT INTO shifts (id, name, code, start_time, end_time,
    late_threshold, early_leave_threshold,
    check_in_start_before, check_in_end_after,
    work_days, break_duration, description, color, is_active,
    created_at, updated_at) VALUES
(1, 'Ca Hành Chính', 'HC',
    '08:00:00', '17:00:00', 15, 15, 30, 120,
    '[1,2,3,4,5,6]', 60,
    'Ca hành chính tiêu chuẩn 8h-17h, áp dụng cho hầu hết các phòng ban.',
    '#2196F3', TRUE, '2026-01-02 08:00:00', '2026-01-02 08:00:00'),
(2, 'Ca Linh Hoạt', 'FLEX',
    '09:00:00', '18:00:00', 30, 15, 60, 180,
    '[1,2,3,4,5]', 60,
    'Ca linh hoạt 9h-18h, phù hợp cho bộ phận IT và R&D.',
    '#FF9800', TRUE, '2026-01-02 08:10:00', '2026-01-02 08:10:00');

-- ------------------------------------------------------------
-- work_locations
-- ------------------------------------------------------------
INSERT INTO work_locations (id, name, code, address, latitude, longitude,
    radius, description, contact_person, contact_phone, is_active,
    created_at, updated_at) VALUES
(1, 'Trụ sở chính TechCorp', 'HQ',
    'Tầng 10-12, Tòa nhà Viettel Complex, P.12, Q.10, TP.HN',
    20.97582480, 105.81565500, 200,
    'Trụ sở chính 3 tầng. T10: Sales/Marketing/Ops. T11: IT/R&D. T12: BGĐ/HR/Finance.',
    'Nguyễn Văn Xuân', '0901000051', TRUE,
    '2026-01-02 08:00:00', '2026-01-02 08:00:00'),
(2, 'Chi nhánh Tân Triều', 'TANTRIỀU',
    '32 Cầu Bươu, Tân Triều, Hà Nội',
    20.95956800, 105.80354100, 500,
    'Chi nhánh phía Bắc.',
    'Phạm Văn Tiến', '0901000036', TRUE,
    '2026-01-02 08:05:00', '2026-01-02 08:05:00'),
(3, 'Văn phòng R&D Hoàng Mai', 'RD-HM',
    'Lô E2a-7, Đường D1, Khu CNC, TP.Hoàng Mai, TP.HCM',
    10.85587600, 106.78603800, 200,
    'Văn phòng R&D tại Khu Công nghệ cao TP.HCM.',
    'Trần Văn Hảo', '0901000058', TRUE,
    '2026-01-02 08:10:00', '2026-01-02 08:10:00');
