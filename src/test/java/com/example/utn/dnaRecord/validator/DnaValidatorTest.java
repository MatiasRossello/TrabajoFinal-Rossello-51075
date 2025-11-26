package com.example.utn.dnaRecord.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para DnaValidator - cubrir casos faltantes
 */
class DnaValidatorTest {

    private DnaValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new DnaValidator();
    }

    @Test
    @DisplayName("DNA válido - matriz 6x6 correcta")
    void testValidDna() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertTrue(validator.isValid(dna, context));
    }

    @Test
    @DisplayName("DNA nulo retorna false")
    void testNullDna() {
        assertFalse(validator.isValid(null, context));
    }

    @Test
    @DisplayName("DNA vacío retorna false")
    void testEmptyDna() {
        String[] dna = {};
        assertFalse(validator.isValid(dna, context));
    }

    @Test
    @DisplayName("Fila nula retorna false")
    void testNullRow() {
        String[] dna = {
                "ATGCGA",
                null,
                "TTATGT"
        };
        assertFalse(validator.isValid(dna, context));
    }

    @Test
    @DisplayName("Matriz no cuadrada retorna false")
    void testNonSquareMatrix() {
        String[] dna = {
                "ATGCGA",
                "CAGTG",  // Solo 5 caracteres en una matriz 6xN
                "TTATGT"
        };
        assertFalse(validator.isValid(dna, context));
    }

    @Test
    @DisplayName("Caracteres inválidos retorna false")
    void testInvalidCharacters() {
        String[] dna = {
                "ATGCGA",
                "CAXTGC",  // X es inválido
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertFalse(validator.isValid(dna, context));
    }

    @Test
    @DisplayName("Letras minúsculas son inválidas")
    void testLowercaseInvalid() {
        String[] dna = {
                "atgcga",  // minúsculas
                "cagtgc",
                "ttatgt",
                "agaagg"
        };
        assertFalse(validator.isValid(dna, context));
    }

    @Test
    @DisplayName("Números son inválidos")
    void testNumbersInvalid() {
        String[] dna = {
                "ATG1GA",  // contiene número
                "CAGTGC",
                "TTATGT",
                "AGAAGG"
        };
        assertFalse(validator.isValid(dna, context));
    }

    @Test
    @DisplayName("Espacios son inválidos")
    void testSpacesInvalid() {
        String[] dna = {
                "ATG GA",  // contiene espacio
                "CAGTGC",
                "TTATGT",
                "AGAAGG"
        };
        assertFalse(validator.isValid(dna, context));
    }

    @Test
    @DisplayName("Matriz 4x4 válida")
    void testValid4x4() {
        String[] dna = {
                "ATGC",
                "CAGT",
                "TTAT",
                "AGAC"
        };
        assertTrue(validator.isValid(dna, context));
    }

    @Test
    @DisplayName("Matriz 1x1 válida")
    void testValid1x1() {
        String[] dna = {"A"};
        assertTrue(validator.isValid(dna, context));
    }
}
