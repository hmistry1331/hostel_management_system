package com.example.hostel_managment.demo.worker.controller;

import com.example.hostel_managment.demo.job.dto.JobResponse;
import com.example.hostel_managment.demo.worker.dto.CreateWorkerRequest;
import com.example.hostel_managment.demo.worker.dto.WorkerResponse;
import com.example.hostel_managment.demo.worker.service.WorkerService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class WorkerController {

    private final WorkerService service;

    public WorkerController(WorkerService service) {
        this.service = service;
    }

    @PostMapping("/admin/workers")
    public ResponseEntity<WorkerResponse> register(@Valid @RequestBody CreateWorkerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(request));
    }

    @GetMapping("/admin/workers/available")
    public List<WorkerResponse> availableWorkers() {
        return service.availableWorkers();
    }

    @PostMapping("/admin/jobs/{jobId}/assign")
    public JobResponse assign(@PathVariable Long jobId, @RequestParam Long workerId) {
        return service.assign(jobId, workerId);
    }

    @PostMapping("/worker/jobs/{jobId}/accept")
    public JobResponse accept(@PathVariable Long jobId, @RequestParam Long workerId) {
        return service.assign(jobId, workerId);
    }
}
