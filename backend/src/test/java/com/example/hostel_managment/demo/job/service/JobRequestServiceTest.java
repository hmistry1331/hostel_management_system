package com.example.hostel_managment.demo.job.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hostel_managment.demo.job.domain.JobCategory;
import com.example.hostel_managment.demo.job.domain.JobRequest;
import com.example.hostel_managment.demo.job.domain.LocationType;
import com.example.hostel_managment.demo.job.dto.CreateJobRequest;
import com.example.hostel_managment.demo.job.repository.JobRequestRepository;
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

    private JobRequestService service;

    @BeforeEach
    void setUp() {
        service = new JobRequestService(repository);
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
        when(repository.findById(10L)).thenReturn(Optional.of(job));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.cancel(10L, 8L));

        assertEquals(403, exception.getStatusCode().value());
    }
}
