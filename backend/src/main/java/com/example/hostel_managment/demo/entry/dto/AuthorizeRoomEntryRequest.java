package com.example.hostel_managment.demo.entry.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AuthorizeRoomEntryRequest(
        @NotNull Long workerId,
        @NotNull Long authorizedByUserId,
        @NotBlank @Size(max = 500) String reason) {
}
