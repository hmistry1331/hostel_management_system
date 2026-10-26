package com.example.hostel_managment.demo.entry.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.hostel_managment.demo.entry.domain.RoomEntryLog;
import com.example.hostel_managment.demo.entry.dto.AuthorizeRoomEntryRequest;
import com.example.hostel_managment.demo.entry.repository.RoomEntryLogRepository;
import com.example.hostel_managment.demo.job.domain.JobCategory;
import com.example.hostel_managment.demo.job.domain.JobRequest;
import com.example.hostel_managment.demo.job.domain.LocationType;
import com.example.hostel_managment.demo.job.domain.Urgency;
import com.example.hostel_managment.demo.job.repository.JobRequestRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class RoomEntryLogServiceTest {

    @Mock
    private RoomEntryLogRepository logRepository;

    @Mock
    private JobRequestRepository jobRepository;

    private RoomEntryLogService service;

    @BeforeEach
    void setUp() {
        service = new RoomEntryLogService(logRepository, jobRepository);
    }

    @Test
    void authorizesUrgentRoomEntryForAssignedWorker() {
        JobRequest job = urgentScheduledJob();
        when(jobRepository.findById(12L)).thenReturn(Optional.of(job));
        when(logRepository.save(any(RoomEntryLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.authorize(12L,
                new AuthorizeRoomEntryRequest(3L, 99L, "Urgent repair; supervisor present"));

        assertEquals(12L, response.jobId());
        assertEquals(3L, response.workerId());
        assertEquals(99L, response.authorizedByUserId());
        // enteredAt is normally assigned by JPA; the domain object remains auditable after save.
        assertEquals("Urgent repair; supervisor present", response.reason());
    }

    @Test
    void rejectsEntryAuthorizationForNormalComplaint() {
        JobRequest job = JobRequest.submitted(7L, "Broken tap", JobCategory.PLUMBING,
                LocationType.ROOM, "B", 2, "204", null, Urgency.NORMAL, null,
                "https://example.test/before.jpg");
        job.assignTo(3L);
        job.schedule(8L);
        when(jobRepository.findById(12L)).thenReturn(Optional.of(job));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.authorize(12L,
                        new AuthorizeRoomEntryRequest(3L, 99L, "Supervisor present")));

        assertEquals(409, exception.getStatusCode().value());
    }

    @Test
    void requiresExitBeforeKeysCanBeReturned() {
        RoomEntryLog log = new RoomEntryLog(12L, 3L, 99L, "Supervisor present");
        when(logRepository.findById(4L)).thenReturn(Optional.of(log));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.markKeysReturned(4L));

        assertEquals(409, exception.getStatusCode().value());
    }

    private JobRequest urgentScheduledJob() {
        JobRequest job = JobRequest.submitted(7L, "Live electrical wire", JobCategory.ELECTRICAL,
                LocationType.ROOM, "B", 2, "204", null, Urgency.URGENT, null,
                "https://example.test/before.jpg");
        job.assignTo(3L);
        job.schedule(8L);
        return job;
    }
}
