package com.example.utn.dnaRecord.repository;

import com.example.utn.dnaRecord.entity.DnaRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DnaRecordRepository extends JpaRepository<DnaRecord, Long> {

    /**
     * Busca un registro de ADN por su hash único.
     * 
     * @param dnaHash Hash SHA-256 de la secuencia de ADN
     * @return Optional con el registro encontrado o vacío si no existe
     */
    Optional<DnaRecord> findByDnaHash(String dnaHash);

    /**
     * Cuenta la cantidad de registros según si son mutantes o humanos.
     * 
     * @param isMutant true para contar mutantes, false para contar humanos
     * @return Cantidad de registros que cumplen la condición
     */
    long countByIsMutant(boolean isMutant);
}