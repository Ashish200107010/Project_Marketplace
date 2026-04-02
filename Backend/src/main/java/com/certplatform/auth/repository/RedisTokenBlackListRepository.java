package com.certplatform.auth.repository;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisTokenBlackListRepository implements TokenBlacklistRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisTokenBlackListRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(String token) {
        // store token with TTL equal to JWT expiry
        redisTemplate.opsForValue().set(token, "BLACKLISTED", Duration.ofHours(1));
    }

    @Override
    public boolean exists(String token) {
        return redisTemplate.hasKey(token);
    }
}
