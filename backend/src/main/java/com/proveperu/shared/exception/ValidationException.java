package com.proveperu.shared.exception;

/**
 * Excepción de negocio utilizada para representar
 * incumplimientos de reglas de validación funcional
 * dentro de la aplicación.
 *
 * <p>
 * Esta excepción debe utilizarse cuando los datos recibidos
 * superan las validaciones técnicas (Bean Validation),
 * pero incumplen restricciones propias del dominio de negocio.
 * </p>
 *
 * <p>
 * Algunos ejemplos son:
 * </p>
 * <ul>
 *     <li>Rangos de fechas inválidos.</li>
 *     <li>Métodos de pago incompatibles.</li>
 *     <li>Totales inconsistentes.</li>
 *     <li>Estados de negocio no permitidos.</li>
 * </ul>
 *
 * <p>
 * Es procesada por {@link GlobalExceptionHandler},
 * transformándose en una respuesta HTTP 400
 * (Bad Request) con información funcional para el cliente.
 * </p>
 */
public class ValidationException extends RuntimeException {

    /**
     * Identificador de versión para serialización.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construye una excepción de validación funcional.
     *
     * @param message mensaje descriptivo del error detectado.
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Construye una excepción de validación funcional
     * asociada a una causa raíz.
     *
     * @param message mensaje descriptivo del error detectado.
     * @param cause excepción original que provocó el error.
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}