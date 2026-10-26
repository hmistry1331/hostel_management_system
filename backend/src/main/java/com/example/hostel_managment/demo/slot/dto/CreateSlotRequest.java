package com.example.hostel_managment.demo.slot.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateSlotRequest(
        @NotNull Long workerId,
        @NotNull LocalDate slotDate,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime) {
}
