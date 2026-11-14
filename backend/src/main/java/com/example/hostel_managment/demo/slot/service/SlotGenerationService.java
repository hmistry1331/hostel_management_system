package com.example.hostel_managment.demo.slot.service;

import com.example.hostel_managment.demo.slot.domain.TimeSlot;
import com.example.hostel_managment.demo.slot.repository.TimeSlotRepository;
import com.example.hostel_managment.demo.worker.domain.Worker;
import com.example.hostel_managment.demo.worker.repository.WorkerRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SlotGenerationService {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private final WorkerRepository workerRepository;
    private final TimeSlotRepository slotRepository;
    private final Clock clock;

    public SlotGenerationService(WorkerRepository workerRepository, TimeSlotRepository slotRepository, Clock clock) {
        this.workerRepository = workerRepository;
        this.slotRepository = slotRepository;
        this.clock = clock;
    }

    @Transactional
    public int generateForWorker(Long workerId, LocalDate from, LocalDate to) {
        if (to.isBefore(from)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot range is invalid");
        }
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Worker not found"));
        if (worker.getShiftStart() == null || worker.getShiftEnd() == null
                || worker.getWorkingDays() == null || worker.getSlotDurationMinutes() <= 0) {
            return 0;
        }
        LocalTime shiftStart;
        LocalTime shiftEnd;
        try {
            shiftStart = LocalTime.parse(worker.getShiftStart().trim(), TIME_FORMAT);
            shiftEnd = LocalTime.parse(worker.getShiftEnd().trim(), TIME_FORMAT);
        } catch (java.time.format.DateTimeParseException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "shiftStart and shiftEnd must use HH:mm format");
        }
        if (!shiftStart.isBefore(shiftEnd)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "shiftStart must be earlier than shiftEnd");
        }
        Set<DayOfWeek> workingDays = parseDays(worker.getWorkingDays());
        int generated = 0;
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            if (!workingDays.contains(date.getDayOfWeek())) {
                continue;
            }
            for (LocalTime start = shiftStart;
                    !start.plusMinutes(worker.getSlotDurationMinutes()).isAfter(shiftEnd);
                    start = start.plusMinutes(worker.getSlotDurationMinutes())) {
                if (!slotRepository.existsByWorkerIdAndSlotDateAndStartTime(workerId, date, start)) {
                    slotRepository.save(new TimeSlot(workerId, date, start,
                            start.plusMinutes(worker.getSlotDurationMinutes())));
                    generated++;
                }
            }
        }
        return generated;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void generateUpcomingSlots() {
        LocalDate from = LocalDate.now(clock);
        LocalDate to = from.plusDays(13);
        workerRepository.findAll().forEach(worker -> generateForWorker(worker.getId(), from, to));
    }

    private Set<DayOfWeek> parseDays(String value) {
        EnumSet<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);
        Arrays.stream(value.split(",")).map(String::trim).map(String::toUpperCase).forEach(day -> {
            try {
                days.add(switch (day) {
                    case "MON", "MONDAY" -> DayOfWeek.MONDAY;
                    case "TUE", "TUESDAY" -> DayOfWeek.TUESDAY;
                    case "WED", "WEDNESDAY" -> DayOfWeek.WEDNESDAY;
                    case "THU", "THURSDAY" -> DayOfWeek.THURSDAY;
                    case "FRI", "FRIDAY" -> DayOfWeek.FRIDAY;
                    case "SAT", "SATURDAY" -> DayOfWeek.SATURDAY;
                    case "SUN", "SUNDAY" -> DayOfWeek.SUNDAY;
                    default -> throw new IllegalArgumentException();
                });
            } catch (IllegalArgumentException exception) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "workingDays must contain MON through SUN");
            }
        });
        return days;
    }
}
