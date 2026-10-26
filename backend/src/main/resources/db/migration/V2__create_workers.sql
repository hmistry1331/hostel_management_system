CREATE TABLE workers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    name VARCHAR(120) NOT NULL,
    trade_category VARCHAR(30) NOT NULL,
    experience_years INT NOT NULL DEFAULT 0,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    shift_start VARCHAR(5) NULL,
    shift_end VARCHAR(5) NULL,
    working_days VARCHAR(50) NULL,
    has_smartphone BOOLEAN NOT NULL DEFAULT FALSE,
    managed_by_supervisor BOOLEAN NOT NULL DEFAULT FALSE,
    preferred_language VARCHAR(20) NOT NULL DEFAULT 'ENGLISH',
    CONSTRAINT uk_worker_user UNIQUE (user_id),
    INDEX idx_worker_available_name (available, name)
);

ALTER TABLE job_requests
    ADD CONSTRAINT fk_job_request_worker
    FOREIGN KEY (worker_id) REFERENCES workers (id);
