package com.certplatform.auth.dto;

import lombok.Getter;

@Getter
public class JwtResponseAdmin {
    private AdminResponseDto admin;

    public JwtResponseAdmin(AdminResponseDto adm) {
        this.admin = adm;
    }
}