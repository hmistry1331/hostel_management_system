package com.example.hostel_managment.demo.job.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectCompletionRequest(@NotBlank @Size(max = 1000) String reason) {
}
