package com.example.hostel_managment.demo.job.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompleteJobRequest(
        @NotBlank @Size(max = 500) String afterImageUrl,
        @NotBlank @Size(max = 500) String idCardPhotoUrl) {
}
