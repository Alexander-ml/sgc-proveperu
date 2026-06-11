package com.proveperu.m01_ventas.service;

import com.proveperu.m01_ventas.dto.request.VentaFiltroRequest;
import com.proveperu.m01_ventas.dto.response.VentaResumenResponseDTO;
import org.springframework.data.domain.Page;


public interface VentaService {

    /**
     * Retorna una página de ventas resumidas que cumplen con los filtros indicados.
     *
     * @param filtro parámetros de búsqueda, filtrado, paginación y ordenamiento
     * @return página de ventas resumidas lista para serializar al frontend
     */
    Page<VentaResumenResponseDTO> listarVentas(VentaFiltroRequest filtro);
}
