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
            // Corrección Error 2 (NullRow): Si una fila es null, no es válido
            if (dna[i] == null) {
                return false;
            }

            // Corrección Error 1 (InvalidCharacters): Si tiene letras raras, no es válido
            // Verifica también que la matriz sea cuadrada (longitud de fila == n)
            if (dna[i].length() != n || !VALID_DNA_PATTERN.matcher(dna[i]).matches()) {
                return false;
            }

            matrix[i] = dna[i].toCharArray();
        }

        // 3. Lógica de Búsqueda (Tu algoritmo original)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (sequenceCount > 1) return true;

                char currentChar = matrix[i][j];

                // Horizontal
                if (j <= n - 4) {
                    if (currentChar == matrix[i][j+1] &&
                            currentChar == matrix[i][j+2] &&
                            currentChar == matrix[i][j+3]) {
                        sequenceCount++;
                    }
                }
                // Vertical
                if (i <= n - 4) {
                    if (currentChar == matrix[i+1][j] &&
                            currentChar == matrix[i+2][j] &&
                            currentChar == matrix[i+3][j]) {
                        sequenceCount++;
                    }
                }
                // Diagonal Principal
                if (i <= n - 4 && j <= n - 4) {
                    if (currentChar == matrix[i+1][j+1] &&
                            currentChar == matrix[i+2][j+2] &&
                            currentChar == matrix[i+3][j+3]) {
                        sequenceCount++;
                    }
                }
                // Diagonal Inversa
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