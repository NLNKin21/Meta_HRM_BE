CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100),
    password VARCHAR(255),
    email VARCHAR(150),
    role VARCHAR(50),
    status VARCHAR(50),
    created_at DATETIME,
    updated_at DATETIME,
    is_deleted BOOLEAN
);