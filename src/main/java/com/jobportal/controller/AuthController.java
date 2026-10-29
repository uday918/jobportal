package com.jobportal.controller;

import com.jobportal.dto.LoginRequest;
import com.jobportal.dto.LoginResponse;
import com.jobportal.entity.AppUser;
import com.jobportal.repository.UserRepository;
import com.jobportal.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtService jwtService) {
        this.users = users;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        AppUser user = users.findByUsername(req.username()).orElse(null);
        if (user == null || !encoder.matches(req.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("status", 401, "message", "Invalid username or password"));
        }
        String token = jwtService.generate(user.getUsername(), user.getRole());
        return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), user.getRole()));
    }
}
