package com.example.utn.dnaRecord.service;

import com.example.utn.dnaRecord.repository.DnaRecordRepository;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
public class MutantService {

    private DnaRecordRepository dnaRecordRepository;

    // Regex para validar que solo tenga A, T, C, G
    private static final Pattern DNA_VALIDO = Pattern.compile("^[ATCG]+$");

    public boolean isMutant(String[] dna) {
        // 1. Validaciones previas
        validateDna(dna);

        int n = dna.length;
        int sequenceCount = 0;

        // Convertir a array de char para acceso rápido directo
        char[][] matrix = new char[n][n];
        for (int i = 0; i < n; i++) {
            matrix[i] = dna[i].toCharArray();
        }

        // 2. Recorremos la matriz
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                // Si ya hay más de 1, cortamos.
                if (sequenceCount > 1) {
                    return true;
                }

                char currentChar = matrix[i][j];

                // A. HORIZONTAL (Hacia la derecha)
                // Solo chequeamos si hay espacio para 4 letras (j <= n-4)
                if (j <= n - 4) {
                    if (currentChar == matrix[i][j+1] &&
                            currentChar == matrix[i][j+2] &&
                            currentChar == matrix[i][j+3]) {
                        sequenceCount++;
                    }
                }

                // B. VERTICAL (Hacia abajo)
                // Solo chequeamos si hay espacio hacia abajo (i <= n-4)
                if (i <= n - 4) {
                    if (currentChar == matrix[i+1][j] &&
                            currentChar == matrix[i+2][j] &&
                            currentChar == matrix[i+3][j]) {
                        sequenceCount++;
                    }
                }

                // C. DIAGONALES
                if (i <= n - 4) {
                    // Diagonal Principal (Abajo-Derecha)
                    if (j <= n - 4) {
                        if (currentChar == matrix[i+1][j+1] &&
                                currentChar == matrix[i+2][j+2] &&
                                currentChar == matrix[i+3][j+3]) {
                            sequenceCount++;
                        }
                    }

                    // Diagonal Inversa (Abajo-Izquierda)
                    if (j >= 3) {
                        if (currentChar == matrix[i+1][j-1] &&
                                currentChar == matrix[i+2][j-2] &&
                                currentChar == matrix[i+3][j-3]) {
                            sequenceCount++;
                        }
                    }
                }
            }
        }

        // Si terminamos y contador > 1, es mutante
        return sequenceCount > 1;
    }

    private void validateDna(String[] dna) {
        if (dna == null) {
            throw new IllegalArgumentException("El array de ADN no puede ser nulo");
        }
        int n = dna.length;
        if (n == 0) {
            throw new IllegalArgumentException("El array de ADN no puede estar vacío");
        }
        for (String row : dna) {
            if (row == null) {
                throw new IllegalArgumentException("El ADN no puede tener filas nulas");
            }
            if (row.length() != n) {
                throw new IllegalArgumentException("La matriz debe ser cuadrada (NxN)");
            }
            if (!DNA_VALIDO.matcher(row).matches()) {
                throw new IllegalArgumentException("El ADN contiene caracteres inválidos (Solo A, T, C, G)");
            }
        }
    }
}