package com.example.utn.dnaRecord.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
    description = "Estadísticas de verificaciones de ADN realizadas. " +
                 "Proporciona información sobre la cantidad de mutantes y humanos detectados, " +
                 "así como el ratio entre ellos.",
    example = "{\"count_mutant_dna\": 40, \"count_human_dna\": 100, \"ratio\": 0.4}"
)
public class StatsResponseDTO {

    @Schema(
            description = "Cantidad total de ADN mutante detectado en todas las verificaciones realizadas. " +
                         "Un ADN es mutante cuando contiene más de una secuencia de 4 letras iguales.",
            example = "40",
            minimum = "0"
    )
    @JsonProperty("count_mutant_dna")
    private long countMutantDna;

    @Schema(
            description = "Cantidad total de ADN humano (no mutante) detectado en todas las verificaciones realizadas. " +
                         "Un ADN es humano cuando contiene 0 o 1 secuencia de 4 letras iguales.",
            example = "100",
            minimum = "0"
    )
    @JsonProperty("count_human_dna")
    private long countHumanDna;

    @Schema(
            description = "Ratio de mutantes sobre humanos (count_mutant_dna / count_human_dna). " +
                         "Retorna 0 si no hay humanos detectados. " +
                         "Ejemplo: Si hay 40 mutantes y 100 humanos, el ratio es 0.4 (40/100).",
            example = "0.4",
            minimum = "0"
    )
    private double ratio;
}