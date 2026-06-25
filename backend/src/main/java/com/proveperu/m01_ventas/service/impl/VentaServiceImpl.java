package com.proveperu.m01_ventas.service.impl;

import com.proveperu.m01_ventas.dto.request.DetalleVentaRequest;
import com.proveperu.m01_ventas.dto.request.VentaCreateRequest;
import com.proveperu.m01_ventas.dto.request.VentaFiltroRequest;
import com.proveperu.m01_ventas.dto.response.*;
import com.proveperu.m01_ventas.entity.*;
import com.proveperu.m01_ventas.enums.EstadoComprobante;
import com.proveperu.m01_ventas.enums.EstadoPago;
import com.proveperu.m01_ventas.enums.EstadoVenta;
import com.proveperu.m01_ventas.enums.TipoComprobante;
import com.proveperu.m01_ventas.mapper.*;
import com.proveperu.m01_ventas.repository.*;
import com.proveperu.m01_ventas.service.VentaService;
import com.proveperu.m01_ventas.validators.VentaCreateValidator;
import com.proveperu.m01_ventas.validators.VentaDetalleValidator;
import com.proveperu.m01_ventas.validators.VentaFiltroValidator;
import com.proveperu.m02_inventario.entity.MovimientoInventario;
import com.proveperu.m02_inventario.entity.Producto;
import com.proveperu.m02_inventario.entity.Stock;
import com.proveperu.m02_inventario.entity.TipoMovimientoInventario;
import com.proveperu.m02_inventario.enums.EstadoMovimientoInventario;
import com.proveperu.m02_inventario.repository.MovimientoInventarioRepository;
import com.proveperu.m02_inventario.repository.ProductoRepository;
import com.proveperu.m02_inventario.repository.StockRepository;
import com.proveperu.m02_inventario.repository.TipoMovimientoInventarioRepository;
import com.proveperu.m04_caja_pagos.entity.Caja;
import com.proveperu.m04_caja_pagos.entity.MovimientoCaja;
import com.proveperu.m04_caja_pagos.entity.TipoMovimientoCaja;
import com.proveperu.m04_caja_pagos.enums.EstadoCaja;
import com.proveperu.m04_caja_pagos.enums.EstadoMovimientoCaja;
import com.proveperu.m04_caja_pagos.enums.NombreTipoMovimientoCaja;
import com.proveperu.m04_caja_pagos.repository.CajaRepository;
import com.proveperu.m04_caja_pagos.repository.MovimientoCajaRepository;
import com.proveperu.m04_caja_pagos.repository.TipoMovimientoCajaRepository;
import com.proveperu.m05_gestion_clientes.entity.Cliente;
import com.proveperu.m05_gestion_clientes.repository.ClienteRepository;
import com.proveperu.m06_usuarios.entity.Usuario;
import com.proveperu.m06_usuarios.repository.UsuarioRepository;
import com.proveperu.shared.entity.MetodoPago;
import com.proveperu.shared.enums.EstadoActivoInactivo;
import com.proveperu.shared.enums.EstadoLogico;
import com.proveperu.shared.exception.BusinessException;
import com.proveperu.shared.exception.ResourceNotFoundException;
import com.proveperu.shared.repository.MetodoPagoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

    private final ClienteRepository  clienteRepository;
    private final ProductoRepository productoRepository;
    private final StockRepository stockRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final TipoMovimientoInventarioRepository tipoMovimientoInventarioRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final CajaRepository cajaRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;
    private final TipoMovimientoCajaRepository tipoMovimientoCajaRepository;
    private final UsuarioRepository usuarioRepository;

    private final ClienteMapper clienteMapper;
    private final MetodoPagoMapper metodoPagoMapper;
    private final ProductoVentaMapper productoVentaMapper;

    private final VentaCreateValidator ventaCreateValidator;


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


    // ======================================================================
    // ENDPOINT 1: Listar clientes activos
    // ======================================================================

    /**
     * {@inheritDoc}
     *
     * <p>
     * Recupera clientes con estado físico ACTIVO.
     * Retorna lista vacía si no hay clientes activos.
     * </p>
     */
    @Override
    @Transactional(readOnly = true)
    public List<ClienteListadoResponse> obtenerClientesParaVenta() {
        log.debug("Recuperando clientes activos para selección en venta");
        List<Cliente> clientes = clienteRepository.findByEstadoCliente(EstadoActivoInactivo.ACTIVO);
        return clienteMapper.toListadoResponseList(clientes);
    }

    // ======================================================================
    // ENDPOINT 2: Listar tipos de comprobante (desde enum, sin BD)
    // ======================================================================

    /**
     * {@inheritDoc}
     *
     * <p>
     * Construye la lista directamente a partir del enum
     * {@link TipoComprobante}. No realiza consultas a base de datos.
     * </p>
     */
    @Override
    public List<TipoComprobanteResponse> obtenerTiposComprobante() {
        log.debug("Generando listado de tipos de comprobante desde enum");
        return Arrays.stream(TipoComprobante.values())
                .map(tipo -> TipoComprobanteResponse.builder()
                        .codigo(tipo.name())
                        .descripcion(resolverDescripcionComprobante(tipo))
                        .build())
                .collect(Collectors.toList());
    }

    // ======================================================================
    // ENDPOINT 3: Listar métodos de pago activos
    // ======================================================================

    /**
     * {@inheritDoc}
     *
     * <p>
     * Recupera métodos de pago con estado físico ACTIVO.
     * Retorna lista vacía si no hay métodos disponibles.
     * </p>
     */
    @Override
    @Transactional(readOnly = true)
    public List<MetodoPagoResponse> obtenerMetodosPago() {
        log.debug("Recuperando métodos de pago activos");
        List<MetodoPago> metodos = metodoPagoRepository.findByEstadoFisico(EstadoActivoInactivo.ACTIVO);
        return metodoPagoMapper.toResponseList(metodos);
    }

    // ======================================================================
    // ENDPOINT 4: Buscar productos para venta con stock
    // ======================================================================

    /**
     * {@inheritDoc}
     *
     * <p>
     * Busca productos activos por nombre parcial y combina
     * los resultados con el stock actual de cada producto.
     * Se cargan los stocks en una única consulta para evitar N+1.
     * </p>
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductoVentaResponse> buscarProductosParaVenta(String nombre) {
        log.debug("Buscando productos para venta con nombre parcial: '{}'", nombre);

        List<Producto> productos = productoRepository
                .findByNombreProductoContainingIgnoreCaseAndEstadoFisico(
                        nombre,
                        EstadoActivoInactivo.ACTIVO
                );

        if (productos.isEmpty()) {
            return new ArrayList<>();
        }

        // Cargar todos los stocks en una sola consulta para evitar N+1
        List<Integer> idsProductos = productos.stream()
                .map(Producto::getIdProducto)
                .collect(Collectors.toList());

        Map<Integer, Stock> stockPorProducto = stockRepository
                .findByIdProductoIn(idsProductos)
                .stream()
                .collect(Collectors.toMap(Stock::getIdProducto, Function.identity()));

        return productos.stream()
                .map(p -> productoVentaMapper.toResponse(
                        p,
                        stockPorProducto.get(p.getIdProducto())
                ))
                .collect(Collectors.toList());
    }

    // ======================================================================
    // ENDPOINT 5: Crear nueva venta (transaccional completo)
    // ======================================================================

    /**
     * {@inheritDoc}
     *
     * <p>
     * Operación transaccional completa. Cualquier excepción
     * revierte todos los cambios realizados durante la ejecución.
     * </p>
     *
     * <p>
     * Flujo de ejecución:
     * </p>
     * <ol>
     *     <li>Validaciones de formato (validator).</li>
     *     <li>Resolución de entidades referenciadas.</li>
     *     <li>Validación de stock por producto.</li>
     *     <li>Cálculo de totales.</li>
     *     <li>Validación del monto pagado.</li>
     *     <li>Persistencia de Venta y sus DetalleVenta (cascade).</li>
     *     <li>Persistencia del Pago.</li>
     *     <li>Generación del Comprobante.</li>
     *     <li>Registro de MovimientoInventario (EGRESO) por producto.</li>
     *     <li>Actualización de stock.</li>
     *     <li>Registro de MovimientoCaja (INGRESO).</li>
     * </ol>
     */
    @Override
    @Transactional
    public VentaCreateResponse crearVenta(VentaCreateRequest request, String loginUsuario) {
        log.info("Iniciando creación de venta para usuario: {}", loginUsuario);

        // --- PASO 1: Validaciones de dominio sin BD ---
        ventaCreateValidator.validar(request);

        // --- PASO 2: Resolver usuario autenticado ---
        Usuario usuario = usuarioRepository.findByUsuarioLogin(loginUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", loginUsuario));

        // --- PASO 3: Resolver cliente (opcional) ---
        Cliente cliente = resolverCliente(request.getIdCliente());

        // --- PASO 4: Resolver método de pago ---
        MetodoPago metodoPago = metodoPagoRepository.findById(request.getIdMetodoPago())
                .orElseThrow(() -> new ResourceNotFoundException("MetodoPago", request.getIdMetodoPago()));

        // --- PASO 5: Cargar productos y stocks en batch (anti N+1) ---
        List<Integer> idsProductos = request.getProductos().stream()
                .map(DetalleVentaRequest::getIdProducto)
                .collect(Collectors.toList());

        Map<Integer, Producto> productoMap = productoRepository.findAllById(idsProductos).stream()
                .filter(p -> EstadoActivoInactivo.ACTIVO.equals(p.getEstadoFisico()))
                .collect(Collectors.toMap(Producto::getIdProducto, Function.identity()));

        Map<Integer, Stock> stockMap = stockRepository.findByIdProductoIn(idsProductos).stream()
                .collect(Collectors.toMap(Stock::getIdProducto, Function.identity()));

        // --- PASO 6: Validar existencia de todos los productos solicitados ---
        for (DetalleVentaRequest detalleReq : request.getProductos()) {
            if (!productoMap.containsKey(detalleReq.getIdProducto())) {
                throw new ResourceNotFoundException("Producto", detalleReq.getIdProducto());
            }
        }

        // --- PASO 7: Validar stock y calcular total ---
        BigDecimal totalVenta = BigDecimal.ZERO;
        List<DetalleVenta> detalles = new ArrayList<>();

        for (DetalleVentaRequest detalleReq : request.getProductos()) {
            Producto producto = productoMap.get(detalleReq.getIdProducto());
            Stock stock       = stockMap.get(detalleReq.getIdProducto());
            BigDecimal stockActual = (stock != null) ? stock.getCantidadActual() : BigDecimal.ZERO;

            // RF-09 / RF-10: Validar stock suficiente
            if (detalleReq.getCantidad().compareTo(stockActual) > 0) {
                throw new BusinessException(String.format(
                        "Stock insuficiente para el producto '%s' (id: %d). " +
                                "Disponible: %s, Solicitado: %s",
                        producto.getNombreProducto(),
                        producto.getIdProducto(),
                        stockActual.toPlainString(),
                        detalleReq.getCantidad().toPlainString()
                ));
            }

            // RF-06: Calcular subtotal
            BigDecimal subtotal = detalleReq.getCantidad().multiply(detalleReq.getPrecioUnitario());
            totalVenta = totalVenta.add(subtotal);

            detalles.add(DetalleVenta.builder()
                    .id(new DetalleVentaId(null, producto.getIdProducto()))
                    .producto(producto)
                    .cantidad(detalleReq.getCantidad())
                    .precioUnitario(detalleReq.getPrecioUnitario())
                    .subtotal(subtotal)
                    .build());
        }

        // --- PASO 8: Validar monto pagado (RF-15) ---
        if (request.getMontoPagado().compareTo(totalVenta) < 0) {
            throw new BusinessException(String.format(
                    "Monto pagado insuficiente. Total de la venta: %s, Monto recibido: %s",
                    totalVenta.toPlainString(),
                    request.getMontoPagado().toPlainString()
            ));
        }
        BigDecimal cambioDevuelto = request.getMontoPagado().subtract(totalVenta);

        // --- PASO 9: Validar unicidad del comprobante ---
        if (comprobanteRepository.existsBySerieAndCorrelativo(
                request.getSerieComprobante(), request.getCorrelativoComprobante())) {
            throw new BusinessException(String.format(
                    "El comprobante con serie '%s' y correlativo '%s' ya existe.",
                    request.getSerieComprobante(),
                    request.getCorrelativoComprobante()
            ));
        }

        // --- PASO 10: Persistir Venta con sus DetalleVenta en cascada ---
        LocalDateTime ahora = LocalDateTime.now();

        Venta venta = Venta.builder()
                .cliente(cliente)
                .usuario(usuario)
                .fechaHoraVenta(ahora)
                .total(totalVenta)
                .estadoVenta(EstadoVenta.REGISTRADA)
                .detallesVenta(new ArrayList<>())
                .pagos(new ArrayList<>())
                .build();

        // Asignar la venta a cada detalle (necesario para EmbeddedId con MapsId)
        for (DetalleVenta detalle : detalles) {
            detalle.setVenta(venta);
            detalle.getId().setIdVenta(null); // JPA lo asignará tras el save
            venta.getDetallesVenta().add(detalle);
        }

        venta = ventaRepository.save(venta);
        log.info("Venta id={} persistida con {} productos", venta.getIdVenta(), detalles.size());

        // --- PASO 11: Persistir Pago (RF-12 / RF-14) ---
        Pago pago = Pago.builder()
                .venta(venta)
                .metodoPago(metodoPago)
                .monto(request.getMontoPagado())
                .estadoPago(EstadoPago.REGISTRADO)
                .build();
        pago.setEstadoLogico(EstadoLogico.ACTIVO);
        pago.setFechaHoraCreacion(ahora);
        pagoRepository.save(pago);

        // --- PASO 12: Generar Comprobante (RF-17 / RF-19) ---
        Comprobante comprobante = Comprobante.builder()
                .venta(venta)
                .tipoComprobante(TipoComprobante.valueOf(request.getTipoComprobante()))
                .serie(request.getSerieComprobante())
                .correlativo(request.getCorrelativoComprobante())
                .fechaEmision(ahora)
                .estadoComprobante(EstadoComprobante.EMITIDO)
                .build();
        comprobanteRepository.save(comprobante);

        // --- PASO 13: Registrar MovimientoInventario EGRESO y actualizar stock ---
        TipoMovimientoInventario tipoEgreso = tipoMovimientoInventarioRepository
                .findByNombre("EGRESO")
                .orElseThrow(() -> new BusinessException(
                        "Tipo de movimiento EGRESO no encontrado en catálogo de inventario."
                ));

        for (DetalleVenta detalle : venta.getDetallesVenta()) {
            Producto producto   = detalle.getProducto();
            Stock stockActual   = stockMap.get(producto.getIdProducto());
            BigDecimal anterior = (stockActual != null) ? stockActual.getCantidadActual() : BigDecimal.ZERO;
            BigDecimal nuevo    = anterior.subtract(detalle.getCantidad());

            MovimientoInventario movInventario = MovimientoInventario.builder()
                    .producto(producto)
                    .tipoMovimientoInventario(tipoEgreso)
                    .usuarioRegistro(usuario)
                    .venta(venta)
                    .cantidad(detalle.getCantidad())
                    .stockAnterior(anterior)
                    .stockNuevo(nuevo)
                    .fechaHoraMovimientoInventario(ahora)
                    .estadoFisico(EstadoMovimientoInventario.REGISTRADO)
                    .build();

            movimientoInventarioRepository.save(movInventario);

            // RF-11: Descontar stock
            stockRepository.decrementarStock(producto.getIdProducto(), detalle.getCantidad());

            log.debug("Stock producto id={} actualizado: {} → {}",
                    producto.getIdProducto(), anterior, nuevo);
        }

        // --- PASO 14: Registrar MovimientoCaja INGRESO (RF-20) ---
        Caja cajaAbierta = cajaRepository.findFirstByEstadoFisico(EstadoCaja.ABIERTA)
                .orElseThrow(() -> new BusinessException(
                        "No existe una caja abierta para registrar el ingreso de la venta."
                ));

        TipoMovimientoCaja tipoIngreso = tipoMovimientoCajaRepository
                .findByNombreTipoMovimiento(NombreTipoMovimientoCaja.INGRESO)
                .orElseThrow(() -> new BusinessException(
                        "Tipo de movimiento de caja INGRESO no encontrado."
                ));

        MovimientoCaja movCaja = MovimientoCaja.builder()
                .caja(cajaAbierta)
                .tipoMovimientoCaja(tipoIngreso)
                .usuarioRegistra(usuario)
                .venta(venta)
                .metodoPago(metodoPago)
                .monto(totalVenta)
                .descripcion("Ingreso por venta id=" + venta.getIdVenta())
                .fechaHoraMovimiento(ahora)
                .estadoFisico(EstadoMovimientoCaja.REGISTRADO)
                .build();

        movimientoCajaRepository.save(movCaja);

        // Actualizar saldo de la caja
        cajaAbierta.setSaldoActual(cajaAbierta.getSaldoActual().add(totalVenta));
        cajaRepository.save(cajaAbierta);

        log.info("Venta id={} completada exitosamente. Total={}, Cambio={}",
                venta.getIdVenta(), totalVenta, cambioDevuelto);

        // --- PASO 15: Construir respuesta ---
        return VentaCreateResponse.builder()
                .idVenta(venta.getIdVenta())
                .fechaHoraVenta(venta.getFechaHoraVenta())
                .total(totalVenta)
                .montoPagado(request.getMontoPagado())
                .cambioDevuelto(cambioDevuelto)
                .tipoComprobante(request.getTipoComprobante())
                .serieComprobante(request.getSerieComprobante())
                .correlativoComprobante(request.getCorrelativoComprobante())
                .mensaje("Venta registrada exitosamente.")
                .build();
    }

    // ======================================================================
    // Métodos privados auxiliares
    // ======================================================================

    /**
     * Resuelve el cliente a partir del ID proporcionado.
     *
     * <p>
     * Si el ID es nulo o cero, la venta se considera
     * "sin cliente" y se retorna {@code null}.
     * Si se proporciona un ID, el cliente debe existir y
     * estar activo; de lo contrario se lanza excepción.
     * </p>
     *
     * @param idCliente identificador del cliente o nulo/cero para venta informal.
     * @return entidad {@link Cliente} o {@code null} si es venta informal.
     * @throws ResourceNotFoundException si el cliente no existe.
     * @throws BusinessException         si el cliente está inactivo.
     */
    private Cliente resolverCliente(Integer idCliente) {
        if (idCliente == null || idCliente == 0) {
            log.debug("Venta registrada sin cliente asociado (venta informal)");
            return null;
        }
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", idCliente));

        if (!EstadoActivoInactivo.ACTIVO.equals(cliente.getEstadoCliente())) {
            throw new BusinessException(
                    String.format("El cliente con id %d no está activo.", idCliente)
            );
        }
        return cliente;
    }

    /**
     * Resuelve la descripción legible de un tipo de comprobante
     * para mostrar en la interfaz de usuario.
     *
     * @param tipo valor del enum {@link TipoComprobante}.
     * @return descripción en español del comprobante.
     */
    private String resolverDescripcionComprobante(TipoComprobante tipo) {
        return switch (tipo) {
            case BOLETA  -> "Boleta de venta";
            case FACTURA -> "Factura";
            case NOTA    -> "Nota interna";
        };
    }
}
