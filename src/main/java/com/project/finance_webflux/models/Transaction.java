package com.project.finance_webflux.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("transactions")
public class Transaction {

    @Id
    private Long id;

    private Long usuarioId;     // FK a users.id
    private Long categoriaId;   // FK a categories.id

    private BigDecimal monto;
    private String descripcion;

    private LocalDateTime createdAt; // INSERT automático desde DB
}