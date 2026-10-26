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
    public RoomEntryLogResponse authorize(Long jobId, AuthorizeRoomEntryRequest request) {
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
                request.workerId(), request.authorizedByUserId(), request.reason())));
    }

    @Transactional(readOnly = true)
    public List<RoomEntryLogResponse> findForJob(Long jobId) {
        return logRepository.findByJobIdOrderByEnteredAtDesc(jobId).stream()
                .map(RoomEntryLogResponse::from).toList();
    }

    @Transactional
    public RoomEntryLogResponse markExited(Long logId) {
        RoomEntryLog log = findLog(logId);
        try {
            log.markExited();
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage(), exception);
        }
        return RoomEntryLogResponse.from(log);
    }

    @Transactional
    public RoomEntryLogResponse markKeysReturned(Long logId) {
        RoomEntryLog log = findLog(logId);
        try {
            log.markKeysReturned();
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage(), exception);
        }
        return RoomEntryLogResponse.from(log);
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
}
