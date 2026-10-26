package com.example.hostel_managment.demo.job.dto;

import com.example.hostel_managment.demo.job.domain.NotSolvedReason;
import jakarta.validation.constraints.NotNull;

public record NotSolvedJobRequest(@NotNull NotSolvedReason reason) {
}
