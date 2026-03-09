CREATE TABLE employee_documents (
    id INT AUTO_INCREMENT PRIMARY KEY,
    emp_id INT,
    doc_type VARCHAR(100),
    file_url VARCHAR(255),
    original_name VARCHAR(255),
    file_size BIGINT,
    created_at DATETIME,
    updated_at DATETIME,
    is_deleted BOOLEAN,
    FOREIGN KEY (emp_id) REFERENCES employees(id)
);