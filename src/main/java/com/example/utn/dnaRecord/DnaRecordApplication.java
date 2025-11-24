package com.example.utn.dnaRecord;

import com.example.utn.dnaRecord.service.MutantService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DnaRecordApplication {

	public static void main(String[] args) {
		SpringApplication.run(DnaRecordApplication.class, args);

        MutantService prueba = new MutantService();

        String[] dnaMutant = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCTCTA",
                "CCACTG"
        };

        String[] dnaHuman = {
                "ATGCCA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCTTA",
                "TCACTG"
        };

        System.out.println(prueba.isMutant(dnaMutant));
        System.out.println(prueba.isMutant(dnaHuman));

	}

}
