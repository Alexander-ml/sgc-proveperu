package com.proveperu.m01_ventas.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO de respuesta que representa el resumen de una venta
 * utilizado en la grilla principal del módulo de ventas.
 *
 * <p>
 * Este objeto no representa la entidad de dominio, sino una proyección
 * optimizada para lectura, consolidando información de cliente,
 * pagos, comprobante y vendedor.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaResumenResponseDTO {
    /** Identificador técnico de la venta. */
    private Integer idVenta;

    /**
     * Identificador visual calculado:
     * formato V-{AÑO}-{ID_6_DÍGITOS}
     */
    private String numeroVenta;

    /** Fecha y hora en la que se registró la venta. */
    private LocalDateTime fechaHoraVenta;

    /**
     * Información del cliente asociado.
     * Puede ser null si la venta no tiene cliente definido.
     */
    private ClienteResumenDTO cliente;

    /** Total de la venta. */
    private BigDecimal total;

    /**
     * Lista de métodos de pago asociados a la venta.
     * Nunca debe ser null.
     */
    @Builder.Default
    private List<MetodoPagoVentaResumenDTO> metodosPago = new ArrayList<>();

    /**
     * Información del comprobante generado.
     * Puede ser null si no se emitió comprobante.
     */
    private ComprobanteResumenDTO comprobante;

    /** Estado actual de la venta (REGISTRADA | ANULADA). */
    private String estadoVenta;

    /** Información del usuario que registró la venta. */
    private UsuarioResumenDTO vendedor;

    /** Rol del usuario vendedor. */
    private String tipoVendedor;
}
