package com.proveperu.m01_ventas.dto.request;

import com.proveperu.m01_ventas.enums.EstadoVenta;
import com.proveperu.m01_ventas.enums.TipoComprobante;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class VentaFiltroRequest {
    /**
     * Búsqueda global sobre: nombre/razón social del cliente,
     * serie+correlativo del comprobante o número visual de venta.
     */
    private String q;

    /** Filtro por identificador exacto del cliente */
    private Integer clienteId;

    /**
     * Filtro por número visual de venta derivado, ej: V-2026-000001.
     * Se resuelve extrayendo el id numérico final y buscando por id_venta.
     */
    private String numeroVenta;

    /** BOLETA | FACTURA | NOTA */
    private String tipoComprobante;

    /** REGISTRADA | ANULADA */
    private String estadoVenta;

    /** Filtro por id de método de pago */
    private Integer metodoPagoId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaFin;

    private int page = 0;
    private int size = 20;
    private String sort = "fechaHoraVenta";
    private String direction = "DESC";
}
