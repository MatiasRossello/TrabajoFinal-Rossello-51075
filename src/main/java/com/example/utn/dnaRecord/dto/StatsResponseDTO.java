package com.example.utn.dnaRecord.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Estadísticas de verificaciones de ADN realizadas")
public class StatsResponseDTO {

    @Schema(
            description = "Cantidad de ADN mutante detectado",
            example = "40"
    )
    @JsonProperty("count_mutant_dna")
    private long countMutantDna;

    @Schema(
            description = "Cantidad de ADN humano (no mutante) detectado",
            example = "100"
    )
    @JsonProperty("count_human_dna")
    private long countHumanDna;

    @Schema(
            description = "Ratio de mutantes sobre humanos (count_mutant_dna / count_human_dna)",
            example = "0.4"
    )
    private double ratio;
}