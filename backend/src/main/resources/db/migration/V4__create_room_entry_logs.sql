CREATE TABLE room_entry_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id BIGINT NOT NULL,
    worker_id BIGINT NOT NULL,
    authorized_by_user_id BIGINT NOT NULL,
    reason VARCHAR(500) NOT NULL,
    entered_at TIMESTAMP(6) NULL,
    exited_at TIMESTAMP(6) NULL,
    keys_returned_at TIMESTAMP(6) NULL,
    CONSTRAINT fk_room_entry_job FOREIGN KEY (job_id) REFERENCES job_requests (id),
    CONSTRAINT fk_room_entry_worker FOREIGN KEY (worker_id) REFERENCES workers (id),
    INDEX idx_room_entry_job_entered (job_id, entered_at)
);
