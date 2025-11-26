package com.example.utn.dnaRecord.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entidad que representa un registro de análisis de ADN.
 * Almacena el hash único del ADN, si es mutante y la fecha de análisis.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DnaRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String dnaHash;

    private Boolean isMutant;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}