-- ============================================================
-- V6: LEAVE & ATTENDANCE SCHEMA
-- FIXED: Khớp chính xác với Java entities
-- Leave entities KHÔNG extends BaseEntity → không có created_at, updated_at, is_deleted
-- Attendance entities extends BaseEntity → có created_at, updated_at, is_deleted
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- LEAVE
-- ============================================================

-- leave_types: không extends BaseEntity
-- columns: id, code, name, maxDaysPerYear, defaultDaysPerYear,
--          paidLeave, requiresApproval, requiresDocument, active,
--          deductBalance, deductFromAnnualLeaveBalance, autoApprove,
--          allowCarryForward, allowEncashment, countsInAttendance,
--          countsInCompanyPayroll, deductSalary, socialInsurancePaid,
--          increaseBySeniority
CREATE TABLE leave_types (
    id                               BIGINT  AUTO_INCREMENT PRIMARY KEY,
    code                             VARCHAR(100) NOT NULL UNIQUE,
    name                             VARCHAR(150) NOT NULL,
    max_days_per_year                INT          NOT NULL,
    default_days_per_year            INT          NOT NULL,
    paid_leave                       BOOLEAN      NOT NULL DEFAULT TRUE,
    requires_approval                BOOLEAN      NOT NULL DEFAULT TRUE,
    requires_document                BOOLEAN      NOT NULL DEFAULT FALSE,
    active                           BOOLEAN      NOT NULL DEFAULT TRUE,
    deduct_balance                   BOOLEAN      NOT NULL DEFAULT TRUE,
    deduct_from_annual_leave_balance BOOLEAN      NOT NULL DEFAULT FALSE,
    auto_approve                     BOOLEAN      NOT NULL DEFAULT FALSE,
    allow_carry_forward              BOOLEAN      NOT NULL DEFAULT FALSE,
    allow_encashment                 BOOLEAN      NOT NULL DEFAULT FALSE,
    counts_in_attendance             BOOLEAN      NOT NULL DEFAULT TRUE,
    counts_in_company_payroll        BOOLEAN      NOT NULL DEFAULT TRUE,
    deduct_salary                    BOOLEAN      NOT NULL DEFAULT FALSE,
    social_insurance_paid            BOOLEAN      NOT NULL DEFAULT FALSE,
    increase_by_seniority            BOOLEAN      NOT NULL DEFAULT FALSE,
    INDEX idx_leave_types_code (code)
);

-- leave_type_seniority_rules: không extends BaseEntity
-- columns: id, leave_type_id, min_years, extra_days
CREATE TABLE leave_type_seniority_rules (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    leave_type_id BIGINT NOT NULL,
    min_years     INT    NOT NULL,
    extra_days    INT    NOT NULL,
    UNIQUE KEY uq_seniority (leave_type_id, min_years),
    FOREIGN KEY (leave_type_id) REFERENCES leave_types(id) ON DELETE CASCADE
);

-- leave_balances: không extends BaseEntity
-- columns: id, employee_id, leave_type_id, year,
--          allocated_days, used_days, pending_days, carry_forward_days, encashed_days
CREATE TABLE leave_balances (
    id                 BIGINT        AUTO_INCREMENT PRIMARY KEY,
    employee_id        INT           NOT NULL,
    leave_type_id      BIGINT        NOT NULL,
    year               INT           NOT NULL,
    allocated_days     DECIMAL(10,2) NOT NULL DEFAULT 0,
    used_days          DECIMAL(10,2) NOT NULL DEFAULT 0,
    pending_days       DECIMAL(10,2) NOT NULL DEFAULT 0,
    carry_forward_days DECIMAL(10,2) NOT NULL DEFAULT 0,
    encashed_days      DECIMAL(10,2) NOT NULL DEFAULT 0,
    UNIQUE KEY uq_leave_balance (employee_id, leave_type_id, year),
    INDEX idx_leave_balances_emp  (employee_id),
    INDEX idx_leave_balances_year (year),
    FOREIGN KEY (employee_id)   REFERENCES employees(id)   ON DELETE CASCADE,
    FOREIGN KEY (leave_type_id) REFERENCES leave_types(id) ON DELETE RESTRICT
);

-- leave_requests: không extends BaseEntity
-- Lưu ý entity dùng @Column(nullable=false) private LeaveUnit leaveUnit (không có name)
--   → Hibernate sẽ dùng snake_case: leave_unit
--   tương tự: startSession→start_session, endSession→end_session,
--              totalDays→total_days, rejectReason→reject_reason,
--              cancelReason→cancel_reason, finalApproved→final_approved
--              createdAt→created_at, updatedAt→updated_at, submittedAt→submitted_at
--              approvedAt→approved_at, cancelledAt→cancelled_at
--              approvalStage→approval_stage
CREATE TABLE leave_requests (
    id             BIGINT        AUTO_INCREMENT PRIMARY KEY,
    employee_id    INT           NOT NULL,
    employee_name  VARCHAR(255),
    manager_id     INT,
    hr_id          INT,
    leave_type_id  BIGINT        NOT NULL,
    start_date     DATE          NOT NULL,
    end_date       DATE          NOT NULL,
    leave_unit     VARCHAR(20)   NOT NULL COMMENT 'DAY | HALF_DAY',
    start_session  VARCHAR(20)   NOT NULL COMMENT 'MORNING | AFTERNOON | FULL',
    end_session    VARCHAR(20)   NOT NULL COMMENT 'MORNING | AFTERNOON | FULL',
    total_days     DECIMAL(10,2) NOT NULL,
    reason         VARCHAR(1000) NOT NULL,
    status         VARCHAR(30)   NOT NULL DEFAULT 'DRAFT',
    approval_stage VARCHAR(30)   NOT NULL DEFAULT 'NONE',
    reject_reason  TEXT,
    cancel_reason  TEXT,
    final_approved BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at     DATETIME      NOT NULL,
    updated_at     DATETIME,
    submitted_at   DATETIME,
    approved_at    DATETIME,
    cancelled_at   DATETIME,
    INDEX idx_leave_req_emp    (employee_id),
    INDEX idx_leave_req_status (status),
    FOREIGN KEY (employee_id)   REFERENCES employees(id)   ON DELETE RESTRICT,
    FOREIGN KEY (leave_type_id) REFERENCES leave_types(id) ON DELETE RESTRICT
);

-- leave_approval_histories: không extends BaseEntity
-- Lưu ý: field action map column "action" (không có @Column name)
--   → snake_case: action, actor_role, stage, note, action_at
--   actorId → actor_id (không có @Column name)
CREATE TABLE leave_approval_histories (
    id               BIGINT      AUTO_INCREMENT PRIMARY KEY,
    leave_request_id BIGINT      NOT NULL,
    actor_id         INT         NOT NULL,
    actor_role       VARCHAR(30) NOT NULL COMMENT 'MANAGER | HR',
    action           VARCHAR(30) NOT NULL COMMENT 'APPROVE | REJECT | FORWARD | CANCEL',
    stage            VARCHAR(30) NOT NULL,
    note             TEXT,
    action_at        DATETIME    NOT NULL,
    INDEX idx_leave_approval_req (leave_request_id),
    FOREIGN KEY (leave_request_id) REFERENCES leave_requests(id) ON DELETE CASCADE
);

-- leave_attachments: không extends BaseEntity
-- columns: id, leave_request_id, file_name, file_url, file_type, file_size
CREATE TABLE leave_attachments (
    id               BIGINT       AUTO_INCREMENT PRIMARY KEY,
    leave_request_id BIGINT       NOT NULL,
    file_name        VARCHAR(255) NOT NULL,
    file_url         VARCHAR(500) NOT NULL,
    file_type        VARCHAR(50)  NOT NULL,
    file_size        BIGINT       NOT NULL,
    INDEX idx_leave_attach_req (leave_request_id),
    FOREIGN KEY (leave_request_id) REFERENCES leave_requests(id) ON DELETE CASCADE
);

-- holidays: không extends BaseEntity
-- columns: id, holiday_date, name, type, active
CREATE TABLE holidays (
    id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
    holiday_date DATE         NOT NULL UNIQUE,
    name         VARCHAR(200) NOT NULL,
    type         VARCHAR(50)  NOT NULL DEFAULT 'NATIONAL',
    active       BOOLEAN      NOT NULL DEFAULT TRUE,
    INDEX idx_holidays_date (holiday_date),
    INDEX idx_holidays_type (type)
);

-- ============================================================
-- ATTENDANCE — extends BaseEntity → có created_at, updated_at, is_deleted
-- ============================================================

-- attendance_records: extends BaseEntity
CREATE TABLE attendance_records (
    id                         INT           AUTO_INCREMENT PRIMARY KEY,
    employee_id                INT           NOT NULL,
    date                       DATE          NOT NULL,
    shift_id                   INT,
    -- Check-in
    check_in_time              DATETIME,
    check_in_location_id       INT,
    check_in_lat               DECIMAL(10,8),
    check_in_lng               DECIMAL(11,8),
    check_in_photo_url         VARCHAR(500),
    check_in_face_match_score  DECIMAL(5,2),
    check_in_device_info       JSON,
    check_in_note              TEXT,
    -- Check-out
    check_out_time             DATETIME,
    check_out_location_id      INT,
    check_out_lat              DECIMAL(10,8),
    check_out_lng              DECIMAL(11,8),
    check_out_photo_url        VARCHAR(500),
    check_out_face_match_score DECIMAL(5,2),
    check_out_device_info      JSON,
    check_out_note             TEXT,
    -- Calculated
    status                     VARCHAR(30)   NOT NULL DEFAULT 'NOT_CHECKED',
    work_hours                 DECIMAL(4,2)  NOT NULL DEFAULT 0,
    overtime_hours             DECIMAL(4,2)  NOT NULL DEFAULT 0,
    late_minutes               INT           NOT NULL DEFAULT 0,
    early_leave_minutes        INT           NOT NULL DEFAULT 0,
    -- Verification
    is_verified                BOOLEAN       NOT NULL DEFAULT FALSE,
    verified_by                INT,
    verified_at                DATETIME,
    verification_note          TEXT,
    -- Approval
    is_approved                BOOLEAN,
    approved_by                INT,
    approved_at                DATETIME,
    approval_note              TEXT,
    note                       TEXT,
    -- BaseEntity
    created_at                 DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                 DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted                 BOOLEAN       NOT NULL DEFAULT FALSE,
    UNIQUE KEY uq_attendance (employee_id, date),
    INDEX idx_attendance_emp    (employee_id),
    INDEX idx_attendance_date   (date),
    INDEX idx_attendance_status (status),
    FOREIGN KEY (employee_id)           REFERENCES employees(id)      ON DELETE RESTRICT,
    FOREIGN KEY (shift_id)              REFERENCES shifts(id)          ON DELETE SET NULL,
    FOREIGN KEY (check_in_location_id)  REFERENCES work_locations(id)  ON DELETE SET NULL,
    FOREIGN KEY (check_out_location_id) REFERENCES work_locations(id)  ON DELETE SET NULL
);

-- attendance_anomalies: extends BaseEntity
CREATE TABLE attendance_anomalies (
    id              INT         AUTO_INCREMENT PRIMARY KEY,
    attendance_id   INT         NOT NULL,
    anomaly_type    VARCHAR(50) NOT NULL,
    description     TEXT,
    severity        VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    metadata        JSON,
    resolved        BOOLEAN     NOT NULL DEFAULT FALSE,
    resolved_by     INT,
    resolved_at     DATETIME,
    resolution_note TEXT,
    -- BaseEntity
    created_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted      BOOLEAN     NOT NULL DEFAULT FALSE,
    INDEX idx_anomalies_attendance (attendance_id),
    INDEX idx_anomalies_resolved   (resolved),
    FOREIGN KEY (attendance_id) REFERENCES attendance_records(id) ON DELETE CASCADE
);

-- attendance_audit_logs: extends BaseEntity
CREATE TABLE attendance_audit_logs (
    id                INT         AUTO_INCREMENT PRIMARY KEY,
    attendance_id     INT         NOT NULL,
    action            VARCHAR(20) NOT NULL COMMENT 'EDIT | APPROVE | REJECT',
    old_value         TEXT,
    new_value         TEXT,
    reason            TEXT,
    performed_by      INT         NOT NULL,
    performed_by_name VARCHAR(150),
    -- BaseEntity
    created_at        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted        BOOLEAN     NOT NULL DEFAULT FALSE,
    INDEX idx_audit_logs_attendance (attendance_id),
    FOREIGN KEY (attendance_id) REFERENCES attendance_records(id) ON DELETE CASCADE
);

-- ============================================================
-- RECRUITMENT (CV) — extends BaseEntity → có created_at, updated_at, is_deleted
-- ============================================================

CREATE TABLE candidates (
    id                  INT          AUTO_INCREMENT PRIMARY KEY,
    full_name           VARCHAR(150) NOT NULL,
    email               VARCHAR(255) NOT NULL,
    phone_number        VARCHAR(20),
    dob                 DATE,
    gender              VARCHAR(10),
    address             VARCHAR(255),
    desired_position    VARCHAR(200) NOT NULL,
    department_id       INT,
    expected_salary     VARCHAR(50),
    cv_file_url         VARCHAR(500),
    cv_file_key         VARCHAR(255),
    cv_file_name        VARCHAR(255),
    cover_letter        TEXT,
    status              VARCHAR(30)  NOT NULL DEFAULT 'NEW',
    applied_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reject_reason       TEXT,
    note                TEXT,
    reviewed_by         INT,
    reviewed_at         DATETIME,
    approved_by         INT,
    approved_at         DATETIME,
    created_user_id     INT,
    created_employee_id INT,
    -- BaseEntity
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    INDEX idx_candidates_email   (email),
    INDEX idx_candidates_status  (status),
    INDEX idx_candidates_applied (applied_at),
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
);

CREATE TABLE interviews (
    id               INT          AUTO_INCREMENT PRIMARY KEY,
    candidate_id     INT          NOT NULL,
    interview_date   DATETIME     NOT NULL,
    duration_minutes INT          NOT NULL DEFAULT 60,
    interview_type   VARCHAR(20)  NOT NULL DEFAULT 'OFFLINE',
    location         VARCHAR(500),
    interviewer_id   INT          NOT NULL,
    interviewer_name VARCHAR(150),
    result           VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    score            INT,
    feedback         TEXT,
    strengths        TEXT,
    weaknesses       TEXT,
    note             TEXT,
    created_by       INT,
    email_sent       BOOLEAN      NOT NULL DEFAULT FALSE,
    email_sent_at    DATETIME,
    -- BaseEntity
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted       BOOLEAN      NOT NULL DEFAULT FALSE,
    INDEX idx_interviews_candidate (candidate_id),
    INDEX idx_interviews_date      (interview_date),
    FOREIGN KEY (candidate_id)   REFERENCES candidates(id) ON DELETE CASCADE,
    FOREIGN KEY (interviewer_id) REFERENCES employees(id)  ON DELETE RESTRICT
);

CREATE TABLE recruitment_histories (
    id                INT         AUTO_INCREMENT PRIMARY KEY,
    candidate_id      INT         NOT NULL,
    action            VARCHAR(50) NOT NULL,
    from_status       VARCHAR(30),
    to_status         VARCHAR(30),
    performed_by      INT,
    performed_by_name VARCHAR(150),
    note              TEXT,
    created_at        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_recruit_hist_candidate (candidate_id),
    FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE CASCADE
);

-- hr_notifications đã được tạo trong V2__schema_employee.sql

SET FOREIGN_KEY_CHECKS = 1;
