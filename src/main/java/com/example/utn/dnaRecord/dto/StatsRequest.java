package com.example.utn.dnaRecord.dto;

import lombok.Data;

@Data
public class StatsRequest {

    private Long contadorMutantes;
    private Long contadorHumanos;
    private Double ratio;

}
