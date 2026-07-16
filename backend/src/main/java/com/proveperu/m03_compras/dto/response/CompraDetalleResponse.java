package com.proveperu.m03_compras.dto.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Respuesta utilizada para mostrar el detalle completo
 * de una compra seleccionada desde la tabla principal.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraDetalleResponse {
      private Integer idCompra;

    private String numeroCompra;

    private String proveedor;

    private String estado;

    private LocalDateTime fecha;

    private String metodoPago;

    private String registradoPor;

    private List<DetalleCompraResponse> productos;

    private BigDecimal total;
}
