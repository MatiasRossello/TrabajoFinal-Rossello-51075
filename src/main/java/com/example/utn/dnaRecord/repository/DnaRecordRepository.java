package com.example.utn.dnaRecord.repository;

import com.example.utn.dnaRecord.model.DnaRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DnaRecordRepository extends JpaRepository<DnaRecord, Long> {

    // Buscamos por el Hash calculado
    Optional<DnaRecord> findByDnaHash(String dnaHash);

    long countByIsMutant(boolean isMutant);
}