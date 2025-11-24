package com.example.utn.dnaRecord.service;

import com.example.utn.dnaRecord.model.DnaRecord;
import com.example.utn.dnaRecord.repository.DnaRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class MutantService {

    private final DnaRecordRepository dnaRecordRepository;

    @Autowired
    public MutantService(DnaRecordRepository dnaRecordRepository) {
        this.dnaRecordRepository = dnaRecordRepository;
    }

    public boolean analyzeDna(String[] dna) {
        // 1. Calcular Hash para búsqueda eficiente
        String dnaHash = calculateDnaHash(dna);

        // 2. Verificar Cache (Base de Datos)
        Optional<DnaRecord> existingRecord = dnaRecordRepository.findByDnaHash(dnaHash);
        if (existingRecord.isPresent()) {
            return existingRecord.get().getIsMutant();
        }

        // 3. Calcular si es mutante (Lógica principal)
        boolean isMutant = isMutant(dna);

        // 4. Guardar resultado
        DnaRecord newRecord = new DnaRecord();
        newRecord.setDnaHash(dnaHash);
        newRecord.setIsMutant(isMutant);
        dnaRecordRepository.save(newRecord);

        return isMutant;
    }

    private boolean isMutant(String[] dna) {
        int n = dna.length;
        int sequenceCount = 0;

        // Optimización: char[][] para acceso rápido
        char[][] matrix = new char[n][n];
        for (int i = 0; i < n; i++) {
            matrix[i] = dna[i].toCharArray();
        }

        // Recorrido de la matriz
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Early Termination: Si ya encontramos más de 1, cortamos.
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

                // 3. Diagonales
                if (i <= n - 4) {
                    // Diagonal Principal (\)
                    if (j <= n - 4) {
                        if (currentChar == matrix[i+1][j+1] &&
                                currentChar == matrix[i+2][j+2] &&
                                currentChar == matrix[i+3][j+3]) {
                            sequenceCount++;
                        }
                    }
                    // Diagonal Inversa (/)
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
        return sequenceCount > 1;
    }

    private String calculateDnaHash(String[] dna) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String dnaSequence = String.join("", dna);
            byte[] encodedhash = digest.digest(dnaSequence.getBytes());

            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al calcular el hash del ADN", e);
        }
    }
}