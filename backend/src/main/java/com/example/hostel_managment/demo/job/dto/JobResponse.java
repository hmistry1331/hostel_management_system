package com.example.hostel_managment.demo.job.dto;

import com.example.hostel_managment.demo.job.domain.JobCategory;
import com.example.hostel_managment.demo.job.domain.JobRequest;
import com.example.hostel_managment.demo.job.domain.JobStatus;
import com.example.hostel_managment.demo.job.domain.LocationType;
import com.example.hostel_managment.demo.job.domain.NotSolvedReason;
import com.example.hostel_managment.demo.job.domain.StudentConfirmation;
import com.example.hostel_managment.demo.job.domain.Urgency;
import java.time.Instant;

public record JobResponse(Long id, Long customerId, Long workerId, String problemDescription, JobCategory category,
        LocationType locationType, String block, Integer floor, String roomNumber, String areaName,
        Urgency urgency, String contactNumber, String beforeImageUrl, JobStatus status,
        StudentConfirmation studentConfirmation, String afterImageUrl, String idCardPhotoUrl,
        NotSolvedReason notSolvedReason, String rejectionReason, Instant createdAt, Instant updatedAt) {

    public static JobResponse from(JobRequest job) {
        return new JobResponse(job.getId(), job.getCustomerId(), job.getWorkerId(), job.getProblemDescription(),
                job.getCategory(), job.getLocationType(), job.getBlock(), job.getFloor(),
                job.getRoomNumber(), job.getAreaName(), job.getUrgency(), job.getContactNumber(),
                job.getBeforeImageUrl(), job.getStatus(), job.getStudentConfirmation(),
                job.getAfterImageUrl(), job.getIdCardPhotoUrl(), job.getNotSolvedReason(),
                job.getRejectionReason(),
                job.getCreatedAt(), job.getUpdatedAt());
    }
}
