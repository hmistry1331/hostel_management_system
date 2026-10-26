package com.example.hostel_managment.demo.job.service;

import com.example.hostel_managment.demo.job.domain.JobCategory;
import com.example.hostel_managment.demo.job.domain.JobRequest;
import com.example.hostel_managment.demo.job.domain.JobStatus;
import com.example.hostel_managment.demo.job.domain.LocationType;
import com.example.hostel_managment.demo.job.domain.Urgency;
import com.example.hostel_managment.demo.job.dto.CreateJobRequest;
import com.example.hostel_managment.demo.job.dto.CompleteJobRequest;
import com.example.hostel_managment.demo.job.dto.JobResponse;
import com.example.hostel_managment.demo.job.dto.NotSolvedJobRequest;
import com.example.hostel_managment.demo.job.dto.RejectCompletionRequest;
import com.example.hostel_managment.demo.job.repository.JobRequestRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class JobRequestService {

    private static final List<JobStatus> OPEN_STATUSES = List.of(
            JobStatus.SUBMITTED, JobStatus.ASSIGNED, JobStatus.SCHEDULED,
            JobStatus.IN_PROGRESS, JobStatus.NOT_SOLVED);

    private final JobRequestRepository repository;

    public JobRequestService(JobRequestRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public JobResponse create(CreateJobRequest request) {
        validateLocation(request);
        JobRequest job = JobRequest.submitted(request.customerId(), request.problemDescription(),
                request.category(), request.locationType(), request.block(), request.floor(),
                request.roomNumber(), request.areaName(),
                request.urgency() == null ? Urgency.NORMAL : request.urgency(),
                request.contactNumber(), request.beforeImageUrl());
        return JobResponse.from(repository.save(job));
    }

    @Transactional(readOnly = true)
    public List<JobResponse> findCustomerJobs(Long customerId) {
        return repository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream().map(JobResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<JobResponse> findAllJobs() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream().map(JobResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<JobResponse> findWorkerJobs() {
        return repository.findByStatusInOrderByCreatedAtDesc(
                List.of(JobStatus.SUBMITTED, JobStatus.NOT_SOLVED, JobStatus.ASSIGNED))
                .stream().map(JobResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<JobResponse> findSimilar(String block, Integer floor, JobCategory category,
            LocationType locationType) {
        return repository
                .findByBlockAndFloorAndCategoryAndLocationTypeAndStatusInOrderByUrgencyDescCreatedAtAsc(
                        block, floor, category, locationType, OPEN_STATUSES)
                .stream().map(JobResponse::from).toList();
    }

    @Transactional
    public void cancel(Long jobId, Long customerId) {
        JobRequest job = repository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Complaint not found"));
        if (!Objects.equals(job.getCustomerId(), customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You may only cancel your own complaints");
        }
        try {
            job.cancel();
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage(), exception);
        }
    }

    @Transactional
    public JobResponse start(Long jobId, Long workerId) {
        JobRequest job = findJob(jobId);
        update(job, () -> job.start(workerId));
        return JobResponse.from(job);
    }

    @Transactional
    public JobResponse complete(Long jobId, Long workerId, CompleteJobRequest request) {
        JobRequest job = findJob(jobId);
        update(job, () -> job.complete(workerId, request.afterImageUrl(), request.idCardPhotoUrl()));
        return JobResponse.from(job);
    }

    @Transactional
    public JobResponse notSolved(Long jobId, Long workerId, NotSolvedJobRequest request) {
        JobRequest job = findJob(jobId);
        update(job, () -> job.markNotSolved(workerId, request.reason()));
        return JobResponse.from(job);
    }

    @Transactional
    public JobResponse confirm(Long jobId, Long customerId) {
        JobRequest job = findJob(jobId);
        update(job, () -> job.confirmByStudent(customerId));
        return JobResponse.from(job);
    }

    @Transactional
    public JobResponse reject(Long jobId, Long customerId, RejectCompletionRequest request) {
        JobRequest job = findJob(jobId);
        update(job, () -> job.rejectByStudent(customerId, request.reason()));
        return JobResponse.from(job);
    }

    private JobRequest findJob(Long jobId) {
        return repository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Complaint not found"));
    }

    private void update(JobRequest job, Runnable operation) {
        try {
            operation.run();
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage(), exception);
        }
    }

    private void validateLocation(CreateJobRequest request) {
        if (request.locationType() == LocationType.ROOM
                && (request.floor() == null || isBlank(request.roomNumber()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Room complaints require floor and room number");
        }
        if (request.locationType() == LocationType.COMMON_AREA && isBlank(request.areaName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Common-area complaints require an area name");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
