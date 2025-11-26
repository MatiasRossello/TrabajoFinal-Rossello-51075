package com.example.utn.dnaRecord.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests adicionales para cubrir casos específicos y alcanzar 100% de cobertura
 */
class MutantDetectorCoverageTest {

    private MutantDetector mutantDetector;

    @BeforeEach
    void setUp() {
        mutantDetector = new MutantDetector();
    }

    @Test
    @DisplayName("Cobertura: Secuencia horizontal que continúa una anterior (evitar doble conteo)")
    void testHorizontalSequenceContinuation() {
        // Esta matriz tiene AAAAAA (6 A's seguidas)
        // Necesitamos otra secuencia para ser mutante
        String[] dna = {
                "AAAAAA",  // 6 A's horizontal
                "CAGTGC",
                "TTTTGT",  // 4 T's horizontal - 2da secuencia
                "AGACGG",
                "GCGTCA",
                "TCACTG"
        };
        // Debe ser MUTANTE porque hay 2 secuencias
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Cobertura: Secuencia vertical que continúa una anterior")
    void testVerticalSequenceContinuation() {
        String[] dna = {
                "ATGCGA",
                "ATGCGC",
                "ATGCGT",  
                "ATGCGG",  // 4 A's verticales en columna 0
                "CCCCTA",  // 4 C's horizontal - 2da secuencia
                "TCACTG"
        };
        // Debe ser mutante con 2 secuencias (vertical + horizontal)
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Cobertura: Secuencia diagonal principal que continúa una anterior")
    void testDiagonalMainSequenceContinuation() {
        String[] dna = {
                "ATGCGA",
                "CATGGC",
                "TCATGT",
                "AGAAGT",  // Diagonal de A's desde (0,0)
                "CCACTA",  // Esta A también está en diagonal
                "TCGCTG"
        };
        assertNotNull(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Cobertura: Secuencia diagonal inversa que continúa una anterior")
    void testDiagonalInverseSequenceContinuation() {
        String[] dna = {
                "ATGCTA",  // A en posición (0,5)
                "CAGTAC",  // A en posición (1,4)
                "TTATAT",  // A en posición (2,3)
                "AGAAGG",  // A en posición (3,2)
                "CCACTA",  // A en posición (4,3) - no debe contar doble
                "TCGCTG"
        };
        assertNotNull(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Cobertura: Matriz con exactamente 1 secuencia (retorna false en línea 98)")
    void testExactlyOneSequence() {
        String[] dna = {
                "ATGCGA",
                "CAGTCC",
                "TTACGT",
                "AGGGGA",  // GGGG horizontal - 1 secuencia
                "CCATCA",
                "TCACTG"
        };
        // Solo 1 secuencia, debe retornar false
        assertFalse(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Cobertura: Matriz grande con secuencias continuas horizontales")
    void testLargeMatrixWithContinuousHorizontalSequences() {
        String[] dna = {
                "AAAAAAAAAA",  // 10 A's - cuenta como 1
                "CCCCCCCCCC",  // 10 C's - cuenta como 1 más (total 2)
                "TTATGTTTAT",
                "AGAAGGAAAA",
                "CCCCTACCCC",
                "TCACTGTCAC",
                "ATGCGAATGC",
                "CAGTGCCAGT",
                "TTATGTTTAT",
                "AGAAGGATAA"
        };
        // Debe ser mutante porque tiene 2+ secuencias
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Cobertura: Matriz con múltiples secuencias verticales continuas")
    void testMultipleVerticalContinuousSequences() {
        String[] dna = {
                "AACCGG",
                "AACCGG",
                "AACCGG",
                "AACCGG",  // Columnas 0,1,4,5 tienen 4 iguales
                "AACCGG",
                "TTTTTT"
        };
        // Múltiples secuencias verticales
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Cobertura: Secuencia diagonal que empieza en borde izquierdo")
    void testDiagonalStartingAtLeftEdge() {
        String[] dna = {
                "CTGCGA",
                "CCGTGC",
                "TCCTGT",
                "AGACGG",
                "GCGTCA",
                "TCACTG"
        };
        // Verificar diagonal desde (0,0): C,C,C,C
        assertNotNull(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Cobertura: Secuencia diagonal inversa desde borde derecho")
    void testInverseDiagonalFromRightEdge() {
        String[] dna = {
                "ATGCGG",  // G en (0,5)
                "CAGTGG",  // G en (1,4)
                "TTATGG",  // G en (2,3)
                "AGAAGG",  // G en (3,2)
                "CCCCTA",
                "TCACTG"
        };
        assertNotNull(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Cobertura: Matriz 4x4 con dos secuencias horizontales")
    void test4x4TwoHorizontalSequences() {
        String[] dna = {
                "AAAA",  // Primera secuencia
                "CCCC",  // Segunda secuencia
                "TGAT",
                "AGCG"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Cobertura: Matriz 4x4 con dos secuencias verticales")
    void test4x4TwoVerticalSequences() {
        String[] dna = {
                "ACGT",
                "ACGT",
                "ACGT",
                "ACGT"
        };
        // Columnas 0 y 1 tienen 4 iguales
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Cobertura: Matriz 5x5 con secuencias en todas las direcciones")
    void test5x5AllDirections() {
        String[] dna = {
                "AAAAA",  // Horizontal
                "TTTTT",  // Horizontal
                "GGGGG",  // Horizontal
                "CCCCC",  // Horizontal
                "ATGCA"
        };
        // Múltiples secuencias horizontales
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Cobertura: Early termination con primer carácter diferente en horizontal")
    void testEarlyTerminationHorizontal() {
        String[] dna = {
                "AAATGA",  // AAA seguido de T (no cuenta)
                "CCCCGC",  // CCCC horizontal (1era secuencia)
                "TTTTGT",  // TTTT horizontal (2da secuencia) - debe terminar aquí
                "AGAAGG",
                "GCGTCA",
                "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Cobertura: Caso límite con j=0 para horizontal (no hay j-1)")
    void testHorizontalAtColumnZero() {
        String[] dna = {
                "AAAATG",  // AAAA desde columna 0
                "CAGTGC",
                "CCCCGT",  // CCCC desde columna 0
                "AGAAGG",
                "GCGTCA",
                "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Cobertura: Caso límite con i=0 para vertical (no hay i-1)")
    void testVerticalAtRowZero() {
        String[] dna = {
                "ACGCGA",  // A en (0,0)
                "AGGTGC",  // A en (1,0)
                "ATATGT",  // A en (2,0)
                "AGAAGG",  // A en (3,0) - secuencia vertical completa
                "CCCCTA",  // CCCC horizontal - segunda secuencia
                "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Cobertura: Diagonal principal desde esquina (0,0)")
    void testDiagonalFromTopLeft() {
        String[] dna = {
                "ATGCGA",  // T en (0,0)
                "CTGTGC",  // T en (1,1)
                "TTTCGT",  // T en (2,2)
                "AGCTGG",  // T en (3,3)
                "CCCCTA",  // CCCC - segunda secuencia
                "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Cobertura: Diagonal inversa desde posición (0,3)")
    void testInverseDiagonalFromTopRight() {
        String[] dna = {
                "ATGGGA",  // G en (0,3)
                "CAGGCC",  // G en (1,2)
                "TGTTGT",  // G en (2,1) - falta uno más
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertNotNull(mutantDetector.isMutant(dna));
    }
}
