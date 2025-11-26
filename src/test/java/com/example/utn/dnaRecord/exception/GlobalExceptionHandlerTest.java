package com.example.utn.dnaRecord.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests para GlobalExceptionHandler - cubrir casos faltantes
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Manejo de IllegalArgumentException")
    void testHandleIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("Argumento inválido");
        
        ResponseEntity<String> response = exceptionHandler.handleIllegalArgument(exception);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Argumento inválido", response.getBody());
    }

    @Test
    @DisplayName("Manejo de MethodArgumentNotValidException")
    void testHandleMethodArgumentNotValidException() {
        // Crear mock de MethodArgumentNotValidException
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("dnaRequest", "dna", "La secuencia de ADN no puede ser nula");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));
        
        ResponseEntity<String> response = exceptionHandler.handleValidationExceptions(exception);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("La secuencia de ADN no puede ser nula", response.getBody());
    }

    @Test
    @DisplayName("Manejo de DnaHashCalculationException")
    void testHandleDnaHashCalculationException() {
        DnaHashCalculationException exception = new DnaHashCalculationException(
                "Error al calcular hash", 
                new RuntimeException("Causa raíz")
        );
        
        ResponseEntity<String> response = exceptionHandler.handleDnaHashCalculationException(exception);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error interno al procesar el ADN"));
        assertTrue(response.getBody().contains("Error al calcular hash"));
    }

    @Test
    @DisplayName("DnaHashCalculationException conserva mensaje y causa")
    void testDnaHashCalculationExceptionWithCause() {
        Throwable cause = new RuntimeException("Algoritmo SHA-256 no disponible");
        DnaHashCalculationException exception = new DnaHashCalculationException(
                "No se pudo calcular el hash", 
                cause
        );
        
        assertEquals("No se pudo calcular el hash", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Validación con mensaje vacío")
    void testValidationExceptionWithEmptyMessage() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("dnaRequest", "dna", "");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));
        
        ResponseEntity<String> response = exceptionHandler.handleValidationExceptions(exception);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
