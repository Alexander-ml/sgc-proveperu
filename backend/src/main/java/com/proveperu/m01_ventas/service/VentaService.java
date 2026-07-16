package com.proveperu.m01_ventas.service;

import com.proveperu.m01_ventas.dto.request.VentaFiltroRequest;
import com.proveperu.m01_ventas.dto.response.VentaDetalleResponseDTO;
import com.proveperu.m01_ventas.dto.response.VentaResumenResponseDTO;
import org.springframework.data.domain.Page;

/**
 * Contrato de aplicación del módulo de ventas.
 *
 * <p>
 * Define las operaciones de consulta disponibles para los
 * controladores del módulo de ventas.
 * </p>
 */
public interface VentaService {

    /**
     * Retorna una página de ventas resumidas que cumplen con los filtros indicados.
     *
     * @param filtro parámetros de búsqueda, filtrado, paginación y ordenamiento
     * @return página de ventas resumidas.
     */
    Page<VentaResumenResponseDTO> listarVentas(VentaFiltroRequest filtro);

    /**
     * Obtiene el detalle completo de una venta identificada por su ID.
     *
     * @param idVenta identificador técnico de la venta.
     * @return DTO con toda la información de la venta.
     * @throws com.proveperu.shared.exception.ResourceNotFoundException si no existe.
     * @throws com.proveperu.shared.exception.ValidationException si el ID es inválido.
     */
    VentaDetalleResponseDTO obtenerDetalleVenta(Integer idVenta);
}
