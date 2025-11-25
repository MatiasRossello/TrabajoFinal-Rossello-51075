package com.example.utn.dnaRecord.service;

import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
public class MutantDetector {

    // Patrón para validar caracteres válidos (solo A, T, C, G)
    private static final Pattern VALID_DNA_PATTERN = Pattern.compile("^[ATCG]+$");

    public boolean isMutant(String[] dna) {
        // 1. Validación básica de entrada
        if (dna == null || dna.length == 0) {
            return false;
        }

        int n = dna.length;
        int sequenceCount = 0;
        char[][] matrix = new char[n][n];

        // 2. Conversión y Validación Fila por Fila
        for (int i = 0; i < n; i++) {
            if (dna[i] == null) {
                return false;
            }

            // Verifica formato NxN y caracteres válidos
            if (dna[i].length() != n || !VALID_DNA_PATTERN.matcher(dna[i]).matches()) {
                return false;
            }

            matrix[i] = dna[i].toCharArray();
        }

        // 3. Lógica de Búsqueda
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Early termination: si ya encontramos más de 1 secuencia, cortamos.
                if (sequenceCount > 1) return true;

                char currentChar = matrix[i][j];

                // --- HORIZONTAL (→) ---
                if (j <= n - 4) {
                    if (currentChar == matrix[i][j+1] &&
                            currentChar == matrix[i][j+2] &&
                            currentChar == matrix[i][j+3]) {

                        // CORRECCIÓN: Si la celda anterior (izquierda) era igual, ya contamos esta secuencia.
                        if (!(j > 0 && matrix[i][j-1] == currentChar)) {
                            sequenceCount++;
                        }
                    }
                }

                // --- VERTICAL (↓) ---
                if (i <= n - 4) {
                    if (currentChar == matrix[i+1][j] &&
                            currentChar == matrix[i+2][j] &&
                            currentChar == matrix[i+3][j]) {

                        // CORRECCIÓN: Si la celda anterior (arriba) era igual, ya contamos esta secuencia.
                        if (!(i > 0 && matrix[i-1][j] == currentChar)) {
                            sequenceCount++;
                        }
                    }
                }

                // --- DIAGONAL PRINCIPAL (↘) ---
                if (i <= n - 4 && j <= n - 4) {
                    if (currentChar == matrix[i+1][j+1] &&
                            currentChar == matrix[i+2][j+2] &&
                            currentChar == matrix[i+3][j+3]) {

                        // CORRECCIÓN: Si la celda anterior (arriba-izquierda) era igual, ya contamos esta secuencia.
                        if (!(i > 0 && j > 0 && matrix[i-1][j-1] == currentChar)) {
                            sequenceCount++;
                        }
                    }
                }

                // --- DIAGONAL INVERSA (↙) ---
                if (i <= n - 4 && j >= 3) {
                    if (currentChar == matrix[i+1][j-1] &&
                            currentChar == matrix[i+2][j-2] &&
                            currentChar == matrix[i+3][j-3]) {

                        // CORRECCIÓN: Si la celda anterior (arriba-derecha) era igual, ya contamos esta secuencia.
                        if (!(i > 0 && j < n - 1 && matrix[i-1][j+1] == currentChar)) {
                            sequenceCount++;
                        }
                    }
                }
            }
        }

        return sequenceCount > 1;
    }
}