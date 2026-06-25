package com.proveperu.m01_ventas.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de solicitud para registrar una nueva venta completa.
 *
 * <p>
 * Encapsula todos los datos necesarios para procesar la
 * venta: cliente (opcional), tipo de comprobante,
 * detalle de productos y datos del pago.
 * </p>
 *
 * <p>
 * Se utiliza como cuerpo del endpoint {@code POST /ventas}.
 * Las validaciones complejas de negocio (stock suficiente,
 * existencia de referencias, monto pagado) se realizan
 * en la capa de servicio.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
public class VentaCreateRequest {

    /**
     * Identificador del cliente asociado a la venta.
     * Es opcional: si es nulo o cero, la venta se registra
     * sin cliente asociado (venta informal).
     * Si se proporciona, debe existir y estar activo.
     */
    private Integer idCliente;

    /**
     * Tipo de comprobante a emitir.
     * Valores válidos: "BOLETA", "FACTURA", "NOTA".
     * Requerido para completar la venta (RF-17).
     */
    @NotBlank(message = "El tipo de comprobante es obligatorio")
    private String tipoComprobante;

    /**
     * Lista de productos incluidos en la venta.
     * Debe contener al menos un elemento.
     * Cada elemento se valida de forma individual con @Valid.
     */
    @NotEmpty(message = "La venta debe contener al menos un producto")
    @Valid
    private List<DetalleVentaRequest> productos;

    /**
     * Identificador del método de pago utilizado.
     * Debe existir y estar activo en el sistema.
     */
    @NotNull(message = "El método de pago es obligatorio")
    private Integer idMetodoPago;

    /**
     * Monto total entregado por el cliente.
     * Debe ser mayor o igual a cero.
     * La validación de suficiencia (monto >= total venta)
     * se realiza en la capa de servicio (RF-15).
     */
    @NotNull(message = "El monto pagado es obligatorio")
    @DecimalMin(value = "0.00", message = "El monto pagado no puede ser negativo")
    private BigDecimal montoPagado;

    /**
     * Serie del comprobante a emitir.
     * Longitud máxima de 4 caracteres.
     */
    @NotBlank(message = "La serie del comprobante es obligatoria")
    private String serieComprobante;

    /**
     * Correlativo del comprobante a emitir.
     * Longitud máxima de 8 caracteres.
     */
    @NotBlank(message = "El correlativo del comprobante es obligatorio")
    private String correlativoComprobante;
}