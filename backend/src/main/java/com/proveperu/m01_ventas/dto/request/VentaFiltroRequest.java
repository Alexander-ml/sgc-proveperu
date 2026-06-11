package com.proveperu.m01_ventas.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * DTO de entrada utilizado para consultar el listado paginado de ventas
 * aplicando búsqueda global, filtros por relaciones y ordenamiento.
 *
 * <p>
 * Este objeto transporta únicamente criterios de consulta desde la capa
 * web hacia la capa de aplicación. No contiene lógica de negocio y no
 * representa una entidad del dominio.
 * </p>
 *
 * <p>
 * Los valores por defecto permiten ejecutar consultas sin parámetros
 * adicionales, manteniendo una experiencia estable para la grilla principal
 * del módulo de ventas.
 * </p>
 */
@Getter
@Setter
public class VentaFiltroRequest {
    /**
     * Búsqueda global sobre el nombre o razón social del cliente,
     * la serie y correlativo del comprobante, o el número visual de venta.
     */
    @Size(max = 150, message = "La búsqueda global no debe superar los 150 caracteres")
    private String q;

    /**
     * Filtro por identificador exacto del cliente.
     */
     @Min(value = 1, message = "El identificador del cliente debe ser mayor que cero")
    private Integer clienteId;

    /**
     * Filtro por número visual de venta derivado, por ejemplo:
     * {@code V-2026-0001}.
     *
     * <p>
     * Este valor no representa una columna física directa, sino un criterio
     * funcional que debe resolverse en la capa de servicio.
     * </p>
     */
    @Size(max = 20, message = "El número de venta no debe superar los 20 caracteres")
    private String numeroVenta;

    /**
     * Tipo de comprobante permitido por el sistema.
     * Valores esperados: {@code BOLETA}, {@code FACTURA}, {@code NOTA}.
     */
    @Size(max = 20, message = "El tipo de comprobante no debe superar los 20 caracteres")
    private String tipoComprobante;

    /**
     * Estado de la venta.
     * Valores esperados: {@code REGISTRADA}, {@code ANULADA}.
     */
    @Size(max = 20, message = "El estado de la venta no debe superar los 20 caracteres")
    private String estadoVenta;

    /**
     * Filtro por identificador del método de pago asociado a la venta.
     */
    @Min(value = 1, message = "El identificador del método de pago debe ser mayor que cero")
    private Integer metodoPagoId;

    /**
     * Fecha y hora inicial del rango de búsqueda.
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaInicio;

    /**
     * Fecha y hora final del rango de búsqueda.
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaFin;

    /**
     * Número de página solicitado.
     */
    @Min(value = 0, message = "La página no puede ser negativa")
    private int page = 0;

    /**
     * Tamaño de página solicitado.
     */
    @Min(value = 1, message = "El tamaño de página debe ser mayor que cero")
    @Max(value = 100, message = "El tamaño de página no debe superar 100 registros")
    private int size = 20;

    /**
     * Campo utilizado para ordenar el resultado.
     */
    @Size(max = 50, message = "El campo de ordenamiento no debe superar los 50 caracteres")
    private String sort = "fechaHoraVenta";

    /**
     * Dirección de ordenamiento.
     *
     * <p>
     * La normalización y validación funcional se realiza
     * en {@code VentaFiltroValidator}.
     * </p>
     */
    private String direction = "DESC";
}
