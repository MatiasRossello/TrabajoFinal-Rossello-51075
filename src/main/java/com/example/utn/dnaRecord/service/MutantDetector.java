package com.example.utn.dnaRecord.service;

import org.springframework.stereotype.Service;
import java.util.Set;

@Service
public class MutantDetector {

    private static final int SEQUENCE_LENGTH = 4;
    private static final Set<Character> VALID_BASES = Set.of('A', 'T', 'C', 'G');

    public boolean isMutant(String[] dna) {
        // 1. Validación básica
        if (dna == null || dna.length == 0) {
            return false;
        }

        final int n = dna.length;
        int sequenceCount = 0;

        // 2. Conversión y Validación
        char[][] matrix = new char[n][];
        for (int i = 0; i < n; i++) {
            if (dna[i] == null || dna[i].length() != n) {
                return false;
            }

            char[] row = dna[i].toCharArray();
            for (char c : row) {
                if (!VALID_BASES.contains(c)) {
                    return false;
                }
            }
            matrix[i] = row;
        }

        // 3. Búsqueda con Early Termination
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {

                //  Horizontal
                if (col <= n - SEQUENCE_LENGTH) {
                    if (checkSequence(matrix, row, col, 0, 1)) {
                        sequenceCount++;
                        if (sequenceCount > 1) return true; // Early termination
                    }
                }

                //  Vertical
                if (row <= n - SEQUENCE_LENGTH) {
                    if (checkSequence(matrix, row, col, 1, 0)) {
                        sequenceCount++;
                        if (sequenceCount > 1) return true;
                    }
                }

                //  Diagonal descendente (\)
                if (row <= n - SEQUENCE_LENGTH && col <= n - SEQUENCE_LENGTH) {
                    if (checkSequence(matrix, row, col, 1, 1)) {
                        sequenceCount++;
                        if (sequenceCount > 1) return true;
                    }
                }

                //  Diagonal ascendente (/)
                if (row >= SEQUENCE_LENGTH - 1 && col <= n - SEQUENCE_LENGTH) {
                    if (checkSequence(matrix, row, col, -1, 1)) {
                        sequenceCount++;
                        if (sequenceCount > 1) return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Verifica si hay una secuencia de 4 caracteres iguales
     * @param matrix Matriz de ADN
     * @param row Fila inicial
     * @param col Columna inicial
     * @param deltaRow Incremento de fila (-1, 0, 1)
     * @param deltaCol Incremento de columna (-1, 0, 1)
     * @return true si hay secuencia
     */
    private boolean checkSequence(char[][] matrix, int row, int col, int deltaRow, int deltaCol) {
        final char base = matrix[row][col];

        for (int i = 1; i < SEQUENCE_LENGTH; i++) {
            if (matrix[row + i * deltaRow][col + i * deltaCol] != base) {
                return false;
            }
        }

        return true;
    }
}