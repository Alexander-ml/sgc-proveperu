package com.proveperu.m01_ventas.service.impl;

import com.proveperu.m01_ventas.dto.request.VentaFiltroRequest;
import com.proveperu.m01_ventas.dto.response.MetodoPagoVentaResumenDTO;
import com.proveperu.m01_ventas.dto.response.VentaResumenResponseDTO;
import com.proveperu.m01_ventas.entity.Comprobante;
import com.proveperu.m01_ventas.entity.Pago;
import com.proveperu.m01_ventas.entity.Venta;
import com.proveperu.m01_ventas.mapper.VentaMapper;
import com.proveperu.m01_ventas.repository.ComprobanteRepository;
import com.proveperu.m01_ventas.repository.PagoRepository;
import com.proveperu.m01_ventas.repository.VentaRepository;
import com.proveperu.m01_ventas.repository.VentaSpecification;
import com.proveperu.m01_ventas.service.VentaService;
import com.proveperu.m01_ventas.validators.VentaFiltroValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {
    private final VentaRepository ventaRepository;
    private final PagoRepository pagoRepository;
    private final ComprobanteRepository comprobanteRepository;
    private final VentaMapper ventaMapper;
    private final VentaFiltroValidator ventaFiltroValidator;

    @Override
    @Transactional(readOnly = true)
    public Page<VentaResumenResponseDTO> listarVentas(VentaFiltroRequest filtro) {

        // 1. Validar consistencia de filtros
        ventaFiltroValidator.validar(filtro);

        // 2. Construir paginación y ordenamiento
        Pageable pageable = construirPageable(filtro);

        // 3. Construir Specification dinámica y ejecutar consulta paginada
        Specification<Venta> spec = VentaSpecification.conFiltros(filtro);
        Page<Venta> ventasPage = ventaRepository.findAll(spec, pageable);

        if (ventasPage.isEmpty()) {
            return Page.empty(pageable);
        }

        // 4. Extraer ids de ventas para carga batch (evita N+1)
        List<Integer> ventaIds = ventasPage.getContent()
                .stream()
                .map(Venta::getIdVenta)
                .collect(Collectors.toList());

        // 5. Carga batch de pagos y comprobantes
        List<Pago> todosLosPagos =
                pagoRepository.findPagosActivosByVentaIds(ventaIds);
        List<Comprobante> todosLosComprobantes =
                comprobanteRepository.findByVentaIdIn(ventaIds);

        // 6. Indexar por idVenta para lookup O(1)
        Map<Integer, List<Pago>> pagosPorVenta = todosLosPagos
                .stream()
                .collect(Collectors.groupingBy(p -> p.getVenta().getIdVenta()));

        Map<Integer, Comprobante> comprobantePorVenta = todosLosComprobantes
                .stream()
                .collect(Collectors.toMap(
                        c -> c.getVenta().getIdVenta(),
                        c -> c,
                        (c1, c2) -> c1));

        // 7. Mapear cada venta a su DTO resumen
        List<VentaResumenResponseDTO> dtos = ventasPage.getContent()
                .stream()
                .map(venta -> ensamblarResumen(venta, pagosPorVenta, comprobantePorVenta))
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, ventasPage.getTotalElements());
    }

    // ─── Métodos privados de soporte ─────────────────────────────────────────

    /**
     * Ensambla el DTO resumen de una venta consolidando todas sus relaciones.
     */
    private VentaResumenResponseDTO ensamblarResumen(
            Venta venta,
            Map<Integer, List<Pago>> pagosPorVenta,
            Map<Integer, Comprobante> comprobantePorVenta) {

        VentaResumenResponseDTO dto = ventaMapper.toResumenDTO(venta);

        // Número visual derivado
        dto.setNumeroVenta(construirNumeroVenta(venta));

        // Tipo de vendedor: nombre del rol del usuario
        if (venta.getUsuario() != null && venta.getUsuario().getRol() != null) {
            dto.setTipoVendedor(venta.getUsuario().getRol().getNombreRol());
        }

        // Métodos de pago desde batch
        List<Pago> pagosVenta =
                pagosPorVenta.getOrDefault(venta.getIdVenta(), Collections.emptyList());
        List<MetodoPagoVentaResumenDTO> metodosPago =
                ventaMapper.toPagoResumenDTOList(pagosVenta);
        dto.setMetodosPago(metodosPago);

        // Comprobante desde batch (puede ser null si la venta no tiene comprobante)
        Comprobante comprobante = comprobantePorVenta.get(venta.getIdVenta());
        if (comprobante != null) {
            dto.setComprobante(ventaMapper.toComprobanteResumenDTO(comprobante));
        }

        return dto;
    }

    /**
     * Construye el número visual de venta.
     * Formato: V-{AÑO_VENTA}-{ID_VENTA_6_DÍGITOS_CON_CEROS}
     * Ejemplo: V-2026-000042
     */
    private String construirNumeroVenta(Venta venta) {
        int anio = venta.getFechaHoraVenta().getYear();
        String idFormateado = String.format("%06d", venta.getIdVenta());
        return "V-" + anio + "-" + idFormateado;
    }

    /**
     * Construye el Pageable a partir del filtro.
     * Solo se permiten campos de ordenamiento conocidos para prevenir
     * inyección de nombres de columna desde el exterior.
     */
    private Pageable construirPageable(VentaFiltroRequest filtro) {
        String campoOrden = resolverCampoOrden(filtro.getSort());
        Sort.Direction direccion = "ASC".equalsIgnoreCase(filtro.getDirection())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Sort sort = Sort.by(direccion, campoOrden);
        return PageRequest.of(filtro.getPage(), filtro.getSize(), sort);
    }

    /**
     * Resuelve el nombre de campo de ordenamiento seguro.
     * Solo se permiten campos conocidos del modelo Venta.
     * Cualquier valor desconocido cae en el default: fechaHoraVenta.
     */
    private String resolverCampoOrden(String sortParam) {
        if (!StringUtils.hasText(sortParam)) {
            return "fechaHoraVenta";
        }
        return switch (sortParam.toLowerCase()) {
            case "fechahoraventa", "fecha" -> "fechaHoraVenta";
            case "total" -> "total";
            case "idventa", "id" -> "idVenta";
            case "estadofisico", "estado" -> "estadoFisico";
            default -> "fechaHoraVenta";
        };
    }
}
