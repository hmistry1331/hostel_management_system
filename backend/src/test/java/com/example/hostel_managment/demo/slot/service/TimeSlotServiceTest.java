package com.example.hostel_managment.demo.slot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hostel_managment.demo.job.domain.JobCategory;
import com.example.hostel_managment.demo.job.domain.JobRequest;
import com.example.hostel_managment.demo.job.domain.LocationType;
import com.example.hostel_managment.demo.job.domain.JobStatus;
import com.example.hostel_managment.demo.job.repository.JobRequestRepository;
import com.example.hostel_managment.demo.slot.domain.TimeSlot;
import com.example.hostel_managment.demo.slot.dto.BookSlotRequest;
import com.example.hostel_managment.demo.slot.repository.TimeSlotRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class TimeSlotServiceTest {

    @Mock
    private TimeSlotRepository slotRepository;

    @Mock
    private JobRequestRepository jobRepository;

    private TimeSlotService service;

    @BeforeEach
    void setUp() {
        service = new TimeSlotService(slotRepository, jobRepository);
    }

    @Test
    void booksAvailableSlotForAssignedJob() {
        JobRequest job = submittedJob();
        job.assignTo(3L);
        TimeSlot slot = new TimeSlot(3L, LocalDate.of(2026, 10, 26),
                LocalTime.of(14, 0), LocalTime.of(15, 0));
        when(jobRepository.findById(12L)).thenReturn(Optional.of(job));
        when(slotRepository.findById(8L)).thenReturn(Optional.of(slot));
        when(slotRepository.bookIfAvailable(8L, 12L)).thenReturn(1);

        var response = service.book(8L, new BookSlotRequest(12L));

        assertEquals(true, response.booked());
        assertEquals(12L, response.jobId());
        assertEquals(JobStatus.SCHEDULED, job.getStatus());
        verify(slotRepository).bookIfAvailable(8L, 12L);
    }

    @Test
    void rejectsAlreadyBookedSlot() {
        JobRequest job = submittedJob();
        job.assignTo(3L);
        TimeSlot slot = new TimeSlot(3L, LocalDate.of(2026, 10, 26),
                LocalTime.of(14, 0), LocalTime.of(15, 0));
        when(jobRepository.findById(12L)).thenReturn(Optional.of(job));
        when(slotRepository.findById(8L)).thenReturn(Optional.of(slot));
        when(slotRepository.bookIfAvailable(8L, 12L)).thenReturn(0);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.book(8L, new BookSlotRequest(12L)));

        assertEquals(409, exception.getStatusCode().value());
    }

    private JobRequest submittedJob() {
        return JobRequest.submitted(7L, "Broken fan", JobCategory.ELECTRICAL,
                LocationType.ROOM, "B", 2, "204", null, null, null,
                "https://example.test/before.jpg");
    }
}
