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

    @InjectMocks
    private MutantService mutantService;

    // ==========================================
    // TEST DE LÓGICA DE DETECCIÓN (MUTANTES)
    // ==========================================

    @Test
    @DisplayName("Mutante: Detectar secuencia HORIZONTAL (AAAA)")
    void testAnalyzeDna_Mutant_Horizontal() {
        String[] dna = {
                "AAAAAA", // <--- Secuencia 1
                "CCCCCC", // <--- Secuencia 2
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());

        boolean result = mutantService.analyzeDna(dna);

        assertTrue(result, "Debería detectar mutante por filas horizontales");
        verify(dnaRecordRepository, times(1)).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("Mutante: Detectar secuencia VERTICAL (Columna 0)")
    void testAnalyzeDna_Mutant_Vertical() {
        String[] dna = {
                "ATGCGA",
                "ATGCGA",
                "ATGCGA",
                "ATGCGA", // A, T, G... se repiten verticalmente 4 veces
                "CCGCTA",
                "TCACTG"
        };
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());

        assertTrue(mutantService.analyzeDna(dna), "Debería detectar mutante por columnas verticales");
    }

    @Test
    @DisplayName("Mutante: Detectar secuencia DIAGONAL PRINCIPAL (\\)")
    void testAnalyzeDna_Mutant_Diagonal() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG", // Diagonal A-A-A-A desde (0,0)
                "CCCCTA", // Horizontal C-C-C-C
                "TCACTG"
        };
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());

        assertTrue(mutantService.analyzeDna(dna), "Debería detectar mutante con diagonal principal");
    }

    @Test
    @DisplayName("Mutante: Detectar secuencia DIAGONAL INVERSA (/)")
    void testAnalyzeDna_Mutant_DiagonalInversa() {
        String[] dna = {
                "ATGCGA",
                "CAGTAC",
                "TTAAGT",
                "AGAAGG",
                "ACCCTA",
                "TCACTG"
        };
        // Contiene patrones suficientes para ser mutante en diagonal inversa
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());

        assertTrue(mutantService.analyzeDna(dna), "Debería detectar mutante con diagonal inversa");
    }

    // ==========================================
    // TEST DE LÓGICA DE DETECCIÓN (HUMANOS)
    // ==========================================

    @Test
    @DisplayName("Humano: ADN sin ninguna secuencia (False)")
    void testAnalyzeDna_Human_NoSequences() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATTT",
                "AGACGG",
                "GCGTCA",
                "TCACTG"
        };
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());

        boolean result = mutantService.analyzeDna(dna);

        assertFalse(result, "No debería detectar mutante si no hay secuencias");
        verify(dnaRecordRepository).save(argThat(record -> !record.getIsMutant())); // Verifica que guarde false
    }

    @Test
    @DisplayName("Caso Borde: Humano con SOLO UNA secuencia (Debe ser Falso)")
    void testAnalyzeDna_Human_OnlyOneSequence() {
        // Regla de negocio: "Sabrás si es mutante si encuentras MÁS DE UNA secuencia"
        String[] dna = {
                "AAAA", // <--- 1 secuencia horizontal
                "CAGT",
                "TTAT",
                "AGAC"
        };
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());

        boolean result = mutantService.analyzeDna(dna);

        assertFalse(result, "Estrictamente necesita >1 secuencia. Con 1 debe ser false.");
    }

    // ==========================================
    // TEST DE PERSISTENCIA Y CACHÉ
    // ==========================================

    @Test
    @DisplayName("Caché: Si ya existe en DB, NO debe recalcular ni guardar de nuevo")
    void testAnalyzeDna_CacheHit_ReturnsSavedValue() {
        String[] dna = {"AAAA", "CCCC", "TTTT", "GGGG"};

        // Simulamos que YA EXISTE un registro
        DnaRecord existingRecord = new DnaRecord();
        existingRecord.setIsMutant(true); // En la DB dice que es mutante

        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.of(existingRecord));

        // Ejecutamos
        boolean result = mutantService.analyzeDna(dna);

        // Verificaciones
        assertTrue(result, "Debería devolver el valor de la DB");
        // CRÍTICO: save() NUNCA debe ejecutarse si ya estaba en DB
        verify(dnaRecordRepository, never()).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("Persistencia: Si es nuevo, DEBE calcular el Hash y Guardar")
    void testAnalyzeDna_NewRecord_SavesWithHash() {
        String[] dna = {"ATGC", "CAGT", "TTAT", "AGAC"}; // Humano 4x4

        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());

        mutantService.analyzeDna(dna);

        // Verificamos que guarde un objeto con Hash calculado
        verify(dnaRecordRepository).save(argThat(record ->
                record.getDnaHash() != null &&
                        !record.getDnaHash().isEmpty() &&
                        !record.getIsMutant() // Es humano
        ));
    }

    // ==========================================
    // 🛡️ TEST DE ROBUSTEZ Y VALIDACIONES
    // ==========================================

    @Test
    @DisplayName("Excepción: Array Nulo lanza IllegalArgumentException")
    void testAnalyzeDna_Invalid_Null() {
        assertThrows(IllegalArgumentException.class, () -> mutantService.analyzeDna(null));
    }

    @Test
    @DisplayName("Excepción: Array Vacío lanza IllegalArgumentException")
    void testAnalyzeDna_Invalid_Empty() {
        assertThrows(IllegalArgumentException.class, () -> mutantService.analyzeDna(new String[]{}));
    }

    @Test
    @DisplayName("Excepción: Matriz NxM (No cuadrada)")
    void testAnalyzeDna_Invalid_NonSquare() {
        String[] dna = {"ATGC", "CAGT", "TTAT"}; // 3 filas, 4 columnas
        assertThrows(IllegalArgumentException.class, () -> mutantService.analyzeDna(dna));
    }

    @Test
    @DisplayName("Excepción: Matriz con números o letras inválidas")
    void testAnalyzeDna_Invalid_Characters() {
        String[] dna = {
                "ATGC",
                "CAGT",
                "TTAT",
                "1234" // <--- Números no permitidos
        };
        assertThrows(IllegalArgumentException.class, () -> mutantService.analyzeDna(dna));
    }

    @Test
    @DisplayName("Excepción: Matriz con fila nula")
    void testAnalyzeDna_Invalid_NullRow() {
        String[] dna = {
                "ATGC",
                null, // <--- Fila nula
                "TTAT",
                "AGAC"
        };
        assertThrows(IllegalArgumentException.class, () -> mutantService.analyzeDna(dna));
    }
}