package com.example.hostel_managment.demo.job.controller;

import com.example.hostel_managment.demo.job.domain.JobCategory;
import com.example.hostel_managment.demo.job.domain.LocationType;
import com.example.hostel_managment.demo.job.dto.CreateJobRequest;
import com.example.hostel_managment.demo.job.dto.CompleteJobRequest;
import com.example.hostel_managment.demo.job.dto.JobResponse;
import com.example.hostel_managment.demo.job.dto.NotSolvedJobRequest;
import com.example.hostel_managment.demo.job.dto.RejectCompletionRequest;
import com.example.hostel_managment.demo.job.service.JobRequestService;
import com.example.hostel_managment.demo.auth.security.AuthenticatedUser;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class JobRequestController {

    private final JobRequestService service;

    public JobRequestController(JobRequestService service) {
        this.service = service;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<JobResponse> create(@Valid @RequestBody CreateJobRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        CreateJobRequest authenticatedRequest = new CreateJobRequest(user.userId(),
                request.problemDescription(), request.category(), request.locationType(),
                request.block(), request.floor(), request.roomNumber(), request.areaName(),
                request.urgency(), request.contactNumber(), request.beforeImageUrl());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(authenticatedRequest));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('STUDENT')")
    public List<JobResponse> customerJobs(@PathVariable Long customerId,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return service.findCustomerJobs(user.userId());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public List<JobResponse> adminJobs() {
        return service.findAllJobs();
    }

    @GetMapping("/worker")
    @PreAuthorize("hasRole('WORKER')")
    public List<JobResponse> workerJobs() {
        return service.findWorkerJobs();
    }

    @GetMapping("/similar")
    @PreAuthorize("hasRole('STUDENT')")
    public List<JobResponse> similar(@RequestParam String block, @RequestParam Integer floor,
            @RequestParam JobCategory category, @RequestParam LocationType locationType) {
        return service.findSimilar(block, floor, category, locationType);
    }

    @PutMapping("/{jobId}/cancel")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> cancel(@PathVariable Long jobId, @RequestParam(required = false) Long customerId,
            @AuthenticationPrincipal AuthenticatedUser user) {
        service.cancel(jobId, user.userId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{jobId}/start")
    @PreAuthorize("hasRole('WORKER')")
    public JobResponse start(@PathVariable Long jobId, @RequestParam(required = false) Long workerId,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return service.start(jobId, requireWorkerId(user));
    }

    @PostMapping("/{jobId}/complete")
    @PreAuthorize("hasRole('WORKER')")
    public JobResponse complete(@PathVariable Long jobId, @RequestParam(required = false) Long workerId,
            @Valid @RequestBody CompleteJobRequest request, @AuthenticationPrincipal AuthenticatedUser user) {
        return service.complete(jobId, requireWorkerId(user), request);
    }

    @PutMapping("/{jobId}/not-solved")
    @PreAuthorize("hasRole('WORKER')")
    public JobResponse notSolved(@PathVariable Long jobId, @RequestParam(required = false) Long workerId,
            @Valid @RequestBody NotSolvedJobRequest request, @AuthenticationPrincipal AuthenticatedUser user) {
        return service.notSolved(jobId, requireWorkerId(user), request);
    }

    @PutMapping("/{jobId}/confirm")
    @PreAuthorize("hasRole('STUDENT')")
    public JobResponse confirm(@PathVariable Long jobId, @RequestParam(required = false) Long customerId,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return service.confirm(jobId, user.userId());
    }

    @PutMapping("/{jobId}/reject")
    @PreAuthorize("hasRole('STUDENT')")
    public JobResponse reject(@PathVariable Long jobId, @RequestParam(required = false) Long customerId,
            @Valid @RequestBody RejectCompletionRequest request, @AuthenticationPrincipal AuthenticatedUser user) {
        return service.reject(jobId, user.userId(), request);
    }

    private Long requireWorkerId(AuthenticatedUser user) {
        if (user.workerId() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Worker profile is required");
        }
        return user.workerId();
    }
}
