package com.proveperu.m01_ventas.validators;

import com.proveperu.shared.exception.ValidationException;
import org.springframework.stereotype.Component;

/**
 * Validador funcional para el endpoint de detalle de venta.
 *
 * <p>
 * Centraliza la validación del identificador técnico recibido
 * como path variable, garantizando que sea un valor positivo
 * antes de delegar al service.
 * </p>
 */
@Component
public class VentaDetalleValidator {
    /**
     * Valida que el identificador de venta sea un valor positivo.
     *
     * @param idVenta identificador técnico recibido desde la capa web.
     * @throws ValidationException si el identificador no es válido.
     */
    public void validar(Integer idVenta) {
        if (idVenta == null) {
            throw new ValidationException("El identificador de la venta es obligatorio.");}

        if (idVenta <= 0) {
            throw new ValidationException("El identificador de la venta debe ser un valor positivo.");
        }
    }
}
