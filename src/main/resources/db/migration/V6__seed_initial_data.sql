-- ============================================
-- V3__seed_initial_data.sql
-- Seed data cho Employee Management System
-- Password mặc định: 12345678
-- ============================================

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- USERS
-- Role:   0=ADMIN, 1=EMPLOYEE, 2=HR, 3=ACCOUNTANT
-- Status: 0=ACTIVE, 1=DELETED, 2=DISABLED, 3=PENDING
-- ============================================
TRUNCATE TABLE `users`;
INSERT INTO `users`
(`id`,`created_at`,`is_deleted`,`updated_at`,`email`,`password`,`role`,`status`,`username`) VALUES
-- Admin hệ thống
(1, '2025-01-02 09:00:00',b'0','2025-01-02 09:00:00','admin@gmail.com',         '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa',0,0,'admin'),
-- Trưởng phòng HR → role=HR(2)
(2, '2025-01-03 09:10:00',b'0','2025-01-03 09:10:00','hr.manager@company.com',   '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa',2,0,'hr_manager'),
-- Trưởng phòng IT → role=EMPLOYEE(1), quyền phòng ban xác định bởi role_in_dept
(3, '2025-01-04 09:20:00',b'0','2025-01-04 09:20:00','it.manager@company.com',   '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa',1,0,'it_manager'),
-- Trưởng phòng Tài chính → role=ACCOUNTANT(3)
(4, '2025-01-05 09:30:00',b'0','2025-01-05 09:30:00','finance.manager@company.com','$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa',3,0,'finance_manager'),
-- Trưởng phòng Sales → role=EMPLOYEE(1)
(5, '2025-01-06 09:40:00',b'0','2025-01-06 09:40:00','sales.manager@company.com','$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa',1,0,'sales_manager'),
-- Nhân viên thường
(6, '2025-01-07 09:50:00',b'0','2025-01-07 09:50:00','nguyen.vana@company.com',  '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa',1,0,'nguyen_vana'),
(7, '2025-01-07 10:00:00',b'0','2025-01-07 10:00:00','tran.thib@company.com',    '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa',1,0,'tran_thib'),
(8, '2025-01-07 10:10:00',b'0','2025-01-07 10:10:00','le.vanc@company.com',      '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa',1,0,'le_vanc'),
(9, '2025-01-07 10:20:00',b'0','2025-01-07 10:20:00','pham.thid@company.com',    '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa',1,0,'pham_thid'),
(10,'2025-01-07 10:30:00',b'0','2025-01-07 10:30:00','hoang.vane@company.com',   '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa',1,0,'hoang_vane'),
(11,'2025-01-07 10:40:00',b'0','2025-01-07 10:40:00','vu.thif@company.com',      '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa',1,0,'vu_thif'),
(12,'2025-01-07 10:50:00',b'0','2025-01-07 10:50:00','do.vang@company.com',      '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa',1,0,'do_vang'),
(13,'2025-01-07 11:00:00',b'0','2025-01-07 11:00:00','bui.thih@company.com',     '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa',1,0,'bui_thih'),
-- Thực tập sinh
(14,'2025-01-07 11:10:00',b'0','2025-01-07 11:10:00','intern1@company.com',      '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa',1,0,'intern_minh'),
(15,'2025-01-07 11:20:00',b'0','2025-01-07 11:20:00','intern2@company.com',      '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa',1,0,'intern_lan'),
-- Đã nghỉ việc → status=DISABLED(2)
(16,'2025-01-07 11:30:00',b'0','2025-01-07 11:30:00','resigned@company.com',     '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa',1,2,'resigned_user'),
-- Chờ kích hoạt → status=PENDING(3)
(17,'2025-01-07 11:40:00',b'0','2025-01-07 11:40:00','pending@company.com',      '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa',1,3,'pending_user'),
-- Đã xóa mềm → is_deleted=1, status=DELETED(1)
(18,'2025-01-07 11:50:00',b'1','2025-01-07 11:50:00','deleted@company.com',      '$2a$10$SPsuhzJ5RkMZXz1hLucDi.pBNNF7E.EaR2VnKpMTxlTKQOW9oMpLa',1,1,'deleted_user');

-- ============================================
-- DEPARTMENTS
-- ============================================
TRUNCATE TABLE `departments`;
INSERT INTO `departments`
(`id`,`created_at`,`is_deleted`,`updated_at`,`dept_name`) VALUES
(1,'2025-01-02 09:00:00',b'0','2025-01-02 09:00:00','Human Resources'),
(2,'2025-01-02 09:05:00',b'0','2025-01-02 09:05:00','Information Technology'),
(3,'2025-01-02 09:10:00',b'0','2025-01-02 09:10:00','Finance & Accounting'),
(4,'2025-01-02 09:15:00',b'0','2025-01-02 09:15:00','Sales & Marketing'),
(5,'2025-01-02 09:20:00',b'0','2025-01-02 09:20:00','Operations'),
(6,'2025-01-02 09:25:00',b'0','2025-01-02 09:25:00','Research & Development'),
(7,'2025-01-02 09:30:00',b'1','2025-01-02 09:30:00','Discontinued Dept');

-- ============================================
-- EMPLOYEES
-- Gender:      MALE | FEMALE | OTHER
-- Status:      ACTIVE | ON_LEAVE | TERMINATED | RESIGNED
-- RoleInDept:  HEAD | DEPUTY | LEADER | STAFF
-- ============================================
TRUNCATE TABLE `employees`;
INSERT INTO `employees`
(`id`,`created_at`,`is_deleted`,`updated_at`,`address`,`basic_salary`,`dept_id`,`dob`,`full_name`,`gender`,`hire_date`,`phone_number`,`role_in_dept`,`status`,`user_id`) VALUES
-- Trưởng phòng HR
(1, '2025-01-03 09:00:00',b'0','2025-01-03 09:00:00','123 Nguyễn Huệ, Q1, TP.HCM',      35000000.00,1,'1985-03-15','Trần Thị Mai',    'FEMALE','2018-01-15','0901234567','HEAD',  'ACTIVE',   2),
-- Trưởng phòng IT
(2, '2025-01-03 09:05:00',b'0','2025-01-03 09:05:00','456 Lê Lợi, Q1, TP.HCM',           45000000.00,2,'1987-07-22','Nguyễn Văn Hùng', 'MALE',  '2017-06-01','0912345678','HEAD',  'ACTIVE',   3),
-- Trưởng phòng Tài chính
(3, '2025-01-03 09:10:00',b'0','2025-01-03 09:10:00','789 Pasteur, Q3, TP.HCM',           40000000.00,3,'1983-11-08','Lê Thị Hương',    'FEMALE','2016-03-10','0923456789','HEAD',  'ACTIVE',   4),
-- Trưởng phòng Sales
(4, '2025-01-03 09:15:00',b'0','2025-01-03 09:15:00','321 Võ Văn Tần, Q3, TP.HCM',       38000000.00,4,'1986-09-30','Phạm Văn Đức',    'MALE',  '2019-02-20','0934567890','HEAD',  'ACTIVE',   5),
-- Nhân viên HR
(5, '2025-01-03 09:20:00',b'0','2025-01-03 09:20:00','654 Điện Biên Phủ, Bình Thạnh',    18000000.00,1,'1994-07-18','Hoàng Thị Lan',   'FEMALE','2022-07-01','0945678901','STAFF', 'ACTIVE',   6),
-- Team lead IT (SENIOR → LEADER)
(6, '2025-01-03 09:25:00',b'0','2025-01-03 09:25:00','987 Cộng Hòa, Tân Bình',           25000000.00,2,'1992-02-12','Vũ Văn Nam',      'MALE',  '2021-05-12','0956789012','LEADER','ACTIVE',   7),
-- Nhân viên IT
(7, '2025-01-03 09:30:00',b'0','2025-01-03 09:30:00','159 Trường Chinh, Tân Bình',       22000000.00,2,'1995-04-05','Đỗ Thị Hồng',     'FEMALE','2022-01-05','0967890123','STAFF', 'ACTIVE',   8),
-- Nhân viên Tài chính
(8, '2025-01-03 09:35:00',b'0','2025-01-03 09:35:00','258 Lý Thường Kiệt, Q10',          20000000.00,3,'1993-10-15','Bùi Văn Khoa',    'MALE',  '2023-03-01','0978901234','STAFF', 'ACTIVE',   9),
-- Nhân viên Sales
(9, '2025-01-03 09:40:00',b'0','2025-01-03 09:40:00','357 Nguyễn Thị Minh Khai, Q1',     19000000.00,4,'1996-06-20','Ngô Thị Yến',     'FEMALE','2023-06-15','0989012345','STAFF', 'ACTIVE',  10),
-- Trưởng phòng Operations - đang nghỉ dài hạn
(10,'2025-01-03 09:45:00',b'0','2025-01-03 09:45:00','468 Hai Bà Trưng, Q1',              30000000.00,5,'1988-08-14','Đinh Văn Tùng',   'MALE',  '2020-09-15','0990123456','HEAD',  'ON_LEAVE', 11),
-- Nhân viên Operations
(11,'2025-01-03 09:50:00',b'0','2025-01-03 09:50:00','579 Nam Kỳ Khởi Nghĩa, Q3',       17000000.00,5,'1997-01-25','Lý Thị Nga',      'FEMALE','2024-01-10','0901122334','STAFF', 'ACTIVE',  12),
-- Nhân viên R&D
(12,'2025-01-03 09:55:00',b'0','2025-01-03 09:55:00','680 Võ Thị Sáu, Q3',               16000000.00,6,'1998-12-05','Trịnh Văn Bình',  'MALE',  '2024-02-01','0902233445','STAFF', 'ACTIVE',  13),
-- Thực tập sinh IT (INTERN → STAFF, status INTERN → ACTIVE)
(13,'2025-01-03 10:00:00',b'0','2025-01-03 10:00:00','791 Phan Xích Long, Phú Nhuận',     8000000.00,2,'2001-03-10','Cao Minh Tuấn',   'MALE',  '2025-01-01','0903344556','STAFF', 'ACTIVE',  14),
-- Thực tập sinh Sales (INTERN → STAFF, status INTERN → ACTIVE)
(14,'2025-01-03 10:05:00',b'0','2025-01-03 10:05:00','802 Nguyễn Văn Trỗi, Phú Nhuận',    8000000.00,4,'2002-05-15','Đặng Thị Lan',    'FEMALE','2025-01-01','0904455667','STAFF', 'ACTIVE',  15),
-- Đã nghỉ việc (INACTIVE → RESIGNED)
(15,'2025-01-03 10:10:00',b'0','2025-01-03 10:10:00','913 Phan Đăng Lưu, Phú Nhuận',     15000000.00,1,'1991-04-08','Mai Văn Quang',   'MALE',  '2021-08-01','0905566778','STAFF', 'RESIGNED', 16),
-- Bị sa thải (TERMINATED)
(16,'2025-01-03 10:15:00',b'0','2025-01-03 10:15:00','024 Hoàng Văn Thụ, Tân Bình',       0.00,      2,'1999-09-20','Phan Thị Thảo',   'FEMALE','2025-02-01','0906677889','STAFF', 'TERMINATED',17);

-- ============================================
-- CONTRACTS
-- ContractType: PROBATION | PART_TIME | FULL_TIME | INTERNSHIP | SEASONAL
-- Status:       ACTIVE | EXPIRED | TERMINATED | PENDING
-- ============================================
TRUNCATE TABLE `contracts`;
INSERT INTO `contracts`
(`id`,`created_at`,`is_deleted`,`updated_at`,`contract_type`,`emp_id`,`end_date`,`file_url`,`start_date`,`status`) VALUES
(1, '2025-01-05 09:00:00',b'0','2025-01-05 09:00:00','FULL_TIME',  1, NULL,        '/contracts/emp1_fulltime.pdf',     '2018-01-15','ACTIVE'),
(2, '2025-01-05 09:05:00',b'0','2025-01-05 09:05:00','FULL_TIME',  2, NULL,        '/contracts/emp2_fulltime.pdf',     '2017-06-01','ACTIVE'),
(3, '2025-01-05 09:10:00',b'0','2025-01-05 09:10:00','FULL_TIME',  3, NULL,        '/contracts/emp3_fulltime.pdf',     '2016-03-10','ACTIVE'),
(4, '2025-01-05 09:15:00',b'0','2025-01-05 09:15:00','FULL_TIME',  4,'2026-02-20','/contracts/emp4_fulltime.pdf',     '2019-02-20','ACTIVE'),
(5, '2025-01-05 09:20:00',b'0','2025-01-05 09:20:00','PROBATION',  5,'2022-09-01','/contracts/emp5_probation.pdf',    '2022-07-01','EXPIRED'),
(6, '2025-01-05 09:25:00',b'0','2025-01-05 09:25:00','FULL_TIME',  5,'2025-07-01','/contracts/emp5_fulltime.pdf',     '2022-09-01','ACTIVE'),
(7, '2025-01-05 09:30:00',b'0','2025-01-05 09:30:00','FULL_TIME',  6,'2026-05-12','/contracts/emp6_fulltime.pdf',     '2021-05-12','ACTIVE'),
(8, '2025-01-05 09:35:00',b'0','2025-01-05 09:35:00','FULL_TIME',  7,'2025-01-05','/contracts/emp7_fulltime.pdf',     '2022-01-05','EXPIRED'),
(9, '2025-01-05 09:40:00',b'0','2025-01-05 09:40:00','FULL_TIME',  7,'2027-01-05','/contracts/emp7_fulltime_v2.pdf',  '2025-01-05','ACTIVE'),
(10,'2025-01-05 09:45:00',b'0','2025-01-05 09:45:00','FULL_TIME',  8,'2026-03-01','/contracts/emp8_fulltime.pdf',     '2023-03-01','ACTIVE'),
(11,'2025-01-05 09:50:00',b'0','2025-01-05 09:50:00','FULL_TIME',  9,'2026-06-15','/contracts/emp9_fulltime.pdf',     '2023-06-15','ACTIVE'),
(12,'2025-01-05 09:55:00',b'0','2025-01-05 09:55:00','FULL_TIME', 10,'2025-09-15','/contracts/emp10_fulltime.pdf',    '2020-09-15','ACTIVE'),
(13,'2025-01-05 10:00:00',b'0','2025-01-05 10:00:00','FULL_TIME', 11,'2027-01-10','/contracts/emp11_fulltime.pdf',    '2024-01-10','ACTIVE'),
(14,'2025-01-05 10:05:00',b'0','2025-01-05 10:05:00','FULL_TIME', 12,'2027-02-01','/contracts/emp12_fulltime.pdf',    '2024-02-01','ACTIVE'),
(15,'2025-01-05 10:10:00',b'0','2025-01-05 10:10:00','INTERNSHIP',13,'2025-06-30','/contracts/emp13_intern.pdf',      '2025-01-01','ACTIVE'),
(16,'2025-01-05 10:15:00',b'0','2025-01-05 10:15:00','INTERNSHIP',14,'2025-06-30','/contracts/emp14_intern.pdf',      '2025-01-01','ACTIVE'),
(17,'2025-01-05 10:20:00',b'0','2025-01-05 10:20:00','FULL_TIME', 15,'2024-08-01','/contracts/emp15_fulltime.pdf',    '2021-08-01','TERMINATED'),
(18,'2025-01-05 10:25:00',b'0','2025-01-05 10:25:00','PROBATION', 16,'2025-04-01','/contracts/emp16_probation.pdf',   '2025-02-01','PENDING');

-- ============================================
-- EMPLOYEE_DOCUMENTS
-- DocType: RESUME | HEALTH_INSURANCE | LABOR_CONTRACT | DEGREE |
--          CERTIFICATE | ID_CARD | TAX_ID | OTHER
-- ============================================
TRUNCATE TABLE `employee_documents`;
INSERT INTO `employee_documents`
(`id`,`created_at`,`is_deleted`,`updated_at`,`doc_type`,`emp_id`,`file_url`,`original_name`,`file_size`) VALUES
(1, '2025-01-06 10:00:00',b'0','2025-01-06 10:00:00','ID_CARD',          1, '/docs/emp1/cccd.pdf',      'CCCD_TranThiMai.pdf',           512000),
(2, '2025-01-06 10:05:00',b'0','2025-01-06 10:05:00','DEGREE',           1, '/docs/emp1/degree.pdf',    'BangDH_TranThiMai.pdf',        1024000),
(3, '2025-01-06 10:10:00',b'0','2025-01-06 10:10:00','CERTIFICATE',      1, '/docs/emp1/hr_cert.pdf',   'ChungChi_HR.pdf',               780000),
(4, '2025-01-06 10:15:00',b'0','2025-01-06 10:15:00','ID_CARD',          2, '/docs/emp2/cccd.pdf',      'CCCD_NguyenVanHung.pdf',        498000),
(5, '2025-01-06 10:20:00',b'0','2025-01-06 10:20:00','DEGREE',           2, '/docs/emp2/degree.pdf',    'BangThacSi_IT.pdf',            1200000),
(6, '2025-01-06 10:25:00',b'0','2025-01-06 10:25:00','CERTIFICATE',      2, '/docs/emp2/aws.pdf',       'AWS_Solutions_Architect.pdf',    650000),
(7, '2025-01-06 10:30:00',b'0','2025-01-06 10:30:00','CERTIFICATE',      2, '/docs/emp2/pmp.pdf',       'PMP_Certificate.pdf',           720000),
(8, '2025-01-06 10:35:00',b'0','2025-01-06 10:35:00','ID_CARD',          3, '/docs/emp3/cccd.pdf',      'CCCD_LeThiHuong.pdf',           505000),
(9, '2025-01-06 10:40:00',b'0','2025-01-06 10:40:00','CERTIFICATE',      3, '/docs/emp3/cpa.pdf',       'CPA_Certificate.pdf',           890000),
(10,'2025-01-06 10:45:00',b'0','2025-01-06 10:45:00','ID_CARD',          4, '/docs/emp4/cccd.pdf',      'CCCD_PhamVanDuc.pdf',           510000),
(11,'2025-01-06 10:50:00',b'0','2025-01-06 10:50:00','RESUME',           5, '/docs/emp5/resume.pdf',    'CV_HoangThiLan.pdf',            420000),
(12,'2025-01-06 10:55:00',b'0','2025-01-06 10:55:00','ID_CARD',          6, '/docs/emp6/cccd.pdf',      'CCCD_VuVanNam.pdf',             515000),
(13,'2025-01-06 11:00:00',b'0','2025-01-06 11:00:00','CERTIFICATE',      6, '/docs/emp6/java.pdf',      'OCP_Java.pdf',                  680000),
(14,'2025-01-06 11:05:00',b'0','2025-01-06 11:05:00','HEALTH_INSURANCE', 7, '/docs/emp7/health.pdf',    'BaoHiemYTe.pdf',                320000),
(15,'2025-01-06 11:10:00',b'0','2025-01-06 11:10:00','ID_CARD',          8, '/docs/emp8/cccd.pdf',      'CCCD_BuiVanKhoa.pdf',           495000),
(16,'2025-01-06 11:15:00',b'0','2025-01-06 11:15:00','DEGREE',           9, '/docs/emp9/degree.pdf',    'BangDH_Marketing.pdf',          980000),
(17,'2025-01-06 11:20:00',b'0','2025-01-06 11:20:00','ID_CARD',         10, '/docs/emp10/cccd.pdf',     'CCCD_DinhVanTung.pdf',          502000),
(18,'2025-01-06 11:25:00',b'0','2025-01-06 11:25:00','OTHER',           13, '/docs/emp13/student.pdf',  'TheSinhVien.pdf',               280000),
(19,'2025-01-06 11:30:00',b'0','2025-01-06 11:30:00','OTHER',           14, '/docs/emp14/student.pdf',  'TheSinhVien.pdf',               275000),
(20,'2025-01-06 11:35:00',b'0','2025-01-06 11:35:00','RESUME',          15, '/docs/emp15/resume.pdf',   'CV_MaiVanQuang.pdf',            450000);

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- SUMMARY DATA
-- ============================================
-- Users:       18 (1 Admin, 1 HR, 1 Accountant, 12 Employee active,
--                   1 Disabled, 1 Pending, 1 Deleted)
-- Departments:  7 (6 Active, 1 Soft-deleted)
-- Employees:   16 (12 Active, 1 On_Leave, 1 Resigned, 1 Terminated, 1 Pending contract)
-- Contracts:   18 (13 Active, 2 Expired, 1 Terminated, 1 Pending)
-- Documents:   20
--
-- ENUM MAPPING:
--   UserRole:          ADMIN=0, EMPLOYEE=1, HR=2, ACCOUNTANT=3
--   UserStatus:        ACTIVE=0, DELETED=1, DISABLED=2, PENDING=3
--   EmployeeStatus:    ACTIVE, ON_LEAVE, TERMINATED, RESIGNED
--   RoleInDepartment:  HEAD, DEPUTY, LEADER, STAFF
--   ContractType:      PROBATION, PART_TIME, FULL_TIME, INTERNSHIP, SEASONAL
--   ContractStatus:    ACTIVE, EXPIRED, TERMINATED, PENDING
--   DocumentType:      RESUME, HEALTH_INSURANCE, LABOR_CONTRACT, DEGREE,
--                      CERTIFICATE, ID_CARD, TAX_ID, OTHER
--   Gender:            MALE, FEMALE, OTHER
-- ============================================