package com.example.hostel_managment.demo.worker.repository;

import com.example.hostel_managment.demo.worker.domain.Worker;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkerRepository extends JpaRepository<Worker, Long> {
    Optional<Worker> findByUserId(Long userId);

    List<Worker> findByAvailableTrueOrderByNameAsc();
}
