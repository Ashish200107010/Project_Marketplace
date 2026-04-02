package com.certplatform.auth.filter;

import com.certplatform.auth.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    private final JwtUtil jwtUtil;
    private UserDetailsService userDetailsService;
    
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService)
    {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/auth/")
            || path.startsWith("/public/")
            || "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
        throws ServletException, IOException {

        // Skip filter for OPTIONS requests (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String adminToken = null;
        String studentToken = null;

        Cookie[] cookies = request.getCookies();
        log.info("=== JWT Filter Debug ===");
        log.info("Request path: {}", request.getRequestURI());
        log.info("Cookies received: {}", cookies != null ? cookies.length : 0);
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.info("Cookie name: {}", cookie.getName());
                if ("admin_token".equals(cookie.getName())) {
                    log.info("admin_token found....");
                    adminToken = cookie.getValue();
                } else if ("token".equals(cookie.getName())) {
                    log.info("student_token found....");
                    studentToken = cookie.getValue();
                }
            }
        } else {
            log.error("No cookies found in request!");
        }
        
        String path = request.getRequestURI();
        String token = null;
        String expectedRole = null;

        if (path.contains("/student")) {
            token = studentToken;
            expectedRole = "STUDENT";
            log.info("Student path detected: {}", path);
            log.info("Student token found: {}", token != null);
        }else if (path.contains("/admin")) {
            token = adminToken;
            expectedRole = "ADMIN";
            log.info("Admin path detected: {}", path);
            log.info("Admin token found: {}", token != null);
        }


        if (token == null) {
            log.warn("TOKEN_MISSING for path: {}", path);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"TOKEN_MISSING\"}");
            return;
        }


        try {
            if (token.chars().filter(ch -> ch == '.').count() != 2) {
                log.warn("Invalid token format for path: {}", path);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Invalid token format\"}");
                return;
            }

            String actualRole = null;
            String email = null;
            try {
                actualRole = jwtUtil.extractRole(token);
                email = jwtUtil.extractEmail(token);
                log.info("Token parsed successfully. Email: {}, Role: {}", email, actualRole);
            } catch (Exception jwtEx) {
                log.error("JWT parsing error: {}", jwtEx.getMessage());
                jwtEx.printStackTrace();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Invalid or malformed token: " + jwtEx.getMessage() + "\"}");
                return;
            }

            log.info("Expected role: {}, Actual role: {}", expectedRole, actualRole);
            if (!expectedRole.equals(actualRole)) {
                log.warn("Role mismatch! Expected: {}, Got: {}", expectedRole, actualRole);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Token role mismatch: expected " + expectedRole + " but got " + actualRole + "\"}");
                return;
            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    if (jwtUtil.isTokenValid(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.info("Authentication set successfully for: {}", email);
                    } else {
                        log.warn("Token validation failed for: {}", email);
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\":\"Invalid or expired token\"}");
                        return;
                    }
                } catch (Exception userEx) {
                    log.error("Error loading user details: {}", userEx.getMessage());
                    userEx.printStackTrace();
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"User not found: " + email + "\"}");
                    return;
                }
            }
        } catch (Exception e) {
            log.error("Unexpected error processing token: {}", e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Error processing authentication: " + e.getMessage() + "\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
