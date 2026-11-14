package com.example.hostel_managment.demo.job.dto;

import com.example.hostel_managment.demo.job.domain.JobRequest;
import com.example.hostel_managment.demo.job.domain.JobStatus;

public record SimilarJobResponse(Long id, String description, JobStatus status, int upvoteCount,
        String area) {

    public static SimilarJobResponse from(JobRequest job) {
        String area = job.getAreaName() != null
                ? job.getAreaName()
                : job.getBlock() + (job.getFloor() == null ? "" : " / Floor " + job.getFloor());
        return new SimilarJobResponse(job.getId(), job.getProblemDescription(), job.getStatus(),
                job.getUpvoteCount(), area);
    }
}
