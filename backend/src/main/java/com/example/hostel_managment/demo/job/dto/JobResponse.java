package com.example.hostel_managment.demo.job.dto;

import com.example.hostel_managment.demo.job.domain.JobCategory;
import com.example.hostel_managment.demo.job.domain.JobRequest;
import com.example.hostel_managment.demo.job.domain.JobStatus;
import com.example.hostel_managment.demo.job.domain.LocationType;
import com.example.hostel_managment.demo.job.domain.NotSolvedReason;
import com.example.hostel_managment.demo.job.domain.StudentConfirmation;
import com.example.hostel_managment.demo.job.domain.Urgency;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import com.example.hostel_managment.demo.slot.domain.TimeSlot;

public record JobResponse(Long id, Long customerId, Long workerId, String problemDescription, JobCategory category,
        LocationType locationType, String block, Integer floor, String roomNumber, String areaName,
        Urgency urgency, String contactNumber, String beforeImageUrl, JobStatus status,
        StudentConfirmation studentConfirmation, String afterImageUrl, String idCardPhotoUrl,
        NotSolvedReason notSolvedReason, String rejectionReason, Instant createdAt, Instant updatedAt,
        Long scheduledSlotId, LocalDate scheduledSlotDate, LocalTime scheduledStartTime,
        LocalTime scheduledEndTime, Instant completedAt, int upvoteCount) {

    public static JobResponse from(JobRequest job) {
        return new JobResponse(job.getId(), job.getCustomerId(), job.getWorkerId(), job.getProblemDescription(),
                job.getCategory(), job.getLocationType(), job.getBlock(), job.getFloor(),
                job.getRoomNumber(), job.getAreaName(), job.getUrgency(), job.getContactNumber(),
                job.getBeforeImageUrl(), job.getStatus(), job.getStudentConfirmation(),
                job.getAfterImageUrl(), job.getIdCardPhotoUrl(), job.getNotSolvedReason(),
                job.getRejectionReason(), job.getCreatedAt(), job.getUpdatedAt(),
                job.getScheduledSlotId(), null, null, null, job.getCompletedAt(), job.getUpvoteCount());
    }

    public static JobResponse from(JobRequest job, TimeSlot slot) {
        JobResponse response = from(job);
        return new JobResponse(response.id(), response.customerId(), response.workerId(),
                response.problemDescription(), response.category(), response.locationType(),
                response.block(), response.floor(), response.roomNumber(), response.areaName(),
                response.urgency(), response.contactNumber(), response.beforeImageUrl(),
                response.status(), response.studentConfirmation(), response.afterImageUrl(),
                response.idCardPhotoUrl(), response.notSolvedReason(), response.rejectionReason(),
                response.createdAt(), response.updatedAt(), response.scheduledSlotId(),
                slot == null ? null : slot.getSlotDate(), slot == null ? null : slot.getStartTime(),
                slot == null ? null : slot.getEndTime(), response.completedAt(), response.upvoteCount());
    }
}
