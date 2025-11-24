package com.example.utn.dnaRecord.service;

import com.example.utn.dnaRecord.model.DnaRecord;
import com.example.utn.dnaRecord.repository.DnaRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MutantServiceTest {

    @Mock
    private DnaRecordRepository dnaRecordRepository;

    @Mock
    private MutantDetector mutantDetector; // <--- ¡AHORA MOCKEAMOS EL DETECTOR!

    @InjectMocks
    private MutantService mutantService;

    @Test
    @DisplayName("Si es MUTANTE (según el detector), debe guardar en DB como true")
    void testAnalyzeDna_NewMutant_SavesAndReturnsTrue() {
        String[] dna = {"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};

        // 1. Simulamos que NO existe en DB
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());

        // 2. Simulamos que el detector dice que ES MUTANTE (true)
        when(mutantDetector.isMutant(dna)).thenReturn(true);

        // Ejecutar
        boolean result = mutantService.analyzeDna(dna);

        // Verificar
        assertTrue(result);
        // Verificamos que llamó al detector
        verify(mutantDetector).isMutant(dna);
        // Verificamos que guardó en la base de datos con isMutant=true
        verify(dnaRecordRepository).save(argThat(record -> record.getIsMutant()));
    }

    @Test
    @DisplayName("Si es HUMANO (según el detector), debe guardar en DB como false")
    void testAnalyzeDna_NewHuman_SavesAndReturnsFalse() {
        String[] dna = {"AAAA", "CCCC", "GGGG", "TTTT"}; // El contenido da igual, mandamos el mock

        // 1. No existe en DB
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());

        // 2. Simulamos que el detector dice que ES HUMANO (false)
        when(mutantDetector.isMutant(dna)).thenReturn(false);

        // Ejecutar
        boolean result = mutantService.analyzeDna(dna);

        // Verificar
        assertFalse(result);
        verify(mutantDetector).isMutant(dna);
        // Verificamos que guardó en DB con isMutant=false
        verify(dnaRecordRepository).save(argThat(record -> !record.getIsMutant()));
    }

    @Test
    @DisplayName("Si YA EXISTE en DB, debe retornar el valor guardado (Caché) y NO llamar al detector")
    void testAnalyzeDna_CacheHit_ReturnsStoredValue() {
        String[] dna = {"AAAA", "CCCC", "GGGG", "TTTT"};

        // 1. Simulamos que YA EXISTE en DB y es MUTANTE
        DnaRecord existingRecord = new DnaRecord();
        existingRecord.setIsMutant(true);
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.of(existingRecord));

        // Ejecutar
        boolean result = mutantService.analyzeDna(dna);

        // Verificar
        assertTrue(result);

        // No debe llamar al detector (ahorramos proceso)
        verify(mutantDetector, never()).isMutant(any());
        // No debe guardar de nuevo
        verify(dnaRecordRepository, never()).save(any());
    }
}