package com.example.utn.dnaRecord.service;

import com.example.utn.dnaRecord.dto.StatsResponseDTO;
import com.example.utn.dnaRecord.repository.DnaRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StatsService {

    private final DnaRecordRepository dnaRecordRepository;

    public StatsResponseDTO getStats() {
        // 1. Consultar a la base de datos los totales
        long countMutant = dnaRecordRepository.countByIsMutant(true);
        long countHuman = dnaRecordRepository.countByIsMutant(false);

        // 2. Calcular el ratio
        double ratio = 0.0;
        if (countHuman > 0) {
            ratio = (double) countMutant / countHuman;
        } else if (countMutant > 0) {
            ratio = 1.0;
        }

        // 3. Retornar el DTO con los datos
        return new StatsResponseDTO(countMutant, countHuman, ratio);
    }
}