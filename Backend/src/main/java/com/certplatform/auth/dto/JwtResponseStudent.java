package com.certplatform.auth.dto;


import lombok.Getter;

@Getter
public class JwtResponseStudent {
    private StudentResponseDto student;

    public JwtResponseStudent(StudentResponseDto stud) {
        this.student = stud;
    }
}


