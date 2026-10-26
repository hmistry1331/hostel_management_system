package com.example.hostel_managment.demo.job.repository;

import com.example.hostel_managment.demo.job.domain.JobCategory;
import com.example.hostel_managment.demo.job.domain.JobRequest;
import com.example.hostel_managment.demo.job.domain.JobStatus;
import com.example.hostel_managment.demo.job.domain.LocationType;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRequestRepository extends JpaRepository<JobRequest, Long> {

    List<JobRequest> findAllByOrderByCreatedAtDesc();

    List<JobRequest> findByStatusInOrderByCreatedAtDesc(Collection<JobStatus> statuses);

    List<JobRequest> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<JobRequest> findByBlockAndFloorAndCategoryAndLocationTypeAndStatusInOrderByUrgencyDescCreatedAtAsc(
            String block, Integer floor, JobCategory category, LocationType locationType,
            Collection<JobStatus> statuses);
}
