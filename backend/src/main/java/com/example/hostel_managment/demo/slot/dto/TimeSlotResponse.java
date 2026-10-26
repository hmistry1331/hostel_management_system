package com.example.hostel_managment.demo.slot.dto;

import com.example.hostel_managment.demo.slot.domain.TimeSlot;
import java.time.LocalDate;
import java.time.LocalTime;

public record TimeSlotResponse(Long id, Long workerId, LocalDate slotDate, LocalTime startTime,
        LocalTime endTime, boolean booked, Long jobId) {

    public static TimeSlotResponse from(TimeSlot slot) {
        return new TimeSlotResponse(slot.getId(), slot.getWorkerId(), slot.getSlotDate(),
                slot.getStartTime(), slot.getEndTime(), slot.isBooked(), slot.getJobId());
    }
}
