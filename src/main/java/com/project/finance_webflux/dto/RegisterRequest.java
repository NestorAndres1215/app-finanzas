package com.project.finance_webflux.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
public class RegisterRequest {
    private String nombre;
    private String email;
    private String username;
    private String password;
    private String telefono;
}