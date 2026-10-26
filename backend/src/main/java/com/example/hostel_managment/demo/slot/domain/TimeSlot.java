package com.example.hostel_managment.demo.slot.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "time_slots")
@Getter
@NoArgsConstructor
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "worker_id", nullable = false)
    private Long workerId;

    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "is_booked", nullable = false)
    private boolean booked;

    @Column(name = "job_id")
    private Long jobId;

    public TimeSlot(Long workerId, LocalDate slotDate, LocalTime startTime, LocalTime endTime) {
        this.workerId = workerId;
        this.slotDate = slotDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void book(Long jobId) {
        if (booked) {
            throw new IllegalStateException("Time slot is already booked");
        }
        booked = true;
        this.jobId = jobId;
    }

    public void release() {
        booked = false;
        jobId = null;
    }
}
