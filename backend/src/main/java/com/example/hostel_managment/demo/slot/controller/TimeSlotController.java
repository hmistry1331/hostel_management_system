package com.example.hostel_managment.demo.slot.controller;

import com.example.hostel_managment.demo.slot.dto.BookSlotRequest;
import com.example.hostel_managment.demo.slot.dto.CreateSlotRequest;
import com.example.hostel_managment.demo.slot.dto.TimeSlotResponse;
import com.example.hostel_managment.demo.slot.service.TimeSlotService;
import jakarta.validation.Valid;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.hostel_managment.demo.auth.security.AuthenticatedUser;

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
    @PreAuthorize("hasRole('STUDENT')")
    public List<TimeSlotResponse> available(@RequestParam Long jobId,
            @RequestParam java.time.LocalDate from, @RequestParam java.time.LocalDate to,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return service.available(jobId, from, to, user.userId());
    }

    @PostMapping("/{slotId}/book")
    @PreAuthorize("hasRole('STUDENT')")
    public TimeSlotResponse book(@PathVariable Long slotId,
            @Valid @RequestBody BookSlotRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return service.book(slotId, request, user.userId());
    }

    @PutMapping("/{slotId}/cancel")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> cancel(@PathVariable Long slotId,
            @AuthenticationPrincipal AuthenticatedUser user) {
        service.cancel(slotId, user.userId());
        return ResponseEntity.noContent().build();
    }
}
