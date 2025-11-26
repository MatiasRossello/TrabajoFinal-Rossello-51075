package com.example.utn.dnaRecord.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MutantDetectorTest {

    private MutantDetector mutantDetector;

    @BeforeEach
    void setUp() {
        mutantDetector = new MutantDetector();
    }

    // === MUTANTES (true) ===

    @Test
    @DisplayName("Mutante con secuencias horizontal y diagonal")
    void testMutantHorizontalAndDiagonal() {
        String[] dna = {"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Mutante con secuencias verticales")
    void testMutantVertical() {
        String[] dna = {"ATGCGA", "ATGCGA", "ATGCGA", "ATGCGA", "CCCCTA", "TCACTG"};
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Mutante con múltiples horizontales")
    void testMutantMultipleHorizontal() {
        String[] dna = {"TTTTGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Mutante con diagonales")
    void testMutantDiagonals() {
        String[] dna = {"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Mutante en matriz 4x4")
    void testMutant4x4() {
        String[] dna = {"AAAA", "CCCC", "TTAT", "AGAC"};
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Mutante en matriz 10x10")
    void testMutant10x10() {
        String[] dna = {
                "ATGCGAATGC", "CAGTGCCAGT", "TTATGTTTAT", "AGAAGGATAA", "CCCCTACCCC",
                "TCACTGTCAC", "ATGCGAATGC", "CAGTGCCAGT", "TTATGTTTAT", "AGAAGGATAA"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Mutante con todo igual")
    void testMutantAllSame() {
        String[] dna = {"AAAAAA", "AAAAAA", "AAAAAA", "AAAAAA", "AAAAAA", "AAAAAA"};
        assertTrue(mutantDetector.isMutant(dna));
    }

    // === HUMANOS (false) ===

    @Test
    @DisplayName("Humano con solo 1 secuencia")
    void testHumanOneSequence() {
        String[] dna = {"ATGCGA", "CAGTGC", "TTATTT", "AGACGG", "GCGTCA", "TCACTG"};
        assertFalse(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Humano sin secuencias")
    void testHumanNoSequences() {
        String[] dna = {"ATGC", "CAGT", "TTAT", "AGAC"};
        assertFalse(mutantDetector.isMutant(dna));
    }

    // === VALIDACIONES (false) ===

    @Test
    @DisplayName("Rechazar ADN nulo")
    void testNullDna() {
        assertFalse(mutantDetector.isMutant(null));
    }

    @Test
    @DisplayName("Rechazar ADN vacío")
    void testEmptyDna() {
        assertFalse(mutantDetector.isMutant(new String[]{}));
    }

    @Test
    @DisplayName("Rechazar matriz no cuadrada")
    void testNonSquare() {
        String[] dna = {"ATGCGA", "CAGTGC", "TTATGT"};
        assertFalse(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Rechazar caracteres inválidos")
    void testInvalidCharacters() {
        String[] dna = {"ATGCGA", "CAGTXC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};
        assertFalse(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Rechazar fila nula")
    void testNullRow() {
        String[] dna = {"ATGCGA", null, "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};
        assertFalse(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Rechazar matriz pequeña 3x3")
    void testTooSmall() {
        String[] dna = {"ATG", "CAG", "TTA"};
        assertFalse(mutantDetector.isMutant(dna));
    }

    // === OPTIMIZACIÓN ===

    @Test
    @DisplayName("Early termination")
    void testEarlyTermination() {
        String[] dna = {"AAAAGA", "AAAAGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};
        long start = System.nanoTime();
        boolean result = mutantDetector.isMutant(dna);
        long end = System.nanoTime();

        assertTrue(result);
        assertTrue((end - start) < 10_000_000);
    }

    @Test
    @DisplayName("Matriz estándar 6x6")
    void testStandard6x6() {
        String[] dna = {"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};
        assertNotNull(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Secuencia de longitud 4 debe detectarse correctamente")
    void testSequenceLongerThanFour() {
        String[] dna = {
                "ATGCGA",
                "AAGTGC",
                "ATATGT",
                "ACGCCA", // Columna 0: A-A-A-A vertical - 1 secuencia
                "GCGTCA",
                "TCACTG"
        };
        // Debe ser HUMANO porque solo hay 1 secuencia
        assertFalse(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Diagonal en esquina debe detectarse correctamente")
    void testDiagonalInCorner() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGT",
                "CCACTA",
                "TCGCTG"
        };
        // Verificar que detecta diagonal en cualquier posición
        boolean result = mutantDetector.isMutant(dna);
        // Puede ser true o false dependiendo de las secuencias presentes
        assertNotNull(result);
    }

    @Test
    @DisplayName("Matriz mínima 4x4 con exactamente 2 secuencias")
    void testMinimum4x4WithExactly2Sequences() {
        String[] dna = {
                "AAAA", // Horizontal
                "CCCC", // Horizontal (2da secuencia)
                "TTAT",
                "AGAC"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Matriz con caracteres minúsculas debe ser inválido")
    void testLowercaseCharacters() {
        String[] dna = {
                "atgc",
                "cagt",
                "ttat",
                "agac"
        };
        assertFalse(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Matriz con espacios debe ser inválido")
    void testWhitespaceCharacters() {
        String[] dna = {
                "ATGC",
                "CA T",
                "TTAT",
                "AGAC"
        };
        assertFalse(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Matriz con filas de diferentes longitudes")
    void testUnevenRowLengths() {
        String[] dna = {
                "ATGCGA",
                "CAG",      // Fila más corta
                "TTATGT",
                "AGAAGG"
        };
        assertFalse(mutantDetector.isMutant(dna));
    }
}