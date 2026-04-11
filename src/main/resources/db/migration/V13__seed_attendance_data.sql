-- ================================================
-- ATTENDANCE MODULE - SEED DATA
-- Version: V13
-- Location: Hà Nội, Vietnam
-- ================================================

-- ================================================
-- WORK LOCATIONS (HÀ NỘI)
-- ================================================
INSERT INTO work_locations (name, code, address, latitude, longitude, radius, description, contact_person, contact_phone, is_active, created_by) VALUES
('Văn phòng HQ - Mễ Trì', 'HQ-MT', 'Tầng 5, Tòa nhà Golden Palace, 123 Mễ Trì, Nam Từ Liêm, Hà Nội', 21.028500, 105.754200, 150, 'Văn phòng trụ sở chính tại Hà Nội', 'Nguyễn Văn A', '0912345678', TRUE, 1),
('Chi nhánh ĐH Thăng Long', 'BR-TL', 'Trường Đại học Thăng Long, Hoàng Mai, Hà Nội', 20.975935, 105.815339, 100, 'Chi nhánh tại khu vực ĐH Thăng Long', 'Hoàng Diệu', '0923456789', TRUE, 1),
('Chi nhánh Thanh Xuân', 'BR-TX', 'Tầng 7, Tòa nhà Diamond Flower, 48 Lê Văn Lương, Thanh Xuân, Hà Nội', 21.003500, 105.806800, 120, 'Chi nhánh tại khu vực Thanh Xuân', 'Lê Văn C', '0934567890', TRUE, 1),
('Chi nhánh Tân Triều', 'BR-TT', 'Quán tóc 32 Cầu Bươu, Thanh Trì, Hà Nội', 20.959510, 105.803589, 100, 'Chi nhánh tại khu vực Tân Triều', 'Nguyễn Long Nhật', '0945678901', TRUE, 1),
('Làm việc từ xa', 'REMOTE', 'N/A', 0.000000, 0.000000, 0, 'Dành cho nhân viên được phép làm việc từ xa', NULL, NULL, TRUE, 1);

-- ================================================
-- SHIFTS (CA LÀM VIỆC)
-- ================================================
INSERT INTO shifts (name, code, start_time, end_time, late_threshold, early_leave_threshold, check_in_start_before, check_in_end_after, work_days, break_duration, description, color, is_active, created_by) VALUES
('Ca hành chính', 'OFFICE', '08:00:00', '17:00:00', 15, 15, 30, 120, '[1, 2, 3, 4, 5]', 60, 'Ca làm việc hành chính tiêu chuẩn: 8:00 - 17:00', '#4CAF50', TRUE, 1),
('Ca sáng', 'MORNING', '07:00:00', '15:00:00', 10, 10, 30, 90, '[1, 2, 3, 4, 5]', 60, 'Ca làm việc sáng: 7:00 - 15:00', '#2196F3', TRUE, 1),
('Ca chiều', 'AFTERNOON', '13:00:00', '21:00:00', 10, 10, 30, 90, '[1, 2, 3, 4, 5]', 30, 'Ca làm việc chiều: 13:00 - 21:00', '#FF9800', TRUE, 1),
('Ca tối', 'NIGHT', '21:00:00', '05:00:00', 10, 10, 30, 90, '[1, 2, 3, 4, 5]', 30, 'Ca làm việc tối: 21:00 - 05:00 (có phụ cấp ca)', '#F44336', TRUE, 1),
('Ca linh hoạt', 'FLEXIBLE', '08:00:00', '17:00:00', 60, 60, 120, 240, '[1, 2, 3, 4, 5, 6, 7]', 60, 'Ca làm việc linh hoạt cho các vị trí đặc biệt', '#9C27B0', TRUE, 1);

-- ================================================
-- ASSIGN DEFAULT SHIFT TO ALL EMPLOYEES
-- ================================================
UPDATE employees 
SET shift_id = (SELECT id FROM shifts WHERE code = 'OFFICE' LIMIT 1)
WHERE shift_id IS NULL;