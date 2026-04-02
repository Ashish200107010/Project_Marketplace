package com.certplatform.auth.controller;

import com.certplatform.auth.dto.AuthRequest;
import com.certplatform.auth.dto.JwtResponseStudent;
import com.certplatform.auth.dto.OtpVerifyRequest;
import com.certplatform.auth.dto.PreOtpVerifyrequest;
import com.certplatform.auth.dto.StudentRegisterRequest;
import com.certplatform.auth.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth/student")
public class StudentAuthController {

    private static final Logger log = LoggerFactory.getLogger(StudentAuthController.class);

    private final AuthService authService;

    public StudentAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseStudent> loginStudent(@Valid @RequestBody AuthRequest request) {
        log.info("[loginStudent] Attempt for student email={}", request.getEmail());
        try {
            ResponseEntity<JwtResponseStudent> response = authService.loginStudent(request);
            log.debug("[loginStudent] Response={}", response);
            return response;

        } catch (ResponseStatusException ex) {
            log.error("[loginStudent] Business error for student email={}", request.getEmail(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("[loginStudent] Unexpected error for student email={}", request.getEmail(), ex);
            throw new ResponseStatusException(
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                "Student login failed",
                ex
            );
        }
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponseStudent> registerStudent(@Valid @RequestBody StudentRegisterRequest request) {
        log.info("[registerStudent] Request received for student email={}", request.getEmail());
        try {
            ResponseEntity<JwtResponseStudent> response = authService.registerStudent(request);
            log.debug("[registerStudent] Response={}", response);
            return response;

        } catch (ResponseStatusException ex) { 
            log.error("[registerStudent] Business error for student email={}", request.getEmail(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("[registerStudent] Unexpected error for student email={}", request.getEmail(), ex);
            throw new ResponseStatusException(
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                "Student registration failed",
                ex
            );
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        log.info("[verifyOtp] OTP verification attempt for studentEmail={} with OTP=****", request.getEmail());
        try {
            ResponseEntity<String> response = authService.verifyStudentOtp(request);
            log.debug("[verifyOtp] Response={}", response);
            return response;

        } catch (ResponseStatusException ex) {
            log.error("[verifyOtp] Business error for student email={}", request.getEmail(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("[verifyOtp] Unexpected error for student email={}", request.getEmail(), ex);
            throw new ResponseStatusException(
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                "OTP verification failed",
                ex
            );
        }
    }


    @PostMapping("/send-otp")
    public ResponseEntity<String> preVerifyOtp(@Valid @RequestBody PreOtpVerifyrequest request) {
        log.info("[verifyOtp] OTP verification attempt for studentEmail={} with OTP=****", request.getEmail());
        try {
            ResponseEntity<String> response = authService.preVerifyStudentOtp(request);
            log.debug("[verifyOtp] Response={}", response);
            return response;

        } catch (ResponseStatusException ex) {
            log.error("[verifyOtp] Business error for student email={}", request.getEmail(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("[verifyOtp] Unexpected error for student email={}", request.getEmail(), ex);
            throw new ResponseStatusException(
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                "OTP verification failed",
                ex
            );
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("[logout] Logout attempt initiated");
        try {
            String token = authService.extractToken(request);

            if (token != null) {
                try{
                    authService.invalidateToken(token);
                    log.debug("[logout] Token invalidated successfully");
                } catch (Exception ex) {
                    log.error("[logout] Error invalidating token", ex);
                    throw new ResponseStatusException(
                        org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to invalidate token",
                        ex
                    );
                }
            } else {
                log.warn("[logout] No token found in request");
                throw new ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "No token provided for logout"
                );
            }

            // Clear cookie if you’re storing JWT/session in HttpOnly cookie
            ResponseCookie cookie = ResponseCookie.from("token", "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(0) // expire immediately
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            // Delete role cookie 
            ResponseCookie roleCookie = ResponseCookie.from("role", "") 
                    .httpOnly(true) 
                    .secure(true) 
                    .path("/") 
                    .maxAge(0) // expire immediately 
                    .build(); 
            response.addHeader(HttpHeaders.SET_COOKIE, roleCookie.toString());

            log.info("[logout] Logout completed successfully");
            return ResponseEntity.ok().build();

        } catch (ResponseStatusException ex) {
            log.error("[logout] Business error during logout", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("[logout] Unexpected error during logout", ex);
            throw new ResponseStatusException(
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                "Logout failed",
                ex
            );
        }
    }

}