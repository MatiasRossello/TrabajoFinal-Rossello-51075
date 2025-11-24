package com.example.utn.dnaRecord.controller;

import com.example.utn.dnaRecord.dto.DnaRequestDTO;
import com.example.utn.dnaRecord.dto.StatsResponseDTO;
import com.example.utn.dnaRecord.service.MutantService;
import com.example.utn.dnaRecord.service.StatsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MutantController.class)
public class MutantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MutantService mutantService;

    @MockBean
    private StatsService statsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /mutant - Retorna 200 OK si es Mutante")
    public void testCheckMutant_IsMutant_Returns200() throws Exception {
        // Simulamos que el servicio dice que es MUTANTE
        when(mutantService.analyzeDna(any())).thenReturn(true);

        DnaRequestDTO request = new DnaRequestDTO();
        request.setDna(new String[]{"ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"});

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /mutant - Retorna 403 Forbidden si es Humano")
    public void testCheckMutant_IsHuman_Returns403() throws Exception {
        // Simulamos que el servicio dice que es HUMANO
        when(mutantService.analyzeDna(any())).thenReturn(false);

        DnaRequestDTO request = new DnaRequestDTO();
        request.setDna(new String[]{"ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCTTA","TCACTG"});

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /mutant - Retorna 400 Bad Request si el JSON es inválido")
    public void testCheckMutant_InvalidDna_Returns400() throws Exception {
        DnaRequestDTO request = new DnaRequestDTO();
        request.setDna(null); // Enviamos null para activar @NotNull

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /stats - Retorna estadísticas correctamente")
    public void testGetStats_Returns200AndJson() throws Exception {
        // Simulamos la respuesta del servicio de estadísticas
        StatsResponseDTO statsResponse = new StatsResponseDTO(40, 100, 0.4);
        when(statsService.getStats()).thenReturn(statsResponse);

        mockMvc.perform(get("/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(40))
                .andExpect(jsonPath("$.count_human_dna").value(100))
                .andExpect(jsonPath("$.ratio").value(0.4));
    }
}