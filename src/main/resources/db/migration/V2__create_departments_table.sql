CREATE TABLE IF NOT EXISTS departments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dept_name VARCHAR(100),
    created_at DATETIME,
    updated_at DATETIME,
    is_deleted BOOLEAN
);