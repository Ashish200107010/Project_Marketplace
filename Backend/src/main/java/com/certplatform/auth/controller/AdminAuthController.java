package com.certplatform.auth.controller;

import com.certplatform.auth.dto.AuthRequest;
import com.certplatform.auth.dto.JwtResponseAdmin;
import com.certplatform.auth.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth/admin")
public class AdminAuthController {

    private static final Logger log = LoggerFactory.getLogger(AdminAuthController.class);

    private final AuthService authService;

    public AdminAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseAdmin> loginAdmin(@Valid @RequestBody AuthRequest request) {
        log.info("Admin login attempt with email={}", request.getEmail());

        try {
            ResponseEntity<JwtResponseAdmin> response = authService.loginAdmin(request);
            log.debug("Admin login response: {}", response);
            return response;

        } catch (ResponseStatusException ex) {
            // Business/validation errors → log stack trace in admin.log
            log.error("[loginAdmin] Business error for admin email={}", request.getEmail(), ex);
            throw ex; // rethrow so GlobalExceptionHandler can format JSON response

        } catch (Exception ex) {
            // Unexpected errors → log stack trace in admin.log
            log.error("[loginAdmin] Unexpected error for admin email={}", request.getEmail(), ex);
            throw new ResponseStatusException(
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                "Admin login failed",
                ex
            );
        }
    }
}
