package com.example.utn.dnaRecord.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DnaRequestDTO{

    @NotNull
    @NotEmpty
    private String[] dna; // CORREGIDO: Ahora es un Array de Strings, no un String simple.
}