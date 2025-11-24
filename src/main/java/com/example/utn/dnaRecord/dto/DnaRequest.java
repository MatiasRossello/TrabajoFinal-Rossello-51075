package com.example.utn.dnaRecord.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DnaRequest {

    @NotNull
    @NotEmpty
    private String dna;

}
