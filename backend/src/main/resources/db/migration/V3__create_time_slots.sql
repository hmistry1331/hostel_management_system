CREATE TABLE time_slots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    worker_id BIGINT NOT NULL,
    slot_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_booked BOOLEAN NOT NULL DEFAULT FALSE,
    job_id BIGINT NULL,
    CONSTRAINT uk_time_slot_worker_date_start UNIQUE (worker_id, slot_date, start_time),
    CONSTRAINT fk_time_slot_worker FOREIGN KEY (worker_id) REFERENCES workers (id),
    CONSTRAINT fk_time_slot_job FOREIGN KEY (job_id) REFERENCES job_requests (id),
    INDEX idx_time_slot_available (worker_id, slot_date, is_booked)
);
