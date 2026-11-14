package com.example.hostel_managment.demo.entry.controller;

import com.example.hostel_managment.demo.entry.dto.AuthorizeRoomEntryRequest;
import com.example.hostel_managment.demo.entry.dto.RoomEntryLogResponse;
import com.example.hostel_managment.demo.entry.service.RoomEntryLogService;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.hostel_managment.demo.auth.security.AuthenticatedUser;
import com.example.hostel_managment.demo.auth.domain.UserRole;

@RestController
@RequestMapping("/api/admin/jobs")
public class RoomEntryLogController {

    private final RoomEntryLogService service;

    public RoomEntryLogController(RoomEntryLogService service) {
        this.service = service;
    }

    @PostMapping("/{jobId}/entry-log")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomEntryLogResponse> authorize(@PathVariable Long jobId,
            @Valid @RequestBody AuthorizeRoomEntryRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.authorize(jobId, request, user.userId()));
    }

    @GetMapping("/{jobId}/entry-log")
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    public List<RoomEntryLogResponse> findForJob(@PathVariable Long jobId,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return user.role() == UserRole.WORKER
                ? service.findForWorker(jobId, user.workerId())
                : service.findForJob(jobId);
    }

    @PutMapping("/entry-log/{logId}/exit")
    @PreAuthorize("hasRole('WORKER')")
    public RoomEntryLogResponse markExited(@PathVariable Long logId,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return service.markExited(logId, user.workerId());
    }

    @PutMapping("/entry-log/{logId}/keys-returned")
    @PreAuthorize("hasRole('WORKER')")
    public RoomEntryLogResponse markKeysReturned(@PathVariable Long logId,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return service.markKeysReturned(logId, user.workerId());
    }
}
