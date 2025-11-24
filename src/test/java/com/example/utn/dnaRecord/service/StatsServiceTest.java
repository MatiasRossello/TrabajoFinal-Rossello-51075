package com.example.utn.dnaRecord.service;

import com.example.utn.dnaRecord.dto.StatsResponseDTO;
import com.example.utn.dnaRecord.repository.DnaRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private DnaRecordRepository dnaRecordRepository;

    @InjectMocks
    private StatsService statsService;

    @Test
    @DisplayName("Caso Normal: Hay mutantes y humanos (Ratio decimal)")
    void testGetStats_NormalCase() {
        // Simulamos: 40 mutantes, 100 humanos
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(40L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(100L);

        StatsResponseDTO response = statsService.getStats();

        // Verificaciones
        assertEquals(40L, response.getCountMutantDna());
        assertEquals(100L, response.getCountHumanDna());
        // Ratio esperado: 40 / 100 = 0.4
        assertEquals(0.4, response.getRatio(), 0.0001, "El ratio debería ser 0.4");
    }

    @Test
    @DisplayName("Caso Borde: No hay humanos (Evitar división por cero)")
    void testGetStats_NoHumans() {
        // Simulamos: 10 mutantes, 0 humanos
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(10L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(0L);

        StatsResponseDTO response = statsService.getStats();

        assertEquals(10L, response.getCountMutantDna());
        assertEquals(0L, response.getCountHumanDna());
        // Según tu lógica en StatsService: si no hay humanos pero sí mutantes, ratio = 1.0
        assertEquals(1.0, response.getRatio(), "Si no hay humanos, el ratio debería ser 1.0 según lógica definida");
    }

    @Test
    @DisplayName("Caso Borde: Base de datos vacía (Todo cero)")
    void testGetStats_EmptyDatabase() {
        // Simulamos: 0 mutantes, 0 humanos
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(0L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(0L);

        StatsResponseDTO response = statsService.getStats();

        assertEquals(0L, response.getCountMutantDna());
        assertEquals(0L, response.getCountHumanDna());
        assertEquals(0.0, response.getRatio(), "Si no hay datos, el ratio debería ser 0.0");
    }

    @Test
    @DisplayName("Caso: Igual cantidad (Ratio debe ser 1.0)")
    void testGetStats_EqualCount() {
        // Simulamos: 50 mutantes, 50 humanos
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(50L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(50L);

        StatsResponseDTO response = statsService.getStats();

        assertEquals(50L, response.getCountMutantDna());
        assertEquals(50L, response.getCountHumanDna());
        // Ratio: 50 / 50 = 1.0
        assertEquals(1.0, response.getRatio());
    }
}