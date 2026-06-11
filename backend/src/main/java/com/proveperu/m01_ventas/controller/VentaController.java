package com.proveperu.m01_ventas.controller;

import com.proveperu.m01_ventas.dto.request.VentaFiltroRequest;
import com.proveperu.m01_ventas.dto.response.VentaResumenResponseDTO;
import com.proveperu.m01_ventas.service.VentaService;

import com.proveperu.shared.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {
    private final VentaService ventaService;

    /**
     * GET /api/v1/ventas
     *
     * Retorna el listado paginado de ventas con filtros opcionales.
     * Permiso requerido: VENTAS - LEER
     */
    @GetMapping
    // @PreAuthorize("hasAuthority('VENTAS_LEER')")
    public ResponseEntity<ApiResponse<Page<VentaResumenResponseDTO>>> listarVentas(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false) String numeroVenta,
            @RequestParam(required = false) String tipoComprobante,
            @RequestParam(required = false) String estadoVenta,
            @RequestParam(required = false) Integer metodoPagoId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "fechaHoraVenta") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        VentaFiltroRequest filtro = new VentaFiltroRequest();
        filtro.setQ(q);
        filtro.setClienteId(clienteId);
        filtro.setNumeroVenta(numeroVenta);
        filtro.setTipoComprobante(tipoComprobante);
        filtro.setEstadoVenta(estadoVenta);
        filtro.setMetodoPagoId(metodoPagoId);
        filtro.setFechaInicio(fechaInicio);
        filtro.setFechaFin(fechaFin);
        filtro.setPage(page);
        filtro.setSize(size);
        filtro.setSort(sort);
        filtro.setDirection(direction);

        Page<VentaResumenResponseDTO> resultado = ventaService.listarVentas(filtro);

        return ResponseEntity.ok(
                ApiResponse.success(resultado, "Ventas obtenidas exitosamente."));
    }
}
