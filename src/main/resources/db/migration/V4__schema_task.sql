-- ============================================================
-- V4: TASK & PROJECT SCHEMA
-- Tables: projects, task_statuses, task_status_transitions,
--         tasks, task_comments, task_histories,
--         task_reminders, notifications
-- Depends on: V2 (employees, departments), V1 (users)
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- projects
-- Nhóm nhiều task lại thành dự án
-- status: PLANNING | IN_PROGRESS | ON_HOLD | COMPLETED | CANCELLED
-- ------------------------------------------------------------
CREATE TABLE projects (
    id           INT          AUTO_INCREMENT PRIMARY KEY,
    project_code VARCHAR(50)  NOT NULL UNIQUE COMMENT 'PRJ-2024-001',
    project_name VARCHAR(200) NOT NULL,
    description  TEXT,
    department_id INT,
    manager_id   INT,
    start_date   DATE,
    end_date     DATE,
    status       VARCHAR(20)  NOT NULL DEFAULT 'PLANNING' COMMENT 'PLANNING | IN_PROGRESS | ON_HOLD | COMPLETED | CANCELLED',
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted   BOOLEAN      NOT NULL DEFAULT FALSE,
    INDEX idx_projects_code   (project_code),
    INDEX idx_projects_dept   (department_id),
    INDEX idx_projects_status (status),
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
    FOREIGN KEY (manager_id)    REFERENCES employees(id)   ON DELETE SET NULL
);

-- ------------------------------------------------------------
-- task_statuses
-- Trạng thái task — có thể tùy chỉnh theo phòng ban
-- department_id = NULL → áp dụng cho tất cả phòng ban
-- ------------------------------------------------------------
CREATE TABLE task_statuses (
    id             INT          AUTO_INCREMENT PRIMARY KEY,
    status_name    VARCHAR(100) NOT NULL COMMENT 'Tên tiếng Việt',
    status_name_en VARCHAR(100) COMMENT 'Tên tiếng Anh',
    order_index    INT          NOT NULL DEFAULT 0 COMMENT 'Thứ tự trên Kanban board',
    color          VARCHAR(7)   NOT NULL DEFAULT '#1976d2' COMMENT 'Hex color',
    icon           VARCHAR(50)  COMMENT 'Tên icon MUI',
    is_completed   BOOLEAN      NOT NULL DEFAULT FALSE COMMENT 'Đây là trạng thái kết thúc?',
    is_default     BOOLEAN      NOT NULL DEFAULT FALSE COMMENT 'Trạng thái mặc định khi tạo task?',
    department_id  INT          COMMENT 'NULL = chung tất cả phòng ban',
    is_active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
    INDEX idx_task_statuses_dept (department_id),
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
);

-- ------------------------------------------------------------
-- task_status_transitions
-- Workflow: kiểm soát chuyển trạng thái nào được phép
-- allowed_roles: JSON array ["HEAD","DEPUTY","LEADER","STAFF"]
-- ------------------------------------------------------------
CREATE TABLE task_status_transitions (
    id             INT     AUTO_INCREMENT PRIMARY KEY,
    from_status_id INT     NOT NULL,
    to_status_id   INT     NOT NULL,
    department_id  INT     COMMENT 'NULL = áp dụng toàn cục',
    allowed_roles  TEXT    COMMENT 'JSON: ["HEAD","LEADER"]',
    is_active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted     BOOLEAN  NOT NULL DEFAULT FALSE,
    UNIQUE KEY uq_transition (from_status_id, to_status_id),
    FOREIGN KEY (from_status_id) REFERENCES task_statuses(id) ON DELETE CASCADE,
    FOREIGN KEY (to_status_id)   REFERENCES task_statuses(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id)  REFERENCES departments(id)   ON DELETE SET NULL
);

-- ------------------------------------------------------------
-- tasks
-- Công việc. task_code tự sinh từ service: TSK-YYYYMM-NNNN
-- task_type:  TASK | BUG | FEATURE | IMPROVEMENT
-- priority:   LOW | MEDIUM | HIGH | CRITICAL
-- ------------------------------------------------------------
CREATE TABLE tasks (
    id              INT           AUTO_INCREMENT PRIMARY KEY,
    task_code       VARCHAR(50)   NOT NULL UNIQUE,
    title           VARCHAR(255)  NOT NULL,
    description     TEXT,
    task_type       VARCHAR(20)   NOT NULL DEFAULT 'TASK'   COMMENT 'TASK | BUG | FEATURE | IMPROVEMENT',
    priority        VARCHAR(20)   NOT NULL DEFAULT 'MEDIUM' COMMENT 'LOW | MEDIUM | HIGH | CRITICAL',
    status_id       INT           NOT NULL,
    reporter_id     INT           NOT NULL COMMENT 'Người tạo task',
    assignee_id     INT           NOT NULL COMMENT 'Người thực hiện',
    approver_id     INT           COMMENT 'Người duyệt (nullable)',
    department_id   INT           NOT NULL,
    project_id      INT           COMMENT 'Nullable',
    estimated_hours DECIMAL(8,2)  COMMENT 'Giờ ước lượng',
    actual_hours    DECIMAL(8,2)  NOT NULL DEFAULT 0 COMMENT 'Giờ thực tế',
    start_date      DATE,
    due_date        DATE,
    completed_at    DATETIME      COMMENT 'Thời điểm hoàn thành thực tế',
    completion_rate INT           NOT NULL DEFAULT 0 COMMENT '0-100%',
    is_late         BOOLEAN       NOT NULL DEFAULT FALSE,
    is_urgent       BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted      BOOLEAN       NOT NULL DEFAULT FALSE,
    INDEX idx_tasks_code       (task_code),
    INDEX idx_tasks_assignee   (assignee_id),
    INDEX idx_tasks_status     (status_id),
    INDEX idx_tasks_due_date   (due_date),
    INDEX idx_tasks_dept       (department_id),
    INDEX idx_tasks_project    (project_id),
    FOREIGN KEY (status_id)     REFERENCES task_statuses(id) ON DELETE RESTRICT,
    FOREIGN KEY (reporter_id)   REFERENCES employees(id)     ON DELETE RESTRICT,
    FOREIGN KEY (assignee_id)   REFERENCES employees(id)     ON DELETE RESTRICT,
    FOREIGN KEY (approver_id)   REFERENCES employees(id)     ON DELETE SET NULL,
    FOREIGN KEY (department_id) REFERENCES departments(id)   ON DELETE RESTRICT,
    FOREIGN KEY (project_id)    REFERENCES projects(id)      ON DELETE SET NULL
);

-- ------------------------------------------------------------
-- task_comments
-- Bình luận trong task, hỗ trợ đính kèm file
-- attachment_type: image | pdf | other
-- ------------------------------------------------------------
CREATE TABLE task_comments (
    id              INT          AUTO_INCREMENT PRIMARY KEY,
    task_id         INT          NOT NULL,
    user_id         INT          NOT NULL,
    content         TEXT         NOT NULL,
    attachment_url  VARCHAR(1000),
    attachment_name VARCHAR(255),
    attachment_type VARCHAR(20)  COMMENT 'image | pdf | other',
    is_deleted      BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_task_comments_task (task_id),
    FOREIGN KEY (task_id)  REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id)  REFERENCES users(id) ON DELETE RESTRICT
);

-- ------------------------------------------------------------
-- task_histories
-- Audit log thay đổi field của task
-- action_type: CREATE | UPDATE | DELETE | STATUS_CHANGE | COMMENT
-- ------------------------------------------------------------
CREATE TABLE task_histories (
    id          INT         AUTO_INCREMENT PRIMARY KEY,
    task_id     INT         NOT NULL,
    user_id     INT         NOT NULL COMMENT 'Employee ID',
    field_name  VARCHAR(50) COMMENT 'status | assignee | due_date | priority...',
    old_value   TEXT,
    new_value   TEXT,
    action_type VARCHAR(50) NOT NULL COMMENT 'CREATE | UPDATE | DELETE | STATUS_CHANGE | COMMENT',
    created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_task_hist_task    (task_id),
    INDEX idx_task_hist_created (created_at),
    FOREIGN KEY (task_id) REFERENCES tasks(id)     ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES employees(id) ON DELETE RESTRICT
);

-- ------------------------------------------------------------
-- task_reminders
-- Scheduler gửi notification khi đến remind_at
-- ------------------------------------------------------------
CREATE TABLE task_reminders (
    id                  INT      AUTO_INCREMENT PRIMARY KEY,
    task_id             INT      NOT NULL,
    remind_before_hours INT      NOT NULL COMMENT '24 | 48 | 72',
    remind_at           DATETIME COMMENT 'Thời điểm gửi nhắc',
    is_sent             BOOLEAN  NOT NULL DEFAULT FALSE,
    sent_at             DATETIME,
    created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN  NOT NULL DEFAULT FALSE,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- notifications
-- Thông báo in-app cho task
-- type: TASK_ASSIGNED | TASK_COMMENTED | STATUS_CHANGED | DEADLINE_REMINDER
-- ------------------------------------------------------------
CREATE TABLE notifications (
    id                INT          AUTO_INCREMENT PRIMARY KEY,
    user_id           INT          NOT NULL COMMENT 'Employee ID người nhận',
    notification_type VARCHAR(50)  COMMENT 'TASK_ASSIGNED | TASK_COMMENTED | STATUS_CHANGED | DEADLINE_REMINDER',
    reference_id      INT          COMMENT 'task_id hoặc comment_id',
    title             VARCHAR(255),
    message           TEXT,
    link              VARCHAR(500) COMMENT 'URL redirect',
    is_read           BOOLEAN      NOT NULL DEFAULT FALSE,
    read_at           DATETIME,
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_notifications_user    (user_id),
    INDEX idx_notifications_read    (is_read),
    INDEX idx_notifications_created (created_at),
    FOREIGN KEY (user_id) REFERENCES employees(id) ON DELETE CASCADE
);

SET FOREIGN_KEY_CHECKS = 1;
