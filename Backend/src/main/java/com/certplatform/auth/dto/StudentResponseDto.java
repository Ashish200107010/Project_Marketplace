package com.certplatform.auth.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StudentResponseDto {
    private UUID id;
    private String name;
    private String email;
    private String college;
    private List<String> skills;
    private List<String> rolesLookingFor;
    private String role;

    // Getters and setters
}

