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

    private boolean checkSequence(char[][] matrix, int row, int col, int deltaRow, int deltaCol) {
        final char base = matrix[row][col];
        
        // Comparación directa sin loop - más eficiente
        return matrix[row + deltaRow][col + deltaCol] == base &&
               matrix[row + 2 * deltaRow][col + 2 * deltaCol] == base &&
               matrix[row + 3 * deltaRow][col + 3 * deltaCol] == base;
    }
}