package com.example.hostel_managment.demo.worker.dto;

import com.example.hostel_managment.demo.worker.domain.PreferredLanguage;
import com.example.hostel_managment.demo.worker.domain.TradeCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateWorkerRequest(
        Long userId,
        @NotBlank @Size(max = 120) String name,
        @NotNull TradeCategory tradeCategory,
        @Min(0) int experienceYears,
        boolean available,
        @Size(max = 5) String shiftStart,
        @Size(max = 5) String shiftEnd,
        @Size(max = 50) String workingDays,
        @Min(15) int slotDurationMinutes,
        boolean hasSmartphone,
        boolean managedBySupervisor,
        @NotNull PreferredLanguage preferredLanguage) {
    public CreateWorkerRequest(Long userId, String name, TradeCategory tradeCategory,
            int experienceYears, boolean available, String shiftStart, String shiftEnd,
            String workingDays, boolean hasSmartphone, boolean managedBySupervisor,
            PreferredLanguage preferredLanguage) {
        this(userId, name, tradeCategory, experienceYears, available, shiftStart, shiftEnd,
                workingDays, 60, hasSmartphone, managedBySupervisor, preferredLanguage);
    }
}
