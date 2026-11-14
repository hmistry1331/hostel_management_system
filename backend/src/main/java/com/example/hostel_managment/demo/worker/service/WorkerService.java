package com.example.hostel_managment.demo.worker.service;

import com.example.hostel_managment.demo.job.domain.JobRequest;
import com.example.hostel_managment.demo.job.dto.JobResponse;
import com.example.hostel_managment.demo.job.repository.JobRequestRepository;
import com.example.hostel_managment.demo.worker.domain.Worker;
import com.example.hostel_managment.demo.worker.dto.CreateWorkerRequest;
import com.example.hostel_managment.demo.worker.dto.WorkerResponse;
import com.example.hostel_managment.demo.worker.repository.WorkerRepository;
import com.example.hostel_managment.demo.slot.service.TimeSlotService;
import com.example.hostel_managment.demo.slot.service.SlotGenerationService;
import com.example.hostel_managment.demo.worker.dto.WorkerScheduleResponse;
import com.example.hostel_managment.demo.slot.repository.TimeSlotRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final JobRequestRepository jobRepository;
    private final TimeSlotService timeSlotService;
    private final SlotGenerationService slotGenerationService;
    private final TimeSlotRepository slotRepository;

    @Autowired
    public WorkerService(WorkerRepository workerRepository, JobRequestRepository jobRepository,
            TimeSlotService timeSlotService, SlotGenerationService slotGenerationService,
            TimeSlotRepository slotRepository) {
        this.workerRepository = workerRepository;
        this.jobRepository = jobRepository;
        this.timeSlotService = timeSlotService;
        this.slotGenerationService = slotGenerationService;
        this.slotRepository = slotRepository;
    }

    public WorkerService(WorkerRepository workerRepository, JobRequestRepository jobRepository,
            TimeSlotService timeSlotService) {
        this(workerRepository, jobRepository, timeSlotService, null, null);
    }

    @Transactional
    public WorkerResponse register(CreateWorkerRequest request) {
        Worker worker = new Worker(request.userId(), request.name(), request.tradeCategory(),
                request.experienceYears(), request.available(), request.shiftStart(),
                request.shiftEnd(), request.workingDays(), request.slotDurationMinutes(),
                request.hasSmartphone(),
                request.managedBySupervisor(), request.preferredLanguage());
        Worker saved = workerRepository.save(worker);
        if (slotGenerationService != null && saved.getId() != null) {
            slotGenerationService.generateForWorker(saved.getId(), LocalDate.now(), LocalDate.now().plusDays(13));
        }
        return WorkerResponse.from(saved);
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
            timeSlotService.releaseSlotForJob(jobId);
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage(), exception);
        }
        return JobResponse.from(job);
    }

    @Transactional(readOnly = true)
    public List<WorkerScheduleResponse> schedule(Long workerId, LocalDate date) {
        if (slotRepository == null) {
            return List.of();
        }
        List<WorkerScheduleResponse> schedule = slotRepository
                .findByWorkerIdAndSlotDateBetweenAndBookedTrueOrderBySlotDateAscStartTimeAsc(
                workerId, date, date).stream()
                .map(slot -> jobRepository.findByIdAndWorkerId(slot.getJobId(), workerId)
                        .map(job -> WorkerScheduleResponse.from(slot, job)).orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
        schedule.addAll(jobRepository
                .findByWorkerIdAndStatusInAndScheduledSlotIdIsNullOrderByCreatedAtDesc(
                        workerId, List.of(com.example.hostel_managment.demo.job.domain.JobStatus.ASSIGNED,
                                com.example.hostel_managment.demo.job.domain.JobStatus.NOT_SOLVED))
                .stream()
                .map(WorkerScheduleResponse::fromAssignedJob)
                .toList());
        return schedule;
    }

    @Transactional(readOnly = true)
    public List<WorkerScheduleResponse> supervisorSchedule(LocalDate date) {
        return workerRepository.findAll().stream()
                .flatMap(worker -> schedule(worker.getId(), date).stream()).toList();
    }
}
