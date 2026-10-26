package com.example.hostel_managment.demo.worker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hostel_managment.demo.job.repository.JobRequestRepository;
import com.example.hostel_managment.demo.worker.domain.PreferredLanguage;
import com.example.hostel_managment.demo.worker.domain.TradeCategory;
import com.example.hostel_managment.demo.worker.domain.Worker;
import com.example.hostel_managment.demo.worker.dto.CreateWorkerRequest;
import com.example.hostel_managment.demo.worker.repository.WorkerRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class WorkerServiceTest {

    @Mock
    private WorkerRepository workerRepository;

    @Mock
    private JobRequestRepository jobRepository;

    private WorkerService service;

    @BeforeEach
    void setUp() {
        service = new WorkerService(workerRepository, jobRepository);
    }

    @Test
    void registersWorkerForSupervisorManagedStaff() {
        CreateWorkerRequest request = new CreateWorkerRequest(null, "Ramesh Patel",
                TradeCategory.ELECTRICAL, 5, true, "10:00", "19:00", "MON,TUE,WED",
                false, true, PreferredLanguage.GUJARATI);
        when(workerRepository.save(any(Worker.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.register(request);

        assertEquals("Ramesh Patel", response.name());
        assertEquals(TradeCategory.ELECTRICAL, response.tradeCategory());
        assertEquals(PreferredLanguage.GUJARATI, response.preferredLanguage());
        assertEquals(true, response.managedBySupervisor());
        verify(workerRepository).save(any(Worker.class));
    }

    @Test
    void rejectsAssignmentToUnavailableWorker() {
        Worker worker = new Worker(null, "Ramesh Patel", TradeCategory.ELECTRICAL, 5,
                false, "10:00", "19:00", "MON,TUE", true, false,
                PreferredLanguage.ENGLISH);
        when(workerRepository.findById(4L)).thenReturn(Optional.of(worker));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.assign(10L, 4L));

        assertEquals(409, exception.getStatusCode().value());
    }
}
