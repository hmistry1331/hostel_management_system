package com.example.hostel_managment.demo.slot.dto;

import jakarta.validation.constraints.NotNull;

public record BookSlotRequest(@NotNull Long jobId) {
}
