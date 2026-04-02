package com.certplatform.auth.repository;

public interface TokenBlacklistRepository {
    void save(String token);
    boolean exists(String token);
}
