package com.proveperu.m01_ventas.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaResumenResponseDTO {
    private Integer idVenta;

    /** Identificador visual calculado: V-{AÑO}-{ID_6_DÍGITOS} */
    private String numeroVenta;

    private LocalDateTime fechaHoraVenta;

    /** Puede ser null si la venta no tiene cliente asociado a nivel funcional */
    private ClienteResumenDTO cliente;

    private BigDecimal total;

    /** Lista de pagos activos (estado_logico = 1) */
    private List<MetodoPagoVentaResumenDTO> metodosPago;

    /** Puede ser null si la venta fue registrada sin comprobante formal */
    private ComprobanteResumenDTO comprobante;

    private String estadoVenta;
    private UsuarioResumenDTO vendedor;

    /** Nombre del rol del usuario vendedor */
    private String tipoVendedor;
}
