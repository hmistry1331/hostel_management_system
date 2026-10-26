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

@RestController
@RequestMapping("/api/admin/jobs")
public class RoomEntryLogController {

    private final RoomEntryLogService service;

    public RoomEntryLogController(RoomEntryLogService service) {
        this.service = service;
    }

    @PostMapping("/{jobId}/entry-log")
    public ResponseEntity<RoomEntryLogResponse> authorize(@PathVariable Long jobId,
            @Valid @RequestBody AuthorizeRoomEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.authorize(jobId, request));
    }

    @GetMapping("/{jobId}/entry-log")
    public List<RoomEntryLogResponse> findForJob(@PathVariable Long jobId) {
        return service.findForJob(jobId);
    }

    @PutMapping("/entry-log/{logId}/exit")
    public RoomEntryLogResponse markExited(@PathVariable Long logId) {
        return service.markExited(logId);
    }

    @PutMapping("/entry-log/{logId}/keys-returned")
    public RoomEntryLogResponse markKeysReturned(@PathVariable Long logId) {
        return service.markKeysReturned(logId);
    }
}
