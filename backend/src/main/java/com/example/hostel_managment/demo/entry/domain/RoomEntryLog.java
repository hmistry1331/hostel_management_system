package com.example.hostel_managment.demo.entry.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "room_entry_logs")
@Getter
@NoArgsConstructor
public class RoomEntryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_id", nullable = false)
    private Long jobId;

    @Column(name = "worker_id", nullable = false)
    private Long workerId;

    @Column(name = "authorized_by_user_id", nullable = false)
    private Long authorizedByUserId;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(name = "entered_at")
    private Instant enteredAt;

    @Column(name = "exited_at")
    private Instant exitedAt;

    @Column(name = "keys_returned_at")
    private Instant keysReturnedAt;

    public RoomEntryLog(Long jobId, Long workerId, Long authorizedByUserId, String reason) {
        this.jobId = jobId;
        this.workerId = workerId;
        this.authorizedByUserId = authorizedByUserId;
        this.reason = reason;
    }

    @PrePersist
    void onCreate() {
        if (enteredAt == null) {
            enteredAt = Instant.now();
        }
    }

    public void markExited() {
        if (exitedAt != null) {
            throw new IllegalStateException("Room entry has already been closed");
        }
        exitedAt = Instant.now();
    }

    public void markKeysReturned() {
        if (exitedAt == null) {
            throw new IllegalStateException("Worker must exit before keys can be returned");
        }
        if (keysReturnedAt != null) {
            throw new IllegalStateException("Keys have already been returned");
        }
        keysReturnedAt = Instant.now();
    }
}
