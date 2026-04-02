package com.certplatform.auth.util;

import java.util.Date;
import java.security.Key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms:86400000}") // configurable, default 1 day
    private long expirationMs;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            // 1. Check if token is blacklisted
            String status = redisTemplate.opsForValue().get(token);
            if ("BLACKLISTED".equals(status)) {
                log.warn("[isTokenValid] Token is blacklisted for user={}", userDetails.getUsername());
                return false;
            }

            // 2. Validate email and expiry
            String email = extractEmail(token);
            boolean valid = email.equals(userDetails.getUsername()) && !isTokenExpired(token);
            log.debug("[isTokenValid] Token validation result={} for email={}", valid, email);
            return valid;

        } catch (JwtException ex) {
            log.warn("[isTokenValid] Invalid JWT token for user={}", userDetails.getUsername(), ex);
            return false;
        }
    }


    private Key getSigningKey() {
        if (secret == null || secret.length() < 32) {
            log.error("[getSigningKey] JWT secret is too short or not configured properly");
            throw new IllegalStateException("JWT secret must be at least 256 bits (32 chars)");
        }
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String email, String role) {
        try {
            String token = Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

            log.info("[generateToken] Generated JWT for email={} role={}", email, role);
            return token;
        } catch (Exception ex) {
            log.error("[generateToken] Failed to generate JWT for email={}", email, ex);
            throw new RuntimeException("Failed to generate JWT", ex);
        }
    }

    public String extractEmail(String token) {
        try {
            return getParser().parseClaimsJws(token).getBody().getSubject();
        } catch (JwtException ex) {
            log.error("[extractEmail] Invalid JWT token", ex);
            throw new RuntimeException("Invalid JWT token", ex);
        }
    }

    public String extractRole(String token) {
        try {
            return getParser().parseClaimsJws(token).getBody().get("role", String.class);
        } catch (JwtException ex) {
            log.error("[extractRole] Invalid JWT token", ex);
            throw new RuntimeException("Invalid JWT token", ex);
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            Date expiration = getParser().parseClaimsJws(token).getBody().getExpiration();
            boolean expired = expiration.before(new Date());
            if (expired) {
                log.warn("[isTokenExpired] Token expired at {}", expiration);
            }
            return expired;
        } catch (JwtException ex) {
            log.error("[isTokenExpired] Failed to parse JWT expiration", ex);
            throw new RuntimeException("Invalid JWT token", ex);
        }
    }

    private JwtParser getParser() {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build();
    }
}
