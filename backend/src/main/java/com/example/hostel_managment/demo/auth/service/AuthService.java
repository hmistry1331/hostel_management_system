package com.example.hostel_managment.demo.auth.service;

import com.example.hostel_managment.demo.auth.domain.AppUser;
import com.example.hostel_managment.demo.auth.domain.UserRole;
import com.example.hostel_managment.demo.auth.dto.AuthResponse;
import com.example.hostel_managment.demo.auth.dto.LoginRequest;
import com.example.hostel_managment.demo.auth.dto.SignupRequest;
import com.example.hostel_managment.demo.auth.repository.AppUserRepository;
import com.example.hostel_managment.demo.auth.security.JwtTokenService;
import com.example.hostel_managment.demo.worker.domain.PreferredLanguage;
import com.example.hostel_managment.demo.worker.domain.TradeCategory;
import com.example.hostel_managment.demo.worker.domain.Worker;
import com.example.hostel_managment.demo.worker.repository.WorkerRepository;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final AppUserRepository repository;
    private final WorkerRepository workerRepository;
    private final JwtTokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(AppUserRepository repository, WorkerRepository workerRepository,
            JwtTokenService tokenService) {
        this.repository = repository;
        this.workerRepository = workerRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (request.role() == UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin accounts cannot be created through signup");
        }
        String email = normalize(request.email());
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists");
        }
        AppUser user = repository.save(new AppUser(request.name().trim(), email,
                passwordEncoder.encode(request.password()), request.role()));
        if (request.role() == UserRole.WORKER) {
            workerRepository.save(new Worker(user.getId(), user.getName(), TradeCategory.GENERAL,
                    0, true, null, null, null, true, false, PreferredLanguage.ENGLISH));
        }
        return response(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        AppUser user = repository.findByEmailIgnoreCase(normalize(request.email()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        return response(user);
    }

    private AuthResponse response(AppUser user) {
        Long workerId = workerRepository.findByUserId(user.getId()).map(Worker::getId).orElse(null);
        return new AuthResponse(tokenService.create(user, workerId), user.getId(), workerId,
                user.getName(), user.getRole());
    }

    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
