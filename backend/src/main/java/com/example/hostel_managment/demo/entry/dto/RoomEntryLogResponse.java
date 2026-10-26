package com.example.hostel_managment.demo.entry.dto;

import com.example.hostel_managment.demo.entry.domain.RoomEntryLog;
import java.time.Instant;

public record RoomEntryLogResponse(Long id, Long jobId, Long workerId, Long authorizedByUserId,
        String reason, Instant enteredAt, Instant exitedAt, Instant keysReturnedAt) {

    public static RoomEntryLogResponse from(RoomEntryLog log) {
        return new RoomEntryLogResponse(log.getId(), log.getJobId(), log.getWorkerId(),
                log.getAuthorizedByUserId(), log.getReason(), log.getEnteredAt(),
                log.getExitedAt(), log.getKeysReturnedAt());
    }
}
