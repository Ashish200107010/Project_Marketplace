package com.certplatform.auth.service;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.Cookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.certplatform.admin.entity.Admin;
import com.certplatform.admin.repository.AdminRepository;
import com.certplatform.auth.dto.AdminResponseDto;
import com.certplatform.auth.dto.AuthRequest;
import com.certplatform.auth.dto.JwtResponseAdmin;
import com.certplatform.auth.dto.JwtResponseStudent;
import com.certplatform.auth.dto.OtpVerifyRequest;
import com.certplatform.auth.dto.PreOtpVerifyrequest;
import com.certplatform.auth.dto.StudentRegisterRequest;
import com.certplatform.auth.dto.StudentResponseDto;
import com.certplatform.auth.repository.RedisTokenBlackListRepository;
import com.certplatform.auth.repository.TokenBlacklistRepository;
import com.certplatform.auth.util.JwtUtil;
import com.certplatform.common.enums.Role;
import com.certplatform.notification.service.OtpEmailService;
import com.certplatform.user.entity.Student;
import com.certplatform.user.repository.StudentRepository;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;


@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final StudentRepository studentRepo;
    private final AdminRepository adminRepo;
    private final PasswordEncoder passwordEncoder;
    private final OtpEmailService otpEmailService;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;
    private final RedisTokenBlackListRepository blacklistRepository;

    public AuthService(StudentRepository studentRepo,
                       AdminRepository adminRepo,
                       PasswordEncoder passwordEncoder,
                       OtpEmailService otpEmailService,
                       OtpService otpService,
                       JwtUtil jwtUtil,
                       RedisTokenBlackListRepository blacklistRepository) {
        this.blacklistRepository = blacklistRepository;
        this.studentRepo = studentRepo;
        this.adminRepo = adminRepo;
        this.passwordEncoder = passwordEncoder;
        this.otpEmailService = otpEmailService;
        this.otpService = otpService;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<JwtResponseStudent> loginStudent(AuthRequest request) {
        log.info("[loginStudent] Attempting login for student: {}", request.getEmail());
        try {
            Student student = studentRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

            if (!passwordEncoder.matches(request.getPassword(), student.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
            }

            String token = jwtUtil.generateToken(student.getEmail(), Role.STUDENT.name());
            ResponseCookie tokenCookie = buildCookie("token", token);
            ResponseCookie roleCookie = buildCookie("role", "STUDENT");

            StudentResponseDto dto = new StudentResponseDto(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getCollege(),
                student.getSkills(),
                student.getRolesLookingFor(),
                "STUDENT"
            );
            log.info("[loginStudent] Successful login for student: {}", student.getEmail());
            return ResponseEntity.ok()
                .header("Set-Cookie", tokenCookie.toString())
                .header("Set-Cookie", roleCookie.toString())
                .body(new JwtResponseStudent(dto));

        } catch (ResponseStatusException ex) {
            log.error("[loginStudent] Business error for student={}", request.getEmail(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("[loginStudent] Unexpected error for student={}", request.getEmail(), ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Login failed", ex);
        }
    }

    public ResponseEntity<JwtResponseAdmin> loginAdmin(AuthRequest request) {
        log.info("[loginAdmin] Attempting login for admin: {}", request.getEmail());
        try {
            Admin admin = adminRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));

            if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
            }

            String token = jwtUtil.generateToken(admin.getEmail(), Role.ADMIN.name());
            ResponseCookie tokenCookie = buildCookie("admin_token", token);
            ResponseCookie roleCookie = buildCookie("role", "ADMIN");

            AdminResponseDto dto = new AdminResponseDto(admin.getId(), admin.getEmail(), admin.getName());

            log.info("[loginAdmin] Successful login for admin: {}", admin.getEmail());
            return ResponseEntity.ok()
                .header("Set-Cookie", tokenCookie.toString())
                .header("Set-Cookie", roleCookie.toString())
                .body(new JwtResponseAdmin(dto));

        } catch (ResponseStatusException ex) {
            log.error("[loginAdmin] Business error for admin={}", request.getEmail(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("[loginAdmin] Unexpected error for admin={}", request.getEmail(), ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Admin login failed", ex);
        }
    }

    @Transactional
    public ResponseEntity<String> verifyStudentOtp(OtpVerifyRequest request) {
        String email = request.getEmail();
        log.info("[verifyStudentOtp] Verifying OTP for email={}", email);
        try {
            if (!otpService.isValid(email, request.getOtp())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired OTP");
            }

            otpService.clear(email);
            log.info("[verifyStudentOtp] Completed successfully for email={}", email);
            return ResponseEntity.ok("OTP_VERIFIED");

        } catch (ResponseStatusException ex) {
            log.error("[verifyStudentOtp] Business error for email={}", email, ex);
            throw ex;
        } catch (Exception ex) {
            log.error("[verifyStudentOtp] Unexpected error for email={}", email, ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "OTP verification failed", ex);
        }
    }

    public ResponseEntity<String> preVerifyStudentOtp(PreOtpVerifyrequest request) {
        log.info("[preVerifyStudentOtp] send otp attempt for email={}", request.getEmail());
        try {
            if (studentRepo.findByEmail(request.getEmail()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
            }

            String otp = otpService.generateOtp();
            otpService.storeOtp(request.getEmail(), otp, Duration.ofMinutes(5));
            otpEmailService.sendOtpEmail(request.getEmail(), otp);

            log.info("[preVerifyStudentOtp] OTP sent to {}", request.getEmail());
            return ResponseEntity.ok("OTP_SENT");

        } catch (ResponseStatusException ex) {
            log.error("[preVerifyStudentOtp] Business error for email={}", request.getEmail(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("[preVerifyStudentOtp] Unexpected error for email={}", request.getEmail(), ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "send-otp failed", ex);
        }
    }

    @Transactional
    public ResponseEntity<JwtResponseStudent> registerStudent(StudentRegisterRequest request) {
        
        String email = request.getEmail();
        log.info("[registerStudent] Registering student for email={}", email);

        try {

            Student student = new Student();
            student.setEmail(request.getEmail());
            student.setPassword(passwordEncoder.encode(request.getPassword()));
            student.setName(request.getName());
            student.setCollege(request.getCollege());
            student.setRolesLookingFor(request.getRolesLookingFor());
            student.setSkills(request.getSkills());

            studentRepo.save(student);
            log.info("[registerStudent] Student registered successfully: {}", student.getEmail());

            String token = jwtUtil.generateToken(student.getEmail(), Role.STUDENT.name());
            ResponseCookie tokenCookie = buildCookie("token", token);
            ResponseCookie roleCookie = buildCookie("role", "STUDENT");

            StudentResponseDto dto = new StudentResponseDto(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getCollege(),
                student.getSkills(),
                student.getRolesLookingFor(),
                "STUDENT"
            );

            log.info("[registerStudent] Completed successfully for email={}", email);
            return ResponseEntity.ok()
                .header("Set-Cookie", tokenCookie.toString())
                .header("Set-Cookie", roleCookie.toString())
                .body(new JwtResponseStudent(dto));

        } catch (ResponseStatusException ex) {
            log.error("[registerStudent] Business error for email={}", email, ex);
            throw ex;
        } catch (Exception ex) {
            log.error("[registerStudent] Unexpected error for email={}", email, ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Student Registration failed", ex);
        }
    }

    // ✅ Helper method for cookie creation
    private ResponseCookie buildCookie(String name, String value) {
        return ResponseCookie.from(name, value)
            .httpOnly(true)
            .secure(false)        // set true in production with HTTPS
            .domain("localhost")  // adjust domain in production
            .path("/")
            .maxAge(Duration.ofHours(1))
            .sameSite("Lax")
            .build();
    }

    /**
     * Extracts the JWT token from the Authorization header.
     * @param request HttpServletRequest
     * @return token string or null if not present
     */
    // public String extractToken(HttpServletRequest request) {
    //     try {
    //         String header = request.getHeader(HttpHeaders.AUTHORIZATION);

    //         if (header != null && header.startsWith("Bearer ")) {
    //             String token = header.substring(7);
    //             log.debug("[extractToken] Token extracted successfully");
    //             return token;
    //         }

    //         log.warn("[extractToken] No Bearer token found in Authorization header");
    //         return null;

    //     } catch (Exception ex) {
    //         log.error("[extractToken] Failed to extract token", ex);
    //         // Bubble up as a business error so controller can handle it
    //         throw new ResponseStatusException(
    //             HttpStatus.BAD_REQUEST,
    //             "Invalid Authorization header",
    //             ex
    //         );
    //     }
    // }
    public String extractToken(HttpServletRequest request) {
    try {
        // 1. Check Authorization header
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            log.debug("[extractToken] Token extracted from Authorization header");
            return token;
        }

        // 2. Check HttpOnly cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    log.debug("[extractToken] Token extracted from HttpOnly cookie");
                    return cookie.getValue();
                }
            }
        }

        // 3. Nothing found
        log.warn("[extractToken] No token found in Authorization header or cookies");
        return null;

    } catch (Exception ex) {
        log.error("[extractToken] Failed to extract token", ex);
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Invalid token format",
            ex
        );
    }
}



    /**
     * Invalidates a token by storing it in a blacklist.
     * @param token JWT token
     */
    public void invalidateToken(String token) {
        try {
            blacklistRepository.save(token);
            log.info("[invalidateToken] Token invalidated and stored in blacklist");
        } catch (Exception ex) {
            log.error("[invalidateToken] Failed to invalidate token", ex);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to invalidate token",
                ex
            );
        }
    }

}

