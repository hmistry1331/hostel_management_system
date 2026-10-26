package com.example.hostel_managment.demo.job.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.hostel_managment.demo.job.domain.JobCategory;
import com.example.hostel_managment.demo.job.domain.JobRequest;
import com.example.hostel_managment.demo.job.domain.JobStatus;
import com.example.hostel_managment.demo.job.domain.LocationType;
import com.example.hostel_managment.demo.job.domain.NotSolvedReason;
import com.example.hostel_managment.demo.job.dto.CompleteJobRequest;
import com.example.hostel_managment.demo.job.dto.NotSolvedJobRequest;
import com.example.hostel_managment.demo.job.dto.RejectCompletionRequest;
import com.example.hostel_managment.demo.job.repository.JobRequestRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class JobCompletionServiceTest {

    @Mock
    private JobRequestRepository repository;

    private JobRequestService service;

    @BeforeEach
    void setUp() {
        service = new JobRequestService(repository);
    }

    @Test
    void completesJobOnlyAfterBothProofPhotosAreProvided() {
        JobRequest job = assignedAndScheduledJob();
        job.start(3L);
        when(repository.findById(12L)).thenReturn(Optional.of(job));

        var response = service.complete(12L, 3L,
                new CompleteJobRequest("https://example.test/after.jpg",
                        "https://example.test/id.jpg"));

        assertEquals(JobStatus.COMPLETED, response.status());
        assertEquals("https://example.test/after.jpg", response.afterImageUrl());
        assertEquals("https://example.test/id.jpg", response.idCardPhotoUrl());
    }

    @Test
    void rejectsCompletionWhenWorkerIsNotAssigned() {
        JobRequest job = assignedAndScheduledJob();
        job.start(3L);
        when(repository.findById(12L)).thenReturn(Optional.of(job));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.complete(12L, 99L,
                        new CompleteJobRequest("https://example.test/after.jpg",
                                "https://example.test/id.jpg")));

        assertEquals(409, exception.getStatusCode().value());
    }

    @Test
    void marksJobNotSolvedWithReason() {
        JobRequest job = assignedAndScheduledJob();
        job.start(3L);
        when(repository.findById(12L)).thenReturn(Optional.of(job));

        var response = service.notSolved(12L, 3L,
                new NotSolvedJobRequest(NotSolvedReason.ROOM_LOCKED));

        assertEquals(JobStatus.NOT_SOLVED, response.status());
        assertEquals(NotSolvedReason.ROOM_LOCKED, response.notSolvedReason());
    }

    @Test
    void studentRejectionReopensCompletedJob() {
        JobRequest job = assignedAndScheduledJob();
        job.start(3L);
        job.complete(3L, "https://example.test/after.jpg", "https://example.test/id.jpg");
        when(repository.findById(12L)).thenReturn(Optional.of(job));

        var response = service.reject(12L, 7L,
                new RejectCompletionRequest("The leak is still present"));

        assertEquals(JobStatus.SUBMITTED, response.status());
        assertEquals("The leak is still present", response.rejectionReason());
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
