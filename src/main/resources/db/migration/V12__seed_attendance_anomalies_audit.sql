-- ============================================================
-- V12: SEED ATTENDANCE ANOMALIES & AUDIT LOGS
-- Map attendance_id cũ → mới theo V11:
--   old 18 (emp6, 04-01 LATE)     → new 6
--   old 27 (emp24, 04-01 EL)      → new 23
--   old 33 (emp30, 04-01 LATE)    → new 29
--   old 63 (emp61, 04-01 LATE)    → new 59
--   old 225 (emp37, 04-06 LATE)   → new 137
--   old 305 (emp55, 04-07 GPS)    → new 147
--   old 325 (emp10, 04-08)        → new 151
--   old 329 (emp14, 04-08)        → new 152
--   old 388 (emp10, 04-09 OT)     → new 157
--   old 506 (emp14, 04-12 lo face)→ new 173
--   old 508 (emp15, 04-12 lo face)→ new 175
--   old 509 (emp17, 04-14 lo face)→ new 176
--   old 512 (emp18, 05-06 lo face)→ new 179
--   old 513 (emp14, 05-12 lo face)→ new 180
-- ============================================================

-- ============================================================
-- ATTENDANCE ANOMALIES
-- ============================================================
INSERT INTO attendance_anomalies
    (id, attendance_id, anomaly_type, description, severity,
     metadata, resolved, resolved_by, resolved_at, resolution_note,
     created_at, updated_at)
VALUES
-- emp 6 (Lê Thị Thảo) - check-in trễ 20 phút 04-01
(1, 6, 'TIME_VIOLATION', 'Check-in trễ 20 phút so với ca hành chính', 'LOW',
 NULL, TRUE, 5, '2026-04-01 09:00:00', 'Nhân viên giải trình: kẹt xe, chấp nhận',
 '2026-04-01 08:20:00', '2026-04-01 09:00:00'),

-- emp 14 (Lê Hoàng Phong) - check-in trễ 35 phút 04-01
(2, 14, 'TIME_VIOLATION', 'Check-in trễ 35 phút so với ca', 'MEDIUM',
 NULL, TRUE, 5, '2026-04-01 10:00:00', 'Đã xác nhận lý do: kẹt xe',
 '2026-04-01 09:35:00', '2026-04-01 10:00:00'),

-- emp 61 (Phạm Văn Lâm) - check-in trễ 40 phút 04-01
(3, 59, 'TIME_VIOLATION', 'Check-in trễ 40 phút so với ca', 'HIGH',
 NULL, FALSE, NULL, NULL, NULL,
 '2026-04-01 09:40:00', '2026-04-01 09:40:00'),

-- emp 24 (Lê Văn Đạt) - checkout sớm 60 phút 04-01 (intern)
(4, 23, 'TIME_VIOLATION', 'Check-out sớm 60 phút (intern có lịch học buổi chiều)', 'MEDIUM',
 NULL, TRUE, 5, '2026-04-02 09:00:00', 'Intern được phép về sớm do lịch học tại trường',
 '2026-04-01 17:00:00', '2026-04-02 09:00:00'),

-- emp 30 (Vũ Thị Hạnh) - check-in trễ 25 phút 04-01
(5, 29, 'TIME_VIOLATION', 'Check-in trễ 25 phút', 'LOW',
 NULL, TRUE, 5, '2026-04-01 09:00:00', 'Nhân viên giải trình: xe hỏng dọc đường',
 '2026-04-01 08:25:00', '2026-04-01 09:00:00'),

-- emp 37 (Hoàng Văn Duy) - face match thấp 04-06
(6, 137, 'FACE_MISMATCH', 'Face match score thấp khi check-in: 89.5% (ngưỡng tối thiểu 90%)', 'HIGH',
 '{"score": 89.5, "threshold": 90.0, "device": "android"}',
 FALSE, NULL, NULL, NULL,
 '2026-04-06 08:54:00', '2026-04-06 08:54:00'),

-- emp 55 (Phạm Văn Đào) - GPS ngoài geofence 04-07
(7, 147, 'LOCATION_MISMATCH', 'Check-in ngoài bán kính geofence cho phép: cách HQ ~250m', 'HIGH',
 '{"distance_meters": 251, "allowed_radius": 100, "check_in_lat": 10.78150, "check_in_lng": 106.67500}',
 FALSE, NULL, NULL, NULL,
 '2026-04-07 07:20:00', '2026-04-07 07:20:00'),

-- emp 15 (Nguyễn Đức Khánh) - low face score check-in 04-12
(8, 175, 'SUSPICIOUS', 'Face match confidence thấp khi check-in: 83.37% (ngưỡng 90%)', 'MEDIUM',
 '{"score": 83.37, "threshold": 90.0, "platform": "ios"}',
 FALSE, NULL, NULL, NULL,
 '2026-04-12 23:27:41', '2026-04-12 23:27:41'),

-- emp 15 - low face score check-out 04-12
(9, 175, 'SUSPICIOUS', 'Face match confidence thấp khi check-out: 84.28%', 'MEDIUM',
 '{"score": 84.28, "threshold": 90.0, "platform": "ios"}',
 FALSE, NULL, NULL, NULL,
 '2026-04-12 23:30:11', '2026-04-12 23:30:11'),

-- emp 14 - low face score check-out 04-12 (overtime late)
(10, 173, 'SUSPICIOUS', 'Face match confidence thấp khi check-out: 84.32%', 'MEDIUM',
 '{"score": 84.32, "threshold": 90.0, "platform": "ios"}',
 FALSE, NULL, NULL, NULL,
 '2026-04-12 23:55:44', '2026-04-12 23:55:44'),

-- emp 17 - low face score check-in 04-14
(11, 176, 'SUSPICIOUS', 'Face match confidence thấp khi check-in: 89.21%', 'MEDIUM',
 '{"score": 89.21, "threshold": 90.0, "platform": "android"}',
 FALSE, NULL, NULL, NULL,
 '2026-04-14 14:10:55', '2026-04-14 14:10:55'),

-- emp 18 - low face score check-in 05-06
(12, 179, 'SUSPICIOUS', 'Face match confidence thấp khi check-in: 88.38%', 'MEDIUM',
 '{"score": 88.38, "threshold": 90.0, "platform": "android"}',
 FALSE, NULL, NULL, NULL,
 '2026-05-06 15:41:11', '2026-05-06 15:41:11'),

-- emp 14 - low face score check-out 05-12
(13, 180, 'SUSPICIOUS', 'Face match confidence thấp khi check-out: 89.41%', 'MEDIUM',
 '{"score": 89.41, "threshold": 90.0, "platform": "android"}',
 FALSE, NULL, NULL, NULL,
 '2026-05-12 14:06:43', '2026-05-12 14:06:43');

-- ============================================================
-- ATTENDANCE AUDIT LOGS
-- performed_by = user_id 5 (hr_director_mai → emp 4 → user 5)
-- Note: audit logs tham chiếu attendance_id mới
-- ============================================================
INSERT INTO attendance_audit_logs
    (id, attendance_id, action, old_value, new_value, reason,
     performed_by, performed_by_name,
     created_at, updated_at)
VALUES
-- 1. Duyệt attendance emp10 (04-01) - id=10
(1, 10, 'APPROVE',
 '{"isApproved":null}',
 '{"isApproved":true}',
 NULL, 5, 'Phạm Thị Mai',
 '2026-04-01 17:30:00', '2026-04-01 17:30:00'),

-- 2. Edit attendance emp24 intern (04-02) - id=82
--    HR sửa checkout từ 17:00 → 18:00, đổi status EARLY_LEAVE → PRESENT
(2, 82, 'EDIT',
 '{"checkOutTime":"2026-04-02T17:00","status":"EARLY_LEAVE","earlyLeaveMinutes":60}',
 '{"checkOutTime":"2026-04-02T18:00","status":"PRESENT","earlyLeaveMinutes":0}',
 'Intern được phép về sớm do lịch học, đã bổ sung giờ ngày hôm sau',
 5, 'Phạm Thị Mai',
 '2026-04-02 09:00:00', '2026-04-02 09:00:00'),

-- 3. Edit attendance emp10 (04-09 OT) - id=157
--    Lần đầu edit (không thay đổi thực)
(3, 157, 'EDIT',
 '{"checkOutTime":"2026-04-09T18:09","earlyLeaveMinutes":0,"status":"PRESENT","lateMinutes":0,"checkInTime":"2026-04-09T08:51"}',
 '{"checkOutTime":"2026-04-09T18:09","earlyLeaveMinutes":0,"status":"PRESENT","lateMinutes":0,"checkInTime":"2026-04-09T08:51"}',
 'Kiểm tra lại dữ liệu',
 5, 'Phạm Thị Mai',
 '2026-05-16 12:59:31', '2026-05-16 12:59:31'),

-- 4. REJECT attendance emp10 (04-08) - id=151
(4, 151, 'REJECT',
 '{"isApproved":true,"approvalNote":""}',
 '{"isApproved":false,"reason":"ok"}',
 'Cần xem xét lại',
 5, 'Phạm Thị Mai',
 '2026-05-16 13:10:56', '2026-05-16 13:10:56'),

-- 5. APPROVE attendance emp10 (04-08) - id=151
(5, 151, 'APPROVE',
 '{"isApproved":false,"approvalNote":"Cần xem xét lại"}',
 '{"isApproved":true,"approvalNote":""}',
 NULL,
 5, 'Phạm Thị Mai',
 '2026-05-16 13:11:05', '2026-05-16 13:11:05'),

-- 6. EDIT attendance emp14 (04-08) - id=152 - sửa status LATE → PRESENT
(6, 152, 'EDIT',
 '{"checkOutTime":"2026-04-08T18:30","earlyLeaveMinutes":0,"status":"LATE","lateMinutes":35,"checkInTime":"2026-04-08T09:33"}',
 '{"checkOutTime":"2026-04-08T18:30","earlyLeaveMinutes":0,"status":"PRESENT","lateMinutes":0,"checkInTime":"2026-04-08T09:33"}',
 'HR đã xác nhận: nhân viên có lý do chính đáng (họp khách hàng buổi sáng)',
 5, 'Phạm Thị Mai',
 '2026-05-16 13:13:41', '2026-05-16 13:13:41'),

-- 7. EDIT lần 2 (confirm lại)
(7, 152, 'EDIT',
 '{"checkOutTime":"2026-04-08T18:30","earlyLeaveMinutes":0,"status":"PRESENT","lateMinutes":0,"checkInTime":"2026-04-08T09:33"}',
 '{"checkOutTime":"2026-04-08T18:30","earlyLeaveMinutes":0,"status":"PRESENT","lateMinutes":0,"checkInTime":"2026-04-08T09:33"}',
 'Xác nhận lần cuối',
 5, 'Phạm Thị Mai',
 '2026-05-16 13:13:55', '2026-05-16 13:13:55'),

-- 8. REJECT attendance emp14 (04-08) - id=152
(8, 152, 'REJECT',
 '{"isApproved":false,"approvalNote":""}',
 '{"isApproved":false,"reason":"Cần bổ sung email xác nhận từ khách hàng"}',
 'Cần bổ sung email xác nhận từ khách hàng',
 5, 'Phạm Thị Mai',
 '2026-05-16 13:14:08', '2026-05-16 13:14:08'),

-- 9. APPROVE attendance emp14 (04-08) - id=152
(9, 152, 'APPROVE',
 '{"isApproved":false,"approvalNote":"Cần bổ sung email xác nhận từ khách hàng"}',
 '{"isApproved":true,"approvalNote":""}',
 NULL,
 5, 'Phạm Thị Mai',
 '2026-05-16 13:14:11', '2026-05-16 13:14:11'),

-- 10. EDIT attendance emp10 (04-09 OT) - id=157 - cập nhật checkout 18:09 → 19:09
(10, 157, 'EDIT',
 '{"checkOutTime":"2026-04-09T18:09","checkInTime":"2026-04-09T08:51","lateMinutes":0,"status":"PRESENT","earlyLeaveMinutes":0}',
 '{"checkOutTime":"2026-04-09T19:09","checkInTime":"2026-04-09T08:51","lateMinutes":0,"status":"PRESENT","earlyLeaveMinutes":0}',
 'Cập nhật giờ OT thực tế: nhân viên làm thêm đến 19:09 theo yêu cầu dự án',
 5, 'Phạm Thị Mai',
 '2026-05-21 15:31:24', '2026-05-21 15:31:24'),

-- 11. REJECT attendance emp10 (04-09 OT) - id=157
(11, 157, 'REJECT',
 '{"approvalNote":"","isApproved":false}',
 '{"reason":"Cần trưởng phòng xác nhận OT","isApproved":false}',
 'Cần trưởng phòng xác nhận OT',
 5, 'Phạm Thị Mai',
 '2026-05-21 15:31:35', '2026-05-21 15:31:35'),

-- 12. APPROVE attendance emp10 (04-09 OT) - id=157
(12, 157, 'APPROVE',
 '{"approvalNote":"Cần trưởng phòng xác nhận OT","isApproved":false}',
 '{"approvalNote":"","isApproved":true}',
 NULL,
 5, 'Phạm Thị Mai',
 '2026-05-21 15:31:41', '2026-05-21 15:31:41');
