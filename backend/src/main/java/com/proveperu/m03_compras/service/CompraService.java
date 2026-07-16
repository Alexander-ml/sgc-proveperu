package com.proveperu.m03_compras.service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proveperu.m02_inventario.entity.Producto;
import com.proveperu.m02_inventario.repository.ProductoRepository;
import com.proveperu.m03_compras.dto.request.CambiarEstadoCompraRequest;
import com.proveperu.m03_compras.dto.request.RegistrarCompraRequest;
import com.proveperu.m03_compras.dto.request.RegistrarDetalleCompraRequest;
import com.proveperu.m03_compras.dto.request.RegistrarProveedorRequest;
import com.proveperu.m03_compras.dto.response.CompraDashboardResponse;
import com.proveperu.m03_compras.dto.response.CompraDetalleResponse;
import com.proveperu.m03_compras.dto.response.CompraListadoResponse;
import com.proveperu.m03_compras.dto.response.CompraOpcionesResponse;
import com.proveperu.m03_compras.dto.response.DetalleCompraResponse;
import com.proveperu.m03_compras.dto.response.MetodoPagoOpcionResponse;
import com.proveperu.m03_compras.dto.response.ProductoOpcionResponse;
import com.proveperu.m03_compras.dto.response.ProveedorListadoResponse;
import com.proveperu.m03_compras.dto.response.ProveedorOpcionResponse;
import com.proveperu.m03_compras.entity.Compra;
import com.proveperu.m03_compras.entity.DetalleCompra;
import com.proveperu.m03_compras.entity.DetalleCompraId;
import com.proveperu.m03_compras.entity.PagoCompra;
import com.proveperu.m03_compras.entity.Proveedor;
import com.proveperu.m03_compras.enums.EstadoCompra;
import com.proveperu.m03_compras.enums.EstadoPagoCompra;
import com.proveperu.m03_compras.repository.CompraRepository;
import com.proveperu.m03_compras.repository.DetalleCompraRepository;
import com.proveperu.m03_compras.repository.PagoCompraRepository;
import com.proveperu.m03_compras.repository.ProveedorRepository;
import com.proveperu.m06_usuarios.entity.Usuario;
import com.proveperu.m06_usuarios.repository.UsuarioRepository;
import com.proveperu.shared.entity.MetodoPago;
import com.proveperu.shared.enums.EstadoActivoInactivo;
import com.proveperu.shared.enums.EstadoLogico;
import com.proveperu.shared.repository.MetodoPagoRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
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
    private final ProveedorRepository proveedorRepository;

private final MetodoPagoRepository metodoPagoRepository;

private final ProductoRepository productoRepository;

private final DetalleCompraRepository detalleCompraRepository;

private final PagoCompraRepository pagoCompraRepository;

private final UsuarioRepository usuarioRepository;
@PersistenceContext
private EntityManager entityManager;
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
 * Obtiene las opciones necesarias para registrar
 * una nueva compra.
 *
 * Incluye proveedores, métodos de pago y productos activos.
 *
 * @return opciones para el formulario de registro de compras.
 */
@Transactional(readOnly = true)
public CompraOpcionesResponse obtenerOpcionesRegistro() {

    log.info("Consultando opciones para registrar compra");

    List<ProveedorOpcionResponse> proveedores =
            proveedorRepository.findAll()
                    .stream()
                    .filter(proveedor ->
                            proveedor.getEstadoLogico() == EstadoLogico.ACTIVO
                                    && proveedor.getEstadoFisico()
                                            == EstadoActivoInactivo.ACTIVO
                    )
                    .sorted(
                            Comparator.comparing(
                                    Proveedor::getRazonSocial
                            )
                    )
                    .map(proveedor ->
                            ProveedorOpcionResponse.builder()
                                    .idProveedor(
                                            proveedor.getIdProveedor()
                                    )
                                    .ruc(proveedor.getRuc())
                                    .razonSocial(
                                            proveedor.getRazonSocial()
                                    )
                                    .build()
                    )
                    .collect(Collectors.toList());

    List<MetodoPagoOpcionResponse> metodosPago =
            metodoPagoRepository.findAll()
                    .stream()
                    .filter(metodoPago ->
                            metodoPago.getEstadoLogico()
                                    == EstadoLogico.ACTIVO
                                    && metodoPago.getEstadoMetodoPago()
                                            == EstadoActivoInactivo.ACTIVO
                    )
                    .sorted(
                            Comparator.comparing(
                                    MetodoPago::getNombreMetodoPago
                            )
                    )
                    .map(metodoPago ->
                            MetodoPagoOpcionResponse.builder()
                                    .idMetodoPago(
                                            metodoPago.getIdMetodoPago()
                                    )
                                    .nombreMetodoPago(
                                            metodoPago.getNombreMetodoPago()
                                    )
                                    .build()
                    )
                    .collect(Collectors.toList());

    List<ProductoOpcionResponse> productos =
            productoRepository.findAll()
                    .stream()
                    .filter(producto ->
                            producto.getEstadoLogico() == EstadoLogico.ACTIVO
                                    && producto.getEstadoFisico()
                                            == EstadoActivoInactivo.ACTIVO
                    )
                    .sorted(
                            Comparator.comparing(
                                    Producto::getNombreProducto
                            )
                    )
                    .map(producto ->
                            ProductoOpcionResponse.builder()
                                    .idProducto(
                                            producto.getIdProducto()
                                    )
                                    .codigoProducto(
                                            producto.getCodigoProducto()
                                    )
                                    .nombreProducto(
                                            producto.getNombreProducto()
                                    )
                                    .unidadMedida(
                                            producto.getUnidadMedida()
                                                    .name()
                                    )
                                    .build()
                    )
                    .collect(Collectors.toList());

    log.info(
            "Opciones de compra consultadas. Proveedores: {}, Métodos de pago: {}, Productos: {}",
            proveedores.size(),
            metodosPago.size(),
            productos.size()
    );

    return CompraOpcionesResponse.builder()
            .proveedores(proveedores)
            .metodosPago(metodosPago)
            .productos(productos)
            .build();
}
/**
 * Lista los proveedores registrados para el módulo de compras.
 *
 * @return lista de proveedores activos y registrados.
 */
@Transactional(readOnly = true)
public List<ProveedorListadoResponse> listarProveedores() {

    log.info("Listando proveedores del módulo de compras");

    List<ProveedorListadoResponse> proveedores =
            proveedorRepository.findAllByOrderByRazonSocialAsc()
                    .stream()
                    .filter(proveedor ->
                            proveedor.getEstadoLogico() == EstadoLogico.ACTIVO
                    )
                    .map(proveedor ->
                            ProveedorListadoResponse.builder()
                                    .idProveedor(proveedor.getIdProveedor())
                                    .ruc(proveedor.getRuc())
                                    .razonSocial(proveedor.getRazonSocial())
                                    .telefono(proveedor.getTelefono())
                                    .direccion(proveedor.getDireccion())
                                    .estado(
                                            proveedor.getEstadoFisico() == EstadoActivoInactivo.ACTIVO
                                                    ? "ACTIVO"
                                                    : "INACTIVO"
                                    )
                                    .build()
                    )
                    .collect(Collectors.toList());

    log.info(
            "Proveedores listados correctamente. Total: {}",
            proveedores.size()
    );

    return proveedores;
}
/**
 * Registra un nuevo proveedor dentro del módulo de compras.
 *
 * @param request datos del proveedor a registrar.
 * @return proveedor registrado.
 */
@Transactional
public ProveedorListadoResponse registrarProveedor(
        RegistrarProveedorRequest request
) {

    log.info(
            "Registrando nuevo proveedor con RUC: {}",
            request.getRuc()
    );

    if (proveedorRepository.existsByRuc(request.getRuc())) {
        throw new RuntimeException("Ya existe un proveedor registrado con ese RUC");
    }

    Proveedor proveedor = new Proveedor();
    proveedor.setRuc(request.getRuc().trim());
    proveedor.setRazonSocial(request.getRazonSocial().trim());
    proveedor.setTelefono(
            request.getTelefono() != null
                    ? request.getTelefono().trim()
                    : null
    );
    proveedor.setDireccion(
            request.getDireccion() != null
                    ? request.getDireccion().trim()
                    : null
    );
    proveedor.setEstadoFisico(EstadoActivoInactivo.ACTIVO);
    proveedor.setEstadoLogico(EstadoLogico.ACTIVO);
    LocalDateTime ahora = LocalDateTime.now();

proveedor.setFechaHoraCreacion(ahora);
proveedor.setFechaHoraActualizacion(ahora);

    Proveedor proveedorGuardado =
            proveedorRepository.save(proveedor);

    log.info(
            "Proveedor registrado correctamente. Id: {}, RUC: {}",
            proveedorGuardado.getIdProveedor(),
            proveedorGuardado.getRuc()
    );

    return ProveedorListadoResponse.builder()
            .idProveedor(proveedorGuardado.getIdProveedor())
            .ruc(proveedorGuardado.getRuc())
            .razonSocial(proveedorGuardado.getRazonSocial())
            .telefono(proveedorGuardado.getTelefono())
            .direccion(proveedorGuardado.getDireccion())
            .estado(proveedorGuardado.getEstadoFisico().name())
            .build();
}

/**
 * Lista las compras realizadas a un proveedor específico.
 *
 * @param idProveedor identificador del proveedor.
 * @return lista de compras asociadas al proveedor.
 */
@Transactional(readOnly = true)
public List<CompraListadoResponse> listarComprasPorProveedor(
        Integer idProveedor
) {

    log.info(
            "Listando compras del proveedor con id: {}",
            idProveedor
    );

    Proveedor proveedor = proveedorRepository.findById(idProveedor)
            .orElseThrow(() ->
                    new RuntimeException("Proveedor no encontrado")
            );

    if (proveedor.getEstadoLogico() != EstadoLogico.ACTIVO) {
        throw new RuntimeException("El proveedor no se encuentra activo");
    }

    List<CompraListadoResponse> compras =
            compraRepository.findByProveedorOrderByFechaHoraCreacionDesc(proveedor)
                    .stream()
                    .filter(compra ->
                            compra.getEstadoLogico() == EstadoLogico.ACTIVO
                    )
                    .map(this::mapearListado)
                    .collect(Collectors.toList());

    log.info(
            "Compras del proveedor listadas correctamente. Proveedor: {}, Total compras: {}",
            proveedor.getRazonSocial(),
            compras.size()
    );

    return compras;
}
/**
 * Registra una nueva compra con sus productos y pago.
 *
 * La compra se registra inicialmente como PENDIENTE,
 * porque el stock debe actualizarse recién cuando se reciba la compra.
 *
 * @param request datos enviados desde el formulario de compra.
 * @param usuarioLogin usuario autenticado que registra la compra.
 * @return detalle de la compra registrada.
 */
@Transactional
public CompraDetalleResponse registrarCompra(
        RegistrarCompraRequest request,
        String usuarioLogin
) {

    log.info(
            "Registrando nueva compra. Usuario: {}, Proveedor: {}, Método de pago: {}",
            usuarioLogin,
            request.getIdProveedor(),
            request.getIdMetodoPago()
    );

    Usuario usuario = usuarioRepository.findByUsuarioLogin(usuarioLogin)
            .orElseThrow(() -> {
                log.warn(
                        "No se encontró el usuario autenticado: {}",
                        usuarioLogin
                );
                return new RuntimeException("Usuario no encontrado");
            });

    Proveedor proveedor = proveedorRepository
            .findById(request.getIdProveedor())
            .orElseThrow(() -> {
                log.warn(
                        "No se encontró el proveedor con id {}",
                        request.getIdProveedor()
                );
                return new RuntimeException("Proveedor no encontrado");
            });

    MetodoPago metodoPago = metodoPagoRepository
            .findById(request.getIdMetodoPago())
            .orElseThrow(() -> {
                log.warn(
                        "No se encontró el método de pago con id {}",
                        request.getIdMetodoPago()
                );
                return new RuntimeException("Método de pago no encontrado");
            });

    Set<Integer> productosAgregados = new HashSet<>();
    List<DetalleCompra> detalles = new ArrayList<>();
    BigDecimal totalCompra = BigDecimal.ZERO;

    for (RegistrarDetalleCompraRequest detalleRequest
            : request.getProductos()) {

        if (!productosAgregados.add(detalleRequest.getIdProducto())) {
            log.warn(
                    "Producto duplicado en la compra. Id producto: {}",
                    detalleRequest.getIdProducto()
            );
            throw new RuntimeException(
                    "No puede repetir el mismo producto en la compra"
            );
        }

        Producto producto = productoRepository
                .findById(detalleRequest.getIdProducto())
                .orElseThrow(() -> {
                    log.warn(
                            "No se encontró el producto con id {}",
                            detalleRequest.getIdProducto()
                    );
                    return new RuntimeException("Producto no encontrado");
                });

        BigDecimal subtotal = detalleRequest.getCantidad()
                .multiply(detalleRequest.getPrecioUnitarioCompra())
                .setScale(2, RoundingMode.HALF_UP);

        totalCompra = totalCompra.add(subtotal);

        DetalleCompra detalle = DetalleCompra.builder()
                .producto(producto)
                .cantidad(detalleRequest.getCantidad())
                .precioUnitarioCompra(
                        detalleRequest.getPrecioUnitarioCompra()
                                .setScale(2, RoundingMode.HALF_UP)
                )
                .subtotal(subtotal)
                .build();

        detalles.add(detalle);
    }

    Compra compra = Compra.builder()
            .proveedor(proveedor)
            .usuarioRegistro(usuario)
            .total(totalCompra)
            .estadoFisico(EstadoCompra.PENDIENTE)
            .build();

    compra.setEstadoLogico(EstadoLogico.ACTIVO);
    compra.setFechaHoraCreacion(LocalDateTime.now());

    compra = compraRepository.saveAndFlush(compra);

    for (DetalleCompra detalle : detalles) {

        detalle.setCompra(compra);
        detalle.setId(
                new DetalleCompraId(
                        compra.getIdCompra(),
                        detalle.getProducto().getIdProducto()
                )
        );
    }

   detalleCompraRepository.saveAll(detalles);

PagoCompra pagoCompra = PagoCompra.builder()
        .compra(compra)
        .metodoPago(metodoPago)
        .usuarioRegistro(usuario)
        .monto(totalCompra)
        .fechaHoraPago(LocalDateTime.now())
        .estadoFisico(EstadoPagoCompra.REGISTRADO)
        .build();

pagoCompra.setEstadoLogico(EstadoLogico.ACTIVO);

pagoCompraRepository.save(pagoCompra);

log.info(
        "Compra registrada correctamente. Id: {}, Total: {}, Productos: {}",
        compra.getIdCompra(),
        totalCompra,
        detalles.size()
);

List<DetalleCompraResponse> productosResponse = detalles.stream()
        .map(this::mapearDetalleProducto)
        .collect(Collectors.toList());

return CompraDetalleResponse.builder()
        .idCompra(compra.getIdCompra())
        .numeroCompra(generarNumeroCompra(compra))
        .proveedor(compra.getProveedor().getRazonSocial())
        .estado(compra.getEstadoFisico().name())
        .fecha(compra.getFechaHoraCreacion())
        .metodoPago(metodoPago.getNombreMetodoPago())
        .registradoPor(usuario.getNombreCompleto())
        .productos(productosResponse)
        .total(compra.getTotal())
        .build();
}
/**
 * Cambia el estado de una compra.
 *
 * Si el nuevo estado es RECIBIDO, se ejecuta el procedimiento almacenado
 * que registra la recepción y actualiza el stock desde la base de datos.
 *
 * @param idCompra identificador de la compra.
 * @param request nuevo estado de la compra.
 * @param usuarioLogin usuario autenticado que realiza el cambio.
 * @return detalle actualizado de la compra.
 */
@Transactional
public CompraDetalleResponse cambiarEstadoCompra(
        Integer idCompra,
        CambiarEstadoCompraRequest request,
        String usuarioLogin
) {

    log.info(
            "Cambiando estado de compra. IdCompra: {}, Nuevo estado: {}, Usuario: {}",
            idCompra,
            request.getEstado(),
            usuarioLogin
    );

    Compra compra = compraRepository.findById(idCompra)
            .orElseThrow(() ->
                    new RuntimeException("Compra no encontrada")
            );

    if (compra.getEstadoLogico() != EstadoLogico.ACTIVO) {
        throw new RuntimeException("La compra no se encuentra activa");
    }

    Usuario usuario = usuarioRepository.findByUsuarioLogin(usuarioLogin)
            .orElseThrow(() ->
                    new RuntimeException("Usuario autenticado no encontrado")
            );
     EstadoCompra estadoAnterior = compra.getEstadoFisico();

    StoredProcedureQuery procedimiento =
            entityManager.createStoredProcedureQuery(
                    "sp_cambiar_estado_compra"
            );

    procedimiento.registerStoredProcedureParameter(
            "p_id_compra",
            Integer.class,
            ParameterMode.IN
    );

    procedimiento.registerStoredProcedureParameter(
            "p_nuevo_estado",
            String.class,
            ParameterMode.IN
    );

    procedimiento.registerStoredProcedureParameter(
            "p_id_usuario",
            Integer.class,
            ParameterMode.IN
    );

    procedimiento.setParameter(
            "p_id_compra",
            idCompra
    );

    procedimiento.setParameter(
            "p_nuevo_estado",
            request.getEstado().name()
    );

    procedimiento.setParameter(
            "p_id_usuario",
            usuario.getIdUsuario()
    );

    procedimiento.execute();
    if (request.getEstado() == EstadoCompra.RECIBIDO
        && estadoAnterior != EstadoCompra.RECIBIDO) {

    registrarEgresoCajaPorCompra(
            idCompra,
            usuario.getIdUsuario()
    );
}

    /*
     * Limpiamos el contexto porque el procedimiento actualiza la base directamente.
     * Así evitamos devolver datos antiguos desde memoria.
     */
    entityManager.flush();
    entityManager.clear();

    log.info(
            "Estado de compra actualizado correctamente. IdCompra: {}, Nuevo estado: {}",
            idCompra,
            request.getEstado()
    );

    return obtenerDetalleCompra(idCompra);
}
private void registrarEgresoCajaPorCompra(
        Integer idCompra,
        Integer idUsuario
) {

    log.info(
            "Registrando egreso automático en caja por compra recibida. IdCompra: {}, IdUsuario: {}",
            idCompra,
            idUsuario
    );

    StoredProcedureQuery procedimientoCaja =
            entityManager.createStoredProcedureQuery(
                    "sp_registrar_egreso_compra_caja"
            );

    procedimientoCaja.registerStoredProcedureParameter(
            "p_id_compra",
            Integer.class,
            ParameterMode.IN
    );

    procedimientoCaja.registerStoredProcedureParameter(
            "p_id_usuario",
            Integer.class,
            ParameterMode.IN
    );

    procedimientoCaja.registerStoredProcedureParameter(
            "p_descripcion",
            String.class,
            ParameterMode.IN
    );

    procedimientoCaja.setParameter(
            "p_id_compra",
            idCompra
    );

    procedimientoCaja.setParameter(
            "p_id_usuario",
            idUsuario
    );

    procedimientoCaja.setParameter(
            "p_descripcion",
            "Pago de compra recibida"
    );

    procedimientoCaja.execute();

    log.info(
            "Egreso automático en caja registrado correctamente. IdCompra: {}",
            idCompra
    );
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
