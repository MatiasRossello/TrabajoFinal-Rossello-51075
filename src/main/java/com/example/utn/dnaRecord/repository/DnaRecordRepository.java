package com.example.utn.dnaRecord.repository;

import com.example.utn.dnaRecord.model.DnaRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DnaRecordRepository extends JpaRepository<DnaRecord, Long> {

public List<DnaRecord> findByIsMutant(boolean isMutant);

public Optional<DnaRecord> findByDna(String dna);

public Long countByIsMutant(boolean isMutant);


}
