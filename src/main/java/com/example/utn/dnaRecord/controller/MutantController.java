package com.example.utn.dnaRecord.controller;

import com.example.utn.dnaRecord.dto.DnaRequestDTO;
import com.example.utn.dnaRecord.dto.StatsResponseDTO;
import com.example.utn.dnaRecord.service.MutantService;
import com.example.utn.dnaRecord.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Mutant Detector", description = "API para la detección de mutantes basada en ADN")
public class MutantController {

    private final MutantService mutantService;
    private final StatsService statsService;

    public MutantController(MutantService mutantService, StatsService statsService) {
        this.mutantService = mutantService;
        this.statsService = statsService;
    }

    @Operation(summary = "Detectar si un humano es mutante", description = "Analiza la secuencia de ADN enviada para determinar si cumple con los criterios de mutante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Es un Mutante", content = @Content),
            @ApiResponse(responseCode = "403", description = "Es un Humano (No Mutante)", content = @Content),
            @ApiResponse(responseCode = "400", description = "ADN inválido (formato incorrecto)", content = @Content)
    })

    @PostMapping("/mutant")
    public ResponseEntity<Void> checkMutant(@Valid @RequestBody DnaRequestDTO dnaRequest) {
        boolean isMutant = mutantService.analyzeDna(dnaRequest.getDna());
        if (isMutant) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


    @Operation(summary = "Obtener estadísticas de verificaciones")
    @GetMapping("/stats") // Esto mapea a /mutant/stats si dejamos el RequestMapping arriba, cuidado.
    // Corrección: Para cumplir con "/stats" literal, lo mejor es sacar el @RequestMapping de la clase o usar "/"
    public ResponseEntity<StatsResponseDTO> getStats() {
        return ResponseEntity.ok(statsService.getStats());
    }
}