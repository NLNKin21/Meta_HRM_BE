CREATE TABLE contracts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    emp_id INT,
    contract_type VARCHAR(50),
    start_date DATE,
    end_date DATE,
    file_url VARCHAR(255),
    status VARCHAR(20),
    created_at DATETIME,
    updated_at DATETIME,
    is_deleted BOOLEAN,
    FOREIGN KEY (emp_id) REFERENCES employees(id)
);