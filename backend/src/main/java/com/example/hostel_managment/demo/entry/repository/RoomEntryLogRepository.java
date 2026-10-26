package com.example.hostel_managment.demo.entry.repository;

import com.example.hostel_managment.demo.entry.domain.RoomEntryLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomEntryLogRepository extends JpaRepository<RoomEntryLog, Long> {

    List<RoomEntryLog> findByJobIdOrderByEnteredAtDesc(Long jobId);
}
