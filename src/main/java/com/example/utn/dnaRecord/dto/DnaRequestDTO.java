package com.example.utn.dnaRecord.dto;

import com.example.utn.dnaRecord.validator.ValidDna;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "Request para verificar si un ADN es mutante",
    example = "{\"dna\": [\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCCTA\",\"TCACTG\"]}"
)
public class DnaRequestDTO {

    @Schema(
            description = "Secuencia de ADN representada como matriz NxN de strings. " +
                         "Cada string es una fila de la matriz y debe contener solo caracteres A, T, C, G (mayúsculas). " +
                         "La matriz debe ser cuadrada (NxN) con N >= 4. " +
                         "Ejemplo: Una matriz 6x6 donde cada elemento tiene 6 caracteres.",
            example = "[\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCCTA\",\"TCACTG\"]",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 4,
            implementation = String[].class
    )
    @NotNull(message = "La secuencia de ADN no puede ser nula")
    @NotEmpty(message = "La secuencia de ADN no puede estar vacía")
    @ValidDna
    private String[] dna;
}