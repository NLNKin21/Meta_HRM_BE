CREATE TABLE employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    dept_id INT,
    position_id INT,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    full_name VARCHAR(150),
    gender VARCHAR(10),
    dob DATE,
    phone_number VARCHAR(20),
    address VARCHAR(255),
    hire_date DATE,
    basic_salary DECIMAL(12,2),
    status VARCHAR(20),
    role_in_dept VARCHAR(20),
    created_at DATETIME,
    updated_at DATETIME,
    is_deleted BOOLEAN,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (dept_id) REFERENCES departments(id),
    FOREIGN KEY (position_id) REFERENCES positions(id)

);