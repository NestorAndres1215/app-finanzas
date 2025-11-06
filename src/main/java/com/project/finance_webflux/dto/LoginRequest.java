package com.project.finance_webflux.dto;

import lombok.Data;
@Data
public class LoginRequest {
    private String username;
    private String password;
}