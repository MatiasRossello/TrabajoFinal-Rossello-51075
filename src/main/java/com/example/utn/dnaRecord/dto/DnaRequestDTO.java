package com.example.utn.dnaRecord.dto;

import com.example.utn.dnaRecord.validator.ValidDna; // Importar tu anotación
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DnaRequestDTO {

    @NotNull
    @NotEmpty
    @ValidDna
    private String[] dna;
}