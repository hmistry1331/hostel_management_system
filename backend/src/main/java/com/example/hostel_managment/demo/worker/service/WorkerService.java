package com.example.hostel_managment.demo.worker.service;

import com.example.hostel_managment.demo.job.domain.JobRequest;
import com.example.hostel_managment.demo.job.dto.JobResponse;
import com.example.hostel_managment.demo.job.repository.JobRequestRepository;
import com.example.hostel_managment.demo.worker.domain.Worker;
import com.example.hostel_managment.demo.worker.dto.CreateWorkerRequest;
import com.example.hostel_managment.demo.worker.dto.WorkerResponse;
import com.example.hostel_managment.demo.worker.repository.WorkerRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final JobRequestRepository jobRepository;

    public WorkerService(WorkerRepository workerRepository, JobRequestRepository jobRepository) {
        this.workerRepository = workerRepository;
        this.jobRepository = jobRepository;
    }

    @Transactional
    public WorkerResponse register(CreateWorkerRequest request) {
        Worker worker = new Worker(request.userId(), request.name(), request.tradeCategory(),
                request.experienceYears(), request.available(), request.shiftStart(),
                request.shiftEnd(), request.workingDays(), request.hasSmartphone(),
                request.managedBySupervisor(), request.preferredLanguage());
        return WorkerResponse.from(workerRepository.save(worker));
    }

    @Transactional(readOnly = true)
    public List<WorkerResponse> availableWorkers() {
        return workerRepository.findByAvailableTrueOrderByNameAsc().stream()
                .map(WorkerResponse::from).toList();
    }

    @Transactional
    public JobResponse assign(Long jobId, Long workerId) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Worker not found"));
        if (!worker.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Worker is not available");
        }
        JobRequest job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Complaint not found"));
        try {
            job.assignTo(worker.getId());
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage(), exception);
        }
        return JobResponse.from(job);
    }
}
