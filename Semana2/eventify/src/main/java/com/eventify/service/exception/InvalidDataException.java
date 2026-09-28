package com.eventify.service.exception;

/**
 * Excepcion de negocio lanzada por la capa @Service cuando los datos
 * de entrada (Event o Venue) no cumplen las reglas de validacion.
 * Impide que datos corruptos lleguen a la capa @Repository.
 */
public class InvalidDataException extends RuntimeException {

    public InvalidDataException(String message) {
        super(message);
    }

}
