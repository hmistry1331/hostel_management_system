package com.example.hostel_managment.demo.job.repository;

import com.example.hostel_managment.demo.job.domain.JobCategory;
import com.example.hostel_managment.demo.job.domain.JobRequest;
import com.example.hostel_managment.demo.job.domain.JobStatus;
import com.example.hostel_managment.demo.job.domain.LocationType;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobRequestRepository extends JpaRepository<JobRequest, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select job from JobRequest job where job.id = :id")
    java.util.Optional<JobRequest> findByIdForUpdate(@Param("id") Long id);

    List<JobRequest> findAllByOrderByCreatedAtDesc();

    List<JobRequest> findByStatusInOrderByCreatedAtDesc(Collection<JobStatus> statuses);

    List<JobRequest> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    java.util.Optional<JobRequest> findByIdAndWorkerId(Long id, Long workerId);

    List<JobRequest> findByWorkerIdAndStatusInOrderByCreatedAtDesc(
            Long workerId, Collection<JobStatus> statuses);

    List<JobRequest> findByWorkerIdAndStatusInAndScheduledSlotIdIsNullOrderByCreatedAtDesc(
            Long workerId, Collection<JobStatus> statuses);

    List<JobRequest> findByBlockAndFloorAndCategoryAndLocationTypeAndStatusInOrderByUrgencyDescCreatedAtAsc(
            String block, Integer floor, JobCategory category, LocationType locationType,
            Collection<JobStatus> statuses);
}
