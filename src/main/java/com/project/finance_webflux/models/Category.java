package com.project.finance_webflux.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("categories")
public class Category {

    @Id
    private Long id;

    private String nombre; // UNIQUE

    private String tipo; // ingreso | gasto (usamos String en lugar de ENUM para evitar problemas en R2DBC)
}