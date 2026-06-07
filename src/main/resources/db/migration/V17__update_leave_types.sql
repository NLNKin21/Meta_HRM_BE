-- ============================================================
-- V17: UPDATE LEAVE TYPES
-- Chỉ giữ lại 4 loại:
--   1. ANNUAL_LEAVE    - Nghỉ phép năm
--   2. SICK_LEAVE      - Nghỉ ốm
--   3. MATERNITY_LEAVE - Nghỉ thai sản
--   5. SPECIAL_LEAVE   - Đổi tên thành "Nghỉ đặc biệt"
--
-- Xóa hẳn 3 loại:
--   4. PATERNITY_LEAVE    (có 2 balances, 0 requests)
--   6. UNPAID_LEAVE       (0 balances, 0 requests)
--   7. COMPENSATORY_LEAVE (0 balances, 0 requests)
-- ============================================================

-- ------------------------------------------------------------
-- 1) Đổi tên SPECIAL_LEAVE thành "Nghỉ đặc biệt"
--    Giữ nguyên code, id, và tất cả balances hiện có
-- ------------------------------------------------------------
UPDATE leave_types
SET name = 'Nghỉ đặc biệt'
WHERE code = 'SPECIAL_LEAVE';

-- ------------------------------------------------------------
-- 2) Xóa dữ liệu liên quan trước khi xóa leave_types
--    Thứ tự: seniority_rules → balances → leave_types
-- ------------------------------------------------------------

-- 2a) Xóa seniority rules
DELETE FROM leave_type_seniority_rules
WHERE leave_type_id IN (
    SELECT id FROM leave_types
    WHERE code IN ('PATERNITY_LEAVE', 'UNPAID_LEAVE', 'COMPENSATORY_LEAVE')
);

-- 2b) Xóa leave balances
DELETE FROM leave_balances
WHERE leave_type_id IN (
    SELECT id FROM leave_types
    WHERE code IN ('PATERNITY_LEAVE', 'UNPAID_LEAVE', 'COMPENSATORY_LEAVE')
);

-- 2c) Xóa leave requests (phòng trường hợp có)
DELETE FROM leave_requests
WHERE leave_type_id IN (
    SELECT id FROM leave_types
    WHERE code IN ('PATERNITY_LEAVE', 'UNPAID_LEAVE', 'COMPENSATORY_LEAVE')
);

-- ------------------------------------------------------------
-- 3) Xóa hẳn 3 loại nghỉ
-- ------------------------------------------------------------
DELETE FROM leave_types
WHERE code IN ('PATERNITY_LEAVE', 'UNPAID_LEAVE', 'COMPENSATORY_LEAVE');