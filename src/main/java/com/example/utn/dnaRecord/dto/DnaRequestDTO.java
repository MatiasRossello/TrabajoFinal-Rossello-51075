package com.example.utn.dnaRecord.dto;

import com.example.utn.dnaRecord.validator.ValidDna;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request para verificar si un ADN es mutante")
public class DnaRequestDTO {

    @Schema(
            description = "Secuencia de ADN representada como matriz NxN de strings. Cada string es una fila de la matriz y debe contener solo caracteres A, T, C, G.",
            example = "[\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCCTA\",\"TCACTG\"]",
            required = true,
            minLength = 4
    )
    @NotNull(message = "La secuencia de ADN no puede ser nula")
    @NotEmpty(message = "La secuencia de ADN no puede estar vacía")
    @ValidDna
    private String[] dna;
}