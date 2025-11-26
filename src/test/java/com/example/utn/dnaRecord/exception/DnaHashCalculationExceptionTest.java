package com.example.utn.dnaRecord.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DnaHashCalculationExceptionTest {

    @Test
    @DisplayName("Constructor con mensaje debe crear excepción correctamente")
    void testConstructorWithMessage() {
        String errorMessage = "Error al calcular hash";
        
        DnaHashCalculationException exception = new DnaHashCalculationException(errorMessage);
        
        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Constructor con mensaje y causa debe crear excepción correctamente")
    void testConstructorWithMessageAndCause() {
        String errorMessage = "Error al calcular hash SHA-256";
        Throwable cause = new RuntimeException("Algoritmo no disponible");
        
        DnaHashCalculationException exception = new DnaHashCalculationException(errorMessage, cause);
        
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
