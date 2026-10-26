package com.example.hostel_managment.demo.worker.dto;

import com.example.hostel_managment.demo.worker.domain.PreferredLanguage;
import com.example.hostel_managment.demo.worker.domain.TradeCategory;
import com.example.hostel_managment.demo.worker.domain.Worker;

public record WorkerResponse(Long id, Long userId, String name, TradeCategory tradeCategory,
        int experienceYears, boolean available, String shiftStart, String shiftEnd,
        String workingDays, boolean hasSmartphone, boolean managedBySupervisor,
        PreferredLanguage preferredLanguage) {

    public static WorkerResponse from(Worker worker) {
        return new WorkerResponse(worker.getId(), worker.getUserId(), worker.getName(),
                worker.getTradeCategory(), worker.getExperienceYears(), worker.isAvailable(),
                worker.getShiftStart(), worker.getShiftEnd(), worker.getWorkingDays(),
                worker.isHasSmartphone(), worker.isManagedBySupervisor(),
                worker.getPreferredLanguage());
    }
}
