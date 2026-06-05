package com.proveperu.shared.exception;

/**
 * Excepción de negocio utilizada para indicar que un recurso solicitado
 * no existe o no pudo ser localizado dentro del sistema.
 *
 * <p>
 * Es interceptada por el manejador global de excepciones
 * ({@code GlobalExceptionHandler}), siendo transformada en una respuesta
 * HTTP 404 (Not Found) para mantener conformidad con los principios REST.
 * </p>
 */
public class ResourceNotFoundException extends RuntimeException{

    /**
     * Crea una nueva excepción de recurso no encontrado.
     *
     * @param message descripción funcional del recurso que no pudo ser localizado.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
