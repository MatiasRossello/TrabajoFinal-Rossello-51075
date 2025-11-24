package com.example.utn.dnaRecord.service;

import org.springframework.stereotype.Service;

@Service
public class MutantDetector {

    /**
     * Determina si una secuencia de ADN pertenece a un mutante.
     * Un humano es mutante si tiene MÁS DE UNA secuencia de 4 letras iguales.
     */
    public boolean isMutant(String[] dna) {
        if (dna == null || dna.length == 0) {
            return false;
        }

        int n = dna.length;
        int sequenceCount = 0;

        // Conversión a char[][] para acceso rápido
        char[][] matrix = new char[n][n];
        for (int i = 0; i < n; i++) {
            matrix[i] = dna[i].toCharArray();
        }

        // Búsqueda de secuencias
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Early Termination: Si ya encontramos >1, cortamos
                if (sequenceCount > 1) return true;

                char currentChar = matrix[i][j];

                // 1. Horizontal
                if (j <= n - 4) {
                    if (currentChar == matrix[i][j+1] &&
                            currentChar == matrix[i][j+2] &&
                            currentChar == matrix[i][j+3]) {
                        sequenceCount++;
                    }
                }

                // 2. Vertical
                if (i <= n - 4) {
                    if (currentChar == matrix[i+1][j] &&
                            currentChar == matrix[i+2][j] &&
                            currentChar == matrix[i+3][j]) {
                        sequenceCount++;
                    }
                }

                // 3. Diagonal Principal (\)
                if (i <= n - 4 && j <= n - 4) {
                    if (currentChar == matrix[i+1][j+1] &&
                            currentChar == matrix[i+2][j+2] &&
                            currentChar == matrix[i+3][j+3]) {
                        sequenceCount++;
                    }
                }

                // 4. Diagonal Inversa (/)
                if (i <= n - 4 && j >= 3) {
                    if (currentChar == matrix[i+1][j-1] &&
                            currentChar == matrix[i+2][j-2] &&
                            currentChar == matrix[i+3][j-3]) {
                        sequenceCount++;
                    }
                }
            }
        }
        return sequenceCount > 1;
    }
}