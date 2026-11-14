package com.example.hostel_managment.demo.entry.service;

import com.example.hostel_managment.demo.entry.domain.RoomEntryLog;
import com.example.hostel_managment.demo.entry.dto.AuthorizeRoomEntryRequest;
import com.example.hostel_managment.demo.entry.dto.RoomEntryLogResponse;
import com.example.hostel_managment.demo.entry.repository.RoomEntryLogRepository;
import com.example.hostel_managment.demo.job.domain.JobRequest;
import com.example.hostel_managment.demo.job.domain.JobStatus;
import com.example.hostel_managment.demo.job.domain.LocationType;
import com.example.hostel_managment.demo.job.repository.JobRequestRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RoomEntryLogService {

    private final RoomEntryLogRepository logRepository;
    private final JobRequestRepository jobRepository;

    public RoomEntryLogService(RoomEntryLogRepository logRepository,
            JobRequestRepository jobRepository) {
        this.logRepository = logRepository;
        this.jobRepository = jobRepository;
    }

    @Transactional
    public RoomEntryLogResponse authorize(Long jobId, AuthorizeRoomEntryRequest request, Long authorizedByUserId) {
        JobRequest job = findJob(jobId);
        if (job.getUrgency() != com.example.hostel_managment.demo.job.domain.Urgency.URGENT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Room entry authorization is only available for urgent complaints");
        }

        if (job.getLocationType() != LocationType.ROOM) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Room entry authorization requires a room complaint");
        }
        if (job.getWorkerId() == null || !job.getWorkerId().equals(request.workerId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Worker is not assigned to this complaint");
        }
        if (job.getStatus() != JobStatus.SCHEDULED && job.getStatus() != JobStatus.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Complaint must be scheduled or in progress");
        }
        return RoomEntryLogResponse.from(logRepository.save(new RoomEntryLog(jobId,
                request.workerId(), authorizedByUserId, request.reason())));
    }

    public RoomEntryLogResponse authorize(Long jobId, AuthorizeRoomEntryRequest request) {
        return authorize(jobId, request, request.authorizedByUserId());
    }

    @Transactional(readOnly = true)
    public List<RoomEntryLogResponse> findForJob(Long jobId) {
        return logRepository.findByJobIdOrderByEnteredAtDesc(jobId).stream()
                .map(RoomEntryLogResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<RoomEntryLogResponse> findForWorker(Long jobId, Long workerId) {
        JobRequest job = findJob(jobId);
        if (workerId == null || !workerId.equals(job.getWorkerId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only the assigned worker can view this entry log");
        }
        return findForJob(jobId);
    }

    @Transactional
    public RoomEntryLogResponse markExited(Long logId, Long workerId) {
        RoomEntryLog log = findLog(logId);
        verifyWorker(log, workerId);
        try {
            log.markExited();
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage(), exception);
        }

        return RoomEntryLogResponse.from(log);
    }

    public RoomEntryLogResponse markExited(Long logId) {
        return markExited(logId, findLog(logId).getWorkerId());
    }

    @Transactional
    public RoomEntryLogResponse markKeysReturned(Long logId, Long workerId) {
        RoomEntryLog log = findLog(logId);
        verifyWorker(log, workerId);
        try {
            log.markKeysReturned();
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage(), exception);
        }

        return RoomEntryLogResponse.from(log);
    }

    public RoomEntryLogResponse markKeysReturned(Long logId) {
        return markKeysReturned(logId, findLog(logId).getWorkerId());
    }

    private JobRequest findJob(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Complaint not found"));
    }

    private RoomEntryLog findLog(Long logId) {
        return logRepository.findById(logId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Room entry log not found"));
    }

    private void verifyWorker(RoomEntryLog log, Long workerId) {
        if (!log.getWorkerId().equals(workerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only the assigned worker can update this entry log");
        }
    }
}
