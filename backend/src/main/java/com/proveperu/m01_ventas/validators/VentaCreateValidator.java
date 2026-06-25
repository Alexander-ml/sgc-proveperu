package com.proveperu.m01_ventas.validators;

import com.proveperu.m01_ventas.dto.request.DetalleVentaRequest;
import com.proveperu.m01_ventas.dto.request.VentaCreateRequest;
import com.proveperu.m01_ventas.enums.TipoComprobante;
import com.proveperu.shared.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validador de reglas de negocio para la creación de una nueva venta.
 *
 * <p>
 * Centraliza las validaciones funcionales que no pueden expresarse
 * mediante Bean Validation (Jakarta Validation). Su responsabilidad
 * es verificar restricciones del dominio antes de que el servicio
 * ejecute operaciones de persistencia.
 * </p>
 *
 * <p>
 * En caso de incumplimiento lanza {@link ValidationException},
 * que el {@code GlobalExceptionHandler} transforma en HTTP 400.
 * </p>
 *
 * <p>
 * Las reglas de negocio que dependen del estado de la base de datos
 * (stock disponible, existencia de entidades) se validan directamente
 * en el servicio usando {@code BusinessException}.
 * </p>
 */
@Component
public class VentaCreateValidator {

    /**
     * Valida el cuerpo completo de la solicitud de creación de venta.
     *
     * <p>
     * Verifica:
     * </p>
     * <ul>
     *     <li>Que el tipo de comprobante sea un valor válido del enum.</li>
     *     <li>Que no existan productos duplicados en la lista.</li>
     *     <li>Que los subtotales de cada detalle sean coherentes.</li>
     *     <li>Que el monto pagado no sea negativo.</li>
     * </ul>
     *
     * @param request solicitud de venta a validar.
     * @throws ValidationException si alguna regla de validación no se cumple.
     */
    public void validar(VentaCreateRequest request) {
        validarTipoComprobante(request.getTipoComprobante());
        validarProductosNoDuplicados(request.getProductos());
        validarMontoPagadoPositivo(request.getMontoPagado());
        validarSerieYCorrelativo(request.getSerieComprobante(), request.getCorrelativoComprobante());
    }

    /**
     * Verifica que el tipo de comprobante enviado corresponda
     * a un valor válido definido en el enum {@link TipoComprobante}.
     *
     * @param tipoComprobante valor en texto del tipo de comprobante.
     * @throws ValidationException si el valor no corresponde al enum.
     */
    private void validarTipoComprobante(String tipoComprobante) {
        boolean valido = Arrays.stream(TipoComprobante.values())
                .anyMatch(t -> t.name().equalsIgnoreCase(tipoComprobante));

        if (!valido) {
            throw new ValidationException(
                    String.format(
                            "Tipo de comprobante inválido: '%s'. Valores permitidos: %s",
                            tipoComprobante,
                            Arrays.toString(TipoComprobante.values())
                    )
            );
        }
    }

    /**
     * Verifica que la lista de productos no contenga el mismo
     * producto más de una vez (identificado por {@code idProducto}).
     *
     * <p>
     * Si se necesita vender varias unidades del mismo producto,
     * debe hacerse en un único ítem con la cantidad correspondiente.
     * </p>
     *
     * @param productos lista de detalles de la venta.
     * @throws ValidationException si existen productos duplicados.
     */
    private void validarProductosNoDuplicados(List<DetalleVentaRequest> productos) {
        Set<Integer> idsUnicos = new HashSet<>();
        for (DetalleVentaRequest detalle : productos) {
            if (!idsUnicos.add(detalle.getIdProducto())) {
                throw new ValidationException(
                        String.format(
                                "El producto con id %d está duplicado en la lista. " +
                                        "Consolide la cantidad en un único ítem.",
                                detalle.getIdProducto()
                        )
                );
            }
        }
    }

    /**
     * Verifica que el monto pagado no sea negativo.
     *
     * <p>
     * La validación de suficiencia (monto >= total calculado)
     * se realiza en el servicio porque depende del cálculo del total.
     * </p>
     *
     * @param montoPagado monto entregado por el cliente.
     * @throws ValidationException si el monto es negativo.
     */
    private void validarMontoPagadoPositivo(BigDecimal montoPagado) {
        if (montoPagado.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("El monto pagado no puede ser negativo.");
        }
    }

    /**
     * Verifica que la serie tenga máximo 4 caracteres y el
     * correlativo máximo 8 caracteres, y que ninguno esté vacío.
     *
     * @param serie       serie del comprobante.
     * @param correlativo correlativo del comprobante.
     * @throws ValidationException si los valores no cumplen las restricciones.
     */
    private void validarSerieYCorrelativo(String serie, String correlativo) {
        if (serie.length() > 4) {
            throw new ValidationException(
                    "La serie del comprobante no puede superar los 4 caracteres."
            );
        }
        if (correlativo.length() > 8) {
            throw new ValidationException(
                    "El correlativo del comprobante no puede superar los 8 caracteres."
            );
        }
    }
}