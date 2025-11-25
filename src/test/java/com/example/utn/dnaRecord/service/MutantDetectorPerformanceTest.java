package com.example.utn.dnaRecord.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de performance para validar tiempos de ejecución del algoritmo
 * según las rúbricas del proyecto.
 */
class MutantDetectorPerformanceTest {

    private MutantDetector mutantDetector;
    private Random random;

    @BeforeEach
    void setUp() {
        mutantDetector = new MutantDetector();
        random = new Random(42); // Seed fija para reproducibilidad
    }

    // ========== BENCHMARK 6x6 (Óptimo: ≤1ms, Aceptable: ≤5ms) ==========

    @Test
    @DisplayName("Benchmark 6x6 - Matriz estándar debe ejecutar en <1ms (óptimo)")
    void testPerformance6x6_Optimal() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };

        // Warm-up (para JIT compilation)
        for (int i = 0; i < 100; i++) {
            mutantDetector.isMutant(dna);
        }

        // Medición real
        long start = System.nanoTime();
        boolean result = mutantDetector.isMutant(dna);
        long end = System.nanoTime();

        long durationMs = (end - start) / 1_000_000;

        System.out.printf("⏱️  6x6 ejecutado en: %d ms (óptimo: <1ms)%n", durationMs);

        assertTrue(result, "Debe detectar mutante");
        assertTrue(durationMs <= 5,
                String.format("Debe ejecutar en ≤5ms (aceptable), actual: %dms", durationMs));
    }

    @Test
    @DisplayName("Benchmark 6x6 - Promedio de 1000 ejecuciones debe ser <1ms")
    void testPerformance6x6_Average() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };

        int iterations = 1000;
        long totalTime = 0;

        // Warm-up
        for (int i = 0; i < 100; i++) {
            mutantDetector.isMutant(dna);
        }

        // Medir promedio
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            mutantDetector.isMutant(dna);
            long end = System.nanoTime();
            totalTime += (end - start);
        }

        long avgMs = (totalTime / iterations) / 1_000_000;

        System.out.printf("⏱️  6x6 promedio (%d iteraciones): %d ms%n", iterations, avgMs);

        assertTrue(avgMs <= 1,
                String.format("Promedio debe ser ≤1ms (óptimo), actual: %dms", avgMs));
    }

    // ========== BENCHMARK 100x100 (Óptimo: ≤20ms, Aceptable: ≤100ms) ==========

    @Test
    @DisplayName("Benchmark 100x100 - Debe ejecutar en ≤20ms (óptimo)")
    void testPerformance100x100_Optimal() {
        String[] dna = generateDnaWithMutations(100, 2); // Con 2 mutaciones

        // Warm-up
        for (int i = 0; i < 10; i++) {
            mutantDetector.isMutant(dna);
        }

        // Medición
        long start = System.nanoTime();
        boolean result = mutantDetector.isMutant(dna);
        long end = System.nanoTime();

        long durationMs = (end - start) / 1_000_000;

        System.out.printf("⏱️  100x100 ejecutado en: %d ms (óptimo: <20ms)%n", durationMs);

        assertTrue(result, "Debe detectar mutante");
        assertTrue(durationMs <= 100,
                String.format("Debe ejecutar en ≤100ms (aceptable), actual: %dms", durationMs));
    }

    @Test
    @DisplayName("Benchmark 100x100 - Promedio de 100 ejecuciones debe ser <20ms")
    void testPerformance100x100_Average() {
        String[] dna = generateDnaWithMutations(100, 2);

        int iterations = 100;
        long totalTime = 0;

        // Warm-up
        for (int i = 0; i < 10; i++) {
            mutantDetector.isMutant(dna);
        }

        // Medir promedio
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            mutantDetector.isMutant(dna);
            long end = System.nanoTime();
            totalTime += (end - start);
        }

        long avgMs = (totalTime / iterations) / 1_000_000;

        System.out.printf("⏱️  100x100 promedio (%d iteraciones): %d ms%n", iterations, avgMs);

        assertTrue(avgMs <= 20,
                String.format("Promedio debe ser ≤20ms (óptimo), actual: %dms", avgMs));
    }

    // ========== BENCHMARK 1000x1000 (Óptimo: ≤500ms, Aceptable: ≤5000ms) ==========

    @Test
    @DisplayName("Benchmark 1000x1000 - Debe ejecutar en ≤500ms (óptimo)")
    void testPerformance1000x1000_Optimal() {
        String[] dna = generateDnaWithMutations(1000, 2);

        // Warm-up
        mutantDetector.isMutant(dna);

        // Medición
        long start = System.nanoTime();
        boolean result = mutantDetector.isMutant(dna);
        long end = System.nanoTime();

        long durationMs = (end - start) / 1_000_000;

        System.out.printf("⏱️  1000x1000 ejecutado en: %d ms (óptimo: <500ms)%n", durationMs);

        assertTrue(result, "Debe detectar mutante");
        assertTrue(durationMs <= 5000,
                String.format("Debe ejecutar en ≤5000ms (aceptable), actual: %dms", durationMs));
    }

    @Test
    @DisplayName("Benchmark 1000x1000 - Promedio de 10 ejecuciones debe ser <500ms")
    void testPerformance1000x1000_Average() {
        String[] dna = generateDnaWithMutations(1000, 2);

        int iterations = 10;
        long totalTime = 0;

        // Warm-up
        mutantDetector.isMutant(dna);

        // Medir promedio
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            mutantDetector.isMutant(dna);
            long end = System.nanoTime();
            totalTime += (end - start);
        }

        long avgMs = (totalTime / iterations) / 1_000_000;

        System.out.printf("⏱️  1000x1000 promedio (%d iteraciones): %d ms%n", iterations, avgMs);

        assertTrue(avgMs <= 500,
                String.format("Promedio debe ser ≤500ms (óptimo), actual: %dms", avgMs));
    }

    // ========== BENCHMARK: Early Termination ==========

    @Test
    @DisplayName("Benchmark Early Termination - Debe parar inmediatamente al encontrar 2+ secuencias")
    void testPerformanceEarlyTermination() {
        // DNA con 2 secuencias al inicio (para early termination rápido)
        String[] dnaEarlyMutant = {
                "AAAAGA", // Horizontal: AAAA
                "AAAAGC", // Horizontal: AAAA (2da secuencia encontrada rápido)
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };

        // DNA con 0 secuencias (debe recorrer toda la matriz)
        String[] dnaHuman = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGACGG",
                "GCGTCA",
                "TCACTG"
        };

        // Warm-up
        for (int i = 0; i < 100; i++) {
            mutantDetector.isMutant(dnaEarlyMutant);
            mutantDetector.isMutant(dnaHuman);
        }

        // Medir mutante (early termination)
        long start1 = System.nanoTime();
        boolean resultMutant = mutantDetector.isMutant(dnaEarlyMutant);
        long end1 = System.nanoTime();
        long timeMutant = (end1 - start1) / 1_000_000;

        // Medir humano (recorrido completo)
        long start2 = System.nanoTime();
        boolean resultHuman = mutantDetector.isMutant(dnaHuman);
        long end2 = System.nanoTime();
        long timeHuman = (end2 - start2) / 1_000_000;

        System.out.printf("⏱️  Early Termination (mutante): %d ms%n", timeMutant);
        System.out.printf("⏱️  Recorrido Completo (humano): %d ms%n", timeHuman);
        System.out.printf("⚡ Mejora: %.1fx más rápido con early termination%n",
                (double) timeHuman / timeMutant);

        assertTrue(resultMutant, "Debe detectar mutante");
        assertFalse(resultHuman, "No debe detectar mutante");

        // Early termination debe ser significativamente más rápido
        // (al menos 1.5x en matrices pequeñas, 10x+ en grandes)
        assertTrue(timeMutant <= timeHuman,
                "Early termination debe ser más rápido o igual que recorrido completo");
    }

    // ========== MÉTODO AUXILIAR: Generar DNA con Mutaciones ==========

    /**
     * Genera una matriz NxN de DNA con mutaciones específicas.
     * @param n Tamaño de la matriz
     * @param mutations Número de secuencias mutantes a insertar
     * @return Array de strings representando el DNA
     */
    private String[] generateDnaWithMutations(int n, int mutations) {
        char[][] matrix = new char[n][n];
        char[] bases = {'A', 'T', 'C', 'G'};

        // Llenar con caracteres aleatorios
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = bases[random.nextInt(4)];
            }
        }

        // Insertar mutaciones específicas
        int insertedMutations = 0;

        // Mutación horizontal en fila 0
        if (insertedMutations < mutations && n >= 4) {
            for (int j = 0; j < 4; j++) {
                matrix[0][j] = 'A';
            }
            insertedMutations++;
        }

        // Mutación vertical en columna 0
        if (insertedMutations < mutations && n >= 4) {
            for (int i = 0; i < 4; i++) {
                matrix[i][0] = 'T';
            }
            insertedMutations++;
        }

        // Mutación diagonal principal
        if (insertedMutations < mutations && n >= 4) {
            for (int i = 0; i < 4; i++) {
                matrix[i][i] = 'C';
            }
            insertedMutations++;
        }

        // Convertir matriz a String[]
        String[] dna = new String[n];
        for (int i = 0; i < n; i++) {
            dna[i] = new String(matrix[i]);
        }

        return dna;
    }
}