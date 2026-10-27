package com.example.hostel_managment.demo.auth.controller;

import com.example.hostel_managment.demo.auth.dto.AuthResponse;
import com.example.hostel_managment.demo.auth.dto.LoginRequest;
import com.example.hostel_managment.demo.auth.dto.SignupRequest;
import com.example.hostel_managment.demo.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.signup(request));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return service.login(request);
    }
}
