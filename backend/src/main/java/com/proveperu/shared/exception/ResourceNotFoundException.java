package com.proveperu.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

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
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{

    /**
     * Crea una nueva excepción de recurso no encontrado.
     *
     * @param message descripción funcional del recurso que no pudo ser localizado.
     */
    private final String recurso;
    private final Object identificador;

    public ResourceNotFoundException(String recurso, Object identificador) {
        super(String.format("%s no encontrado con identificador: %s", recurso, identificador));
        this.recurso       = recurso;
        this.identificador = identificador;
    }

    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
        this.recurso       = null;
        this.identificador = null;
    }

    public String getRecurso() {
        return recurso;
    }

    public Object getIdentificador() {
        return identificador;
    }
}
