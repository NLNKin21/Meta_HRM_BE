-- ============================================
-- V3__seed_initial_data.sql
-- Seed data cho Employee Management System
-- Password mặc định: 12345678
-- Chuẩn doanh nghiệp với 60 nhân sự
-- ============================================

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- USERS
-- Role: 0=ADMIN, 1=EMPLOYEE, 2=HR, 3=ACCOUNTANT
-- Status: 0=ACTIVE, 1=DELETED, 2=DISABLED, 3=PENDING
-- ============================================
TRUNCATE TABLE users;
INSERT INTO users (id, created_at, is_deleted, updated_at, email, password, role, status, username) VALUES
-- Admin hệ thống
(1, '2024-01-02 08:00:00', b'0', '2024-01-02 08:00:00', 'admin@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 0, 0, 'admin'),

-- Ban Giám đốc (2-4)
(2, '2024-01-03 08:00:00', b'0', '2024-01-03 08:00:00', 'ceo@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'ceo_hung'),
(3, '2024-01-03 08:10:00', b'0', '2024-01-03 08:10:00', 'cto@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'cto_minh'),
(4, '2024-01-03 08:20:00', b'0', '2024-01-03 08:20:00', 'cfo@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 3, 0, 'cfo_lan'),

-- Phòng Nhân sự - HR (5-10)
(5, '2024-01-04 08:00:00', b'0', '2024-01-04 08:00:00', 'hr.director@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 2, 0, 'hr_director_mai'),
(6, '2024-01-04 08:10:00', b'0', '2024-01-04 08:10:00', 'hr.deputy@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 2, 0, 'hr_deputy_hoa'),
(7, '2024-01-04 08:20:00', b'0', '2024-01-04 08:20:00', 'hr.recruit@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 2, 0, 'hr_thao'),
(8, '2024-01-04 08:30:00', b'0', '2024-01-04 08:30:00', 'hr.training@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 2, 0, 'hr_linh'),
(9, '2024-01-04 08:40:00', b'0', '2024-01-04 08:40:00', 'hr.payroll@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 2, 0, 'hr_trang'),
(10, '2024-01-04 08:50:00', b'0', '2024-01-04 08:50:00', 'hr.admin@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 2, 0, 'hr_nam'),

-- Phòng IT (11-25)
(11, '2024-01-05 08:00:00', b'0', '2024-01-05 08:00:00', 'it.director@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'it_director_tuan'),
(12, '2024-01-05 08:10:00', b'0', '2024-01-05 08:10:00', 'it.deputy@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'it_deputy_duc'),
(13, '2024-01-05 08:20:00', b'0', '2024-01-05 08:20:00', 'it.lead.backend@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'backend_lead_long'),
(14, '2024-01-05 08:30:00', b'0', '2024-01-05 08:30:00', 'it.lead.frontend@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'frontend_lead_hieu'),
(15, '2024-01-05 08:40:00', b'0', '2024-01-05 08:40:00', 'it.senior1@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'dev_senior_phong'),
(16, '2024-01-05 08:50:00', b'0', '2024-01-05 08:50:00', 'it.senior2@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'dev_senior_khanh'),
(17, '2024-01-05 09:00:00', b'0', '2024-01-05 09:00:00', 'it.senior3@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'dev_senior_hai'),
(18, '2024-01-05 09:10:00', b'0', '2024-01-05 09:10:00', 'it.mid1@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'dev_mid_hoang'),
(19, '2024-01-05 09:20:00', b'0', '2024-01-05 09:20:00', 'it.mid2@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'dev_mid_quynh'),
(20, '2024-01-05 09:30:00', b'0', '2024-01-05 09:30:00', 'it.junior1@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'dev_junior_an'),
(21, '2024-01-05 09:40:00', b'0', '2024-01-05 09:40:00', 'it.junior2@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'dev_junior_binh'),
(22, '2024-01-05 09:50:00', b'0', '2024-01-05 09:50:00', 'devops@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'devops_cuong'),
(23, '2024-01-05 10:00:00', b'0', '2024-01-05 10:00:00', 'qa.lead@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'qa_lead_dung'),
(24, '2024-01-05 10:10:00', b'0', '2024-01-05 10:10:00', 'qa1@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'qa_thu'),
(25, '2024-01-05 10:20:00', b'0', '2024-01-05 10:20:00', 'it.intern@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'intern_it_dat'),

-- Phòng Tài chính - Kế toán (26-33)
(26, '2024-01-06 08:00:00', b'0', '2024-01-06 08:00:00', 'finance.director@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 3, 0, 'fin_director_huong'),
(27, '2024-01-06 08:10:00', b'0', '2024-01-06 08:10:00', 'finance.deputy@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 3, 0, 'fin_deputy_phuong'),
(28, '2024-01-06 08:20:00', b'0', '2024-01-06 08:20:00', 'accountant.chief@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 3, 0, 'chief_accountant_nga'),
(29, '2024-01-06 08:30:00', b'0', '2024-01-06 08:30:00', 'accountant1@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 3, 0, 'accountant_van'),
(30, '2024-01-06 08:40:00', b'0', '2024-01-06 08:40:00', 'accountant2@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 3, 0, 'accountant_yen'),
(31, '2024-01-06 08:50:00', b'0', '2024-01-06 08:50:00', 'accountant3@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 3, 0, 'accountant_hanh'),
(32, '2024-01-06 09:00:00', b'0', '2024-01-06 09:00:00', 'tax@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 3, 0, 'tax_thuy'),
(33, '2024-01-06 09:10:00', b'0', '2024-01-06 09:10:00', 'cashier@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 3, 0, 'cashier_loan'),

-- Phòng Kinh doanh - Sales (34-45)
(34, '2024-01-07 08:00:00', b'0', '2024-01-07 08:00:00', 'sales.director@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'sales_director_thanh'),
(35, '2024-01-07 08:10:00', b'0', '2024-01-07 08:10:00', 'sales.deputy@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'sales_deputy_son'),
(36, '2024-01-07 08:20:00', b'0', '2024-01-07 08:20:00', 'sales.lead.north@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'sales_lead_hung'),
(37, '2024-01-07 08:30:00', b'0', '2024-01-07 08:30:00', 'sales.lead.south@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'sales_lead_tien'),
(38, '2024-01-07 08:40:00', b'0', '2024-01-07 08:40:00', 'sales1@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'sales_duy'),
(39, '2024-01-07 08:50:00', b'0', '2024-01-07 08:50:00', 'sales2@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'sales_giang'),
(40, '2024-01-07 09:00:00', b'0', '2024-01-07 09:00:00', 'sales3@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'sales_khoi'),
(41, '2024-01-07 09:10:00', b'0', '2024-01-07 09:10:00', 'sales4@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'sales_ly'),
(42, '2024-01-07 09:20:00', b'0', '2024-01-07 09:20:00', 'sales5@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'sales_my'),
(43, '2024-01-07 09:30:00', b'0', '2024-01-07 09:30:00', 'sales6@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'sales_nhi'),
(44, '2024-01-07 09:40:00', b'0', '2024-01-07 09:40:00', 'presales@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'presales_oanh'),
(45, '2024-01-07 09:50:00', b'0', '2024-01-07 09:50:00', 'sales.support@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'sales_support_phat'),

-- Phòng Marketing (46-51)
(46, '2024-01-08 08:00:00', b'0', '2024-01-08 08:00:00', 'marketing.director@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'mkt_director_quyen'),
(47, '2024-01-08 08:10:00', b'0', '2024-01-08 08:10:00', 'marketing.digital@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'mkt_digital_rose'),
(48, '2024-01-08 08:20:00', b'0', '2024-01-08 08:20:00', 'marketing.content@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'mkt_content_suong'),
(49, '2024-01-08 08:30:00', b'0', '2024-01-08 08:30:00', 'marketing.design@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'mkt_design_tam'),
(50, '2024-01-08 08:40:00', b'0', '2024-01-08 08:40:00', 'marketing.seo@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'mkt_seo_uyen'),
(51, '2024-01-08 08:50:00', b'0', '2024-01-08 08:50:00', 'marketing.event@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'mkt_event_vy'),

-- Phòng Operations (52-58)
(52, '2024-01-09 08:00:00', b'0', '2024-01-09 08:00:00', 'ops.director@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'ops_director_xuan'),
(53, '2024-01-09 08:10:00', b'0', '2024-01-09 08:10:00', 'ops.deputy@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'ops_deputy_yen'),
(54, '2024-01-09 08:20:00', b'0', '2024-01-09 08:20:00', 'ops1@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'ops_bach'),
(55, '2024-01-09 08:30:00', b'0', '2024-01-09 08:30:00', 'ops2@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'ops_chi'),
(56, '2024-01-09 08:40:00', b'0', '2024-01-09 08:40:00', 'ops3@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'ops_dao'),
(57, '2024-01-09 08:50:00', b'0', '2024-01-09 08:50:00', 'receptionist@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'reception_em'),
(58, '2024-01-09 09:00:00', b'0', '2024-01-09 09:00:00', 'driver@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'driver_giai'),

-- Phòng R&D (59-63)
(59, '2024-01-10 08:00:00', b'0', '2024-01-10 08:00:00', 'rd.director@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'rd_director_hao'),
(60, '2024-01-10 08:10:00', b'0', '2024-01-10 08:10:00', 'researcher1@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'researcher_ich'),
(61, '2024-01-10 08:20:00', b'0', '2024-01-10 08:20:00', 'researcher2@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'researcher_khang'),
(62, '2024-01-10 08:30:00', b'0', '2024-01-10 08:30:00', 'researcher3@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 0, 'researcher_lam'),

-- Users đặc biệt (nghỉ việc, chờ kích hoạt, đã xóa)
(63, '2024-01-11 08:00:00', b'0', '2024-06-15 17:00:00', 'resigned1@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 2, 'resigned_minh'),
(64, '2024-01-11 08:10:00', b'0', '2024-08-30 17:00:00', 'resigned2@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 2, 'resigned_nhung'),
(65, '2024-12-01 08:00:00', b'0', '2024-12-01 08:00:00', 'pending.new1@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 3, 'pending_new_anh'),
(66, '2024-12-05 08:00:00', b'0', '2024-12-05 08:00:00', 'pending.new2@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 3, 'pending_new_bao'),
(67, '2024-03-01 08:00:00', b'1', '2024-05-01 08:00:00', 'deleted@techcorp.vn', '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa', 1, 1, 'deleted_user');

-- ============================================
-- DEPARTMENTS
-- ============================================
TRUNCATE TABLE departments;
INSERT INTO departments (id, created_at, is_deleted, updated_at, dept_name) VALUES
(1, '2024-01-01 08:00:00', b'0', '2024-01-01 08:00:00', 'Ban Giám đốc'),
(2, '2024-01-01 08:05:00', b'0', '2024-01-01 08:05:00', 'Phòng Nhân sự'),
(3, '2024-01-01 08:10:00', b'0', '2024-01-01 08:10:00', 'Phòng Công nghệ thông tin'),
(4, '2024-01-01 08:15:00', b'0', '2024-01-01 08:15:00', 'Phòng Tài chính - Kế toán'),
(5, '2024-01-01 08:20:00', b'0', '2024-01-01 08:20:00', 'Phòng Kinh doanh'),
(6, '2024-01-01 08:25:00', b'0', '2024-01-01 08:25:00', 'Phòng Marketing'),
(7, '2024-01-01 08:30:00', b'0', '2024-01-01 08:30:00', 'Phòng Vận hành'),
(8, '2024-01-01 08:35:00', b'0', '2024-01-01 08:35:00', 'Phòng Nghiên cứu & Phát triển'),
(9, '2024-01-01 08:40:00', b'1', '2024-06-01 08:00:00', 'Phòng Dự án (Đã giải thể)');

-- ============================================
-- POSITIONS
-- ============================================
TRUNCATE TABLE positions;

INSERT INTO positions 
(position_code, position_name, department_id, parent_position_id, level_order, sort_order, is_active, is_deleted, created_at, updated_at)
VALUES

-- ================= LEVEL 1 =================
('CEO', 'Tổng Giám đốc', 1, NULL, 1, 1, TRUE, FALSE, NOW(), NOW()),

-- ================= LEVEL 2 =================
('CTO', 'Giám đốc Công nghệ', 3, 1, 2, 1, TRUE, FALSE, NOW(), NOW()),
('CFO', 'Giám đốc Tài chính', 4, 1, 2, 2, TRUE, FALSE, NOW(), NOW()),
('HR_DIR', 'Giám đốc Nhân sự', 2, 1, 2, 3, TRUE, FALSE, NOW(), NOW()),
('SALES_DIR', 'Giám đốc Kinh doanh', 5, 1, 2, 4, TRUE, FALSE, NOW(), NOW()),
('MKT_DIR', 'Giám đốc Marketing', 6, 1, 2, 5, TRUE, FALSE, NOW(), NOW()),
('OPS_DIR', 'Trưởng phòng Vận hành', 7, 1, 2, 6, TRUE, FALSE, NOW(), NOW()),
('RD_DIR', 'Giám đốc R&D', 8, 1, 2, 7, TRUE, FALSE, NOW(), NOW()),

-- ================= IT =================
('IT_DEP', 'Phó phòng CNTT', 3, 2, 3, 1, TRUE, FALSE, NOW(), NOW()),
('TECH_LEAD', 'Trưởng nhóm Kỹ thuật', 3, 9, 4, 1, TRUE, FALSE, NOW(), NOW()),
('SEN_DEV', 'Senior Developer', 3, 10, 5, 1, TRUE, FALSE, NOW(), NOW()),
('MID_DEV', 'Mid Developer', 3, 10, 5, 2, TRUE, FALSE, NOW(), NOW()),
('JUN_DEV', 'Junior Developer', 3, 10, 6, 1, TRUE, FALSE, NOW(), NOW()),
('DEVOPS', 'DevOps Engineer', 3, 9, 4, 2, TRUE, FALSE, NOW(), NOW()),
('QA_LEAD', 'QA Lead', 3, 9, 4, 3, TRUE, FALSE, NOW(), NOW()),
('QA_ENG', 'QA Engineer', 3, 15, 5, 1, TRUE, FALSE, NOW(), NOW()),
('IT_INTERN', 'Intern IT', 3, 10, 7, 1, TRUE, FALSE, NOW(), NOW()),

-- ================= HR =================
('HR_DEP', 'Phó phòng Nhân sự', 2, 4, 3, 1, TRUE, FALSE, NOW(), NOW()),
('HR_REC', 'Chuyên viên Tuyển dụng', 2, 18, 4, 1, TRUE, FALSE, NOW(), NOW()),
('HR_TRN', 'Chuyên viên Đào tạo', 2, 18, 4, 2, TRUE, FALSE, NOW(), NOW()),
('HR_PAY', 'Chuyên viên Tiền lương', 2, 18, 4, 3, TRUE, FALSE, NOW(), NOW()),
('HR_ADM', 'Hành chính Nhân sự', 2, 18, 4, 4, TRUE, FALSE, NOW(), NOW()),

-- ================= FINANCE =================
('FIN_DEP', 'Phó phòng Tài chính', 4, 3, 3, 1, TRUE, FALSE, NOW(), NOW()),
('CHIEF_ACC', 'Kế toán trưởng', 4, 23, 4, 1, TRUE, FALSE, NOW(), NOW()),
('ACCOUNTANT', 'Kế toán viên', 4, 24, 5, 1, TRUE, FALSE, NOW(), NOW()),
('TAX_SPEC', 'Chuyên viên Thuế', 4, 24, 5, 2, TRUE, FALSE, NOW(), NOW()),
('CASHIER', 'Thủ quỹ', 4, 24, 5, 3, TRUE, FALSE, NOW(), NOW()),

-- ================= SALES =================
('SALES_DEP', 'Phó phòng Kinh doanh', 5, 5, 3, 1, TRUE, FALSE, NOW(), NOW()),
('SALES_LEAD', 'Trưởng nhóm Kinh doanh', 5, 29, 4, 1, TRUE, FALSE, NOW(), NOW()),
('SALES_EXEC', 'Nhân viên Kinh doanh', 5, 30, 5, 1, TRUE, FALSE, NOW(), NOW()),
('PRESALES', 'Pre-sales', 5, 29, 4, 2, TRUE, FALSE, NOW(), NOW()),
('SALES_SUP', 'Hỗ trợ Kinh doanh', 5, 29, 4, 3, TRUE, FALSE, NOW(), NOW()),

-- ================= MARKETING =================
('DIGITAL_MKT', 'Digital Marketing', 6, 6, 3, 1, TRUE, FALSE, NOW(), NOW()),
('CONTENT_MKT', 'Content Marketing', 6, 6, 3, 2, TRUE, FALSE, NOW(), NOW()),
('DESIGNER', 'Designer', 6, 6, 3, 3, TRUE, FALSE, NOW(), NOW()),
('SEO_SPEC', 'SEO Specialist', 6, 6, 3, 4, TRUE, FALSE, NOW(), NOW()),
('EVENT_COORD', 'Event Coordinator', 6, 6, 3, 5, TRUE, FALSE, NOW(), NOW()),

-- ================= OPERATIONS =================
('OPS_DEP', 'Phó phòng Vận hành', 7, 7, 3, 1, TRUE, FALSE, NOW(), NOW()),
('OPS_STAFF', 'Nhân viên Vận hành', 7, 38, 4, 1, TRUE, FALSE, NOW(), NOW()),
('RECEPTION', 'Lễ tân', 7, 38, 4, 2, TRUE, FALSE, NOW(), NOW()),
('DRIVER', 'Tài xế', 7, 38, 4, 3, TRUE, FALSE, NOW(), NOW()),

-- ================= R&D =================
('RESEARCHER', 'Nghiên cứu viên', 8, 8, 3, 1, TRUE, FALSE, NOW(), NOW());

-- ============================================
-- EMPLOYEES (60 nhân sự)
-- ============================================
TRUNCATE TABLE employees;
INSERT INTO employees (id, created_at, is_deleted, updated_at, address, basic_salary, dept_id, dob, full_name, gender, hire_date, phone_number, role_in_dept, status, user_id, position_id) VALUES
-- Ban Giám đốc (1-3)
(1, '2024-01-03 08:00:00', b'0', '2024-01-03 08:00:00', '1 Lê Duẩn, Quận 1, TP.HCM', 120000000.00, 1, '1975-05-15', 'Nguyễn Đình Hùng', 'MALE', '2015-01-05', '0901000001', 'HEAD', 'ACTIVE', 2, 1),
(2, '2024-01-03 08:10:00', b'0', '2024-01-03 08:10:00', '25 Nguyễn Huệ, Quận 1, TP.HCM', 95000000.00, 1, '1978-08-22', 'Trần Quốc Minh', 'MALE', '2016-03-01', '0901000002', 'DEPUTY', 'ACTIVE', 3, 2),
(3, '2024-01-03 08:20:00', b'0', '2024-01-03 08:20:00', '50 Đồng Khởi, Quận 1, TP.HCM', 90000000.00, 1, '1980-11-08', 'Lê Thị Lan', 'FEMALE', '2017-06-01', '0901000003', 'DEPUTY', 'ACTIVE', 4, 3),

-- Phòng Nhân sự (4-9)
(4, '2024-01-04 08:00:00', b'0', '2024-01-04 08:00:00', '123 Lý Thường Kiệt, Quận 10, TP.HCM', 48000000.00, 2, '1982-03-20', 'Phạm Thị Mai', 'FEMALE', '2017-09-15', '0901000004', 'HEAD', 'ACTIVE', 5, 4),
(5, '2024-01-04 08:10:00', b'0', '2024-01-04 08:10:00', '456 Trần Hưng Đạo, Quận 5, TP.HCM', 38000000.00, 2, '1985-07-12', 'Nguyễn Thị Hoa', 'FEMALE', '2018-02-01', '0901000005', 'DEPUTY', 'ACTIVE', 6, 5),
(6, '2024-01-04 08:20:00', b'0', '2024-01-04 08:20:00', '789 Cách Mạng Tháng 8, Quận 3, TP.HCM', 22000000.00, 2, '1990-12-05', 'Lê Thị Thảo', 'FEMALE', '2020-05-15', '0901000006', 'STAFF', 'ACTIVE', 7, 6),
(7, '2024-01-04 08:30:00', b'0', '2024-01-04 08:30:00', '321 Nguyễn Thị Minh Khai, Quận 3, TP.HCM', 20000000.00, 2, '1992-04-18', 'Trần Thị Linh', 'FEMALE', '2021-01-10', '0901000007', 'STAFF', 'ACTIVE', 8, 7),
(8, '2024-01-04 08:40:00', b'0', '2024-01-04 08:40:00', '654 Điện Biên Phủ, Quận Bình Thạnh, TP.HCM', 24000000.00, 2, '1988-09-25', 'Hoàng Thị Trang', 'FEMALE', '2019-08-01', '0901000008', 'STAFF', 'ACTIVE', 9, 8),
(9, '2024-01-04 08:50:00', b'0', '2024-01-04 08:50:00', '987 Võ Văn Tần, Quận 3, TP.HCM', 15000000.00, 2, '1995-01-30', 'Đỗ Văn Nam', 'MALE', '2022-06-01', '0901000009', 'STAFF', 'ACTIVE', 10, 9),

-- Phòng IT (10-24)
(10, '2024-01-05 08:00:00', b'0', '2024-01-05 08:00:00', '100 Nguyễn Văn Linh, Quận 7, TP.HCM', 65000000.00, 3, '1980-06-10', 'Võ Văn Tuấn', 'MALE', '2016-08-01', '0901000010', 'HEAD', 'ACTIVE', 11, 10),
(11, '2024-01-05 08:10:00', b'0', '2024-01-05 08:10:00', '200 Phú Mỹ Hưng, Quận 7, TP.HCM', 52000000.00, 3, '1983-02-28', 'Nguyễn Văn Đức', 'MALE', '2017-03-15', '0901000011', 'DEPUTY', 'ACTIVE', 12, 11),
(12, '2024-01-05 08:20:00', b'0', '2024-01-05 08:20:00', '55 Nguyễn Trãi, Quận 1, TP.HCM', 45000000.00, 3, '1986-09-14', 'Trần Văn Long', 'MALE', '2018-06-01', '0901000012', 'LEADER', 'ACTIVE', 13, 12),
(13, '2024-01-05 08:30:00', b'0', '2024-01-05 08:30:00', '78 Lê Văn Sỹ, Quận 3, TP.HCM', 42000000.00, 3, '1988-11-22', 'Phạm Minh Hiếu', 'MALE', '2019-01-15', '0901000013', 'LEADER', 'ACTIVE', 14, 12),
(14, '2024-01-05 08:40:00', b'0', '2024-01-05 08:40:00', '90 Phan Xích Long, Quận Phú Nhuận, TP.HCM', 38000000.00, 3, '1989-04-05', 'Lê Hoàng Phong', 'MALE', '2019-09-01', '0901000014', 'STAFF', 'ACTIVE', 15, 13),
(15, '2024-01-05 08:50:00', b'0', '2024-01-05 08:50:00', '120 Hoàng Văn Thụ, Quận Tân Bình, TP.HCM', 40000000.00, 3, '1987-07-18', 'Nguyễn Đức Khánh', 'MALE', '2019-05-15', '0901000015', 'STAFF', 'ACTIVE', 16, 13),
(16, '2024-01-05 09:00:00', b'0', '2024-01-05 09:00:00', '150 Cộng Hòa, Quận Tân Bình, TP.HCM', 36000000.00, 3, '1990-10-30', 'Trần Minh Hải', 'MALE', '2020-02-01', '0901000016', 'STAFF', 'ACTIVE', 17, 13),
(17, '2024-01-05 09:10:00', b'0', '2024-01-05 09:10:00', '180 Trường Chinh, Quận Tân Bình, TP.HCM', 28000000.00, 3, '1993-05-12', 'Lý Minh Hoàng', 'MALE', '2021-08-01', '0901000017', 'STAFF', 'ACTIVE', 18, 14),
(18, '2024-01-05 09:20:00', b'0', '2024-01-05 09:20:00', '210 Âu Cơ, Quận Tân Phú, TP.HCM', 26000000.00, 3, '1994-08-25', 'Đặng Thị Quỳnh', 'FEMALE', '2022-01-15', '0901000018', 'STAFF', 'ACTIVE', 19, 14),
(19, '2024-01-05 09:30:00', b'0', '2024-01-05 09:30:00', '240 Lũy Bán Bích, Quận Tân Phú, TP.HCM', 18000000.00, 3, '1997-12-08', 'Ngô Văn An', 'MALE', '2023-03-01', '0901000019', 'STAFF', 'ACTIVE', 20, 15),
(20, '2024-01-05 09:40:00', b'0', '2024-01-05 09:40:00', '270 Tân Sơn Nhì, Quận Tân Phú, TP.HCM', 16000000.00, 3, '1998-03-15', 'Bùi Thanh Bình', 'MALE', '2023-07-01', '0901000020', 'STAFF', 'ACTIVE', 21, 15),
(21, '2024-01-05 09:50:00', b'0', '2024-01-05 09:50:00', '300 Hậu Giang, Quận 6, TP.HCM', 42000000.00, 3, '1988-01-20', 'Mai Văn Cường', 'MALE', '2019-04-01', '0901000021', 'STAFF', 'ACTIVE', 22, 16),
(22, '2024-01-05 10:00:00', b'0', '2024-01-05 10:00:00', '330 An Dương Vương, Quận 5, TP.HCM', 38000000.00, 3, '1989-06-28', 'Trịnh Văn Dũng', 'MALE', '2020-01-15', '0901000022', 'LEADER', 'ACTIVE', 23, 17),
(23, '2024-01-05 10:10:00', b'0', '2024-01-05 10:10:00', '360 Nguyễn Chí Thanh, Quận 5, TP.HCM', 24000000.00, 3, '1992-09-15', 'Nguyễn Thị Thu', 'FEMALE', '2021-06-01', '0901000023', 'STAFF', 'ACTIVE', 24, 18),
(24, '2024-01-05 10:20:00', b'0', '2024-01-05 10:20:00', '390 Hùng Vương, Quận 5, TP.HCM', 8000000.00, 3, '2001-11-25', 'Lê Văn Đạt', 'MALE', '2024-09-01', '0901000024', 'STAFF', 'ACTIVE', 25, 19),

-- Phòng Tài chính - Kế toán (25-32)
(25, '2024-01-06 08:00:00', b'0', '2024-01-06 08:00:00', '420 Lê Hồng Phong, Quận 10, TP.HCM', 52000000.00, 4, '1979-04-12', 'Nguyễn Thị Hương', 'FEMALE', '2016-05-01', '0901000025', 'HEAD', 'ACTIVE', 26, 20),
(26, '2024-01-06 08:10:00', b'0', '2024-01-06 08:10:00', '450 Ba Tháng Hai, Quận 10, TP.HCM', 40000000.00, 4, '1982-08-30', 'Trần Thị Phương', 'FEMALE', '2017-08-15', '0901000026', 'DEPUTY', 'ACTIVE', 27, 21),
(27, '2024-01-06 08:20:00', b'0', '2024-01-06 08:20:00', '480 Tô Hiến Thành, Quận 10, TP.HCM', 45000000.00, 4, '1981-12-18', 'Lê Thị Nga', 'FEMALE', '2017-02-01', '0901000027', 'LEADER', 'ACTIVE', 28, 22),
(28, '2024-01-06 08:30:00', b'0', '2024-01-06 08:30:00', '510 Sư Vạn Hạnh, Quận 10, TP.HCM', 24000000.00, 4, '1990-02-14', 'Phạm Thị Vân', 'FEMALE', '2020-04-01', '0901000028', 'STAFF', 'ACTIVE', 29, 23),
(29, '2024-01-06 08:40:00', b'0', '2024-01-06 08:40:00', '540 Nguyễn Tri Phương, Quận 10, TP.HCM', 22000000.00, 4, '1991-06-22', 'Hoàng Thị Yến', 'FEMALE', '2021-01-15', '0901000029', 'STAFF', 'ACTIVE', 30, 23),
(30, '2024-01-06 08:50:00', b'0', '2024-01-06 08:50:00', '570 Thành Thái, Quận 10, TP.HCM', 20000000.00, 4, '1993-10-05', 'Vũ Thị Hạnh', 'FEMALE', '2022-03-01', '0901000030', 'STAFF', 'ACTIVE', 31, 23),
(31, '2024-01-06 09:00:00', b'0', '2024-01-06 09:00:00', '600 Ngô Gia Tự, Quận 10, TP.HCM', 28000000.00, 4, '1988-03-28', 'Đỗ Thị Thúy', 'FEMALE', '2019-11-01', '0901000031', 'STAFF', 'ACTIVE', 32, 24),
(32, '2024-01-06 09:10:00', b'0', '2024-01-06 09:10:00', '630 Lý Thái Tổ, Quận 10, TP.HCM', 16000000.00, 4, '1995-07-15', 'Bùi Thị Loan', 'FEMALE', '2023-02-01', '0901000032', 'STAFF', 'ACTIVE', 33, 25),

-- Phòng Kinh doanh (33-44)
(33, '2024-01-07 08:00:00', b'0', '2024-01-07 08:00:00', '660 Nguyễn Đình Chiểu, Quận 3, TP.HCM', 55000000.00, 5, '1978-09-08', 'Trần Văn Thành', 'MALE', '2016-04-15', '0901000033', 'HEAD', 'ACTIVE', 34, 26),
(34, '2024-01-07 08:10:00', b'0', '2024-01-07 08:10:00', '690 Võ Thị Sáu, Quận 3, TP.HCM', 45000000.00, 5, '1981-12-25', 'Nguyễn Văn Sơn', 'MALE', '2017-07-01', '0901000034', 'DEPUTY', 'ACTIVE', 35, 27),
(35, '2024-01-07 08:20:00', b'0', '2024-01-07 08:20:00', '720 Hai Bà Trưng, Quận 1, TP.HCM', 38000000.00, 5, '1984-05-18', 'Lê Văn Hùng', 'MALE', '2018-10-01', '0901000035', 'LEADER', 'ACTIVE', 36, 28),
(36, '2024-01-07 08:30:00', b'0', '2024-01-07 08:30:00', '750 Pasteur, Quận 3, TP.HCM', 36000000.00, 5, '1985-08-10', 'Phạm Văn Tiến', 'MALE', '2019-02-15', '0901000036', 'LEADER', 'ACTIVE', 37, 28),
(37, '2024-01-07 08:40:00', b'0', '2024-01-07 08:40:00', '780 Nam Kỳ Khởi Nghĩa, Quận 3, TP.HCM', 28000000.00, 5, '1989-11-30', 'Hoàng Văn Duy', 'MALE', '2020-06-01', '0901000037', 'STAFF', 'ACTIVE', 38, 29),
(38, '2024-01-07 08:50:00', b'0', '2024-01-07 08:50:00', '810 Trần Cao Vân, Quận 3, TP.HCM', 26000000.00, 5, '1991-02-22', 'Nguyễn Thị Giang', 'FEMALE', '2021-03-15', '0901000038', 'STAFF', 'ACTIVE', 39, 29),
(39, '2024-01-07 09:00:00', b'0', '2024-01-07 09:00:00', '840 Lê Văn Sỹ, Quận Phú Nhuận, TP.HCM', 25000000.00, 5, '1992-05-14', 'Trần Văn Khôi', 'MALE', '2021-08-01', '0901000039', 'STAFF', 'ACTIVE', 40, 29),
(40, '2024-01-07 09:10:00', b'0', '2024-01-07 09:10:00', '870 Nguyễn Kiệm, Quận Phú Nhuận, TP.HCM', 24000000.00, 5, '1993-08-28', 'Lê Thị Lý', 'FEMALE', '2022-01-10', '0901000040', 'STAFF', 'ACTIVE', 41, 29),
(41, '2024-01-07 09:20:00', b'0', '2024-01-07 09:20:00', '900 Phan Đăng Lưu, Quận Phú Nhuận, TP.HCM', 22000000.00, 5, '1994-10-12', 'Ngô Thị Mỹ', 'FEMALE', '2022-06-01', '0901000041', 'STAFF', 'ACTIVE', 42, 29),
(42, '2024-01-07 09:30:00', b'0', '2024-01-07 09:30:00', '930 Nguyễn Văn Đậu, Quận Bình Thạnh, TP.HCM', 20000000.00, 5, '1995-12-05', 'Bùi Thị Nhi', 'FEMALE', '2023-01-15', '0901000042', 'STAFF', 'ACTIVE', 43, 29),
(43, '2024-01-07 09:40:00', b'0', '2024-01-07 09:40:00', '960 Xô Viết Nghệ Tĩnh, Quận Bình Thạnh, TP.HCM', 30000000.00, 5, '1988-04-20', 'Đặng Thị Oanh', 'FEMALE', '2019-12-01', '0901000043', 'STAFF', 'ACTIVE', 44, 30),
(44, '2024-01-07 09:50:00', b'0', '2024-01-07 09:50:00', '990 Bạch Đằng, Quận Bình Thạnh, TP.HCM', 16000000.00, 5, '1996-06-18', 'Lê Văn Phát', 'MALE', '2023-05-01', '0901000044', 'STAFF', 'ACTIVE', 45, 31),

-- Phòng Marketing (45-50)
(45, '2024-01-08 08:00:00', b'0', '2024-01-08 08:00:00', '1020 Đinh Tiên Hoàng, Quận 1, TP.HCM', 48000000.00, 6, '1983-07-25', 'Nguyễn Thị Quyên', 'FEMALE', '2017-11-15', '0901000045', 'HEAD', 'ACTIVE', 46, 32),
(46, '2024-01-08 08:10:00', b'0', '2024-01-08 08:10:00', '1050 Trần Quang Khải, Quận 1, TP.HCM', 26000000.00, 6, '1991-10-08', 'Trần Hồng Rose', 'FEMALE', '2021-04-01', '0901000046', 'STAFF', 'ACTIVE', 47, 33),
(47, '2024-01-08 08:20:00', b'0', '2024-01-08 08:20:00', '1080 Nguyễn Hữu Cảnh, Quận Bình Thạnh, TP.HCM', 22000000.00, 6, '1993-01-15', 'Lê Thị Sương', 'FEMALE', '2022-02-15', '0901000047', 'STAFF', 'ACTIVE', 48, 34),
(48, '2024-01-08 08:30:00', b'0', '2024-01-08 08:30:00', '1110 Nguyễn Xí, Quận Bình Thạnh, TP.HCM', 24000000.00, 6, '1992-04-22', 'Phạm Văn Tâm', 'MALE', '2021-09-01', '0901000048', 'STAFF', 'ACTIVE', 49, 35),
(49, '2024-01-08 08:40:00', b'0', '2024-01-08 08:40:00', '1140 Ung Văn Khiêm, Quận Bình Thạnh, TP.HCM', 20000000.00, 6, '1994-07-30', 'Hoàng Thị Uyên', 'FEMALE', '2022-07-01', '0901000049', 'STAFF', 'ACTIVE', 50, 36),
(50, '2024-01-08 08:50:00', b'0', '2024-01-08 08:50:00', '1170 Điện Biên Phủ, Quận 3, TP.HCM', 18000000.00, 6, '1995-09-12', 'Vũ Thị Vy', 'FEMALE', '2023-03-01', '0901000050', 'STAFF', 'ACTIVE', 51, 37),

-- Phòng Vận hành (51-57)
(51, '2024-01-09 08:00:00', b'0', '2024-01-09 08:00:00', '1200 Cách Mạng Tháng 8, Quận 10, TP.HCM', 42000000.00, 7, '1980-11-18', 'Nguyễn Văn Xuân', 'MALE', '2017-05-01', '0901000051', 'HEAD', 'ACTIVE', 52, 38),
(52, '2024-01-09 08:10:00', b'0', '2024-01-09 08:10:00', '1230 Hòa Hảo, Quận 10, TP.HCM', 35000000.00, 7, '1983-02-25', 'Trần Thị Yến', 'FEMALE', '2018-08-15', '0901000052', 'DEPUTY', 'ON_LEAVE', 53, 39),
(53, '2024-01-09 08:20:00', b'0', '2024-01-09 08:20:00', '1260 Nguyễn Tiểu La, Quận 10, TP.HCM', 18000000.00, 7, '1992-05-10', 'Lê Văn Bách', 'MALE', '2021-05-01', '0901000053', 'STAFF', 'ACTIVE', 54, 40),
(54, '2024-01-09 08:30:00', b'0', '2024-01-09 08:30:00', '1290 Vĩnh Viễn, Quận 10, TP.HCM', 16000000.00, 7, '1993-08-15', 'Nguyễn Thị Chi', 'FEMALE', '2022-01-10', '0901000054', 'STAFF', 'ACTIVE', 55, 40),
(55, '2024-01-09 08:40:00', b'0', '2024-01-09 08:40:00', '1320 Lý Thường Kiệt, Quận 11, TP.HCM', 15000000.00, 7, '1994-11-22', 'Phạm Văn Đào', 'MALE', '2022-08-01', '0901000055', 'STAFF', 'ACTIVE', 56, 40),
(56, '2024-01-09 08:50:00', b'0', '2024-01-09 08:50:00', '1350 Lạc Long Quân, Quận 11, TP.HCM', 14000000.00, 7, '1997-03-08', 'Trần Thị Em', 'FEMALE', '2023-06-01', '0901000056', 'STAFF', 'ACTIVE', 57, 41),
(57, '2024-01-09 09:00:00', b'0', '2024-01-09 09:00:00', '1380 Bình Thới, Quận 11, TP.HCM', 15000000.00, 7, '1985-06-28', 'Nguyễn Văn Giải', 'MALE', '2020-03-01', '0901000057', 'STAFF', 'ACTIVE', 58, 42),

-- Phòng R&D (58-61)
(58, '2024-01-10 08:00:00', b'0', '2024-01-10 08:00:00', '1410 Kha Vạn Cân, Quận Thủ Đức, TP.HCM', 60000000.00, 8, '1977-08-15', 'Trần Văn Hảo', 'MALE', '2016-06-01', '0901000058', 'HEAD', 'ACTIVE', 59, 43),
(59, '2024-01-10 08:10:00', b'0', '2024-01-10 08:10:00', '1440 Võ Văn Ngân, Quận Thủ Đức, TP.HCM', 38000000.00, 8, '1987-11-20', 'Lê Văn Ích', 'MALE', '2019-09-01', '0901000059', 'STAFF', 'ACTIVE', 60, 44),
(60, '2024-01-10 08:20:00', b'0', '2024-01-10 08:20:00', '1470 Đặng Văn Bi, Quận Thủ Đức, TP.HCM', 35000000.00, 8, '1989-04-12', 'Nguyễn Văn Khang', 'MALE', '2020-05-15', '0901000060', 'STAFF', 'ACTIVE', 61, 44),
(61, '2024-01-10 08:30:00', b'0', '2024-01-10 08:30:00', '1500 Lê Văn Việt, Quận 9, TP.HCM', 32000000.00, 8, '1990-07-25', 'Phạm Văn Lâm', 'MALE', '2021-01-10', '0901000061', 'STAFF', 'ACTIVE', 62, 44),

-- Nhân viên đã nghỉ việc
(62, '2024-01-11 08:00:00', b'0', '2024-06-15 17:00:00', '1530 Tăng Nhơn Phú, Quận 9, TP.HCM', 25000000.00, 3, '1991-09-18', 'Hoàng Văn Minh', 'MALE', '2020-01-15', '0901000062', 'STAFF', 'INACTIVE', 63, 14),
(63, '2024-01-11 08:10:00', b'0', '2024-08-30 17:00:00', '1560 Man Thiện, Quận 9, TP.HCM', 22000000.00, 5, '1993-12-05', 'Trần Thị Nhung', 'FEMALE', '2021-06-01', '0901000063', 'STAFF', 'INACTIVE', 64, 29);

-- ============================================
-- CONTRACTS
-- ============================================
TRUNCATE TABLE contracts;
INSERT INTO contracts (id, created_at, is_deleted, updated_at, contract_type, emp_id, end_date, file_url, start_date, status) VALUES
-- Ban Giám đốc - Hợp đồng không thời hạn
(1, '2024-01-03 09:00:00', b'0', '2024-01-03 09:00:00', 'FULL_TIME', 1, NULL, '/contracts/CEO_fulltime.pdf', '2015-01-05', 'ACTIVE'),
(2, '2024-01-03 09:05:00', b'0', '2024-01-03 09:05:00', 'FULL_TIME', 2, NULL, '/contracts/CTO_fulltime.pdf', '2016-03-01', 'ACTIVE'),
(3, '2024-01-03 09:10:00', b'0', '2024-01-03 09:10:00', 'FULL_TIME', 3, NULL, '/contracts/CFO_fulltime.pdf', '2017-06-01', 'ACTIVE'),

-- Phòng HR
(4, '2024-01-04 09:00:00', b'0', '2024-01-04 09:00:00', 'FULL_TIME', 4, NULL, '/contracts/HR_DIR_fulltime.pdf', '2017-09-15', 'ACTIVE'),
(5, '2024-01-04 09:05:00', b'0', '2024-01-04 09:05:00', 'FULL_TIME', 5, '2027-02-01', '/contracts/HR_DEP_fulltime.pdf', '2018-02-01', 'ACTIVE'),
(6, '2024-01-04 09:10:00', b'0', '2024-01-04 09:10:00', 'FULL_TIME', 6, '2026-05-15', '/contracts/HR_REC_fulltime.pdf', '2020-05-15', 'ACTIVE'),
(7, '2024-01-04 09:15:00', b'0', '2024-01-04 09:15:00', 'FULL_TIME', 7, '2027-01-10', '/contracts/HR_TRN_fulltime.pdf', '2021-01-10', 'ACTIVE'),
(8, '2024-01-04 09:20:00', b'0', '2024-01-04 09:20:00', 'FULL_TIME', 8, '2025-08-01', '/contracts/HR_PAY_fulltime.pdf', '2019-08-01', 'ACTIVE'),
(9, '2024-01-04 09:25:00', b'0', '2024-01-04 09:25:00', 'FULL_TIME', 9, '2028-06-01', '/contracts/HR_ADM_fulltime.pdf', '2022-06-01', 'ACTIVE'),

-- Phòng IT
(10, '2024-01-05 09:00:00', b'0', '2024-01-05 09:00:00', 'FULL_TIME', 10, NULL, '/contracts/IT_DIR_fulltime.pdf', '2016-08-01', 'ACTIVE'),
(11, '2024-01-05 09:05:00', b'0', '2024-01-05 09:05:00', 'FULL_TIME', 11, NULL, '/contracts/IT_DEP_fulltime.pdf', '2017-03-15', 'ACTIVE'),
(12, '2024-01-05 09:10:00', b'0', '2024-01-05 09:10:00', 'FULL_TIME', 12, '2027-06-01', '/contracts/TECH_LEAD1_fulltime.pdf', '2018-06-01', 'ACTIVE'),
(13, '2024-01-05 09:15:00', b'0', '2024-01-05 09:15:00', 'FULL_TIME', 13, '2028-01-15', '/contracts/TECH_LEAD2_fulltime.pdf', '2019-01-15', 'ACTIVE'),
(14, '2024-01-05 09:20:00', b'0', '2024-01-05 09:20:00', 'FULL_TIME', 14, '2028-09-01', '/contracts/SEN_DEV1_fulltime.pdf', '2019-09-01', 'ACTIVE'),
(15, '2024-01-05 09:25:00', b'0', '2024-01-05 09:25:00', 'FULL_TIME', 15, '2028-05-15', '/contracts/SEN_DEV2_fulltime.pdf', '2019-05-15', 'ACTIVE'),
(16, '2024-01-05 09:30:00', b'0', '2024-01-05 09:30:00', 'FULL_TIME', 16, '2029-02-01', '/contracts/SEN_DEV3_fulltime.pdf', '2020-02-01', 'ACTIVE'),
(17, '2024-01-05 09:35:00', b'0', '2024-01-05 09:35:00', 'FULL_TIME', 17, '2027-08-01', '/contracts/MID_DEV1_fulltime.pdf', '2021-08-01', 'ACTIVE'),
(18, '2024-01-05 09:40:00', b'0', '2024-01-05 09:40:00', 'FULL_TIME', 18, '2028-01-15', '/contracts/MID_DEV2_fulltime.pdf', '2022-01-15', 'ACTIVE'),
(19, '2024-01-05 09:45:00', b'0', '2024-01-05 09:45:00', 'PROBATION', 19, '2023-06-01', '/contracts/JUN_DEV1_probation.pdf', '2023-03-01', 'EXPIRED'),
(20, '2024-01-05 09:50:00', b'0', '2024-01-05 09:50:00', 'FULL_TIME', 19, '2026-03-01', '/contracts/JUN_DEV1_fulltime.pdf', '2023-06-01', 'ACTIVE'),
(21, '2024-01-05 09:55:00', b'0', '2024-01-05 09:55:00', 'PROBATION', 20, '2023-10-01', '/contracts/JUN_DEV2_probation.pdf', '2023-07-01', 'EXPIRED'),
(22, '2024-01-05 10:00:00', b'0', '2024-01-05 10:00:00', 'FULL_TIME', 20, '2026-07-01', '/contracts/JUN_DEV2_fulltime.pdf', '2023-10-01', 'ACTIVE'),
(23, '2024-01-05 10:05:00', b'0', '2024-01-05 10:05:00', 'FULL_TIME', 21, '2028-04-01', '/contracts/DEVOPS_fulltime.pdf', '2019-04-01', 'ACTIVE'),
(24, '2024-01-05 10:10:00', b'0', '2024-01-05 10:10:00', 'FULL_TIME', 22, '2029-01-15', '/contracts/QA_LEAD_fulltime.pdf', '2020-01-15', 'ACTIVE'),
(25, '2024-01-05 10:15:00', b'0', '2024-01-05 10:15:00', 'FULL_TIME', 23, '2027-06-01', '/contracts/QA_ENG_fulltime.pdf', '2021-06-01', 'ACTIVE'),
(26, '2024-01-05 10:20:00', b'0', '2024-01-05 10:20:00', 'INTERNSHIP', 24, '2025-03-01', '/contracts/IT_INTERN_intern.pdf', '2024-09-01', 'ACTIVE'),

-- Phòng Tài chính - Kế toán
(27, '2024-01-06 09:00:00', b'0', '2024-01-06 09:00:00', 'FULL_TIME', 25, NULL, '/contracts/FIN_DIR_fulltime.pdf', '2016-05-01', 'ACTIVE'),
(28, '2024-01-06 09:05:00', b'0', '2024-01-06 09:05:00', 'FULL_TIME', 26, '2026-08-15', '/contracts/FIN_DEP_fulltime.pdf', '2017-08-15', 'ACTIVE'),
(29, '2024-01-06 09:10:00', b'0', '2024-01-06 09:10:00', 'FULL_TIME', 27, '2026-02-01', '/contracts/CHIEF_ACC_fulltime.pdf', '2017-02-01', 'ACTIVE'),
(30, '2024-01-06 09:15:00', b'0', '2024-01-06 09:15:00', 'FULL_TIME', 28, '2029-04-01', '/contracts/ACC1_fulltime.pdf', '2020-04-01', 'ACTIVE'),
(31, '2024-01-06 09:20:00', b'0', '2024-01-06 09:20:00', 'FULL_TIME', 29, '2030-01-15', '/contracts/ACC2_fulltime.pdf', '2021-01-15', 'ACTIVE'),
(32, '2024-01-06 09:25:00', b'0', '2024-01-06 09:25:00', 'FULL_TIME', 30, '2031-03-01', '/contracts/ACC3_fulltime.pdf', '2022-03-01', 'ACTIVE'),
(33, '2024-01-06 09:30:00', b'0', '2024-01-06 09:30:00', 'FULL_TIME', 31, '2028-11-01', '/contracts/TAX_fulltime.pdf', '2019-11-01', 'ACTIVE'),
(34, '2024-01-06 09:35:00', b'0', '2024-01-06 09:35:00', 'FULL_TIME', 32, '2029-02-01', '/contracts/CASHIER_fulltime.pdf', '2023-02-01', 'ACTIVE'),

-- Phòng Kinh doanh
(35, '2024-01-07 09:00:00', b'0', '2024-01-07 09:00:00', 'FULL_TIME', 33, NULL, '/contracts/SALES_DIR_fulltime.pdf', '2016-04-15', 'ACTIVE'),
(36, '2024-01-07 09:05:00', b'0', '2024-01-07 09:05:00', 'FULL_TIME', 34, '2026-07-01', '/contracts/SALES_DEP_fulltime.pdf', '2017-07-01', 'ACTIVE'),
(37, '2024-01-07 09:10:00', b'0', '2024-01-07 09:10:00', 'FULL_TIME', 35, '2027-10-01', '/contracts/SALES_LEAD1_fulltime.pdf', '2018-10-01', 'ACTIVE'),
(38, '2024-01-07 09:15:00', b'0', '2024-01-07 09:15:00', 'FULL_TIME', 36, '2028-02-15', '/contracts/SALES_LEAD2_fulltime.pdf', '2019-02-15', 'ACTIVE'),
(39, '2024-01-07 09:20:00', b'0', '2024-01-07 09:20:00', 'FULL_TIME', 37, '2029-06-01', '/contracts/SALES1_fulltime.pdf', '2020-06-01', 'ACTIVE'),
(40, '2024-01-07 09:25:00', b'0', '2024-01-07 09:25:00', 'FULL_TIME', 38, '2030-03-15', '/contracts/SALES2_fulltime.pdf', '2021-03-15', 'ACTIVE'),
(41, '2024-01-07 09:30:00', b'0', '2024-01-07 09:30:00', 'FULL_TIME', 39, '2027-08-01', '/contracts/SALES3_fulltime.pdf', '2021-08-01', 'ACTIVE'),
(42, '2024-01-07 09:35:00', b'0', '2024-01-07 09:35:00', 'FULL_TIME', 40, '2028-01-10', '/contracts/SALES4_fulltime.pdf', '2022-01-10', 'ACTIVE'),
(43, '2024-01-07 09:40:00', b'0', '2024-01-07 09:40:00', 'FULL_TIME', 41, '2028-06-01', '/contracts/SALES5_fulltime.pdf', '2022-06-01', 'ACTIVE'),
(44, '2024-01-07 09:45:00', b'0', '2024-01-07 09:45:00', 'PROBATION', 42, '2023-04-15', '/contracts/SALES6_probation.pdf', '2023-01-15', 'EXPIRED'),
(45, '2024-01-07 09:50:00', b'0', '2024-01-07 09:50:00', 'FULL_TIME', 42, '2029-01-15', '/contracts/SALES6_fulltime.pdf', '2023-04-15', 'ACTIVE'),
(46, '2024-01-07 09:55:00', b'0', '2024-01-07 09:55:00', 'FULL_TIME', 43, '2028-12-01', '/contracts/PRESALES_fulltime.pdf', '2019-12-01', 'ACTIVE'),
(47, '2024-01-07 10:00:00', b'0', '2024-01-07 10:00:00', 'FULL_TIME', 44, '2029-05-01', '/contracts/SALES_SUP_fulltime.pdf', '2023-05-01', 'ACTIVE'),

-- Phòng Marketing
(48, '2024-01-08 09:00:00', b'0', '2024-01-08 09:00:00', 'FULL_TIME', 45, NULL, '/contracts/MKT_DIR_fulltime.pdf', '2017-11-15', 'ACTIVE'),
(49, '2024-01-08 09:05:00', b'0', '2024-01-08 09:05:00', 'FULL_TIME', 46, '2030-04-01', '/contracts/DIGITAL_MKT_fulltime.pdf', '2021-04-01', 'ACTIVE'),
(50, '2024-01-08 09:10:00', b'0', '2024-01-08 09:10:00', 'FULL_TIME', 47, '2028-02-15', '/contracts/CONTENT_MKT_fulltime.pdf', '2022-02-15', 'ACTIVE'),
(51, '2024-01-08 09:15:00', b'0', '2024-01-08 09:15:00', 'FULL_TIME', 48, '2027-09-01', '/contracts/DESIGNER_fulltime.pdf', '2021-09-01', 'ACTIVE'),
(52, '2024-01-08 09:20:00', b'0', '2024-01-08 09:20:00', 'FULL_TIME', 49, '2028-07-01', '/contracts/SEO_fulltime.pdf', '2022-07-01', 'ACTIVE'),
(53, '2024-01-08 09:25:00', b'0', '2024-01-08 09:25:00', 'FULL_TIME', 50, '2029-03-01', '/contracts/EVENT_fulltime.pdf', '2023-03-01', 'ACTIVE'),

-- Phòng Vận hành
(54, '2024-01-09 09:00:00', b'0', '2024-01-09 09:00:00', 'FULL_TIME', 51, NULL, '/contracts/OPS_DIR_fulltime.pdf', '2017-05-01', 'ACTIVE'),
(55, '2024-01-09 09:05:00', b'0', '2024-01-09 09:05:00', 'FULL_TIME', 52, '2027-08-15', '/contracts/OPS_DEP_fulltime.pdf', '2018-08-15', 'ACTIVE'),
(56, '2024-01-09 09:10:00', b'0', '2024-01-09 09:10:00', 'FULL_TIME', 53, '2027-05-01', '/contracts/OPS1_fulltime.pdf', '2021-05-01', 'ACTIVE'),
(57, '2024-01-09 09:15:00', b'0', '2024-01-09 09:15:00', 'FULL_TIME', 54, '2028-01-10', '/contracts/OPS2_fulltime.pdf', '2022-01-10', 'ACTIVE'),
(58, '2024-01-09 09:20:00', b'0', '2024-01-09 09:20:00', 'FULL_TIME', 55, '2028-08-01', '/contracts/OPS3_fulltime.pdf', '2022-08-01', 'ACTIVE'),
(59, '2024-01-09 09:25:00', b'0', '2024-01-09 09:25:00', 'FULL_TIME', 56, '2029-06-01', '/contracts/RECEPTION_fulltime.pdf', '2023-06-01', 'ACTIVE'),
(60, '2024-01-09 09:30:00', b'0', '2024-01-09 09:30:00', 'FULL_TIME', 57, '2029-03-01', '/contracts/DRIVER_fulltime.pdf', '2020-03-01', 'ACTIVE'),

-- Phòng R&D
(61, '2024-01-10 09:00:00', b'0', '2024-01-10 09:00:00', 'FULL_TIME', 58, NULL, '/contracts/RD_DIR_fulltime.pdf', '2016-06-01', 'ACTIVE'),
(62, '2024-01-10 09:05:00', b'0', '2024-01-10 09:05:00', 'FULL_TIME', 59, '2028-09-01', '/contracts/RESEARCHER1_fulltime.pdf', '2019-09-01', 'ACTIVE'),
(63, '2024-01-10 09:10:00', b'0', '2024-01-10 09:10:00', 'FULL_TIME', 60, '2029-05-15', '/contracts/RESEARCHER2_fulltime.pdf', '2020-05-15', 'ACTIVE'),
(64, '2024-01-10 09:15:00', b'0', '2024-01-10 09:15:00', 'FULL_TIME', 61, '2030-01-10', '/contracts/RESEARCHER3_fulltime.pdf', '2021-01-10', 'ACTIVE'),

-- Nhân viên đã nghỉ việc
(65, '2024-01-11 09:00:00', b'0', '2024-06-15 17:00:00', 'FULL_TIME', 62, '2024-06-15', '/contracts/RESIGNED1_fulltime.pdf', '2020-01-15', 'TERMINATED'),
(66, '2024-01-11 09:05:00', b'0', '2024-08-30 17:00:00', 'FULL_TIME', 63, '2024-08-30', '/contracts/RESIGNED2_fulltime.pdf', '2021-06-01', 'TERMINATED');

-- ============================================
-- EMPLOYEE_DOCUMENTS
-- ============================================
TRUNCATE TABLE employee_documents;
INSERT INTO employee_documents (id, created_at, is_deleted, updated_at, doc_type, emp_id, file_url, original_name, file_size) VALUES
-- Ban Giám đốc
(1, '2024-01-03 10:00:00', b'0', '2024-01-03 10:00:00', 'ID_CARD', 1, '/docs/emp1/cccd.pdf', 'CCCD_NguyenDinhHung.pdf', 512000),
(2, '2024-01-03 10:05:00', b'0', '2024-01-03 10:05:00', 'DEGREE', 1, '/docs/emp1/degree.pdf', 'MBA_NguyenDinhHung.pdf', 1200000),
(3, '2024-01-03 10:10:00', b'0', '2024-01-03 10:10:00', 'ID_CARD', 2, '/docs/emp2/cccd.pdf', 'CCCD_TranQuocMinh.pdf', 510000),
(4, '2024-01-03 10:15:00', b'0', '2024-01-03 10:15:00', 'DEGREE', 2, '/docs/emp2/degree.pdf', 'ThacSi_CNTT_TranQuocMinh.pdf', 1150000),
(5, '2024-01-03 10:20:00', b'0', '2024-01-03 10:20:00', 'CERTIFICATE', 2, '/docs/emp2/cert_aws.pdf', 'AWS_Solutions_Architect.pdf', 680000),
(6, '2024-01-03 10:25:00', b'0', '2024-01-03 10:25:00', 'ID_CARD', 3, '/docs/emp3/cccd.pdf', 'CCCD_LeThiLan.pdf', 505000),
(7, '2024-01-03 10:30:00', b'0', '2024-01-03 10:30:00', 'CERTIFICATE', 3, '/docs/emp3/cert_cpa.pdf', 'CPA_Certificate.pdf', 720000),

-- Phòng HR
(8, '2024-01-04 10:00:00', b'0', '2024-01-04 10:00:00', 'ID_CARD', 4, '/docs/emp4/cccd.pdf', 'CCCD_PhamThiMai.pdf', 498000),
(9, '2024-01-04 10:05:00', b'0', '2024-01-04 10:05:00', 'DEGREE', 4, '/docs/emp4/degree.pdf', 'ThacSi_QTNS_PhamThiMai.pdf', 1100000),
(10, '2024-01-04 10:10:00', b'0', '2024-01-04 10:10:00', 'CERTIFICATE', 4, '/docs/emp4/cert_shrm.pdf', 'SHRM_Certificate.pdf', 650000),
(11, '2024-01-04 10:15:00', b'0', '2024-01-04 10:15:00', 'ID_CARD', 5, '/docs/emp5/cccd.pdf', 'CCCD_NguyenThiHoa.pdf', 502000),
(12, '2024-01-04 10:20:00', b'0', '2024-01-04 10:20:00', 'ID_CARD', 6, '/docs/emp6/cccd.pdf', 'CCCD_LeThiThao.pdf', 495000),
(13, '2024-01-04 10:25:00', b'0', '2024-01-04 10:25:00', 'RESUME', 6, '/docs/emp6/resume.pdf', 'CV_LeThiThao.pdf', 380000),
(14, '2024-01-04 10:30:00', b'0', '2024-01-04 10:30:00', 'ID_CARD', 7, '/docs/emp7/cccd.pdf', 'CCCD_TranThiLinh.pdf', 500000),
(15, '2024-01-04 10:35:00', b'0', '2024-01-04 10:35:00', 'ID_CARD', 8, '/docs/emp8/cccd.pdf', 'CCCD_HoangThiTrang.pdf', 508000),
(16, '2024-01-04 10:40:00', b'0', '2024-01-04 10:40:00', 'ID_CARD', 9, '/docs/emp9/cccd.pdf', 'CCCD_DoVanNam.pdf', 515000),

-- Phòng IT
(17, '2024-01-05 10:00:00', b'0', '2024-01-05 10:00:00', 'ID_CARD', 10, '/docs/emp10/cccd.pdf', 'CCCD_VoVanTuan.pdf', 520000),
(18, '2024-01-05 10:05:00', b'0', '2024-01-05 10:05:00', 'DEGREE', 10, '/docs/emp10/degree.pdf', 'TienSi_CNTT_VoVanTuan.pdf', 1500000),
(19, '2024-01-05 10:10:00', b'0', '2024-01-05 10:10:00', 'CERTIFICATE', 10, '/docs/emp10/cert_pmp.pdf', 'PMP_Certificate.pdf', 700000),
(20, '2024-01-05 10:15:00', b'0', '2024-01-05 10:15:00', 'ID_CARD', 11, '/docs/emp11/cccd.pdf', 'CCCD_NguyenVanDuc.pdf', 512000),
(21, '2024-01-05 10:20:00', b'0', '2024-01-05 10:20:00', 'CERTIFICATE', 11, '/docs/emp11/cert_scrum.pdf', 'ScrumMaster_Certificate.pdf', 620000),
(22, '2024-01-05 10:25:00', b'0', '2024-01-05 10:25:00', 'ID_CARD', 12, '/docs/emp12/cccd.pdf', 'CCCD_TranVanLong.pdf', 505000),
(23, '2024-01-05 10:30:00', b'0', '2024-01-05 10:30:00', 'CERTIFICATE', 12, '/docs/emp12/cert_java.pdf', 'OCP_Java17_Certificate.pdf', 680000),
(24, '2024-01-05 10:35:00', b'0', '2024-01-05 10:35:00', 'ID_CARD', 13, '/docs/emp13/cccd.pdf', 'CCCD_PhamMinhHieu.pdf', 510000),
(25, '2024-01-05 10:40:00', b'0', '2024-01-05 10:40:00', 'ID_CARD', 14, '/docs/emp14/cccd.pdf', 'CCCD_LeHoangPhong.pdf', 502000),
(26, '2024-01-05 10:45:00', b'0', '2024-01-05 10:45:00', 'ID_CARD', 15, '/docs/emp15/cccd.pdf', 'CCCD_NguyenDucKhanh.pdf', 498000),
(27, '2024-01-05 10:50:00', b'0', '2024-01-05 10:50:00', 'CERTIFICATE', 15, '/docs/emp15/cert_k8s.pdf', 'CKA_Kubernetes.pdf', 710000),
(28, '2024-01-05 10:55:00', b'0', '2024-01-05 10:55:00', 'ID_CARD', 16, '/docs/emp16/cccd.pdf', 'CCCD_TranMinhHai.pdf', 505000),
(29, '2024-01-05 11:00:00', b'0', '2024-01-05 11:00:00', 'ID_CARD', 17, '/docs/emp17/cccd.pdf', 'CCCD_LyMinhHoang.pdf', 515000),
(30, '2024-01-05 11:05:00', b'0', '2024-01-05 11:05:00', 'ID_CARD', 18, '/docs/emp18/cccd.pdf', 'CCCD_DangThiQuynh.pdf', 495000),
(31, '2024-01-05 11:10:00', b'0', '2024-01-05 11:10:00', 'RESUME', 19, '/docs/emp19/resume.pdf', 'CV_NgoVanAn.pdf', 420000),
(32, '2024-01-05 11:15:00', b'0', '2024-01-05 11:15:00', 'RESUME', 20, '/docs/emp20/resume.pdf', 'CV_BuiThanhBinh.pdf', 410000),
(33, '2024-01-05 11:20:00', b'0', '2024-01-05 11:20:00', 'ID_CARD', 21, '/docs/emp21/cccd.pdf', 'CCCD_MaiVanCuong.pdf', 508000),
(34, '2024-01-05 11:25:00', b'0', '2024-01-05 11:25:00', 'CERTIFICATE', 21, '/docs/emp21/cert_aws.pdf', 'AWS_DevOps_Engineer.pdf', 690000),
(35, '2024-01-05 11:30:00', b'0', '2024-01-05 11:30:00', 'ID_CARD', 22, '/docs/emp22/cccd.pdf', 'CCCD_TrinhVanDung.pdf', 500000),
(36, '2024-01-05 11:35:00', b'0', '2024-01-05 11:35:00', 'CERTIFICATE', 22, '/docs/emp22/cert_istqb.pdf', 'ISTQB_Foundation.pdf', 580000),
(37, '2024-01-05 11:40:00', b'0', '2024-01-05 11:40:00', 'ID_CARD', 23, '/docs/emp23/cccd.pdf', 'CCCD_NguyenThiThu.pdf', 502000),
(38, '2024-01-05 11:45:00', b'0', '2024-01-05 11:45:00', 'OTHER', 24, '/docs/emp24/student_card.pdf', 'TheSinhVien_LeVanDat.pdf', 280000),

-- Phòng Tài chính
(39, '2024-01-06 10:00:00', b'0', '2024-01-06 10:00:00', 'ID_CARD', 25, '/docs/emp25/cccd.pdf', 'CCCD_NguyenThiHuong.pdf', 510000),
(40, '2024-01-06 10:05:00', b'0', '2024-01-06 10:05:00', 'CERTIFICATE', 25, '/docs/emp25/cert_cpa.pdf', 'CPA_Vietnam.pdf', 750000),
(41, '2024-01-06 10:10:00', b'0', '2024-01-06 10:10:00', 'CERTIFICATE', 25, '/docs/emp25/cert_acca.pdf', 'ACCA_Certificate.pdf', 820000),
(42, '2024-01-06 10:15:00', b'0', '2024-01-06 10:15:00', 'ID_CARD', 26, '/docs/emp26/cccd.pdf', 'CCCD_TranThiPhuong.pdf', 505000),
(43, '2024-01-06 10:20:00', b'0', '2024-01-06 10:20:00', 'ID_CARD', 27, '/docs/emp27/cccd.pdf', 'CCCD_LeThiNga.pdf', 498000),
(44, '2024-01-06 10:25:00', b'0', '2024-01-06 10:25:00', 'CERTIFICATE', 27, '/docs/emp27/cert_chief_acc.pdf', 'ChiefAccountant_License.pdf', 680000),
(45, '2024-01-06 10:30:00', b'0', '2024-01-06 10:30:00', 'ID_CARD', 28, '/docs/emp28/cccd.pdf', 'CCCD_PhamThiVan.pdf', 512000),
(46, '2024-01-06 10:35:00', b'0', '2024-01-06 10:35:00', 'ID_CARD', 29, '/docs/emp29/cccd.pdf', 'CCCD_HoangThiYen.pdf', 495000),
(47, '2024-01-06 10:40:00', b'0', '2024-01-06 10:40:00', 'ID_CARD', 30, '/docs/emp30/cccd.pdf', 'CCCD_VuThiHanh.pdf', 502000),
(48, '2024-01-06 10:45:00', b'0', '2024-01-06 10:45:00', 'ID_CARD', 31, '/docs/emp31/cccd.pdf', 'CCCD_DoThiThuy.pdf', 508000),
(49, '2024-01-06 10:50:00', b'0', '2024-01-06 10:50:00', 'ID_CARD', 32, '/docs/emp32/cccd.pdf', 'CCCD_BuiThiLoan.pdf', 500000),

-- Phòng Kinh doanh
(50, '2024-01-07 10:00:00', b'0', '2024-01-07 10:00:00', 'ID_CARD', 33, '/docs/emp33/cccd.pdf', 'CCCD_TranVanThanh.pdf', 515000),
(51, '2024-01-07 10:05:00', b'0', '2024-01-07 10:05:00', 'DEGREE', 33, '/docs/emp33/degree.pdf', 'MBA_TranVanThanh.pdf', 1100000),
(52, '2024-01-07 10:10:00', b'0', '2024-01-07 10:10:00', 'ID_CARD', 34, '/docs/emp34/cccd.pdf', 'CCCD_NguyenVanSon.pdf', 510000),
(53, '2024-01-07 10:15:00', b'0', '2024-01-07 10:15:00', 'ID_CARD', 35, '/docs/emp35/cccd.pdf', 'CCCD_LeVanHung.pdf', 502000),
(54, '2024-01-07 10:20:00', b'0', '2024-01-07 10:20:00', 'ID_CARD', 36, '/docs/emp36/cccd.pdf', 'CCCD_PhamVanTien.pdf', 498000),
(55, '2024-01-07 10:25:00', b'0', '2024-01-07 10:25:00', 'ID_CARD', 37, '/docs/emp37/cccd.pdf', 'CCCD_HoangVanDuy.pdf', 505000),
(56, '2024-01-07 10:30:00', b'0', '2024-01-07 10:30:00', 'ID_CARD', 38, '/docs/emp38/cccd.pdf', 'CCCD_NguyenThiGiang.pdf', 495000),
(57, '2024-01-07 10:35:00', b'0', '2024-01-07 10:35:00', 'ID_CARD', 39, '/docs/emp39/cccd.pdf', 'CCCD_TranVanKhoi.pdf', 512000),
(58, '2024-01-07 10:40:00', b'0', '2024-01-07 10:40:00', 'ID_CARD', 40, '/docs/emp40/cccd.pdf', 'CCCD_LeThiLy.pdf', 500000),
(59, '2024-01-07 10:45:00', b'0', '2024-01-07 10:45:00', 'ID_CARD', 41, '/docs/emp41/cccd.pdf', 'CCCD_NgoThiMy.pdf', 508000),
(60, '2024-01-07 10:50:00', b'0', '2024-01-07 10:50:00', 'ID_CARD', 42, '/docs/emp42/cccd.pdf', 'CCCD_BuiThiNhi.pdf', 502000),
(61, '2024-01-07 10:55:00', b'0', '2024-01-07 10:55:00', 'ID_CARD', 43, '/docs/emp43/cccd.pdf', 'CCCD_DangThiOanh.pdf', 498000),
(62, '2024-01-07 11:00:00', b'0', '2024-01-07 11:00:00', 'ID_CARD', 44, '/docs/emp44/cccd.pdf', 'CCCD_LeVanPhat.pdf', 515000),

-- Phòng Marketing
(63, '2024-01-08 10:00:00', b'0', '2024-01-08 10:00:00', 'ID_CARD', 45, '/docs/emp45/cccd.pdf', 'CCCD_NguyenThiQuyen.pdf', 510000),
(64, '2024-01-08 10:05:00', b'0', '2024-01-08 10:05:00', 'CERTIFICATE', 45, '/docs/emp45/cert_google.pdf', 'GoogleAds_Certification.pdf', 580000),
(65, '2024-01-08 10:10:00', b'0', '2024-01-08 10:10:00', 'ID_CARD', 46, '/docs/emp46/cccd.pdf', 'CCCD_TranHongRose.pdf', 502000),
(66, '2024-01-08 10:15:00', b'0', '2024-01-08 10:15:00', 'ID_CARD', 47, '/docs/emp47/cccd.pdf', 'CCCD_LeThiSuong.pdf', 495000),
(67, '2024-01-08 10:20:00', b'0', '2024-01-08 10:20:00', 'ID_CARD', 48, '/docs/emp48/cccd.pdf', 'CCCD_PhamVanTam.pdf', 508000),
(68, '2024-01-08 10:25:00', b'0', '2024-01-08 10:25:00', 'ID_CARD', 49, '/docs/emp49/cccd.pdf', 'CCCD_HoangThiUyen.pdf', 500000),
(69, '2024-01-08 10:30:00', b'0', '2024-01-08 10:30:00', 'ID_CARD', 50, '/docs/emp50/cccd.pdf', 'CCCD_VuThiVy.pdf', 512000),

-- Phòng Vận hành
(70, '2024-01-09 10:00:00', b'0', '2024-01-09 10:00:00', 'ID_CARD', 51, '/docs/emp51/cccd.pdf', 'CCCD_NguyenVanXuan.pdf', 515000),
(71, '2024-01-09 10:05:00', b'0', '2024-01-09 10:05:00', 'ID_CARD', 52, '/docs/emp52/cccd.pdf', 'CCCD_TranThiYen.pdf', 505000),
(72, '2024-01-09 10:10:00', b'0', '2024-01-09 10:10:00', 'ID_CARD', 53, '/docs/emp53/cccd.pdf', 'CCCD_LeVanBach.pdf', 502000),
(73, '2024-01-09 10:15:00', b'0', '2024-01-09 10:15:00', 'ID_CARD', 54, '/docs/emp54/cccd.pdf', 'CCCD_NguyenThiChi.pdf', 498000),
(74, '2024-01-09 10:20:00', b'0', '2024-01-09 10:20:00', 'ID_CARD', 55, '/docs/emp55/cccd.pdf', 'CCCD_PhamVanDao.pdf', 508000),
(75, '2024-01-09 10:25:00', b'0', '2024-01-09 10:25:00', 'ID_CARD', 56, '/docs/emp56/cccd.pdf', 'CCCD_TranThiEm.pdf', 495000),
(76, '2024-01-09 10:30:00', b'0', '2024-01-09 10:30:00', 'ID_CARD', 57, '/docs/emp57/cccd.pdf', 'CCCD_NguyenVanGiai.pdf', 510000),
(77, '2024-01-09 10:35:00', b'0', '2024-01-09 10:35:00', 'OTHER', 57, '/docs/emp57/license.pdf', 'BangLaiXe_B2.pdf', 350000),

-- Phòng R&D
(78, '2024-01-10 10:00:00', b'0', '2024-01-10 10:00:00', 'ID_CARD', 58, '/docs/emp58/cccd.pdf', 'CCCD_TranVanHao.pdf', 520000),
(79, '2024-01-10 10:05:00', b'0', '2024-01-10 10:05:00', 'DEGREE', 58, '/docs/emp58/degree.pdf', 'TienSi_KhoaHocMayTinh.pdf', 1600000),
(80, '2024-01-10 10:10:00', b'0', '2024-01-10 10:10:00', 'ID_CARD', 59, '/docs/emp59/cccd.pdf', 'CCCD_LeVanIch.pdf', 505000),
(81, '2024-01-10 10:15:00', b'0', '2024-01-10 10:15:00', 'DEGREE', 59, '/docs/emp59/degree.pdf', 'ThacSi_AI_ML.pdf', 1200000),
(82, '2024-01-10 10:20:00', b'0', '2024-01-10 10:20:00', 'ID_CARD', 60, '/docs/emp60/cccd.pdf', 'CCCD_NguyenVanKhang.pdf', 498000),
(83, '2024-01-10 10:25:00', b'0', '2024-01-10 10:25:00', 'ID_CARD', 61, '/docs/emp61/cccd.pdf', 'CCCD_PhamVanLam.pdf', 502000),

-- Nhân viên nghỉ việc
(84, '2024-01-11 10:00:00', b'0', '2024-01-11 10:00:00', 'ID_CARD', 62, '/docs/emp62/cccd.pdf', 'CCCD_HoangVanMinh.pdf', 510000),
(85, '2024-01-11 10:05:00', b'0', '2024-01-11 10:05:00', 'ID_CARD', 63, '/docs/emp63/cccd.pdf', 'CCCD_TranThiNhung.pdf', 495000);

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- SUMMARY DATA
-- ============================================
-- Users: 67 (1 Admin, 6 HR, 3 Accountant, 54 Employee active, 2 Disabled/Resigned, 2 Pending, 1 Deleted)
-- Departments: 9 (8 Active, 1 Soft-deleted)
-- Positions: 44 (đa dạng theo phòng ban)
-- Employees: 63 (60 Active + 1 On_Leave + 2 Resigned)
-- Contracts: 66 (62 Active, 4 Expired/Terminated)
-- Documents: 85

-- PHÂN BỐ NHÂN SỰ THEO PHÒNG BAN:
-- Ban Giám đốc: 3 người (CEO, CTO, CFO)
-- Phòng Nhân sự: 6 người
-- Phòng IT: 15 người
-- Phòng Tài chính: 8 người
-- Phòng Kinh doanh: 12 người
-- Phòng Marketing: 6 người
-- Phòng Vận hành: 7 người
-- Phòng R&D: 4 người
-- Đã nghỉ việc: 2 người

-- MỨC LƯƠNG:
-- Ban Giám đốc: 90-120 triệu
-- Trưởng phòng: 42-65 triệu
-- Phó phòng/Leader: 30-52 triệu
-- Senior/Chuyên viên: 20-45 triệu
-- Junior/Nhân viên: 12-24 triệu
-- Thực tập: 5-10 triệu
-- ============================================