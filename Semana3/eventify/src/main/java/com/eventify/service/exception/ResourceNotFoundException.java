package com.eventify.service.exception;

/**
 * Excepcion de negocio lanzada cuando se consulta, actualiza o elimina
 * un recurso (Event/Venue) cuyo ID no existe en la base de datos.
 * Es traducida por GlobalExceptionHandler en un 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

}
