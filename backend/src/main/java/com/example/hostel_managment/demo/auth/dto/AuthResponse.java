package com.example.hostel_managment.demo.auth.dto;

import com.example.hostel_managment.demo.auth.domain.UserRole;

public record AuthResponse(String token, Long userId, Long workerId, String name, UserRole role) {
}
