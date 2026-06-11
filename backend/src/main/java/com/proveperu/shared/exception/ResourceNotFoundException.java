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
public class ResourceNotFoundException extends RuntimeException{

    /**
     * Identificador de versión para serialización.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Nombre lógico del recurso no encontrado.
     *
     * <p>
     * Ejemplos:
     * </p>
     * <ul>
     *     <li>Cliente</li>
     *     <li>Venta</li>
     *     <li>Producto</li>
     *     <li>Usuario</li>
     * </ul>
     */
    private final String recurso;

    /**
     * Valor utilizado para buscar el recurso.
     *
     * <p>
     * Puede corresponder a:
     * </p>
     * <ul>
     *     <li>ID</li>
     *     <li>Código</li>
     *     <li>Correo electrónico</li>
     *     <li>Documento de identidad</li>
     * </ul>
     */
    private final Object identificador;

    /**
     * Construye una excepción indicando que un recurso
     * específico no pudo ser encontrado.
     *
     * @param recurso nombre lógico de la entidad buscada.
     * @param identificador valor utilizado en la búsqueda.
     */
    public ResourceNotFoundException(String recurso, Object identificador) {
        super(String.format(
                "%s no encontrado con identificador: %s",
                recurso,
                identificador
        ));

        this.recurso = recurso;
        this.identificador = identificador;
    }

    /**
     * Construye una excepción utilizando un mensaje
     * personalizado.
     *
     * @param mensaje descripción funcional del error.
     */
    public ResourceNotFoundException(String mensaje) {
        super(mensaje);

        this.recurso = null;
        this.identificador = null;
    }

    /**
     * Obtiene el nombre lógico del recurso.
     *
     * @return nombre del recurso buscado.
     */
    public String getRecurso() {
        return recurso;
    }

    /**
     * Obtiene el identificador utilizado durante la búsqueda.
     *
     * @return identificador asociado al recurso.
     */
    public Object getIdentificador() {
        return identificador;
    }
}
