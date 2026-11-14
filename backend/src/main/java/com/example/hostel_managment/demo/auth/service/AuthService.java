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
import com.example.hostel_managment.demo.worker.dto.CreateStaffRequest;
import com.example.hostel_managment.demo.worker.dto.WorkerResponse;
import com.example.hostel_managment.demo.slot.service.SlotGenerationService;
import java.time.LocalDate;
import java.time.Clock;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;

@Service
public class AuthService {
    private final AppUserRepository repository;
    private final WorkerRepository workerRepository;
    private final JwtTokenService tokenService;
    private final String universityEmailDomain;
    private final SlotGenerationService slotGenerationService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Clock clock;

    public AuthService(AppUserRepository repository, WorkerRepository workerRepository,
            JwtTokenService tokenService,
            @Value("${app.auth.university-email-domain:}") String universityEmailDomain,
            SlotGenerationService slotGenerationService, Clock clock) {
        this.repository = repository;
        this.workerRepository = workerRepository;
        this.tokenService = tokenService;
        this.universityEmailDomain = universityEmailDomain.trim().toLowerCase(Locale.ROOT);
        this.slotGenerationService = slotGenerationService;
        this.clock = clock;
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        String email = normalize(request.email());
        if (!universityEmailDomain.isBlank()
                && !email.endsWith("@" + universityEmailDomain)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Signup is restricted to university email addresses");
        }
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists");
        }
        AppUser user = repository.save(new AppUser(request.name().trim(), email,
                passwordEncoder.encode(request.password()), UserRole.STUDENT));
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

    @Transactional
    public WorkerResponse createStaff(CreateStaffRequest request) {
        String email = normalize(request.email());
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists");
        }
        AppUser user = repository.save(new AppUser(request.name().trim(), email,
                passwordEncoder.encode(request.password()), UserRole.WORKER));
        Worker worker = workerRepository.save(new Worker(user.getId(), user.getName(),
                request.tradeCategory(), request.experienceYears(), request.available(),
                request.shiftStart(), request.shiftEnd(), request.workingDays(),
                request.slotDurationMinutes(),
                request.hasSmartphone(), request.managedBySupervisor(), request.preferredLanguage()));
        LocalDate today = LocalDate.now(clock);
        slotGenerationService.generateForWorker(worker.getId(), today, today.plusDays(13));
        return WorkerResponse.from(worker);
    }

    private AuthResponse response(AppUser user) {
        Long workerId = workerRepository.findByUserId(user.getId()).map(Worker::getId).orElse(null);
        return new AuthResponse(tokenService.create(user, workerId), user.getId(), workerId,
                user.getName(), user.getEmail(), user.getRole());
    }

    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
