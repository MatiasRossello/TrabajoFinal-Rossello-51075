package com.example.utn.dnaRecord.integration;

import com.example.utn.dnaRecord.entity.DnaRecord;
import com.example.utn.dnaRecord.repository.DnaRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de INTEGRACIÓN REAL con base de datos H2.
 * Verifican el flujo completo: Controller → Service → Repository → BD
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Rollback automático después de cada test
class MutantControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DnaRecordRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Limpiar BD antes de cada test
        repository.deleteAll();
    }

    // ========== TESTS DE INTEGRACIÓN POST /mutant ==========

    @Test
    @DisplayName("POST /mutant - Mutante debe guardar en BD con isMutant=true")
    void testMutantSavesToDatabase() throws Exception {
        String jsonRequest = """
            {
                "dna": ["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]
            }
            """;

        // Ejecutar request
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        assertEquals(1, repository.count(), "Debe haber 1 registro en BD");

        DnaRecord saved = repository.findAll().get(0);
        assertNotNull(saved.getDnaHash(), "Debe tener hash");
        assertTrue(saved.getIsMutant(), "Debe estar marcado como mutante");
        assertNotNull(saved.getCreatedAt(), "Debe tener timestamp");
    }

    @Test
    @DisplayName("POST /mutant - Humano debe guardar en BD con isMutant=false")
    void testHumanSavesToDatabase() throws Exception {
        String jsonRequest = """
            {
                "dna": ["ATGCGA","CAGTGC","TTATTT","AGACGG","GCGTCA","TCACTG"]
            }
            """;

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());


        assertEquals(1, repository.count());

        DnaRecord saved = repository.findAll().get(0);
        assertFalse(saved.getIsMutant(), "Debe estar marcado como humano");
    }

    @Test
    @DisplayName("POST /mutant - DNA duplicado debe usar caché (no duplicar en BD)")
    void testDuplicateDnaUsesCache() throws Exception {
        String jsonRequest = """
            {
                "dna": ["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]
            }
            """;

        // Primera llamada
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        assertEquals(1, repository.count(), "Debe haber 1 registro");

        // Segunda llamada con MISMO DNA
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        assertEquals(1, repository.count(),
                "Debe seguir habiendo 1 registro (caché funcionando)");
    }

    @Test
    @DisplayName("POST /mutant - DNA inválido NO debe guardar en BD")
    void testInvalidDnaDoesNotSave() throws Exception {
        String jsonRequest = """
            {
                "dna": ["ATGX","CAGT"]
            }
            """;

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());

        assertEquals(0, repository.count(),
                "No debe guardar DNA inválido");
    }

    // ========== TESTS DE INTEGRACIÓN GET /stats ==========

    @Test
    @DisplayName("GET /stats - Debe reflejar datos REALES de la BD")
    void testStatsReflectsRealDatabase() throws Exception {
        // Insertar 2 mutantes y 3 humanos directamente en BD
        repository.save(createDnaRecord("hash1", true));
        repository.save(createDnaRecord("hash2", true));
        repository.save(createDnaRecord("hash3", false));
        repository.save(createDnaRecord("hash4", false));
        repository.save(createDnaRecord("hash5", false));

        // Consultar /stats
        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(2))
                .andExpect(jsonPath("$.count_human_dna").value(3))
                .andExpect(jsonPath("$.ratio").value(2.0 / 3.0));
    }

    @Test
    @DisplayName("GET /stats - BD vacía debe retornar todo en cero")
    void testStatsWithEmptyDatabase() throws Exception {
        // BD ya está vacía por @BeforeEach

        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(0))
                .andExpect(jsonPath("$.count_human_dna").value(0))
                .andExpect(jsonPath("$.ratio").value(0.0));
    }

    @Test
    @DisplayName("GET /stats - Solo mutantes (sin humanos) debe manejar división por cero")
    void testStatsOnlyMutants() throws Exception {
        repository.save(createDnaRecord("hash1", true));
        repository.save(createDnaRecord("hash2", true));

        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(2))
                .andExpect(jsonPath("$.count_human_dna").value(0))
                .andExpect(jsonPath("$.ratio").value(1.0)); // Según tu lógica
    }

    // ========== TEST DE FLUJO COMPLETO END-TO-END ==========

    @Test
    @DisplayName("E2E - Verificar varios DNAs y luego consultar stats")
    void testEndToEndWorkflow() throws Exception {
        // 1. Enviar 2 mutantes
        String mutant1 = """
            {"dna":["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]}
            """;
        String mutant2 = """
            {"dna":["AAAAGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]}
            """;

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mutant1))
                .andExpect(status().isOk());

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mutant2))
                .andExpect(status().isOk());

        // 2. Enviar 3 humanos
        String human1 = """
            {"dna":["ATGCGA","CAGTGC","TTATTT","AGACGG","GCGTCA","TCACTG"]}
            """;
        String human2 = """
            {"dna":["ATGC","CAGT","TTAT","AGAC"]}
            """;
        String human3 = """
            {"dna":["ATGCG","CAGTG","TTATT","AGACG","GCGTC"]}
            """;

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(human1))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(human2))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(human3))
                .andExpect(status().isForbidden());

        // 3. Verificar BD
        assertEquals(5, repository.count(), "Debe haber 5 registros");

        // 4. Consultar stats y verificar ratio
        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(2))
                .andExpect(jsonPath("$.count_human_dna").value(3))
                .andExpect(jsonPath("$.ratio").value(2.0 / 3.0));
    }

    // ========== MÉTODO AUXILIAR ==========

    private DnaRecord createDnaRecord(String hash, boolean isMutant) {
        DnaRecord record = new DnaRecord();
        record.setDnaHash(hash);
        record.setIsMutant(isMutant);
        return record;
    }
}