package com.project.finance_webflux.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String nombre;
    private String email;
    private String telefono;
}