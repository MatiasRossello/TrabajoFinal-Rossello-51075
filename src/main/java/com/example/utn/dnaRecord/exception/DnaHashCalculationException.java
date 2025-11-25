package com.example.utn.dnaRecord.exception;

/**
 * Excepción lanzada cuando ocurre un error al calcular el hash SHA-256 del ADN.
 */
public class DnaHashCalculationException extends RuntimeException {

    public DnaHashCalculationException(String message) {
        super(message);
    }

    public DnaHashCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}