package com.certplatform.auth.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminResponseDto {
    
    private UUID id;
    private String email;
    private String name;
    private String role;

    public AdminResponseDto(UUID id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = "ADMIN";
    }
}
