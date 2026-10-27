CREATE TABLE app_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT chk_app_user_role CHECK (role IN ('STUDENT', 'WORKER'))
);
