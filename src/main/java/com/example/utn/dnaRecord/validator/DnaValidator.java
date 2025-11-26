package com.example.utn.dnaRecord.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class DnaValidator implements ConstraintValidator<ValidDna, String[]> {

    private static final Pattern DNA_VALIDO = Pattern.compile("^[ATCG]+$");

    @Override
    public boolean isValid(String[] dna, ConstraintValidatorContext context) {
        if (dna == null) return false;
        if (dna.length == 0) return false;

        int n = dna.length;
        for (String row : dna) {
            if (row == null) return false;
            if (row.length() != n) return false;
            if (!DNA_VALIDO.matcher(row).matches()) return false;
        }
        return true;
    }
}