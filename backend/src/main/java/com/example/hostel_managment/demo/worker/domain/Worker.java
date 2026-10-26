package com.example.hostel_managment.demo.worker.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "workers")
@Getter
@NoArgsConstructor
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true)
    private Long userId;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_category", nullable = false, length = 30)
    private TradeCategory tradeCategory;

    @Column(name = "experience_years", nullable = false)
    private int experienceYears;

    @Column(nullable = false)
    private boolean available;

    @Column(name = "shift_start", length = 5)
    private String shiftStart;

    @Column(name = "shift_end", length = 5)
    private String shiftEnd;

    @Column(name = "working_days", length = 50)
    private String workingDays;

    @Column(name = "has_smartphone", nullable = false)
    private boolean hasSmartphone;

    @Column(name = "managed_by_supervisor", nullable = false)
    private boolean managedBySupervisor;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_language", nullable = false, length = 20)
    private PreferredLanguage preferredLanguage;

    public Worker(Long userId, String name, TradeCategory tradeCategory, int experienceYears,
            boolean available, String shiftStart, String shiftEnd, String workingDays,
            boolean hasSmartphone, boolean managedBySupervisor,
            PreferredLanguage preferredLanguage) {
        this.userId = userId;
        this.name = name;
        this.tradeCategory = tradeCategory;
        this.experienceYears = experienceYears;
        this.available = available;
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
        this.workingDays = workingDays;
        this.hasSmartphone = hasSmartphone;
        this.managedBySupervisor = managedBySupervisor;
        this.preferredLanguage = preferredLanguage;
    }
}
