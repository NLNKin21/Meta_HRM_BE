-- ========================================
-- SEED DATA FOR TASK MANAGEMENT
-- Version: V9
-- Author: System
-- Description: Insert initial data for task management
-- ========================================

-- ========================================
-- 1. TASK STATUSES - COMMON (Áp dụng chung)
-- ========================================
INSERT INTO task_statuses 
    (status_name, status_name_en, order_index, color, icon, is_default, is_completed, department_id) 
VALUES
    ('Chờ xử lý', 'To Do', 1, '#2196F3', 'Assignment', TRUE, FALSE, NULL),
    ('Đang làm', 'In Progress', 2, '#FF9800', 'PlayCircle', FALSE, FALSE, NULL),
    ('Đang review', 'In Review', 3, '#9C27B0', 'RateReview', FALSE, FALSE, NULL),
    ('Hoàn thành', 'Done', 4, '#4CAF50', 'CheckCircle', FALSE, TRUE, NULL),
    ('Hủy', 'Cancelled', 5, '#F44336', 'Cancel', FALSE, TRUE, NULL);

-- ========================================
-- 2. TASK STATUSES - IT DEPARTMENT (Ví dụ)
-- ========================================
-- Lấy ID của IT Department (giả sử IT Department có id = 1)
SET @it_dept_id = (SELECT id FROM departments WHERE dept_name LIKE '%IT%' OR dept_name LIKE '%Công nghệ%' LIMIT 1);

INSERT INTO task_statuses 
    (status_name, status_name_en, order_index, color, icon, is_completed, department_id) 
VALUES
    ('Backlog', 'Backlog', 1, '#9E9E9E', 'Inventory', FALSE, @it_dept_id),
    ('Dev', 'Development', 2, '#2196F3', 'Code', FALSE, @it_dept_id),
    ('Testing', 'Testing', 3, '#FFEB3B', 'BugReport', FALSE, @it_dept_id),
    ('Deploy', 'Deployment', 4, '#00BCD4', 'Rocket', FALSE, @it_dept_id);

-- ========================================
-- 3. SAMPLE PROJECT
-- ========================================
INSERT INTO projects 
    (project_code, project_name, description, department_id, manager_id, start_date, end_date, status, created_by) 
VALUES
    (
        'PRJ-2024-001', 
        'Hệ thống quản lý nhân sự HRM', 
        'Phát triển hệ thống quản lý nhân sự toàn diện cho công ty',
        @it_dept_id,
        (SELECT id FROM employees WHERE role_in_dept = 'HEAD' AND dept_id = @it_dept_id LIMIT 1),
        '2024-01-01',
        '2024-12-31',
        'ACTIVE',
        1
    );

-- ========================================
-- 4. SAMPLE TASKS
-- ========================================
SET @project_id = (SELECT id FROM projects WHERE project_code = 'PRJ-2024-001');
SET @default_status = (SELECT id FROM task_statuses WHERE is_default = TRUE AND department_id IS NULL LIMIT 1);
SET @manager_id = (SELECT id FROM employees WHERE role_in_dept = 'HEAD' AND dept_id = @it_dept_id LIMIT 1);
SET @staff_id = (SELECT id FROM employees WHERE role_in_dept = 'STAFF' AND dept_id = @it_dept_id LIMIT 1);

INSERT INTO tasks 
    (task_code, title, description, task_type, priority, status_id, reporter_id, assignee_id, 
     department_id, project_id, estimated_hours, start_date, due_date, created_by) 
VALUES
    (
        'TSK-20240101-001',
        'Thiết kế database cho module Employee',
        'Thiết kế và tạo migration cho bảng employees, positions, departments',
        'TASK',
        'HIGH',
        @default_status,
        @manager_id,
        @staff_id,
        @it_dept_id,
        @project_id,
        16.00,
        '2024-01-02',
        '2024-01-05',
        @manager_id
    ),
    (
        'TSK-20240101-002',
        'Phát triển API CRUD cho Employee',
        'Xây dựng các API endpoints cho Employee Management',
        'FEATURE',
        'HIGH',
        @default_status,
        @manager_id,
        @staff_id,
        @it_dept_id,
        @project_id,
        32.00,
        '2024-01-06',
        '2024-01-15',
        @manager_id
    ),
    (
        'TSK-20240101-003',
        'Fix lỗi validation form đăng ký',
        'Người dùng có thể submit form với dữ liệu không hợp lệ',
        'BUG',
        'URGENT',
        @default_status,
        @manager_id,
        @staff_id,
        @it_dept_id,
        @project_id,
        4.00,
        '2024-01-08',
        '2024-01-09',
        @manager_id
    );

-- ========================================
-- 5. SAMPLE COMMENTS
-- ========================================
SET @task1_id = (SELECT id FROM tasks WHERE task_code = 'TSK-20240101-001');
SET @task2_id = (SELECT id FROM tasks WHERE task_code = 'TSK-20240101-002');

INSERT INTO task_comments (task_id, user_id, content, created_by) 
VALUES
    (@task1_id, @staff_id, 'Đã hoàn thành phân tích yêu cầu, bắt đầu thiết kế schema', @staff_id),
    (@task1_id, @manager_id, 'Nhớ tham khảo best practices cho naming convention nhé', @manager_id),
    (@task2_id, @staff_id, 'Đã setup project structure, đang implement CRUD operations', @staff_id);

-- ========================================
-- 6. SAMPLE TASK HISTORY
-- ========================================
INSERT INTO task_histories (task_id, user_id, field_name, old_value, new_value, action_type) 
VALUES
    (@task1_id, @manager_id, 'task', NULL, 'TSK-20240101-001', 'CREATE'),
    (@task2_id, @manager_id, 'task', NULL, 'TSK-20240101-002', 'CREATE'),
    (@task1_id, @staff_id, 'comment', NULL, 'Đã hoàn thành phân tích yêu cầu...', 'COMMENT');

-- ========================================
-- 7. SAMPLE REMINDERS (cho tasks có due_date)
-- ========================================
INSERT INTO task_reminders (task_id, remind_before_hours, remind_at, created_by)
SELECT 
    id,
    24,
    TIMESTAMP(DATE_SUB(due_date, INTERVAL 1 DAY)),
    created_by
FROM tasks 
WHERE due_date IS NOT NULL;

INSERT INTO task_reminders (task_id, remind_before_hours, remind_at, created_by)
SELECT 
    id,
    48,
    TIMESTAMP(DATE_SUB(due_date, INTERVAL 2 DAY)),
    created_by
FROM tasks 
WHERE due_date IS NOT NULL;

-- ========================================
-- COMPLETED
-- ========================================