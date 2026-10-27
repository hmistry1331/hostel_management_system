package com.example.hostel_managment.demo.auth.security;

import com.example.hostel_managment.demo.auth.domain.UserRole;

public record AuthenticatedUser(Long userId, Long workerId, UserRole role, String name) {
}
