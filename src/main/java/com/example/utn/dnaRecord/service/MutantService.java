package com.example.utn.dnaRecord.service;

import com.example.utn.dnaRecord.model.DnaRecord;
import com.example.utn.dnaRecord.repository.DnaRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class MutantService {

    private final DnaRecordRepository dnaRecordRepository;
    private static final Pattern DNA_VALIDO = Pattern.compile("^[ATCG]+$");

    @Autowired
    public MutantService(DnaRecordRepository dnaRecordRepository) {
        this.dnaRecordRepository = dnaRecordRepository;
    }

    public boolean analyzeDna(String[] dna) {
        // 1. Calcular el Hash SHA-256 del ADN
        String dnaHash = calculateDnaHash(dna);

        // 2. Verificar si ya existe en la Base de Datos usando el Hash
        Optional<DnaRecord> existingRecord = dnaRecordRepository.findByDnaHash(dnaHash);

        if (existingRecord.isPresent()) {
            // Si existe, devolvemos el resultado guardado
            return existingRecord.get().getIsMutant();
        }

        // 3. Si no existe, calculamos si es mutante
        boolean isMutant = isMutant(dna);

        // 4. Guardamos el nuevo registro con su Hash
        DnaRecord newRecord = new DnaRecord();
        newRecord.setDnaHash(dnaHash);
        newRecord.setIsMutant(isMutant);
        dnaRecordRepository.save(newRecord);

        return isMutant;
    }

    private String calculateDnaHash(String[] dna) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Convertimos el array a un único String para hashear
            String dnaSequence = String.join("", dna);
            byte[] encodedhash = digest.digest(dnaSequence.getBytes());

            // Convertir bytes a Hexadecimal
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

    // --- Lógica de Detección (Tu algoritmo original) ---
    private boolean isMutant(String[] dna) {
        validateDna(dna);
        int n = dna.length;
        int sequenceCount = 0;

        char[][] matrix = new char[n][n];
        for (int i = 0; i < n; i++) {
            matrix[i] = dna[i].toCharArray();
        }

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
                // Diagonales
                if (i <= n - 4) {
                    if (j <= n - 4) {
                        if (currentChar == matrix[i+1][j+1] &&
                                currentChar == matrix[i+2][j+2] &&
                                currentChar == matrix[i+3][j+3]) {
                            sequenceCount++;
                        }
                    }
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

    private void validateDna(String[] dna) {
        if (dna == null) throw new IllegalArgumentException("El array de ADN no puede ser nulo");
        int n = dna.length;
        if (n == 0) throw new IllegalArgumentException("El array de ADN no puede estar vacío");
        for (String row : dna) {
            if (row == null) throw new IllegalArgumentException("El ADN no puede tener filas nulas");
            if (row.length() != n) throw new IllegalArgumentException("La matriz debe ser cuadrada (NxN)");
            if (!DNA_VALIDO.matcher(row).matches()) throw new IllegalArgumentException("El ADN contiene caracteres inválidos (Solo A, T, C, G)");
        }
    }
}