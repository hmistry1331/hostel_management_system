package com.example.hostel_managment.demo.slot.service;

import com.example.hostel_managment.demo.job.domain.JobRequest;
import com.example.hostel_managment.demo.job.domain.JobStatus;
import com.example.hostel_managment.demo.job.repository.JobRequestRepository;
import com.example.hostel_managment.demo.slot.domain.TimeSlot;
import com.example.hostel_managment.demo.slot.dto.BookSlotRequest;
import com.example.hostel_managment.demo.slot.dto.CreateSlotRequest;
import com.example.hostel_managment.demo.slot.dto.TimeSlotResponse;
import com.example.hostel_managment.demo.slot.repository.TimeSlotRepository;
import java.time.LocalDate;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class TimeSlotService {

    private final TimeSlotRepository slotRepository;
    private final JobRequestRepository jobRepository;
    private final Clock clock;

    @Autowired
    public TimeSlotService(TimeSlotRepository slotRepository, JobRequestRepository jobRepository, Clock clock) {
        this.slotRepository = slotRepository;
        this.jobRepository = jobRepository;
        this.clock = clock;
    }

    @Transactional
    public TimeSlotResponse create(CreateSlotRequest request) {
        if (!request.endTime().isAfter(request.startTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Slot end time must be after start time");
        }
        if (slotRepository.existsByWorkerIdAndSlotDateAndStartTime(
                request.workerId(), request.slotDate(), request.startTime())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A slot already exists for this worker and start time");
        }
        return TimeSlotResponse.from(slotRepository.save(new TimeSlot(request.workerId(),
                request.slotDate(), request.startTime(), request.endTime())));
    }

    @Transactional(readOnly = true)
    public List<TimeSlotResponse> available(Long jobId, LocalDate from, LocalDate to, Long customerId) {
        JobRequest job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found"));
        if (!job.getCustomerId().equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You may only view slots for your own complaint");
        }
        if (job.getWorkerId() == null) {
            return List.of();
        }
        LocalDate today = LocalDate.now(clock);
        LocalDate effectiveFrom = from.isBefore(today) ? today : from;
        if (to.isBefore(effectiveFrom)) {
            return List.of();
        }
        return slotRepository.findByWorkerIdAndSlotDateBetweenAndBookedFalseOrderBySlotDateAscStartTimeAsc(
                job.getWorkerId(), effectiveFrom, to).stream().map(TimeSlotResponse::from).toList();
    }

    @Transactional
    public TimeSlotResponse book(Long slotId, BookSlotRequest request, Long customerId) {
        JobRequest job = jobRepository.findByIdForUpdate(request.jobId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Complaint not found"));
        if (!job.getCustomerId().equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You may only book a slot for your own complaint");
        }
        if (job.getStatus() != JobStatus.ASSIGNED && job.getStatus() != JobStatus.NOT_SOLVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Only assigned complaints can book a time slot");
        }
        if (job.getScheduledSlotId() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Complaint already has a scheduled slot");
        }
        TimeSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Time slot not found"));
        if (!slot.getWorkerId().equals(job.getWorkerId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Slot does not belong to the assigned worker");
        }
        if (!slot.getSlotDate().atTime(slot.getStartTime())
                .isAfter(LocalDateTime.now(clock))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot book a slot in the past");
        }
        if (slotRepository.bookIfAvailable(slotId, request.jobId()) != 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Time slot is already booked");
        }
        slot.book(request.jobId());
        job.schedule(slotId);
        return TimeSlotResponse.from(slot);
    }

    @Transactional
    public void cancel(Long slotId, Long customerId) {
        TimeSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Time slot not found"));
        if (slot.getJobId() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Time slot is not booked");
        }
        JobRequest job = jobRepository.findByIdForUpdate(slot.getJobId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Complaint not found"));
        if (!job.getCustomerId().equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You may only cancel your own complaint slot");
        }
        if (job.getStatus() != JobStatus.SCHEDULED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Only scheduled complaints can cancel their slot");
        }
        if (!slot.getSlotDate().atTime(slot.getStartTime())
                .isAfter(LocalDateTime.now(clock).plusHours(2))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Slots can only be cancelled at least two hours before start");
        }
        job.unschedule();
        slot.release();
    }

    @Transactional
    public void releaseSlotForJob(Long jobId) {
        slotRepository.findByJobId(jobId).ifPresent(TimeSlot::release);
    }
}
