package com.example.utn.dnaRecord.service;

import com.example.utn.dnaRecord.entity.DnaRecord;
import com.example.utn.dnaRecord.repository.DnaRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@RequiredArgsConstructor

@Service
public class MutantService {

    private final DnaRecordRepository dnaRecordRepository;
    private final MutantDetector mutantDetector;


    public boolean analyzeDna(String[] dna) {
        // 1. Calcular Hash
        String dnaHash = calculateDnaHash(dna);

        // 2. Verificar Cache
        Optional<DnaRecord> existingRecord = dnaRecordRepository.findByDnaHash(dnaHash);
        if (existingRecord.isPresent()) {
            return existingRecord.get().getIsMutant();
        }

        // 3. CAMBIO: Delegar al MutantDetector
        boolean isMutant = mutantDetector.isMutant(dna); // ← CAMBIO: antes era this.isMutant(dna)

        // 4. Guardar resultado
        DnaRecord newRecord = new DnaRecord();
        newRecord.setDnaHash(dnaHash);
        newRecord.setIsMutant(isMutant);
        dnaRecordRepository.save(newRecord);

        return isMutant;
    }

    // ELIMINAR: el método isMutant() que tenías aquí
    // Ya no es necesario, está en MutantDetector

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