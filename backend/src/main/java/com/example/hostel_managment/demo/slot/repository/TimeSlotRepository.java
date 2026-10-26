package com.example.hostel_managment.demo.slot.repository;

import com.example.hostel_managment.demo.slot.domain.TimeSlot;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    List<TimeSlot> findByWorkerIdAndSlotDateAndBookedFalseOrderByStartTime(
            Long workerId, LocalDate slotDate);

    Optional<TimeSlot> findByIdAndBookedFalse(Long id);

    @Modifying
    @Query("""
            update TimeSlot slot
               set slot.booked = true, slot.jobId = :jobId
             where slot.id = :slotId and slot.booked = false
            """)
    int bookIfAvailable(@Param("slotId") Long slotId, @Param("jobId") Long jobId);

    boolean existsByWorkerIdAndSlotDateAndStartTime(
            Long workerId, LocalDate slotDate, LocalTime startTime);
}
