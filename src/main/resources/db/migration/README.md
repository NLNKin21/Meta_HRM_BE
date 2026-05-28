# MetaHRMS — Migration Guide

## Cấu trúc 12 files

| File | Nội dung |
|------|----------|
| V1__schema_core.sql | users, role_permissions, departments, shifts, work_locations |
| V2__schema_employee.sql | positions, employees, employee_documents, employee_faces, employee_tax_info, hr_notifications |
| V3__schema_contract.sql | contract_types, contracts, contract_notification_logs |
| V4__schema_task.sql | projects, task_statuses, task_status_transitions, tasks, task_comments, task_histories, task_reminders, notifications |
| V5__schema_payroll.sql | payroll_config, allowances, bonuses, deductions, payslips, payslip_details |
| V6__schema_leave_attendance.sql | leave_types, leave_type_seniority_rules, leave_balances, leave_requests, leave_approval_histories, leave_attachments, holidays, attendance_records, attendance_anomalies, attendance_audit_logs, candidates, interviews, recruitment_histories |
| V7__seed_core.sql | 64 users, 22 role_permissions, 8 departments, 2 shifts, 3 work_locations |
| V8__seed_positions_employees.sql | 48 positions, 63 employees |
| V9__seed_business_config.sql | 7 contract_types, 5 task_statuses, 8 transitions, 33 payroll_config, 7 leave_types, 4 seniority_rules, 39 holidays |
| V10__seed_tax_allowances_projects_leave.sql | 61 employee_tax_info, ~200 allowances, 10 projects, ~240 leave_balances |
| V11__seed_attendance_records.sql | 181 attendance_records (01/04–20/05/2026) |
| V12__seed_attendance_anomalies_audit.sql | 13 anomalies, 12 audit_logs |

---

## Các bước deploy

### Bước 1: Đảm bảo DB đã xóa sạch
```sql
DROP DATABASE IF EXISTS employee_management_dev;
CREATE DATABASE employee_management_dev
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

### Bước 2: Xóa 19 file cũ trong db/migration
Xóa toàn bộ file V1__create_users_table.sql ... V19__normalize_payroll_taxinfo_allowances.sql

### Bước 3: Copy 12 file mới vào db/migration
```
src/main/resources/db/migration/
  V1__schema_core.sql
  V2__schema_employee.sql
  V3__schema_contract.sql
  V4__schema_task.sql
  V5__schema_payroll.sql
  V6__schema_leave_attendance.sql
  V7__seed_core.sql
  V8__seed_positions_employees.sql
  V9__seed_business_config.sql
  V10__seed_tax_allowances_projects_leave.sql
  V11__seed_attendance_records.sql
  V12__seed_attendance_anomalies_audit.sql
```

### Bước 4: Sửa application-dev.yml
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate   # PHẢI đổi từ update → validate
```

### Bước 5: Chạy app
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Flyway sẽ tự tạo bảng flyway_schema_history và chạy V1→V12 tuần tự.

---

## Lưu ý quan trọng

### BaseEntity compliance
Các entity extends BaseEntity → bảng CÓ created_at, updated_at, is_deleted:
- users, departments, shifts, work_locations
- positions, employees, employee_documents, employee_faces, employee_tax_info
- contract_types, contracts, contract_notification_logs
- projects, task_statuses, task_status_transitions, tasks, task_comments, task_reminders
- payroll_config, allowances, bonuses, deductions, payslips, payslip_details
- attendance_records, attendance_anomalies, attendance_audit_logs
- candidates, interviews

Các entity KHÔNG extends BaseEntity → bảng KHÔNG có created_at/updated_at/is_deleted:
- role_permissions
- hr_notifications (dùng @PrePersist thủ công, chỉ có created_at)
- leave_types, leave_type_seniority_rules, leave_balances
- leave_requests (có created_at/updated_at thủ công qua @PrePersist)
- leave_approval_histories (chỉ có action_at qua @PrePersist)
- leave_attachments, holidays
- task_histories (chỉ có created_at)
- notifications (chỉ có created_at)
- recruitment_histories (chỉ có created_at)

### Password mặc định
Tất cả users: BCrypt của `Password@123` — chỉ dùng cho dev.

### Database name
Dev profile: `employee_management_dev`
Production: `metahrm`

### Kiểm tra sau khi chạy
```sql
SELECT version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;
-- Kỳ vọng: 12 dòng, tất cả success=1
```
