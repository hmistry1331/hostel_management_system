package com.example.hostel_managment.demo.job.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hostel_managment.demo.job.domain.JobCategory;
import com.example.hostel_managment.demo.job.domain.JobRequest;
import com.example.hostel_managment.demo.job.domain.LocationType;
import com.example.hostel_managment.demo.job.domain.JobStatus;
import com.example.hostel_managment.demo.job.dto.CreateJobRequest;
import com.example.hostel_managment.demo.job.repository.JobRequestRepository;
import com.example.hostel_managment.demo.slot.service.TimeSlotService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class JobRequestServiceTest {

    @Mock
    private JobRequestRepository repository;

    @Mock
    private TimeSlotService timeSlotService;

    private JobRequestService service;

    @BeforeEach
    void setUp() {
        service = new JobRequestService(repository, timeSlotService);
    }

    @Test
    void createsRoomComplaintWithNormalUrgencyByDefault() {
        CreateJobRequest request = new CreateJobRequest(7L, "Broken fan", JobCategory.ELECTRICAL,
                LocationType.ROOM, "B", 2, "204", null, null, "9999999999",
                "https://example.test/before.jpg");
        when(repository.save(any(JobRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.create(request);

        assertEquals(7L, response.customerId());
        verify(repository).save(any(JobRequest.class));
    }

    @Test
    void rejectsRoomComplaintWithoutRoomNumber() {
        CreateJobRequest request = new CreateJobRequest(7L, "Broken fan", JobCategory.ELECTRICAL,
                LocationType.ROOM, "B", 2, null, null, null, null,
                "https://example.test/before.jpg");

        assertThrows(ResponseStatusException.class, () -> service.create(request));
    }

    @Test
    void onlyOwnerCanCancelComplaint() {
        JobRequest job = JobRequest.submitted(7L, "Broken fan", JobCategory.ELECTRICAL,
                LocationType.ROOM, "B", 2, "204", null, null, null,
                "https://example.test/before.jpg");
        when(repository.findByIdForUpdate(10L)).thenReturn(Optional.of(job));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.cancel(10L, 8L));

        assertEquals(403, exception.getStatusCode().value());
    }

    @Test
    void unscheduleReturnsScheduledComplaintToAssigned() {
        JobRequest job = JobRequest.submitted(7L, "Broken fan", JobCategory.ELECTRICAL,
                LocationType.ROOM, "B", 2, "204", null, null, null,
                "https://example.test/before.jpg");
        job.assignTo(3L);
        job.schedule(8L);

        job.unschedule();

        assertEquals(JobStatus.ASSIGNED, job.getStatus());
        assertEquals(null, job.getScheduledSlotId());
    }

    @Test
    void confirmationAndRejectionAreSingleUse() {
        JobRequest confirmed = JobRequest.submitted(7L, "Broken fan", JobCategory.ELECTRICAL,
                LocationType.ROOM, "B", 2, "204", null, null, null,
                "https://example.test/before.jpg");
        confirmed.assignTo(3L);
        confirmed.schedule(8L);
        confirmed.start(3L);
        confirmed.complete(3L, "https://example.test/after.jpg", "https://example.test/id.jpg");
        confirmed.confirmByStudent(7L);

        assertThrows(IllegalStateException.class, () -> confirmed.rejectByStudent(7L, "Still broken"));
    }

    @Test
    void studentConfirmationKeepsCompletedStatusForAdmin() {
        JobRequest job = assignedAndScheduledJob();
        job.start(3L);
        job.complete(3L, "https://example.test/after.jpg", "https://example.test/id.jpg");

        job.confirmByStudent(7L);

        assertEquals(JobStatus.COMPLETED, job.getStatus());
    }

    @Test
    void studentRejectionChangesStatusToNotSolvedForAdmin() {
        JobRequest job = assignedAndScheduledJob();
        job.start(3L);
        job.complete(3L, "https://example.test/after.jpg", "https://example.test/id.jpg");

        job.rejectByStudent(7L, "Still broken");

        assertEquals(JobStatus.NOT_SOLVED, job.getStatus());
    }

    private JobRequest assignedAndScheduledJob() {
        JobRequest job = JobRequest.submitted(7L, "Broken tap", JobCategory.PLUMBING,
                LocationType.ROOM, "B", 2, "204", null, null, null,
                "https://example.test/before.jpg");
        job.assignTo(3L);
        job.schedule(8L);
        return job;
    }
}
