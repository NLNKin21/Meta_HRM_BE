# MetaHRM — Employee Management System

> Hệ thống quản lý nhân sự toàn diện, được xây dựng trên nền tảng Spring Boot, hỗ trợ toàn bộ vòng đời nhân viên từ tuyển dụng đến bảng lương.

---

## Mục Lục

1. [Tổng Quan Dự Án](#1-tổng-quan-dự-án)
2. [Công Nghệ Sử Dụng](#2-công-nghệ-sử-dụng)
3. [Kiến Trúc Dự Án](#3-kiến-trúc-dự-án)
4. [Cơ Sở Dữ Liệu](#4-cơ-sở-dữ-liệu)
5. [Các Module Chức Năng](#5-các-module-chức-năng)
6. [API Endpoints](#6-api-endpoints)
7. [Cấu Hình & Tích Hợp](#7-cấu-hình--tích-hợp)
8. [Khởi Chạy Dự Án](#8-khởi-chạy-dự-án)
9. [Quan Hệ Giữa Các Module](#9-quan-hệ-giữa-các-module)

---

## 1. Tổng Quan Dự Án

| Thuộc tính | Giá trị |
|---|---|
| **Tên dự án** | MetaHRM — Employee Management System |
| **Group ID** | `com.metahrms` |
| **Artifact ID** | `employee-management` |
| **Phiên bản** | `0.0.1-SNAPSHOT` |
| **Ngôn ngữ** | Java 21 (LTS) |
| **Framework** | Spring Boot 3.5.11 |
| **Build Tool** | Gradle (Kotlin DSL — `build.gradle.kts`) |

### Mô tả

MetaHRM là hệ thống quản lý nhân sự cấp doanh nghiệp, được thiết kế để số hóa và tự động hóa toàn bộ các nghiệp vụ HR. Hệ thống cung cấp **9 module** hoạt động độc lập nhưng tích hợp chặt chẽ với nhau:

| Module | Mô tả ngắn |
|---|---|
| **Quản lý Nhân viên** | CRUD hồ sơ, phòng ban, chức vụ, hợp đồng |
| **Chấm Công** | Check-in/out bằng nhận diện khuôn mặt + GPS |
| **Nghỉ Phép** | Luồng phê duyệt 2 cấp (Quản lý → HR) |
| **Bảng Lương** | Tính lương tự động, phê duyệt đa bước |
| **Quản lý Công Việc** | Dự án, task, Kanban board, bình luận |
| **Tuyển Dụng** | Pipeline ứng viên, phỏng vấn, onboarding |
| **Hợp Đồng** | Quản lý vòng đời hợp đồng, lưu trữ file |
| **Thông Báo** | Thông báo HR, hết hạn hợp đồng, task, nghỉ phép |
| **Quản Trị** | Phân quyền RBAC, ca làm việc, địa điểm làm việc |

---

## 2. Công Nghệ Sử Dụng

### 2.1 Core Stack

| Công nghệ | Phiên bản | Mục đích |
|---|---|---|
| Java | 21 | Ngôn ngữ lập trình chính |
| Spring Boot | 3.5.11 | Framework ứng dụng |
| Spring Security | (Boot managed) | Bảo mật, phân quyền |
| Spring Data JPA | (Boot managed) | ORM, truy vấn database |
| Spring Web MVC | (Boot managed) | REST API |
| Spring WebFlux | (Boot managed) | WebClient cho gọi API ngoài |
| Spring AOP | (Boot managed) | Aspect-oriented programming |
| Spring Mail | (Boot managed) | Gửi email |
| Spring Actuator | (Boot managed) | Health check, metrics |
| Spring Validation | (Boot managed) | Validate dữ liệu đầu vào |
| Thymeleaf | (Boot managed) | Template engine (server-side) |
| Lombok | (Boot managed) | Giảm boilerplate code |

### 2.2 Bảo Mật

| Công nghệ | Mục đích |
|---|---|
| Spring Security OAuth2 Resource Server | Xác thực JWT |
| JJWT | Tạo và xác minh JWT token |
| BCrypt | Hash mật khẩu |
| RBAC | Phân quyền theo role (ADMIN, HR, MANAGER, EMPLOYEE) |

### 2.3 Database & Migration

| Công nghệ | Phiên bản | Mục đích |
|---|---|---|
| MySQL | 8.x | Hệ quản trị cơ sở dữ liệu |
| Hibernate / JPA | (Boot managed) | ORM framework |
| HikariCP | (Boot managed) | Connection pool |
| Flyway Core | (Boot managed) | Quản lý migration schema |
| Flyway MySQL | (Boot managed) | MySQL support cho Flyway |

### 2.4 Cloud & File Storage

| Công nghệ | Phiên bản | Mục đích |
|---|---|---|
| Cloudinary SDK | `1.36.0` | Lưu trữ ảnh, file trên cloud |
| Apache HttpClient 5 | (Boot managed) | HTTP client |
| Commons IO | `2.16.1` | Tiện ích xử lý file |
| imgscalr | `4.2` | Nén, resize ảnh |

### 2.5 Xử Lý Dữ Liệu & Báo Cáo

| Công nghệ | Phiên bản | Mục đích |
|---|---|---|
| Apache POI | `5.2.5` | Xuất file Excel (.xls) |
| Apache POI OOXML | `5.2.5` | Xuất file Excel (.xlsx) |

### 2.6 Độ Bền Vững & Resilience

| Công nghệ | Phiên bản | Mục đích |
|---|---|---|
| Resilience4j Spring Boot 2 | `2.1.0` | Circuit breaker cho Face Recognition API |

### 2.7 API Documentation

| Công nghệ | Phiên bản | Mục đích |
|---|---|---|
| SpringDoc OpenAPI | `2.8.0` | Swagger UI & OpenAPI 3.0 tự động |

### 2.8 Testing

| Công nghệ | Mục đích |
|---|---|
| Spring Boot Test | Unit & Integration testing |
| Spring Security Test | Test bảo mật |
| JUnit 5 | Test framework |

---

## 3. Kiến Trúc Dự Án

### 3.1 Kiến Trúc Phân Lớp (Layered Architecture)

```
┌─────────────────────────────────────────────────────────┐
│                    CLIENT (Frontend / Mobile)            │
└───────────────────────────┬─────────────────────────────┘
                            │ HTTP/HTTPS (JWT Bearer / Cookie)
┌───────────────────────────▼─────────────────────────────┐
│              SECURITY LAYER (Spring Security + JWT)      │
│     JwtAuthenticationFilter → CustomUserDetailsService  │
└───────────────────────────┬─────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────┐
│                   CONTROLLER LAYER                       │
│        REST Controllers — nhận request, trả response    │
│        Validation (@Valid), Role Guard (@PreAuthorize)  │
└───────────────────────────┬─────────────────────────────┘
                            │ DTO (Request/Response)
┌───────────────────────────▼─────────────────────────────┐
│                    SERVICE LAYER                         │
│        Business logic, tính toán, điều phối module      │
│        Interface + Impl pattern                         │
└───────────────┬───────────────────────┬─────────────────┘
                │                       │
┌───────────────▼──────┐   ┌────────────▼────────────────┐
│  REPOSITORY LAYER    │   │   EXTERNAL INTEGRATIONS      │
│  Spring Data JPA     │   │   - Cloudinary (file storage)│
│  JpaSpecification    │   │   - Face Recognition API     │
│  (dynamic queries)   │   │   - Gmail SMTP               │
└───────────────┬──────┘   └─────────────────────────────┘
                │
┌───────────────▼─────────────────────────────────────────┐
│                    ENTITY LAYER                          │
│        JPA Entities, Enums, BaseEntity (soft delete)    │
└───────────────┬─────────────────────────────────────────┘
                │
┌───────────────▼─────────────────────────────────────────┐
│                     MySQL 8.x                           │
│        Schema quản lý bởi Flyway (V1 → V18)            │
└─────────────────────────────────────────────────────────┘
```

### 3.2 Cấu Trúc Package

```
src/main/java/com/metahrms/employee_management/
│
├── config/                         # Cấu hình ứng dụng
│   ├── SecurityConfig.java         # JWT, CORS, phân quyền endpoint
│   ├── CloudinaryConfig.java       # Cloudinary SDK setup
│   ├── EmailConfig.java            # JavaMailSender setup
│   ├── SwaggerConfig.java          # OpenAPI / Swagger UI
│   ├── WebConfig.java              # CORS, WebMvcConfigurer
│   ├── JpaAuditingConfig.java      # Audit @CreatedDate/@UpdatedDate
│   └── RestTemplateConfig.java     # HTTP client config
│
├── controller/                     # REST API Controllers (46 controllers)
│   ├── AuthController.java
│   ├── EmployeeController.java
│   ├── UserController.java
│   ├── DepartmentController.java
│   ├── PositionController.java
│   ├── ContractController.java
│   ├── ContractTypeController.java
│   ├── AttendanceController.java
│   ├── MyAttendanceController.java
│   ├── PayrollController.java
│   ├── LeaveRequestController.java
│   ├── EmployeeFaceController.java
│   ├── WorkLocationController.java
│   ├── EmployeeDocumentController.java
│   ├── CalendarController.java
│   ├── HRNotificationController.java
│   ├── PermissionController.java
│   ├── HealthController.java
│   ├── Leave/                      # 7 controllers nghỉ phép
│   ├── task/                       # 6 controllers quản lý task
│   ├── CV/                         # 3 controllers tuyển dụng
│   └── admin/
│       ├── payroll/                # 6 controllers bảng lương admin
│       └── attendance/             # 4 controllers chấm công admin
│
├── service/                        # Business Logic (~75 services)
│   ├── [Core services...]
│   ├── Leave/
│   ├── payroll/
│   ├── task/
│   ├── CV/
│   └── impl/
│
├── repository/                     # JPA Repositories (43 repos)
│   ├── [Core repositories...]
│   ├── Attendance/
│   ├── Leave/  (chứa ShiftRepository)
│   ├── Payroll/
│   ├── Task/
│   └── CV/
│
├── entity/                         # JPA Entities (45 entities)
│   ├── BaseEntity.java             # Superclass: id, createdAt, updatedAt, isDeleted
│   ├── User.java
│   ├── Employee.java
│   ├── Department.java
│   ├── Position.java
│   ├── Contract.java
│   ├── ContractType.java
│   ├── EmployeeDocument.java
│   ├── RolePermission.java
│   ├── HRNotification.java
│   ├── ContractNotificationLog.java
│   ├── Attendance/                 # 6 entities chấm công
│   ├── Leave/                      # 7 entities nghỉ phép
│   ├── Payroll/                    # 7 entities bảng lương
│   ├── Task/                       # 8 entities quản lý task
│   └── CV/                         # 3 entities tuyển dụng
│
├── dto/
│   ├── request/                    # Request DTOs (theo module)
│   └── response/                   # Response DTOs + ApiResponse wrapper
│
├── enums/                          # 42+ enum classes
│   ├── UserRole, UserStatus
│   ├── EmployeeStatus, Gender, RoleInDepartment
│   ├── ContractStatus, DurationUnit
│   ├── Attendance/                 # AttendanceStatus, AnomalyType...
│   ├── Leave/                      # LeaveStatus, LeaveApprovalStage...
│   ├── Payroll/                    # PayrollStatus, AllowanceType...
│   └── Task/                       # TaskPriority, TaskType, ProjectStatus...
│
├── mapper/                         # Entity ↔ DTO mapping
│   ├── task/
│   ├── CV/
│   └── attenden/
│
├── specification/                  # JPA Specification (dynamic query)
│   ├── EmployeeSpecification.java
│   ├── UserSpecification.java
│   ├── PositionSpecification.java
│   └── EmployeeDocumentSpecification.java
│
├── exception/                      # Custom exceptions
│   ├── GlobalExceptionHandler.java
│   ├── BadRequestException.java
│   └── ResourceNotFoundException.java
│
├── scheduler/                      # @Scheduled jobs
│   └── [Scheduled jobs...]
│
├── util/
│   └── SecurityUtils.java
│
└── EmployeeManagementApplication.java  # Entry point
```

### 3.3 Luồng Xác Thực JWT

```
1. Client gửi POST /auth/login { email, password }
2. AuthController → AuthService.authenticate()
3. Tạo JWT token (expires: 24h) + Refresh token (expires: 7 ngày)
4. Set-Cookie: access_token=<JWT> (cho web)  OR  trả JSON Bearer token (cho mobile)

5. Mỗi request tiếp theo:
   Priority 1: Header "Authorization: Bearer <token>"
   Priority 2: Cookie "access_token"

6. JwtAuthenticationFilter trích xuất claims: { id, username, email, role }
7. SecurityContextHolder lưu UserDetails để dùng trong toàn request
```

---

## 4. Cơ Sở Dữ Liệu

### 4.1 Cấu Hình Kết Nối

| Tham số | Giá trị |
|---|---|
| **Driver** | `mysql-connector-j` |
| **URL (default)** | `jdbc:mysql://127.0.0.1:3306/metahrm` |
| **URL (dev)** | `jdbc:mysql://localhost:3306/employee_management_dev` |
| **Timezone** | `Asia/Ho_Chi_Minh` |
| **DDL Auto** | `validate` (production) / `update` (dev) |

**HikariCP Connection Pool:**

| Tham số | Giá trị |
|---|---|
| Maximum Pool Size | 10 |
| Minimum Idle | 5 |
| Idle Timeout | 30,000 ms |
| Connection Timeout | 20,000 ms |
| Max Lifetime | 1,800,000 ms (30 phút) |

### 4.2 Database Migration (Flyway)

Toàn bộ schema được quản lý bởi **18 file migration** tại `src/main/resources/db/migration/`:

| Version | Nội dung |
|---|---|
| V1 | Tạo bảng `users` |
| V2 | Tạo bảng `departments` |
| V3 | Tạo bảng `positions` (có cấu trúc cây) |
| V4 | Tạo bảng `employees` (FK: users, departments, positions, shifts) |
| V5 | Tạo bảng `contracts` |
| V6 | Tạo bảng `employee_documents` |
| V7 | Seed dữ liệu khởi tạo |
| V8 | Tạo các bảng quản lý task (tasks, projects, task_statuses, task_history, v.v.) |
| V9 | Seed dữ liệu task |
| V10 | Tạo các bảng nghỉ phép (leave_types, leave_requests, leave_balances, v.v.) |
| V11 | Seed dữ liệu nghỉ phép |
| V12 | Tạo bảng nhận diện khuôn mặt (`employee_faces`) |
| V13 | Seed dữ liệu chấm công |
| V14 | Thêm `attendance_audit_logs`, cập nhật cấu trúc bảng chấm công |
| V15 | Seed dữ liệu chấm công đầy đủ |
| V16 | Tạo các bảng bảng lương (payroll_config, allowances, bonuses, deductions, payslips, v.v.) |
| V17 | Tạo bảng `contract_types` |
| V18 | Reset tiêu chuẩn nghỉ phép theo quy định Việt Nam |

### 4.3 Bảng Chính và Các Trường Quan Trọng

#### BaseEntity (lớp cha trừu tượng)
```
id           INT PRIMARY KEY AUTO_INCREMENT
created_at   DATETIME (auto)
updated_at   DATETIME (auto)
is_deleted   BOOLEAN DEFAULT false    ← Soft delete
```

#### users
```
id, username, password (BCrypt), email, role (ORDINAL), status (ORDINAL), created_at, updated_at, is_deleted
```

#### employees
```
id, user_id (FK), dept_id (FK), position_id (FK), shift_id (FK),
full_name (150), gender, dob, phone_number (20), address (255),
profile_pic_image (500 — Cloudinary URL), hire_date,
basic_salary (12,2 decimal), status, role_in_dept,
created_at, updated_at, is_deleted
```

#### departments
```
id, dept_name (100), created_at, updated_at, is_deleted
```

#### positions
```
id, position_code (UNIQUE, 20), position_name (100), description (TEXT),
min_salary, max_salary, parent_position_id (FK — self-referencing),
level_order, sort_order, department_id (FK), is_active,
created_at, updated_at, is_deleted
```

#### contracts
```
id, emp_id (FK), contract_type_id (FK), start_date, end_date,
file_url (500 — Cloudinary), file_key (255 — Cloudinary public_id),
preview_url, file_format, previewable, status,
created_at, updated_at, is_deleted
```

#### attendance_records
```
id, employee_id (FK), date, shift_id (FK),
check_in_time, check_out_time,
check_in_location_id (FK), check_out_location_id (FK),
check_in_lat, check_in_lng, check_out_lat, check_out_lng,
check_in_photo_url (Cloudinary), check_out_photo_url (Cloudinary),
check_in_face_match_score, check_out_face_match_score,
status, work_hours, overtime_hours, late_minutes,
created_at, updated_at
```

#### leave_requests
```
id, employee_id (FK), manager_id (FK), hr_id (FK), leave_type_id (FK),
start_date, end_date, total_days,
status (DRAFT→SUBMITTED→APPROVED/REJECTED),
approval_stage (NONE|WAITING_MANAGER|WAITING_HR|COMPLETED),
created_at, updated_at, is_deleted
```

#### payslips
```
id, employee_id (FK), month, year,
status (DRAFT→CALCULATED→APPROVED→PAID|REJECTED),
standard_work_days, actual_work_days,
basic_salary, allowances, overtime_pay, bonus,
insurance, tax, net_salary,
created_at, updated_at
```

#### tasks
```
id, task_code (UNIQUE), title, description,
task_type (TASK|BUG|FEATURE|IMPROVEMENT),
priority (LOW|MEDIUM|HIGH|URGENT),
status_id (FK → task_statuses),
reporter_id (FK → users), assignee_id (FK → users), approver_id (FK → users),
department_id (FK), project_id (FK),
estimated_hours, actual_hours, due_date, completion_rate,
created_at, updated_at, is_deleted
```

#### shifts
```
id, name, code,
start_time, end_time,
late_threshold (phút), early_leave_threshold (phút),
check_in_start_before, check_in_end_after,
work_days (JSON — danh sách ngày làm việc), break_duration (phút),
created_at, updated_at, is_deleted
```

### 4.4 Roles & Permissions

| Role | Quyền |
|---|---|
| `ADMIN` | Toàn quyền hệ thống |
| `HR` | Quản lý nhân sự, duyệt nghỉ phép cấp 2, xem báo cáo |
| `MANAGER` | Duyệt nghỉ phép cấp 1, xem chấm công phòng ban, quản lý task |
| `EMPLOYEE` (STAFF) | Xem thông tin cá nhân, chấm công, tạo yêu cầu nghỉ phép |

---

## 5. Các Module Chức Năng

### 5.1 Module Quản Lý Nhân Viên

**Chức năng:** CRUD hồ sơ nhân viên, phân công phòng ban/chức vụ, quản lý tài liệu.

- Tìm kiếm và lọc nhân viên theo trạng thái, phòng ban, ngày tuyển dụng, từ khóa
- Tạo nhân viên kèm hợp đồng và upload file trong một request
- Upload ảnh đại diện lên Cloudinary
- Phân cấp chức vụ dạng cây (Position hierarchy)
- Mỗi phòng ban chỉ có tối đa 1 trưởng phòng (`HEAD`)
- Soft delete — dữ liệu được giữ lại, không xóa vật lý

**Trạng thái nhân viên:** `ACTIVE` → `ON_LEAVE` → `INACTIVE`

### 5.2 Module Chấm Công

**Chức năng:** Check-in/out bằng nhận diện khuôn mặt kết hợp GPS, báo cáo chấm công.

**Luồng check-in:**
```
1. Nhân viên gửi ảnh selfie + tọa độ GPS
2. Ảnh được upload lên Cloudinary (nén trước)
3. Hệ thống gọi Face Recognition API (localhost:8000) — Circuit Breaker bảo vệ
4. So khớp với ảnh đã đăng ký (ngưỡng 80%)
5. Xác minh vị trí GPS (trong bán kính cho phép)
6. Lưu AttendanceRecord với điểm số, ảnh, tọa độ
7. Tính toán: giờ làm việc, phút đi trễ, tăng ca
```

**Trạng thái chấm công:** `PRESENT` | `LATE` | `EARLY_LEAVE` | `ABSENT` | `LEAVE` | `NOT_CHECKED`

**Admin:** Duyệt/từ chối/sửa bản ghi chấm công, toàn bộ thay đổi được lưu vào `AttendanceAuditLog`.

**Quản lý ca làm việc:** Cấu hình giờ bắt đầu/kết thúc, ngưỡng trễ, ngày làm việc trong tuần.

### 5.3 Module Nghỉ Phép

**Chức năng:** Quản lý yêu cầu nghỉ phép với luồng phê duyệt 2 cấp.

**Luồng phê duyệt:**
```
DRAFT → SUBMITTED → [Quản lý duyệt] → [HR duyệt] → APPROVED
                                      ↘ REJECTED (ở bất kỳ bước nào)
                  ↘ CANCELLED (nhân viên tự hủy khi còn DRAFT/SUBMITTED)
```

**Tính năng nổi bật:**
- Quy tắc cấp phép ngày nghỉ theo thâm niên (`LeaveTypeSeniorityRule`)
- Theo dõi số dư nghỉ phép: allocated, used, pending, carry-forward, encashed
- Tích hợp Payroll: nghỉ có phép/không lương ảnh hưởng đến bảng lương
- Tích hợp Attendance: khi nghỉ phép được duyệt, chấm công ngày đó tự động là `LEAVE`
- Đính kèm file (giấy tờ y tế, v.v.) upload Cloudinary
- Hiển thị lịch nghỉ phép toàn công ty

**Loại nghỉ phép:** Nghỉ phép năm (`ANNUAL_LEAVE`), Nghỉ ốm (`SICK_LEAVE`), v.v. (cấu hình được)

### 5.4 Module Bảng Lương

**Chức năng:** Tính lương tự động, quản lý phụ cấp/thưởng/khấu trừ, xuất báo cáo.

**Luồng xử lý bảng lương:**
```
1. GENERATE    → Tạo phiếu lương DRAFT cho tháng
2. CALCULATE   → Tính toán: lương cơ bản + phụ cấp + tăng ca + thưởng - khấu trừ - bảo hiểm - thuế
3. APPROVE     → Admin/HR phê duyệt từng phiếu hoặc duyệt hàng loạt
4. PAY         → Đánh dấu đã thanh toán
```

**Thành phần tính lương:**

| Loại | Ví dụ |
|---|---|
| Phụ cấp (Allowance) | Ăn trưa, đi lại, điện thoại, nhà ở, chức vụ |
| Thưởng (Bonus) | KPI, dự án, lễ tết, tháng 13, hiệu suất |
| Khấu trừ (Deduction) | Phạt đi trễ, phạt, vay, hư hỏng tài sản |
| Bảo hiểm | BHXH, BHYT, BHTN (theo cấu hình) |
| Thuế | Thuế TNCN (theo thông tin thuế nhân viên) |

**Xuất file:** Excel, định dạng chuyển khoản Techcombank.

**Trạng thái phiếu lương:** `DRAFT` → `CALCULATED` → `APPROVED` → `PAID` | `REJECTED`

### 5.5 Module Quản Lý Công Việc (Task Management)

**Chức năng:** Quản lý dự án, task, Kanban board, bình luận, thông báo.

**Cấu trúc dữ liệu:**
```
Project (Dự án)
  └── Task (Công việc)
        ├── TaskStatus (Trạng thái — cấu hình được)
        ├── TaskComment (Bình luận)
        ├── TaskHistory (Lịch sử thay đổi)
        └── TaskReminder (Nhắc nhở)
```

**Phân loại task:**
- **Loại:** `TASK` | `BUG` | `FEATURE` | `IMPROVEMENT` | `DOCUMENTATION` | `OTHER`
- **Ưu tiên:** `LOW` | `MEDIUM` | `HIGH` | `URGENT`
- **Trạng thái dự án:** `PLANNING` → `ACTIVE` → `ON_HOLD` | `COMPLETED` → `ARCHIVED`

**Tính năng:** Kanban board theo phòng ban, lọc task theo người thực hiện/báo cáo/phòng ban/dự án, cảnh báo quá hạn và sắp đến hạn, theo dõi tỷ lệ hoàn thành.

### 5.6 Module Tuyển Dụng (CV/Recruitment)

**Chức năng:** Quản lý pipeline tuyển dụng từ đơn ứng tuyển đến onboarding.

**Pipeline ứng viên:**
```
NEW → SHORTLISTED → INTERVIEWED → APPROVED → ONBOARDED
                              ↘ REJECTED (ở bất kỳ bước nào)
```

**Tính năng:**
- Đăng tin tuyển dụng công khai (`/public/recruitment/jobs`)
- Nhận đơn ứng tuyển kèm CV
- Lên lịch phỏng vấn (PHONE | VIDEO | IN_PERSON | GROUP)
- Ghi nhận kết quả phỏng vấn
- Onboarding: tự động tạo tài khoản hệ thống cho nhân viên mới
- Thống kê tuyển dụng (dashboard)

### 5.7 Module Hợp Đồng

**Chức năng:** Quản lý vòng đời hợp đồng lao động, lưu trữ file.

- Upload file hợp đồng (PDF/DOCX/ảnh) lên Cloudinary
- Theo dõi trạng thái: `ACTIVE` | `EXPIRED` | `TERMINATED` | `DRAFT` | `PENDING_APPROVAL`
- Lịch sử hợp đồng theo nhân viên
- Cảnh báo hết hạn hợp đồng qua `ContractNotificationLog`
- Quản lý loại hợp đồng (`ContractType`) — cấu hình được

### 5.8 Module Thông Báo

**Chức năng:** Thông báo nội bộ cho các sự kiện hệ thống.

| Loại | Sự kiện |
|---|---|
| **HR Notification** | Hợp đồng sắp hết hạn, sự kiện nhân sự |
| **Task Notification** | Được giao task, task thay đổi trạng thái, nhắc nhở deadline |
| **Leave Notification** | Yêu cầu nghỉ phép gửi/duyệt/từ chối |
| **Contract Notification** | Cảnh báo hợp đồng sắp hết hạn |

### 5.9 Module Quản Trị (Admin)

- **Phân quyền:** RBAC với `RolePermission`, phân quyền theo endpoint
- **Ca làm việc:** Cấu hình giờ vào/ra, ngưỡng trễ, ngày làm việc
- **Địa điểm làm việc:** GPS location với bán kính cho phép check-in
- **Lịch công ty:** Ngày lễ, ngày nghỉ bù (`Holiday`)
- **Nhận diện khuôn mặt:** Đăng ký/xóa khuôn mặt nhân viên

---

## 6. API Endpoints

**Base URL:** `http://localhost:8080/api`

**Swagger UI:** `http://localhost:8080/api/swagger-ui.html`

### 6.1 Authentication
| Method | Endpoint | Mô tả |
|---|---|---|
| POST | `/auth/login` | Đăng nhập, nhận JWT token |
| POST | `/auth/logout` | Đăng xuất |
| POST | `/auth/refresh` | Làm mới token |
| POST | `/auth/forgot-password` | Yêu cầu reset mật khẩu |

### 6.2 Employee Management
| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/employees` | Danh sách nhân viên (phân trang, lọc) |
| POST | `/employees` | Tạo nhân viên mới |
| POST | `/employees/with-contract` | Tạo nhân viên kèm hợp đồng |
| GET | `/employees/{id}` | Chi tiết nhân viên |
| PUT | `/employees/{id}` | Cập nhật nhân viên |
| DELETE | `/employees/{id}` | Xóa nhân viên (soft delete) |
| GET | `/employees/me` | Thông tin nhân viên hiện tại |
| POST | `/employees/{id}/avatar` | Upload ảnh đại diện |

### 6.3 Attendance
| Method | Endpoint | Mô tả |
|---|---|---|
| POST | `/attendance/check-in` | Check-in (khuôn mặt + GPS) |
| POST | `/attendance/check-out` | Check-out |
| GET | `/attendance/today` | Bản ghi hôm nay |
| GET | `/attendance/history` | Lịch sử chấm công |
| GET | `/admin/attendance/department/{deptId}/daily` | Báo cáo ngày theo phòng ban |
| GET | `/admin/attendance/department/{deptId}/monthly` | Báo cáo tháng theo phòng ban |
| PUT | `/admin/attendance/{id}/approve` | Duyệt bản ghi chấm công |
| PUT | `/admin/attendance/{id}/edit` | Sửa bản ghi chấm công |

### 6.4 Leave Management
| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/leaves/my` | Danh sách nghỉ phép của tôi |
| POST | `/leaves` | Tạo yêu cầu nghỉ phép (DRAFT) |
| POST | `/leaves/{id}/submit` | Nộp yêu cầu |
| POST | `/leaves/{id}/cancel` | Hủy yêu cầu |
| PUT | `/leave-approvals/{id}/approve` | Duyệt (Manager/HR) |
| PUT | `/leave-approvals/{id}/reject` | Từ chối |
| GET | `/leave-balances/{employeeId}` | Số dư ngày nghỉ |
| GET | `/leave-calendar` | Lịch nghỉ phép |

### 6.5 Payroll
| Method | Endpoint | Mô tả |
|---|---|---|
| POST | `/admin/payroll/generate` | Tạo phiếu lương tháng |
| POST | `/admin/payroll/calculate` | Tính lương toàn bộ |
| PUT | `/admin/payroll/approve-all` | Duyệt hàng loạt |
| PUT | `/admin/payroll/pay-all` | Đánh dấu đã thanh toán |
| GET | `/admin/payroll/export/excel` | Xuất Excel |
| GET | `/admin/payroll/export/techcombank` | Xuất file chuyển khoản |
| GET | `/payroll/me/payslips` | Phiếu lương của tôi |

### 6.6 Task Management
| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/tasks` | Danh sách task (phân trang) |
| GET | `/tasks/my-tasks` | Task của tôi |
| POST | `/tasks` | Tạo task mới |
| PUT | `/tasks/{id}/status` | Cập nhật trạng thái |
| GET | `/tasks/overdue` | Task quá hạn |
| GET | `/tasks/board/department/{deptId}` | Kanban board |
| GET | `/projects` | Danh sách dự án |
| POST | `/task-comments` | Thêm bình luận |

### 6.7 Recruitment
| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/public/recruitment/jobs` | Tin tuyển dụng (công khai) |
| POST | `/public/recruitment/apply` | Nộp đơn ứng tuyển |
| GET | `/recruitment/candidates` | Danh sách ứng viên |
| PUT | `/recruitment/candidates/{id}/approve` | Phê duyệt ứng viên |
| POST | `/recruitment/candidates/{id}/onboard` | Onboarding nhân viên mới |
| POST | `/interviews` | Lên lịch phỏng vấn |

### 6.8 Contracts & Departments
| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/contracts` | Danh sách hợp đồng |
| POST | `/contracts` | Tạo hợp đồng + upload file |
| GET | `/contracts/employee/{empId}/history` | Lịch sử hợp đồng nhân viên |
| GET | `/departments` | Danh sách phòng ban |
| GET | `/departments/{id}/members` | Thành viên phòng ban |
| GET | `/departments/{id}/workload` | Khối lượng công việc phòng ban |

---

## 7. Cấu Hình & Tích Hợp

### 7.1 JWT Configuration

```yaml
jwt:
  signer-key: <256-bit secret key>
  expiration: 86400000       # 24 giờ
  refresh-expiration: 604800000  # 7 ngày
```

Token chứa claims: `id`, `username`, `email`, `role`

### 7.2 CORS Configuration

| Tham số | Giá trị |
|---|---|
| Allowed Origins | `*` (development) |
| Allowed Methods | GET, POST, PUT, DELETE, OPTIONS, PATCH |
| Allowed Headers | `*` |
| Credentials | Allowed |
| Max Age | 3,600 seconds |

### 7.3 Email (SMTP Gmail)

```yaml
spring.mail:
  host: smtp.gmail.com
  port: 587
  protocol: smtp
  from: nlnhttl21@gmail.com
```

Sử dụng App Password của Gmail, STARTTLS bắt buộc.

### 7.4 Cloudinary

```yaml
cloudinary:
  cloud-name: dyjfpbj5e
  folder: MetaHRM
  max-file-size: 10485760  # 10 MB
```

Sử dụng cho: ảnh đại diện, ảnh check-in/out, file hợp đồng, tài liệu nhân viên, đính kèm nghỉ phép, CV ứng viên.

### 7.5 Face Recognition Service

```yaml
face-recognition:
  base-url: http://localhost:8000
  connection-timeout: 5s
  read-timeout: 30s
  verification-threshold: 80.0   # % khớp tối thiểu
  min-image-quality: 0.7
  circuit-breaker: enabled       # Resilience4j
```

Circuit Breaker: ngắt nếu tỷ lệ lỗi vượt 50%, chờ 10 giây trước khi thử lại.

### 7.6 Server & Actuator

```yaml
server:
  port: 8080
  servlet.context-path: /api

management.endpoints.web.exposure.include: health, info, metrics
```

### 7.7 File Upload Limits

```yaml
spring.servlet.multipart:
  max-file-size: 10MB
  max-request-size: 10MB
```

---

## 8. Khởi Chạy Dự Án

### 8.1 Yêu Cầu Môi Trường

| Yêu cầu | Phiên bản tối thiểu |
|---|---|
| Java JDK | 21 |
| MySQL | 8.0+ |
| Gradle | 8.x (hoặc dùng `./gradlew`) |
| (Tùy chọn) Face Recognition Service | Chạy tại `localhost:8000` |

### 8.2 Cấu Hình Database

Tạo database MySQL (Flyway sẽ tự tạo nếu dùng `createDatabaseIfNotExist=true`):

```sql
CREATE DATABASE metahrm CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Cập nhật thông tin kết nối trong `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/metahrm?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
    username: root
    password: your_password
```

### 8.3 Cấu Hình Biến Môi Trường

Nên đặt các giá trị nhạy cảm qua biến môi trường hoặc file `.env`:

```
DB_USERNAME=root
DB_PASSWORD=your_db_password
CLOUDINARY_API_SECRET=your_cloudinary_secret
MAIL_PASSWORD=your_gmail_app_password
JWT_SIGNER_KEY=your_256bit_secret_key
```

### 8.4 Chạy Ứng Dụng

```bash
# Sử dụng Gradle Wrapper
./gradlew bootRun

# Hoặc build JAR và chạy
./gradlew build
java -jar build/libs/employee-management-0.0.1-SNAPSHOT.jar

# Chạy với profile dev
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### 8.5 Kiểm Tra Sau Khi Chạy

| URL | Mô tả |
|---|---|
| `http://localhost:8080/api/health` | Health check endpoint |
| `http://localhost:8080/api/swagger-ui.html` | Swagger UI — toàn bộ API docs |
| `http://localhost:8080/api/v3/api-docs` | OpenAPI JSON spec |
| `http://localhost:8080/api/actuator/health` | Spring Actuator health |

### 8.6 Tài Khoản Mặc Định (Seed Data)

Sau khi chạy Flyway migration (V7), hệ thống sẽ có dữ liệu mẫu ban đầu. Xem file `V7__seed_initial_data.sql` để biết thông tin tài khoản admin mặc định.

---

## 9. Quan Hệ Giữa Các Module

```
┌──────────────────────────────────────────────────────────────┐
│                        EMPLOYEE (Hub)                        │
│  Mọi module đều liên kết với Employee thông qua employee_id  │
└──────┬──────────┬──────────┬──────────┬──────────┬───────────┘
       │          │          │          │          │
   ATTENDANCE   LEAVE     PAYROLL    TASK      CONTRACT
       │          │          │
       └──────────┴──────────┘
           Tích hợp 3 chiều:
           - Leave duyệt → Attendance tự đánh LEAVE
           - Leave duyệt → Payroll khấu trừ ngày không lương
           - Attendance → Payroll tính ngày công thực tế

RECRUITMENT ──onboard──→ EMPLOYEE + USER (tạo tài khoản tự động)

DEPARTMENT ──filter──→ tất cả module (lọc dữ liệu theo phòng ban)

NOTIFICATION ←── tất cả module (sự kiện kích hoạt thông báo)
```

### Ma Trận Tích Hợp

| Từ Module | Đến Module | Tích hợp |
|---|---|---|
| Leave (duyệt) | Attendance | Đánh dấu ngày nghỉ là `LEAVE` |
| Leave (duyệt) | Payroll | Tính toán khấu trừ lương ngày nghỉ không lương |
| Attendance | Payroll | Số ngày công thực tế → tính lương |
| Recruitment (onboard) | Employee | Tạo hồ sơ nhân viên mới |
| Recruitment (onboard) | User | Tạo tài khoản đăng nhập |
| Employee | Contract | Lịch sử hợp đồng theo nhân viên |
| Contract (hết hạn) | Notification | Gửi cảnh báo HR |
| Task (thay đổi) | Notification | Thông báo cho người được giao |
| Department | Tất cả | Lọc dữ liệu theo phòng ban |

---

## Phụ Lục

### Danh Sách Enums Quan Trọng

| Enum | Giá trị |
|---|---|
| `UserRole` | ADMIN, HR, MANAGER, EMPLOYEE |
| `EmployeeStatus` | ACTIVE, ON_LEAVE, PENDING, INACTIVE |
| `RoleInDepartment` | STAFF, TEAM_LEAD, HEAD |
| `ContractStatus` | ACTIVE, EXPIRED, TERMINATED, DRAFT, PENDING_APPROVAL |
| `AttendanceStatus` | PRESENT, LATE, EARLY_LEAVE, ABSENT, LEAVE, NOT_CHECKED |
| `LeaveStatus` | DRAFT, SUBMITTED, APPROVED, REJECTED, CANCELLED |
| `LeaveApprovalStage` | NONE, WAITING_MANAGER, WAITING_HR, COMPLETED |
| `PayslipStatus` | DRAFT, CALCULATED, APPROVED, REJECTED, PAID |
| `TaskPriority` | LOW, MEDIUM, HIGH, URGENT |
| `TaskType` | TASK, BUG, FEATURE, IMPROVEMENT, DOCUMENTATION, OTHER |
| `ProjectStatus` | PLANNING, ACTIVE, ON_HOLD, COMPLETED, ARCHIVED |
| `CandidateStatus` | NEW, SHORTLISTED, INTERVIEWED, APPROVED, ONBOARDED, REJECTED |
| `InterviewType` | PHONE, VIDEO, IN_PERSON, GROUP |
| `AllowanceType` | MEAL, TRANSPORT, PHONE, HOUSING, POSITION, TOXIC, ATTENDANCE, OTHER |
| `BonusType` | KPI, PROJECT, HOLIDAY, MONTH_13, PERFORMANCE, OTHER |
| `DeductionType` | PENALTY, LATE_PENALTY, LOAN, DAMAGE, OTHER |

### Cấu Trúc Response Chuẩn

Mọi API đều trả về theo chuẩn `ApiResponse<T>`:

```json
{
  "success": true,
  "message": "Thao tác thành công",
  "data": { ... },
  "timestamp": "2026-05-23T10:00:00"
}
```

Danh sách phân trang sử dụng `PageResponse<T>`:

```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 150,
  "totalPages": 8,
  "last": false
}
```
