package com.certplatform.auth.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentRegisterRequest {
    
    @NotBlank 
    private String name;
    private String college;
    @NotBlank
    private List<String> rolesLookingFor;
    private List<String> skills;
    @NotBlank
    @Email 
    private String email;
    @NotBlank
    @Size(min = 8) 
    private String password;

}

