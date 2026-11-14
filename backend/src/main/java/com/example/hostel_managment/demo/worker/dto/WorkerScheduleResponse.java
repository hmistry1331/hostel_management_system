package com.example.hostel_managment.demo.worker.dto;

import com.example.hostel_managment.demo.job.domain.JobCategory;
import com.example.hostel_managment.demo.job.domain.JobStatus;
import com.example.hostel_managment.demo.job.domain.Urgency;
import com.example.hostel_managment.demo.job.domain.JobRequest;
import com.example.hostel_managment.demo.slot.domain.TimeSlot;
import java.time.LocalDate;
import java.time.LocalTime;

public record WorkerScheduleResponse(Long slotId, Long jobId, Long workerId, LocalDate date,
        LocalTime startTime, LocalTime endTime, String block, String roomNumber,
        JobCategory category, Urgency urgency, String contactNumber, JobStatus status) {
    public static WorkerScheduleResponse from(TimeSlot slot, JobRequest job) {
        return new WorkerScheduleResponse(slot.getId(), job.getId(), slot.getWorkerId(),
                slot.getSlotDate(), slot.getStartTime(), slot.getEndTime(), job.getBlock(),
                job.getRoomNumber(), job.getCategory(), job.getUrgency(),
                job.getContactNumber(), job.getStatus());
    }

    public static WorkerScheduleResponse fromAssignedJob(JobRequest job) {
        return new WorkerScheduleResponse(null, job.getId(), job.getWorkerId(),
                null, null, null, job.getBlock(), job.getRoomNumber(), job.getCategory(),
                job.getUrgency(), job.getContactNumber(), job.getStatus());
    }
}
