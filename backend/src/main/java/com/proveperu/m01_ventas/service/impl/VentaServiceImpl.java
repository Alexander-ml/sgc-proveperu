package com.proveperu.m01_ventas.service.impl;

import com.proveperu.m01_ventas.dto.request.VentaFiltroRequest;
import com.proveperu.m01_ventas.dto.response.ComprobanteResumenDTO;
import com.proveperu.m01_ventas.dto.response.MetodoPagoVentaResumenDTO;
import com.proveperu.m01_ventas.dto.response.VentaDetalleResponseDTO;
import com.proveperu.m01_ventas.dto.response.VentaResumenResponseDTO;
import com.proveperu.m01_ventas.entity.Comprobante;
import com.proveperu.m01_ventas.entity.DetalleVenta;
import com.proveperu.m01_ventas.entity.Pago;
import com.proveperu.m01_ventas.entity.Venta;
import com.proveperu.m01_ventas.mapper.VentaDetalleMapper;
import com.proveperu.m01_ventas.mapper.VentaMapper;
import com.proveperu.m01_ventas.repository.*;
import com.proveperu.m01_ventas.service.VentaService;
import com.proveperu.m01_ventas.validators.VentaDetalleValidator;
import com.proveperu.m01_ventas.validators.VentaFiltroValidator;
import com.proveperu.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación encargado de listar ventas paginadas con filtros dinámicos
 * y enriquecimiento de información resumida para la capa de presentación.
 *
 * <p>
 * Esta implementación coordina el flujo completo del endpoint de consulta de ventas:
 * </p>
 *
 * <ul>
 *     <li>Valida la consistencia funcional del filtro recibido.</li>
 *     <li>Construye el {@link Pageable} con ordenamiento seguro.</li>
 *     <li>Ejecuta la consulta dinámica mediante {@link VentaSpecification}.</li>
 *     <li>Carga en batch los pagos y comprobantes para evitar N+1.</li>
 *     <li>Ensambla el DTO de respuesta resumido por cada venta encontrada.</li>
 * </ul>
 *
 * <p>
 * El servicio mantiene el contrato de lectura aislado mediante
 * {@code @Transactional(readOnly = true)} para optimizar el acceso a datos.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VentaServiceImpl implements VentaService {

    private static final String DEFAULT_SORT_FIELD = "fechaHoraVenta";
    private static final String DEFAULT_SORT_DIRECTION = "DESC";

    private static final String SORT_ID_VENTA = "idVenta";
    private static final String SORT_FECHA_HORA_VENTA = "fechaHoraVenta";
    private static final String SORT_TOTAL = "total";

    private final VentaRepository ventaRepository;
    private final PagoRepository pagoRepository;
    private final ComprobanteRepository comprobanteRepository;
    private final VentaMapper ventaMapper;
    private final VentaFiltroValidator ventaFiltroValidator;

    private final DetalleVentaRepository detalleVentaRepository;
    private final VentaDetalleMapper ventaDetalleMapper;
    private final VentaDetalleValidator ventaDetalleValidator;

    /**
     * Obtiene un listado paginado de ventas con filtros funcionales,
     * ordenamiento seguro y enriquecimiento del resumen de salida.
     *
     * <p>
     * El flujo se ejecuta en cuatro etapas:
     * </p>
     *
     * <ol>
     *     <li>Validación funcional del filtro recibido.</li>
     *     <li>Construcción del {@link Pageable} seguro.</li>
     *     <li>Ejecución de la consulta dinámica con {@link Specification}.</li>
     *     <li>Enriquecimiento del resultado con pagos, comprobante y datos resumidos.</li>
     * </ol>
     *
     * <p>
     * Si no existen resultados, se retorna una página vacía conservando la paginación
     * solicitada para mantener coherencia con el contrato de la API.
     * </p>
     *
     * @param filtro criterios de búsqueda, paginación y ordenamiento.
     * @return página de ventas resumidas listas para consumo de la interfaz.
     */
    @Override
    public Page<VentaResumenResponseDTO> listarVentas(VentaFiltroRequest filtro) {
        ventaFiltroValidator.validar(filtro);

        Pageable pageable = construirPageable(filtro);
        Specification<Venta> spec = VentaSpecification.conFiltros(filtro);

        Page<Venta> ventasPage = ventaRepository.findAll(spec, pageable);

        if (ventasPage == null || ventasPage.isEmpty()) {
            return paginaVacia(pageable, ventasPage != null ? ventasPage.getTotalElements() : 0L);
        }

        List<Integer> ventaIds = extraerIdsVenta(ventasPage);

        if (ventaIds.isEmpty()) {
            return paginaVacia(pageable, ventasPage.getTotalElements());
        }

        Map<Integer, List<Pago>> pagosPorVenta = indexarPagosPorVenta(pagoRepository.findPagosActivosByVentaIds(ventaIds));

        Map<Integer, Comprobante> comprobantePorVenta = indexarComprobantesPorVenta(comprobanteRepository.findByVentaIdIn(ventaIds));

        List<VentaResumenResponseDTO> dtos = ventasPage.getContent()
                .stream()
                .map(venta -> ensamblarResumen(venta, pagosPorVenta, comprobantePorVenta))
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, ventasPage.getTotalElements());
    }

    /**
     * Ensambla el resumen funcional de una venta consolidando su información base,
     * el cliente, el vendedor, los métodos de pago y el comprobante emitido.
     *
     * @param venta venta origen.
     * @param pagosPorVenta mapa indexado por identificador de venta con sus pagos activos.
     * @param comprobantePorVenta mapa indexado por identificador de venta con su comprobante.
     * @return DTO resumido de la venta.
     */
    private VentaResumenResponseDTO ensamblarResumen(
            Venta venta,
            Map<Integer, List<Pago>> pagosPorVenta,
            Map<Integer, Comprobante> comprobantePorVenta) {

        VentaResumenResponseDTO dto = ventaMapper.toResumenDTO(venta);

        dto.setNumeroVenta(construirNumeroVenta(venta));
        dto.setEstadoVenta(venta.getEstadoVenta() != null ? venta.getEstadoVenta().name() : null);
        dto.setCliente(ventaMapper.toClienteResumenDTO(venta.getCliente()));
        dto.setVendedor(ventaMapper.toUsuarioResumenDTO(venta.getUsuario()));
        dto.setTipoVendedor(resolverTipoVendedor(venta));

        List<Pago> pagosVenta = pagosPorVenta.getOrDefault(venta.getIdVenta(), Collections.emptyList());
        List<MetodoPagoVentaResumenDTO> metodosPago = ventaMapper.toPagoResumenDTOList(pagosVenta);
        dto.setMetodosPago(metodosPago);

        Comprobante comprobante = comprobantePorVenta.get(venta.getIdVenta());
        if (comprobante != null) {
            ComprobanteResumenDTO comprobanteDto = ventaMapper.toComprobanteResumenDTO(comprobante);
            dto.setComprobante(comprobanteDto);
        }

        return dto;
    }

    /**
     * Construye el número visual de la venta usando el año de registro y el identificador técnico.
     *
     * <p>
     * Formato final:
     * {@code V-{AÑO}-{ID_6_DÍGITOS}}
     * </p>
     *
     * <p>
     * Ejemplo:
     * {@code V-2026-000042}
     * </p>
     *
     * @param venta venta origen.
     * @return número visual de la venta o {@code null} si faltan datos mínimos.
     */
    private String construirNumeroVenta(Venta venta) {
        LocalDateTime fechaHoraVenta = venta.getFechaHoraVenta();
        Integer idVenta = venta.getIdVenta();

        if (fechaHoraVenta == null || idVenta == null) {
            return null;
        }

        int anio = fechaHoraVenta.getYear();
        String idFormateado = String.format("%06d", idVenta);
        return "V-" + anio + "-" + idFormateado;
    }

    /**
     * Determina el nombre del rol del usuario asociado a la venta para mostrar el tipo de vendedor.
     *
     * @param venta venta origen.
     * @return nombre del rol o {@code null} si no existe información suficiente.
     */
    private String resolverTipoVendedor(Venta venta) {
        if (venta.getUsuario() == null || venta.getUsuario().getRol() == null) {
            return null;
        }

        return venta.getUsuario().getRol().getNombreRol();
    }

    /**
     * Construye el {@link Pageable} a partir del filtro recibido.
     *
     * <p>
     * El ordenamiento se resuelve únicamente con campos permitidos por el contrato
     * funcional del endpoint, evitando que nombres externos lleguen directamente a JPA.
     * </p>
     *
     * @param filtro filtro de entrada.
     * @return instancia de paginación segura.
     */
    private Pageable construirPageable(VentaFiltroRequest filtro) {
        String campoOrden = resolverCampoOrden(filtro.getSort());

        Sort.Direction direccion = Sort.Direction.fromOptionalString(filtro.getDirection())
                .orElse(Sort.Direction.DESC);

        Sort sort = Sort.by(direccion, campoOrden);

        if (!SORT_ID_VENTA.equals(campoOrden)) {
            sort = sort.and(Sort.by(direccion, SORT_ID_VENTA));
        }

        return PageRequest.of(filtro.getPage(), filtro.getSize(), sort);
    }

    /**
     * Resuelve el campo de ordenamiento permitido por el contrato funcional.
     *
     * <p>
     * Este método solo acepta los atributos que existen en la entidad {@link Venta}
     * y que están validados por {@link VentaFiltroValidator}.
     * </p>
     *
     * @param sortParam nombre lógico del campo de ordenamiento.
     * @return nombre físico o lógico aceptado por JPA.
     */
    private String resolverCampoOrden(String sortParam) {
        if (!StringUtils.hasText(sortParam)) {
            return DEFAULT_SORT_FIELD;
        }

        return switch (sortParam.trim()) {
            case SORT_ID_VENTA -> SORT_ID_VENTA;
            case SORT_FECHA_HORA_VENTA -> SORT_FECHA_HORA_VENTA;
            case SORT_TOTAL -> SORT_TOTAL;
            default -> DEFAULT_SORT_FIELD;
        };
    }

    /**
     * Extrae los identificadores de las ventas incluidas en la página actual.
     *
     * @param ventasPage página de ventas consultada.
     * @return lista de identificadores técnicos de venta.
     */
    private List<Integer> extraerIdsVenta(Page<Venta> ventasPage) {
        return ventasPage.getContent()
                .stream()
                .map(Venta::getIdVenta)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Indexa los pagos por identificador de venta para permitir búsqueda O(1).
     *
     * @param pagos lista de pagos cargados en batch.
     * @return mapa agrupado por venta.
     */
    private Map<Integer, List<Pago>> indexarPagosPorVenta(List<Pago> pagos) {
        return pagos.stream()
                .collect(Collectors.groupingBy(pago -> pago.getVenta().getIdVenta()));
    }

    /**
     * Indexa los comprobantes por identificador de venta para permitir búsqueda O(1).
     *
     * @param comprobantes lista de comprobantes cargados en batch.
     * @return mapa indexado por venta.
     */
    private Map<Integer, Comprobante> indexarComprobantesPorVenta(List<Comprobante> comprobantes) {
        return comprobantes.stream()
                .collect(Collectors.toMap(
                        comprobante -> comprobante.getVenta().getIdVenta(),
                        Function.identity(),
                        (primero, segundo) -> primero
                ));
    }

    /**
     * Retorna una página vacía manteniendo el Pageable solicitado.
     */
    private Page<VentaResumenResponseDTO> paginaVacia(Pageable pageable, long totalElements) {
        return new PageImpl<>(Collections.emptyList(), pageable, totalElements);
    }

    /**
     * Obtiene el detalle completo de una venta: cabecera, cliente, vendedor,
     * comprobante, productos y pagos con valores derivados calculados.
     *
     * <p>
     * El flujo opera en tres fases:
     * </p>
     * <ol>
     *     <li>Validación del identificador recibido.</li>
     *     <li>Carga de la venta con sus relaciones en batch para evitar N+1.</li>
     *     <li>Cálculo de subtotalGeneral, montoPagadoTotal y cambio.</li>
     * </ol>
     *
     * <p>
     * Se aplica {@code @Transactional(readOnly = true)} heredado de la clase.
     * </p>
     *
     * @param idVenta identificador técnico de la venta.
     * @return DTO completo con toda la información para la vista de detalle.
     */
    @Override
    public VentaDetalleResponseDTO obtenerDetalleVenta(Integer idVenta) {

        ventaDetalleValidator.validar(idVenta);

        Venta venta = ventaRepository.findDetalleCompletoById(idVenta).orElseThrow(() -> new ResourceNotFoundException("Venta", idVenta));

        List<DetalleVenta> detalles = detalleVentaRepository.findDetalleConProductoByVentaId(idVenta);

        List<Pago> pagos = pagoRepository.findPagosActivosByVentaId(idVenta);

        VentaDetalleResponseDTO dto = ventaDetalleMapper.toDetalleDTO(venta);

        dto.setNumeroVenta(construirNumeroVenta(venta));

        if (venta.getCliente() != null) {
            dto.setCliente(ventaDetalleMapper.toClienteDetalleDTO(venta.getCliente()));
        }

        dto.setVendedor(ventaDetalleMapper.toUsuarioDetalleDTO(venta.getUsuario()));
        dto.setTipoVendedor(resolverTipoVendedor(venta));

        if (venta.getComprobante() != null) {
            dto.setComprobante(
                    ventaDetalleMapper.toComprobanteDetalleDTO(venta.getComprobante())
            );
        }

        dto.setPagos(ventaDetalleMapper.toPagoDetalleDTOList(pagos));
        dto.setProductos(ventaDetalleMapper.toDetalleProductoDTOList(detalles));

        BigDecimal subtotalGeneral = calcularSubtotalGeneral(detalles);
        BigDecimal montoPagadoTotal = calcularMontoPagadoTotal(pagos);
        BigDecimal cambio = calcularCambio(montoPagadoTotal, venta.getTotal());

        dto.setSubtotalGeneral(subtotalGeneral);
        dto.setMontoPagadoTotal(montoPagadoTotal);
        dto.setCambio(cambio);

        return dto;
    }

    /**
     * Calcula la suma de subtotales de todos los productos incluidos en la venta.
     *
     * @param detalles lista de detalles de venta.
     * @return suma de subtotales; cero si la lista está vacía.
     */
    private BigDecimal calcularSubtotalGeneral(List<DetalleVenta> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return detalles.stream()
                .map(DetalleVenta::getSubtotal)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula la suma de montos de todos los pagos activos de la venta.
     *
     * @param pagos lista de pagos activos.
     * @return suma de montos; cero si la lista está vacía.
     */
    private BigDecimal calcularMontoPagadoTotal(List<Pago> pagos) {
        if (pagos == null || pagos.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return pagos.stream()
                .map(Pago::getMonto)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula el cambio a devolver al cliente.
     *
     * <p>
     * El cambio es la diferencia entre el monto pagado y el total de la venta.
     * Si el monto pagado es menor o igual al total, el cambio es cero.
     * </p>
     *
     * @param montoPagado monto total pagado.
     * @param totalVenta  total de la venta.
     * @return cambio calculado; nunca negativo.
     */
    private BigDecimal calcularCambio(BigDecimal montoPagado, BigDecimal totalVenta) {
        if (montoPagado == null || totalVenta == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal diferencia = montoPagado.subtract(totalVenta);
        return diferencia.compareTo(BigDecimal.ZERO) > 0
                ? diferencia
                : BigDecimal.ZERO;
    }

}
