package com.example.hostel_managment.demo.job.controller;

import com.example.hostel_managment.demo.job.domain.JobCategory;
import com.example.hostel_managment.demo.job.domain.LocationType;
import com.example.hostel_managment.demo.job.dto.CreateJobRequest;
import com.example.hostel_managment.demo.job.dto.CompleteJobRequest;
import com.example.hostel_managment.demo.job.dto.JobResponse;
import com.example.hostel_managment.demo.job.dto.NotSolvedJobRequest;
import com.example.hostel_managment.demo.job.dto.RejectCompletionRequest;
import com.example.hostel_managment.demo.job.service.JobRequestService;
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
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class JobRequestController {

    private final JobRequestService service;

    public JobRequestController(JobRequestService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<JobResponse> create(@Valid @RequestBody CreateJobRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/customer/{customerId}")
    public List<JobResponse> customerJobs(@PathVariable Long customerId) {
        return service.findCustomerJobs(customerId);
    }

    @GetMapping("/admin")
    public List<JobResponse> adminJobs() {
        return service.findAllJobs();
    }

    @GetMapping("/worker")
    public List<JobResponse> workerJobs() {
        return service.findWorkerJobs();
    }

    @GetMapping("/similar")
    public List<JobResponse> similar(@RequestParam String block, @RequestParam Integer floor,
            @RequestParam JobCategory category, @RequestParam LocationType locationType) {
        return service.findSimilar(block, floor, category, locationType);
    }

    @PutMapping("/{jobId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long jobId, @RequestParam Long customerId) {
        service.cancel(jobId, customerId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{jobId}/start")
    public JobResponse start(@PathVariable Long jobId, @RequestParam Long workerId) {
        return service.start(jobId, workerId);
    }

    @PostMapping("/{jobId}/complete")
    public JobResponse complete(@PathVariable Long jobId, @RequestParam Long workerId,
            @Valid @RequestBody CompleteJobRequest request) {
        return service.complete(jobId, workerId, request);
    }

    @PutMapping("/{jobId}/not-solved")
    public JobResponse notSolved(@PathVariable Long jobId, @RequestParam Long workerId,
            @Valid @RequestBody NotSolvedJobRequest request) {
        return service.notSolved(jobId, workerId, request);
    }

    @PutMapping("/{jobId}/confirm")
    public JobResponse confirm(@PathVariable Long jobId, @RequestParam Long customerId) {
        return service.confirm(jobId, customerId);
    }

    @PutMapping("/{jobId}/reject")
    public JobResponse reject(@PathVariable Long jobId, @RequestParam Long customerId,
            @Valid @RequestBody RejectCompletionRequest request) {
        return service.reject(jobId, customerId, request);
    }
}
