package com.proveperu.shared.exception;

/**
 * Excepción utilizada para representar
 * incumplimientos de reglas propias del negocio.
 *
 * <p>
 * Se utiliza cuando los datos son válidos
 * y los recursos existen, pero la operación
 * solicitada no puede ejecutarse debido
 * a restricciones funcionales del dominio.
 * </p>
 *
 * <p>
 * Ejemplos:
 * </p>
 *
 * <ul>
 *     <li>Stock insuficiente.</li>
 *     <li>No existe una caja abierta.</li>
 *     <li>Cliente inactivo.</li>
 *     <li>Comprobante duplicado.</li>
 *     <li>Venta ya anulada.</li>
 * </ul>
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}