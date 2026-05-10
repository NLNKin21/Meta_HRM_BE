-- V17__create_contract_types_table.sql
-- ============================================================================
-- Module: Contract Type Configuration
-- Description: Chuyển contract_type từ enum sang bảng cấu hình do HR quản lý
-- ============================================================================

SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================================================
-- BƯỚC 1: Tạo bảng contract_types
-- ============================================================================
CREATE TABLE contract_types (
    id              INT AUTO_INCREMENT PRIMARY KEY,

    type_code       VARCHAR(50)  NOT NULL,
    type_name       VARCHAR(255) NOT NULL,
    description     TEXT         NULL,
    notes           TEXT         NULL,

    duration_unit   ENUM('MONTH', 'YEAR', 'INDEFINITE') NOT NULL DEFAULT 'MONTH',
    duration_value  INT          NULL,

    require_file    BIT(1)       NOT NULL DEFAULT b'1',
    clause_template TEXT         NULL,

    is_active       BIT(1)       NOT NULL DEFAULT b'1',
    is_deleted      BIT(1)       NOT NULL DEFAULT b'0',

    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      INT          NULL,
    updated_by      INT          NULL,

    CONSTRAINT uk_contract_types_type_code UNIQUE (type_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- BƯỚC 2: Seed dữ liệu
-- ============================================================================
INSERT INTO contract_types
    (type_code, type_name, description, duration_unit, duration_value, require_file, clause_template, is_active, is_deleted, created_by, updated_by)
VALUES
    ('PERMANENT',  'Hợp đồng lao động vô thời hạn', 'Hợp đồng không xác định thời hạn, áp dụng cho nhân viên chính thức lâu năm, đã qua ít nhất 2 lần ký hợp đồng có thời hạn', 'INDEFINITE', NULL, b'1', 'Điều 1: Loại hợp đồng - Hợp đồng lao động không xác định thời hạn\nĐiều 2: Công việc - Theo mô tả công việc đính kèm\nĐiều 3: Thời gian làm việc - 8 giờ/ngày, 5 ngày/tuần\nĐiều 4: Chế độ - Theo quy chế công ty và pháp luật lao động hiện hành', b'1', b'0', NULL, NULL),
    ('DEFINITE',   'Hợp đồng lao động có thời hạn', 'Hợp đồng xác định thời hạn từ 12 đến 36 tháng, áp dụng cho nhân viên chính thức sau thử việc', 'YEAR', 3, b'1', 'Điều 1: Loại hợp đồng - Hợp đồng lao động xác định thời hạn\nĐiều 2: Thời hạn - Theo thời hạn ghi trên hợp đồng\nĐiều 3: Công việc - Theo mô tả công việc đính kèm\nĐiều 4: Thời gian làm việc - 8 giờ/ngày, 5 ngày/tuần\nĐiều 5: Gia hạn - Hợp đồng có thể gia hạn theo thỏa thuận hai bên', b'1', b'0', NULL, NULL),
    ('PROBATION',  'Hợp đồng thử việc', 'Hợp đồng trong thời gian thử việc, từ 1 đến 6 tháng tùy vị trí. Lương thử việc tối thiểu 85% lương chính thức', 'MONTH', 2, b'1', 'Điều 1: Loại hợp đồng - Hợp đồng thử việc\nĐiều 2: Thời gian thử việc - Theo thời hạn ghi trên hợp đồng\nĐiều 3: Mức lương thử việc - 85% lương chính thức\nĐiều 4: Đánh giá - Kết thúc thử việc sẽ được đánh giá để ký hợp đồng chính thức\nĐiều 5: Chấm dứt - Hai bên có quyền chấm dứt mà không cần báo trước', b'1', b'0', NULL, NULL),
    ('SEASONAL',   'Hợp đồng thời vụ', 'Hợp đồng ngắn hạn theo mùa vụ hoặc dự án cụ thể, thời hạn dưới 12 tháng', 'MONTH', 6, b'1', 'Điều 1: Loại hợp đồng - Hợp đồng lao động theo mùa vụ\nĐiều 2: Thời hạn - Theo thời hạn ghi trên hợp đồng\nĐiều 3: Công việc - Theo yêu cầu dự án/mùa vụ cụ thể\nĐiều 4: Thanh toán - Theo thỏa thuận trên hợp đồng', b'1', b'0', NULL, NULL),
    ('PART_TIME',  'Hợp đồng bán thời gian', 'Hợp đồng dành cho nhân viên làm việc bán thời gian, dưới 8 giờ/ngày hoặc dưới 5 ngày/tuần', 'MONTH', 12, b'1', 'Điều 1: Loại hợp đồng - Hợp đồng lao động bán thời gian\nĐiều 2: Thời gian làm việc - Theo lịch thỏa thuận, không quá 4 giờ/ngày\nĐiều 3: Lương - Tính theo giờ làm việc thực tế\nĐiều 4: Chế độ - Theo quy định pháp luật về lao động bán thời gian', b'1', b'0', NULL, NULL),
    ('INTERNSHIP', 'Hợp đồng thực tập', 'Hợp đồng dành cho sinh viên thực tập, có thể có hoặc không có lương. Thời hạn từ 1 đến 6 tháng', 'MONTH', 6, b'0', 'Điều 1: Loại hợp đồng - Hợp đồng thực tập sinh\nĐiều 2: Thời hạn thực tập - Theo thời hạn ghi trên hợp đồng\nĐiều 3: Phụ cấp - Theo chính sách công ty\nĐiều 4: Người hướng dẫn - Được phân công người hướng dẫn\nĐiều 5: Đánh giá - Được đánh giá cuối kỳ thực tập', b'1', b'0', NULL, NULL),
    ('FULL_TIME',  'Hợp đồng lao động toàn thời gian', 'Hợp đồng toàn thời gian cho nhân viên chính thức, làm việc 8 giờ/ngày, 5 ngày/tuần', 'YEAR', 3, b'1', 'Điều 1: Loại hợp đồng - Hợp đồng lao động toàn thời gian\nĐiều 2: Thời hạn - Theo thời hạn ghi trên hợp đồng\nĐiều 3: Thời gian làm việc - 8 giờ/ngày, 5 ngày/tuần\nĐiều 4: Lương và phụ cấp - Theo thỏa thuận trên hợp đồng\nĐiều 5: Chế độ BHXH, BHYT, BHTN - Theo quy định pháp luật hiện hành', b'1', b'0', NULL, NULL);

-- ============================================================================
-- BƯỚC 3: Thêm cột contract_type_id vào bảng contracts
-- ============================================================================
ALTER TABLE contracts
    ADD COLUMN contract_type_id INT NULL AFTER emp_id;

-- ============================================================================
-- BƯỚC 4: Migrate dữ liệu từ cột enum cũ sang FK mới
-- Nếu cột contract_type cũ có dữ liệu thì map sang
-- Nếu dữ liệu cũ là NULL thì giữ nguyên contract_type_id = NULL
-- ============================================================================
UPDATE contracts c
INNER JOIN contract_types ct
    ON ct.type_code COLLATE utf8mb4_unicode_ci = CAST(c.contract_type AS CHAR) COLLATE utf8mb4_unicode_ci
SET c.contract_type_id = ct.id
WHERE c.contract_type IS NOT NULL;

-- ============================================================================
-- BƯỚC 5: Tạo FK constraint
-- ============================================================================
ALTER TABLE contracts
    ADD CONSTRAINT fk_contracts_contract_type_id
        FOREIGN KEY (contract_type_id)
        REFERENCES contract_types(id);

-- ============================================================================
-- BƯỚC 6: Tạo index
-- ============================================================================
CREATE INDEX idx_contracts_contract_type_id
    ON contracts(contract_type_id);

CREATE INDEX idx_contract_types_is_active
    ON contract_types(is_active, is_deleted);

CREATE INDEX idx_contract_types_type_code
    ON contract_types(type_code, is_deleted);

-- ============================================================================
-- BƯỚC 7: CHƯA ép NOT NULL và CHƯA drop cột cũ trong migration này
-- Vì dữ liệu cũ hiện không có contract_type để migrate
-- Sẽ xử lý ở migration sau sau khi dữ liệu được bổ sung / làm sạch
-- ============================================================================
-- ALTER TABLE contracts
--     MODIFY COLUMN contract_type_id INT NOT NULL;

-- ALTER TABLE contracts
--     DROP COLUMN contract_type;