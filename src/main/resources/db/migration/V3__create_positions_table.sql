CREATE TABLE positions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    position_code VARCHAR(20) UNIQUE NOT NULL,
    position_name VARCHAR(100) NOT NULL,
    description TEXT,
    min_salary DECIMAL(15, 2),
    max_salary DECIMAL(15, 2),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    department_id INT,
    created_at DATETIME,
    updated_at DATETIME,
    is_deleted BOOLEAN,
    FOREIGN KEY (department_id) REFERENCES departments(id)
);