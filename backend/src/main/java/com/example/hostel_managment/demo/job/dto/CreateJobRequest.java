package com.example.hostel_managment.demo.job.dto;

import com.example.hostel_managment.demo.job.domain.JobCategory;
import com.example.hostel_managment.demo.job.domain.LocationType;
import com.example.hostel_managment.demo.job.domain.Urgency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateJobRequest(
        @NotNull Long customerId,
        @NotBlank @Size(max = 2000) String problemDescription,
        @NotNull JobCategory category,
        @NotNull LocationType locationType,
        @NotBlank @Size(max = 100) String block,
        Integer floor,
        @Size(max = 30) String roomNumber,
        @Size(max = 120) String areaName,
        Urgency urgency,
        @Size(max = 30) String contactNumber,
        @NotBlank @Size(max = 500) String beforeImageUrl) {
}
