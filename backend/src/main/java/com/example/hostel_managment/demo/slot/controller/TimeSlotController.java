package com.example.hostel_managment.demo.slot.controller;

import com.example.hostel_managment.demo.slot.dto.BookSlotRequest;
import com.example.hostel_managment.demo.slot.dto.CreateSlotRequest;
import com.example.hostel_managment.demo.slot.dto.TimeSlotResponse;
import com.example.hostel_managment.demo.slot.service.TimeSlotService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/slots")
public class TimeSlotController {

    private final TimeSlotService service;

    public TimeSlotController(TimeSlotService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TimeSlotResponse> create(@Valid @RequestBody CreateSlotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('STUDENT', 'WORKER', 'ADMIN')")
    public List<TimeSlotResponse> available(@RequestParam Long workerId,
            @RequestParam LocalDate date) {
        return service.available(workerId, date);
    }

    @PostMapping("/{slotId}/book")
    @PreAuthorize("hasRole('STUDENT')")
    public TimeSlotResponse book(@PathVariable Long slotId,
            @Valid @RequestBody BookSlotRequest request) {
        return service.book(slotId, request);
    }

    @PutMapping("/{slotId}/cancel")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> cancel(@PathVariable Long slotId) {
        service.cancel(slotId);
        return ResponseEntity.noContent().build();
    }
}
