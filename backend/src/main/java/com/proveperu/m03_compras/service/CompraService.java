package com.proveperu.m03_compras.service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proveperu.m03_compras.dto.response.CompraDashboardResponse;
import com.proveperu.m03_compras.dto.response.CompraDetalleResponse;
import com.proveperu.m03_compras.dto.response.CompraListadoResponse;
import com.proveperu.m03_compras.dto.response.DetalleCompraResponse;
import com.proveperu.m03_compras.entity.Compra;
import com.proveperu.m03_compras.entity.DetalleCompra;
import com.proveperu.m03_compras.entity.PagoCompra;
import com.proveperu.m03_compras.enums.EstadoCompra;
import com.proveperu.m03_compras.repository.CompraRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
/**
 * Servicio encargado de gestionar las operaciones
 * de consulta del módulo de compras.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CompraService {
    
    private final CompraRepository compraRepository;

    /**
     * Obtiene los indicadores principales del módulo de compras.
     *
     * @return dashboard de compras.
     */
    @Transactional(readOnly = true)
    public CompraDashboardResponse obtenerDashboard() {

        log.info("Consultando dashboard de compras");

        List<Compra> compras = compraRepository.findAll();

        long totalCompras = compras.stream()
                .filter(compra -> compra.getEstadoFisico() != EstadoCompra.ANULADO)
                .count();

        BigDecimal montoTotalInvertido = compras.stream()
                .filter(compra -> compra.getEstadoFisico() != EstadoCompra.ANULADO)
                .map(Compra::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long comprasRecibidas =
                compraRepository.countByEstadoFisico(EstadoCompra.RECIBIDO);

        long comprasPendientes =
                compraRepository.countByEstadoFisico(EstadoCompra.PENDIENTE);

        log.info(
                "Dashboard de compras consultado. Total: {}, Monto: {}, Recibidas: {}, Pendientes: {}",
                totalCompras,
                montoTotalInvertido,
                comprasRecibidas,
                comprasPendientes
        );

        return CompraDashboardResponse.builder()
                .totalCompras(totalCompras)
                .montoTotalInvertido(montoTotalInvertido)
                .comprasRecibidas(comprasRecibidas)
                .comprasPendientes(comprasPendientes)
                .build();
    }

    /**
     * Lista las compras registradas, permitiendo buscar
     * por proveedor o número de compra y filtrar por estado.
     *
     * @param buscar texto opcional de búsqueda.
     * @param estado estado opcional de la compra.
     * @return lista de compras para la tabla principal.
     */
    @Transactional(readOnly = true)
    public List<CompraListadoResponse> listarCompras(
            String buscar,
            String estado
    ) {

        log.info(
                "Listando compras. Buscar: {}, Estado: {}",
                buscar,
                estado
        );

        EstadoCompra estadoFiltro = convertirEstado(estado);

        String buscarNormalizado = buscar == null
                ? ""
                : buscar.trim().toLowerCase(Locale.ROOT);

        List<Compra> compras =
                compraRepository.findAllByOrderByFechaHoraCreacionDesc();

        List<CompraListadoResponse> response = compras.stream()
                .filter(compra ->
                        estadoFiltro == null
                                || compra.getEstadoFisico() == estadoFiltro
                )
                .filter(compra ->
                        buscarNormalizado.isBlank()
                                || coincideBusqueda(compra, buscarNormalizado)
                )
                .map(this::mapearListado)
                .collect(Collectors.toList());

        log.info(
                "Compras listadas correctamente. Total registros: {}",
                response.size()
        );

        return response;
    }
/**
 * Obtiene el detalle completo de una compra.
 *
 * @param idCompra identificador de la compra.
 * @return detalle de la compra.
 */
@Transactional(readOnly = true)
public CompraDetalleResponse obtenerDetalleCompra(Integer idCompra) {

    log.info("Consultando detalle de compra con id {}", idCompra);

    Compra compra = compraRepository.findById(idCompra)
            .orElseThrow(() -> {
                log.warn("No se encontró la compra con id {}", idCompra);
                return new RuntimeException("Compra no encontrada");
            });

    List<DetalleCompraResponse> productos = compra.getDetallesCompra()
            .stream()
            .map(this::mapearDetalleProducto)
            .collect(Collectors.toList());

    log.info(
            "Detalle de compra consultado correctamente. Id: {}, productos: {}",
            idCompra,
            productos.size()
    );

    return CompraDetalleResponse.builder()
            .idCompra(compra.getIdCompra())
            .numeroCompra(generarNumeroCompra(compra))
            .proveedor(compra.getProveedor().getRazonSocial())
            .estado(compra.getEstadoFisico().name())
            .fecha(compra.getFechaHoraCreacion())
            .metodoPago(obtenerMetodoPago(compra))
            .registradoPor(
                    compra.getUsuarioRegistro().getNombreCompleto()
            )
            .productos(productos)
            .total(compra.getTotal())
            .build();
}
    /**
     * Convierte una entidad Compra en una respuesta
     * para la tabla principal.
     */
    private CompraListadoResponse mapearListado(Compra compra) {

        int cantidadProductos = compra.getDetallesCompra() == null
                ? 0
                : compra.getDetallesCompra().size();

        return CompraListadoResponse.builder()
                .idCompra(compra.getIdCompra())
                .numeroCompra(generarNumeroCompra(compra))
                .fecha(compra.getFechaHoraCreacion())
                .proveedor(compra.getProveedor().getRazonSocial())
                .productos(cantidadProductos + " producto(s)")
                .total(compra.getTotal())
                .metodoPago(obtenerMetodoPago(compra))
                .estado(compra.getEstadoFisico().name())
                .registradoPor(
                        compra.getUsuarioRegistro().getNombreCompleto()
                )
                .build();
    }
/**
 * Convierte un detalle de compra en respuesta
 * para el modal de detalle.
 */
private DetalleCompraResponse mapearDetalleProducto(
        DetalleCompra detalle
) {

    return DetalleCompraResponse.builder()
            .idProducto(detalle.getProducto().getIdProducto())
            .producto(detalle.getProducto().getNombreProducto())
            .cantidad(detalle.getCantidad())
            .precioCompra(detalle.getPrecioUnitarioCompra())
            .subtotal(detalle.getSubtotal())
            .build();
}
    /**
     * Verifica si una compra coincide con el texto buscado.
     */
    private boolean coincideBusqueda(
            Compra compra,
            String buscarNormalizado
    ) {

        String proveedor = compra.getProveedor().getRazonSocial()
                .toLowerCase(Locale.ROOT);

        String numeroCompra = generarNumeroCompra(compra)
                .toLowerCase(Locale.ROOT);

        return proveedor.contains(buscarNormalizado)
                || numeroCompra.contains(buscarNormalizado);
    }

    /**
     * Genera el número visual de compra.
     * Ejemplo: C-2026-0001.
     */
    private String generarNumeroCompra(Compra compra) {

        int anio = compra.getFechaHoraCreacion() != null
                ? compra.getFechaHoraCreacion().getYear()
                : LocalDate.now().getYear();

        return String.format(
                "C-%d-%04d",
                anio,
                compra.getIdCompra()
        );
    }

    /**
     * Obtiene el método de pago asociado a la compra.
     */
    private String obtenerMetodoPago(Compra compra) {

        if (compra.getPagos() == null || compra.getPagos().isEmpty()) {
            return "Sin pago registrado";
        }

        if (compra.getPagos().size() > 1) {
            return "Varios métodos";
        }

        PagoCompra pago = compra.getPagos().get(0);

        return pago.getMetodoPago().getNombreMetodoPago();
    }

    /**
     * Convierte el texto recibido desde el frontend
     * al enum EstadoCompra.
     */
    private EstadoCompra convertirEstado(String estado) {

        if (estado == null || estado.trim().isBlank()) {
            return null;
        }

        String estadoNormalizado = estado.trim().toUpperCase(Locale.ROOT);

        if ("TODOS".equals(estadoNormalizado)) {
            return null;
        }

        if ("RECIBIDAS".equals(estadoNormalizado)) {
            estadoNormalizado = "RECIBIDO";
        }

        if ("PENDIENTES".equals(estadoNormalizado)) {
            estadoNormalizado = "PENDIENTE";
        }

        try {
            return EstadoCompra.valueOf(estadoNormalizado);
        } catch (IllegalArgumentException ex) {

            log.warn("Estado de compra no válido: {}", estado);

            throw new RuntimeException(
                    "Estado de compra no válido: " + estado
            );
        }
    }
}
