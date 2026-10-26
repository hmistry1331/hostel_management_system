package com.example.hostel_managment.demo.job.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "job_requests", uniqueConstraints = {
        @UniqueConstraint(name = "uk_job_request_id", columnNames = "id")
})
@Getter
@NoArgsConstructor
public class JobRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "worker_id")
    private Long workerId;

    @Column(name = "problem_description", nullable = false, length = 2000)
    private String problemDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private JobCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", nullable = false, length = 20)
    private LocationType locationType;

    @Column(nullable = false, length = 100)
    private String block;

    @Column
    private Integer floor;

    @Column(name = "room_number", length = 30)
    private String roomNumber;

    @Column(name = "area_name", length = 120)
    private String areaName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Urgency urgency;

    @Column(name = "contact_number", length = 30)
    private String contactNumber;

    @Column(name = "before_image_url", nullable = false, length = 500)
    private String beforeImageUrl;

    @Column(name = "after_image_url", length = 500)
    private String afterImageUrl;

    @Column(name = "id_card_photo_url", length = 500)
    private String idCardPhotoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "not_solved_reason", length = 30)
    private NotSolvedReason notSolvedReason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private JobStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "student_confirmation", nullable = false, length = 20)
    private StudentConfirmation studentConfirmation;

    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;

    @Column(name = "upvote_count", nullable = false)
    private int upvoteCount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "scheduled_slot_id")
    private Long scheduledSlotId;

    protected JobRequest(Long customerId, String problemDescription, JobCategory category,
            LocationType locationType, String block, Integer floor, String roomNumber,
            String areaName, Urgency urgency, String contactNumber, String beforeImageUrl) {
        this.customerId = customerId;
        this.problemDescription = problemDescription;
        this.category = category;
        this.locationType = locationType;
        this.block = block;
        this.floor = floor;
        this.roomNumber = roomNumber;
        this.areaName = areaName;
        this.urgency = urgency;
        this.contactNumber = contactNumber;
        this.beforeImageUrl = beforeImageUrl;
        this.status = JobStatus.SUBMITTED;
        this.studentConfirmation = StudentConfirmation.PENDING;
    }

    public static JobRequest submitted(Long customerId, String problemDescription, JobCategory category,
            LocationType locationType, String block, Integer floor, String roomNumber,
            String areaName, Urgency urgency, String contactNumber, String beforeImageUrl) {
        return new JobRequest(customerId, problemDescription, category, locationType, block, floor,
                roomNumber, areaName, urgency, contactNumber, beforeImageUrl);
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) {
            status = JobStatus.SUBMITTED;
        }
        if (studentConfirmation == null) {
            studentConfirmation = StudentConfirmation.PENDING;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public void cancel() {
        if (status == JobStatus.COMPLETED || status == JobStatus.CANCELLED) {
            throw new IllegalStateException("A completed or cancelled complaint cannot be cancelled");
        }
        status = JobStatus.CANCELLED;
    }

    public void assignTo(Long workerId) {
        if (status != JobStatus.SUBMITTED && status != JobStatus.NOT_SOLVED) {
            throw new IllegalStateException("Only submitted or not-solved complaints can be assigned");
        }
        this.workerId = workerId;
        this.status = JobStatus.ASSIGNED;
    }

    public void schedule(Long slotId) {
        if (status != JobStatus.ASSIGNED && status != JobStatus.NOT_SOLVED) {
            throw new IllegalStateException("Only assigned complaints can be scheduled");
        }
        scheduledSlotId = slotId;
        status = JobStatus.SCHEDULED;
    }

    public void start(Long workerId) {
        verifyWorker(workerId);
        if (status != JobStatus.SCHEDULED) {
            throw new IllegalStateException("Only scheduled complaints can be started");
        }
        status = JobStatus.IN_PROGRESS;
    }

    public void complete(Long workerId, String afterImageUrl, String idCardPhotoUrl) {
        verifyWorker(workerId);
        if (status != JobStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only in-progress complaints can be completed");
        }
        if (isBlank(afterImageUrl) || isBlank(idCardPhotoUrl)) {
            throw new IllegalArgumentException("After-photo and student ID photo are required");
        }
        this.afterImageUrl = afterImageUrl;
        this.idCardPhotoUrl = idCardPhotoUrl;
        this.completedAt = Instant.now();
        this.status = JobStatus.COMPLETED;
        this.studentConfirmation = StudentConfirmation.PENDING;
    }

    public void markNotSolved(Long workerId, NotSolvedReason reason) {
        verifyWorker(workerId);
        if (status != JobStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only in-progress complaints can be marked not solved");
        }
        if (reason == null) {
            throw new IllegalArgumentException("A not-solved reason is required");
        }
        notSolvedReason = reason;
        status = JobStatus.NOT_SOLVED;
    }

    public void confirmByStudent(Long customerId) {
        verifyCustomer(customerId);
        if (status != JobStatus.COMPLETED) {
            throw new IllegalStateException("Only completed complaints can be confirmed");
        }
        studentConfirmation = StudentConfirmation.CONFIRMED;
    }

    public void rejectByStudent(Long customerId, String reason) {
        verifyCustomer(customerId);
        if (status != JobStatus.COMPLETED) {
            throw new IllegalStateException("Only completed complaints can be rejected");
        }
        if (isBlank(reason)) {
            throw new IllegalArgumentException("A rejection reason is required");
        }
        rejectionReason = reason;
        studentConfirmation = StudentConfirmation.REJECTED;
        status = JobStatus.SUBMITTED;
    }

    private void verifyWorker(Long requestedWorkerId) {
        if (workerId == null || !workerId.equals(requestedWorkerId)) {
            throw new IllegalStateException("Only the assigned worker can update this complaint");
        }
    }

    private void verifyCustomer(Long requestedCustomerId) {
        if (customerId == null || !customerId.equals(requestedCustomerId)) {
            throw new IllegalStateException("Only the complaint student can confirm this complaint");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
