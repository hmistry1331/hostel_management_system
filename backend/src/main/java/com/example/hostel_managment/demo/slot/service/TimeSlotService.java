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
import java.time.LocalTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TimeSlotService {

    private final TimeSlotRepository slotRepository;
    private final JobRequestRepository jobRepository;

    public TimeSlotService(TimeSlotRepository slotRepository, JobRequestRepository jobRepository) {
        this.slotRepository = slotRepository;
        this.jobRepository = jobRepository;
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
    public List<TimeSlotResponse> available(Long workerId, LocalDate date) {
        return slotRepository.findByWorkerIdAndSlotDateAndBookedFalseOrderByStartTime(workerId, date)
                .stream().map(TimeSlotResponse::from).toList();
    }

    @Transactional
    public TimeSlotResponse book(Long slotId, BookSlotRequest request) {
        JobRequest job = jobRepository.findById(request.jobId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Complaint not found"));
        if (job.getStatus() != JobStatus.ASSIGNED && job.getStatus() != JobStatus.NOT_SOLVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Only assigned complaints can book a time slot");
        }
        TimeSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Time slot not found"));
        if (!slot.getWorkerId().equals(job.getWorkerId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Slot does not belong to the assigned worker");
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
    public void cancel(Long slotId) {
        TimeSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Time slot not found"));
        slot.release();
    }
}
