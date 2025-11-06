package com.project.finance_webflux.models;


import org.springframework.data.annotation.Id;

import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("users")
public class User {

    @Id
    private Long id;

    private String nombre;
    private String email;
    private String username;
    private String password;
    private String telefono;
    private Boolean estado;
    private String rol; // Aunque no lo usaremos para seguridad, queda como info
    private LocalDateTime createdAt;
}