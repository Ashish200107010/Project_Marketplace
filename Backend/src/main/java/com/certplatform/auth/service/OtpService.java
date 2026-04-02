package com.certplatform.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
public class OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpService.class);
    private static final String OTP_PREFIX = "OTP_";
    private static final SecureRandom secureRandom = new SecureRandom();

    private final StringRedisTemplate redisTemplate;

    public OtpService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Generate a 6-digit OTP using SecureRandom for better entropy.
     */
    public String generateOtp() {
        int otp = 100000 + secureRandom.nextInt(900000);
        log.debug("[generateOtp] Generated OTP (masked)={}", "******");
        return String.valueOf(otp);
    }

    /**
     * Store OTP in Redis with expiry.
     */
    public void storeOtp(String email, String otp, Duration expiry) {
        try {
            String key = OTP_PREFIX + email;
            redisTemplate.opsForValue().set(key, otp, expiry);
            log.info("[storeOtp] Stored OTP for email={} with expiry={}s", email, expiry.toSeconds());
        } catch (Exception ex) {
            log.error("[storeOtp] Failed to store OTP for email={}", email, ex);
            throw new RuntimeException("Failed to store OTP", ex);
        }
    }

    /**
     * Validate OTP against stored value.
     */
    public boolean isValid(String email, String otp) {
        try {
            String key = OTP_PREFIX + email;
            String stored = redisTemplate.opsForValue().get(key);
            boolean valid = stored != null && stored.equals(otp);
            log.info("[isValid] OTP validation for email={} result={}", email, valid);
            return valid;
        } catch (Exception ex) {
            log.error("[isValid] Error validating OTP for email={}", email, ex);
            throw new RuntimeException("Failed to validate OTP", ex);
        }
    }

    /**
     * Clear OTP from Redis after use.
     */
    public void clear(String email) {
        try {
            redisTemplate.delete(OTP_PREFIX + email);
            log.info("[clear] Cleared OTP for email={}", email);
        } catch (Exception ex) {
            log.error("[clear] Failed to clear OTP for email={}", email, ex);
            throw new RuntimeException("Failed to clear OTP", ex);
        }
    }
}
