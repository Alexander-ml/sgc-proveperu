package com.proveperu.m05_gestion_clientes.service;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proveperu.m05_gestion_clientes.dto.request.EditarClienteRequest;
import com.proveperu.m05_gestion_clientes.dto.request.RegistrarClienteRequest;
import com.proveperu.m05_gestion_clientes.dto.response.ClienteDashboardResponse;
import com.proveperu.m05_gestion_clientes.dto.response.ClienteDetalleResponse;
import com.proveperu.m05_gestion_clientes.dto.response.ClienteHistorialDetalleResponse;
import com.proveperu.m05_gestion_clientes.dto.response.ClienteHistorialListadoResponse;
import com.proveperu.m05_gestion_clientes.dto.response.ClienteListadoResponse;
import com.proveperu.m05_gestion_clientes.dto.response.CompraHistorialResponse;
import com.proveperu.m05_gestion_clientes.dto.response.ProductoHistorialCompraResponse;
import com.proveperu.m05_gestion_clientes.entity.Cliente;
import com.proveperu.m05_gestion_clientes.entity.ClienteHistorialCompra;
import com.proveperu.m05_gestion_clientes.entity.ClienteHistorialProducto;
import com.proveperu.m05_gestion_clientes.entity.ClienteResumenVenta;
import com.proveperu.m05_gestion_clientes.enums.TipoCliente;
import com.proveperu.m05_gestion_clientes.repository.ClienteHistorialCompraRepository;
import com.proveperu.m05_gestion_clientes.repository.ClienteHistorialProductoRepository;
import com.proveperu.m05_gestion_clientes.repository.ClienteRepository;
import com.proveperu.m05_gestion_clientes.repository.ClienteResumenVentaRepository;
import com.proveperu.shared.enums.EstadoActivoInactivo;
import com.proveperu.shared.enums.EstadoLogico;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
/**
 * Servicio encargado de la lógica del módulo de gestión de clientes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteService {

    /**
     * Cantidad mínima de compras registradas para considerar
     * a un cliente como frecuente.
     */
    private static final Long MINIMO_COMPRAS_CLIENTE_FRECUENTE = 3L;

    private final ClienteRepository clienteRepository;
    private final ClienteResumenVentaRepository clienteResumenVentaRepository;
    private final ClienteHistorialCompraRepository clienteHistorialCompraRepository;
    private final ClienteHistorialProductoRepository clienteHistorialProductoRepository;

    /**
     * Obtiene los indicadores principales del módulo de clientes.
     *
     * @return resumen de clientes registrados.
     */
    @Transactional(readOnly = true)
    public ClienteDashboardResponse obtenerDashboard() {

        log.info("Obteniendo dashboard del módulo de clientes");

        Long totalClientes =
                clienteRepository.countByEstadoLogico(
                        EstadoLogico.ACTIVO
                );

        Long empresasTalleres =
                clienteRepository.countByTipoClienteAndEstadoLogico(
                        TipoCliente.EMPRESA,
                        EstadoLogico.ACTIVO
                );

        Long personasNaturales =
                clienteRepository.countByTipoClienteAndEstadoLogico(
                        TipoCliente.PERSONA,
                        EstadoLogico.ACTIVO
                );

        Long clientesFrecuentes =
                clienteResumenVentaRepository
                        .countByNumeroComprasGreaterThanEqual(
                                MINIMO_COMPRAS_CLIENTE_FRECUENTE
                        );

        log.info(
                "Dashboard de clientes obtenido. Total: {}, frecuentes: {}",
                totalClientes,
                clientesFrecuentes
        );

        return ClienteDashboardResponse.builder()
                .totalClientes(totalClientes)
                .empresasTalleres(empresasTalleres)
                .personasNaturales(personasNaturales)
                .clientesFrecuentes(clientesFrecuentes)
                .build();
    }

    /**
 * Registra un nuevo cliente en el sistema.
 *
 * Para clientes PERSONA se almacena nombre completo y DNI.
 * Para clientes EMPRESA se almacena razón social y RUC.
 *
 * @param request datos del nuevo cliente.
 * @return detalle del cliente registrado.
 */
@Transactional
public ClienteDetalleResponse registrarCliente(
        RegistrarClienteRequest request
) {

    log.info(
            "Registrando nuevo cliente. Tipo: {}",
            request.getTipoCliente()
    );

    String nombreCliente =
            request.getNombreCliente().trim();

    String numeroDocumento =
            request.getNumeroDocumento().trim();

    Cliente cliente = Cliente.builder()
            .tipoCliente(request.getTipoCliente())
            .telefono(limpiarTextoOpcional(request.getTelefono()))
            .direccion(limpiarTextoOpcional(request.getDireccion()))
            .estadoCliente(EstadoActivoInactivo.ACTIVO)
            .build();

    if (request.getTipoCliente() == TipoCliente.PERSONA) {

        if (nombreCliente.length() > 100) {
            throw new RuntimeException(
                    "El nombre completo no puede superar los 100 caracteres"
            );
        }

        if (numeroDocumento.length() != 8) {
            throw new RuntimeException(
                    "El DNI debe contener exactamente 8 dígitos"
            );
        }

        if (clienteRepository.existsByDni(numeroDocumento)) {
            log.warn(
                    "Intento de registrar un DNI duplicado: {}",
                    numeroDocumento
            );

            throw new RuntimeException(
                    "Ya existe un cliente registrado con ese DNI"
            );
        }

        cliente.setNombreCompleto(nombreCliente);
        cliente.setDni(numeroDocumento);

        cliente.setRazonSocial(null);
        cliente.setRuc(null);

    } else if (request.getTipoCliente() == TipoCliente.EMPRESA) {

        if (nombreCliente.length() > 150) {
            throw new RuntimeException(
                    "La razón social no puede superar los 150 caracteres"
            );
        }

        if (numeroDocumento.length() != 11) {
            throw new RuntimeException(
                    "El RUC debe contener exactamente 11 dígitos"
            );
        }

        if (clienteRepository.existsByRuc(numeroDocumento)) {
            log.warn(
                    "Intento de registrar un RUC duplicado: {}",
                    numeroDocumento
            );

            throw new RuntimeException(
                    "Ya existe un cliente registrado con ese RUC"
            );
        }

        cliente.setRazonSocial(nombreCliente);
        cliente.setRuc(numeroDocumento);

        cliente.setNombreCompleto(null);
        cliente.setDni(null);

    } else {
        throw new RuntimeException(
                "El tipo de cliente seleccionado no es válido"
        );
    }

    LocalDateTime ahora = LocalDateTime.now();

    cliente.setEstadoLogico(EstadoLogico.ACTIVO);
    cliente.setFechaHoraCreacion(ahora);
    cliente.setFechaHoraActualizacion(ahora);

    Cliente clienteGuardado =
            clienteRepository.saveAndFlush(cliente);

    log.info(
            "Cliente registrado correctamente. IdCliente: {}, Tipo: {}",
            clienteGuardado.getIdCliente(),
            clienteGuardado.getTipoCliente()
    );

    return obtenerDetalleCliente(
            clienteGuardado.getIdCliente()
    );
}
/**
 * Edita los datos de un cliente existente.
 *
 * Valida que el DNI o RUC no pertenezca a otro cliente.
 *
 * @param idCliente identificador del cliente.
 * @param request nuevos datos del cliente.
 * @return detalle actualizado del cliente.
 */
@Transactional
public ClienteDetalleResponse editarCliente(
        Integer idCliente,
        EditarClienteRequest request
) {

    log.info(
            "Editando cliente. IdCliente: {}, Tipo: {}",
            idCliente,
            request.getTipoCliente()
    );

    Cliente cliente = clienteRepository.findById(idCliente)
            .orElseThrow(() ->
                    new RuntimeException("Cliente no encontrado")
            );

    if (cliente.getEstadoLogico() != EstadoLogico.ACTIVO) {
        throw new RuntimeException(
                "El cliente no se encuentra activo"
        );
    }

    String nombreCliente =
            request.getNombreCliente().trim();

    String numeroDocumento =
            request.getNumeroDocumento().trim();

    if (request.getTipoCliente() == TipoCliente.PERSONA) {

        if (nombreCliente.length() > 100) {
            throw new RuntimeException(
                    "El nombre completo no puede superar los 100 caracteres"
            );
        }

        if (numeroDocumento.length() != 8) {
            throw new RuntimeException(
                    "El DNI debe contener exactamente 8 dígitos"
            );
        }

        clienteRepository.findByDni(numeroDocumento)
                .filter(clienteEncontrado ->
                        !clienteEncontrado.getIdCliente().equals(idCliente)
                )
                .ifPresent(clienteEncontrado -> {

                    log.warn(
                            "Intento de asignar un DNI duplicado. DNI: {}, IdCliente: {}",
                            numeroDocumento,
                            idCliente
                    );

                    throw new RuntimeException(
                            "Ya existe otro cliente registrado con ese DNI"
                    );
                });

        cliente.setTipoCliente(TipoCliente.PERSONA);
        cliente.setNombreCompleto(nombreCliente);
        cliente.setDni(numeroDocumento);

        cliente.setRazonSocial(null);
        cliente.setRuc(null);

    } else if (request.getTipoCliente() == TipoCliente.EMPRESA) {

        if (nombreCliente.length() > 150) {
            throw new RuntimeException(
                    "La razón social no puede superar los 150 caracteres"
            );
        }

        if (numeroDocumento.length() != 11) {
            throw new RuntimeException(
                    "El RUC debe contener exactamente 11 dígitos"
            );
        }

        clienteRepository.findByRuc(numeroDocumento)
                .filter(clienteEncontrado ->
                        !clienteEncontrado.getIdCliente().equals(idCliente)
                )
                .ifPresent(clienteEncontrado -> {

                    log.warn(
                            "Intento de asignar un RUC duplicado. RUC: {}, IdCliente: {}",
                            numeroDocumento,
                            idCliente
                    );

                    throw new RuntimeException(
                            "Ya existe otra empresa registrada con ese RUC"
                    );
                });

        cliente.setTipoCliente(TipoCliente.EMPRESA);
        cliente.setRazonSocial(nombreCliente);
        cliente.setRuc(numeroDocumento);

        cliente.setNombreCompleto(null);
        cliente.setDni(null);

    } else {
        throw new RuntimeException(
                "El tipo de cliente seleccionado no es válido"
        );
    }

    cliente.setTelefono(
            limpiarTextoOpcional(request.getTelefono())
    );

    cliente.setDireccion(
            limpiarTextoOpcional(request.getDireccion())
    );

    cliente.setFechaHoraActualizacion(
            LocalDateTime.now()
    );

    Cliente clienteActualizado =
            clienteRepository.saveAndFlush(cliente);

    log.info(
            "Cliente actualizado correctamente. IdCliente: {}",
            clienteActualizado.getIdCliente()
    );

    return obtenerDetalleCliente(
            clienteActualizado.getIdCliente()
    );
}

/**
 * Desactiva un cliente sin eliminarlo del sistema.
 *
 * @param idCliente identificador del cliente.
 * @return detalle del cliente desactivado.
 */
@Transactional
public ClienteDetalleResponse desactivarCliente(
        Integer idCliente
) {

    log.info(
            "Desactivando cliente. IdCliente: {}",
            idCliente
    );

    Cliente cliente = clienteRepository.findById(idCliente)
            .orElseThrow(() ->
                    new RuntimeException("Cliente no encontrado")
            );

    if (cliente.getEstadoLogico() != EstadoLogico.ACTIVO) {
        throw new RuntimeException(
                "El cliente no se encuentra disponible"
        );
    }

    if (cliente.getEstadoCliente()
            == EstadoActivoInactivo.INACTIVO) {

        log.info(
                "El cliente ya se encuentra inactivo. IdCliente: {}",
                idCliente
        );

        return obtenerDetalleCliente(idCliente);
    }

    cliente.setEstadoCliente(
            EstadoActivoInactivo.INACTIVO
    );

    cliente.setFechaHoraActualizacion(
            LocalDateTime.now()
    );

    clienteRepository.saveAndFlush(cliente);

    log.info(
            "Cliente desactivado correctamente. IdCliente: {}",
            idCliente
    );

    return obtenerDetalleCliente(idCliente);
}

/**
 * Activa nuevamente un cliente desactivado.
 *
 * @param idCliente identificador del cliente.
 * @return detalle del cliente activado.
 */
@Transactional
public ClienteDetalleResponse activarCliente(
        Integer idCliente
) {

    log.info(
            "Activando cliente. IdCliente: {}",
            idCliente
    );

    Cliente cliente = clienteRepository.findById(idCliente)
            .orElseThrow(() ->
                    new RuntimeException("Cliente no encontrado")
            );

    if (cliente.getEstadoLogico() != EstadoLogico.ACTIVO) {
        throw new RuntimeException(
                "El cliente no se encuentra disponible"
        );
    }

    if (cliente.getEstadoCliente()
            == EstadoActivoInactivo.ACTIVO) {

        log.info(
                "El cliente ya se encuentra activo. IdCliente: {}",
                idCliente
        );

        return obtenerDetalleCliente(idCliente);
    }

    cliente.setEstadoCliente(
            EstadoActivoInactivo.ACTIVO
    );

    cliente.setFechaHoraActualizacion(
            LocalDateTime.now()
    );

    clienteRepository.saveAndFlush(cliente);

    log.info(
            "Cliente activado correctamente. IdCliente: {}",
            idCliente
    );

    return obtenerDetalleCliente(idCliente);
}

    /**
     * Lista clientes con búsqueda y filtro por tipo.
     *
     * @param buscar texto para buscar por nombre, razón social, DNI o RUC.
     * @param tipo filtro: TODOS, EMPRESA o PERSONA.
     * @return lista de clientes.
     */
    @Transactional(readOnly = true)
    public List<ClienteListadoResponse> listarClientes(
            String buscar,
            String tipo
    ) {

        log.info(
                "Listando clientes. Buscar: {}, Tipo: {}",
                buscar,
                tipo
        );

        /*
         * La vista devuelve como máximo un resumen por cliente.
         * Se carga una sola vez para evitar consultar la base de datos
         * nuevamente por cada cliente del listado.
         */
        Map<Integer, ClienteResumenVenta> resumenesPorCliente =
                clienteResumenVentaRepository.findAll()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        ClienteResumenVenta::getIdCliente,
                                        resumen -> resumen
                                )
                        );

        List<ClienteListadoResponse> clientes =
                clienteRepository.findAllByOrderByIdClienteDesc()
                        .stream()
                        .filter(cliente ->
                                cliente.getEstadoLogico()
                                        == EstadoLogico.ACTIVO
                        )
                        .filter(cliente ->
                                coincideTipo(cliente, tipo)
                        )
                        .filter(cliente ->
                                coincideBusqueda(cliente, buscar)
                        )
                        .map(cliente ->
                                mapearListado(
                                        cliente,
                                        resumenesPorCliente.get(
                                                cliente.getIdCliente()
                                        )
                                )
                        )
                        .collect(Collectors.toList());

        log.info(
                "Clientes listados correctamente. Total: {}",
                clientes.size()
        );

        return clientes;
        }

        /**
 * Lista los clientes que poseen al menos una compra registrada.
 *
 * Si se proporciona un texto de búsqueda, filtra por nombre,
 * razón social, DNI o RUC.
 *
 * @param buscar texto opcional de búsqueda.
 * @return clientes que cuentan con historial de compras.
 */
@Transactional(readOnly = true)
public List<ClienteHistorialListadoResponse>
        listarClientesConHistorial(
                String buscar
        ) {

    log.info(
            "Listando clientes con historial. Buscar: {}",
            buscar
    );

    List<ClienteResumenVenta> resumenes =
            clienteResumenVentaRepository
                    .findByNumeroComprasGreaterThanOrderByUltimaCompraDesc(
                            0L
                    );

    if (resumenes.isEmpty()) {
        log.info(
                "No existen clientes con historial de compras"
        );

        return List.of();
    }

    List<Integer> idsClientes =
            resumenes.stream()
                    .map(
                            ClienteResumenVenta::getIdCliente
                    )
                    .collect(Collectors.toList());

    Map<Integer, Cliente> clientesPorId =
            clienteRepository.findAllById(idsClientes)
                    .stream()
                    .collect(
                            Collectors.toMap(
                                    Cliente::getIdCliente,
                                    cliente -> cliente
                            )
                    );

    List<ClienteHistorialListadoResponse> clientes =
            resumenes.stream()
                    .filter(resumen -> {

                        Cliente cliente =
                                clientesPorId.get(
                                        resumen.getIdCliente()
                                );

                        return cliente != null
                                && coincideBusqueda(
                                        cliente,
                                        buscar
                                );
                    })
                    .map(resumen -> {

                        Cliente cliente =
                                clientesPorId.get(
                                        resumen.getIdCliente()
                                );

                        String nombreCliente =
                                obtenerNombreCliente(cliente);

                        return ClienteHistorialListadoResponse
                                .builder()
                                .idCliente(
                                        cliente.getIdCliente()
                                )
                                .nombreCliente(nombreCliente)
                                .iniciales(
                                        generarIniciales(
                                                nombreCliente
                                        )
                                )
                                .numeroCompras(
                                        obtenerNumeroCompras(
                                                resumen
                                        )
                                )
                                .montoTotal(
                                        obtenerMontoTotal(
                                                resumen
                                        )
                                )
                                .build();
                    })
                    .collect(Collectors.toList());

    log.info(
            "Clientes con historial listados correctamente. Total: {}",
            clientes.size()
    );

    return clientes;
}

/**
 * Obtiene el historial completo de compras de un cliente.
 *
 * Incluye información del cliente, indicadores generales,
 * ventas realizadas y productos incluidos en cada venta.
 *
 * @param idCliente identificador del cliente.
 * @return historial completo de compras.
 */
@Transactional(readOnly = true)
public ClienteHistorialDetalleResponse obtenerHistorialCompras(
        Integer idCliente
) {

    log.info(
            "Obteniendo historial de compras. IdCliente: {}",
            idCliente
    );

    Cliente cliente = clienteRepository.findById(idCliente)
            .orElseThrow(() ->
                    new RuntimeException("Cliente no encontrado")
            );

    if (cliente.getEstadoLogico() != EstadoLogico.ACTIVO) {
        throw new RuntimeException(
                "El cliente no se encuentra disponible"
        );
    }

    ClienteResumenVenta resumen =
            clienteResumenVentaRepository.findById(idCliente)
                    .orElse(null);

    List<ClienteHistorialCompra> compras =
            clienteHistorialCompraRepository
                    .findByIdClienteOrderByFechaHoraVentaDesc(
                            idCliente
                    );

    List<Integer> idsVentas =
            compras.stream()
                    .map(
                            ClienteHistorialCompra::getIdVenta
                    )
                    .collect(Collectors.toList());

    List<ClienteHistorialProducto> productos = idsVentas.isEmpty()
            ? List.of()
            : clienteHistorialProductoRepository
                    .findByIdVentaIn(idsVentas);

    Map<Integer, List<ClienteHistorialProducto>>
            productosPorVenta =
                    productos.stream()
                            .collect(
                                    Collectors.groupingBy(
                                            ClienteHistorialProducto::getIdVenta
                                    )
                            );

    List<CompraHistorialResponse> comprasResponse =
            compras.stream()
                    .map(compra ->
                            mapearCompraHistorial(
                                    compra,
                                    productosPorVenta.getOrDefault(
                                            compra.getIdVenta(),
                                            List.of()
                                    )
                            )
                    )
                    .collect(Collectors.toList());

    String nombreCliente =
            obtenerNombreCliente(cliente);

    ClienteHistorialDetalleResponse response =
            ClienteHistorialDetalleResponse.builder()
                    .idCliente(cliente.getIdCliente())
                    .nombreCliente(nombreCliente)
                    .iniciales(
                            generarIniciales(nombreCliente)
                    )
                    .tipoDocumento(
                            obtenerTipoDocumento(cliente)
                    )
                    .numeroDocumento(
                            obtenerNumeroDocumento(cliente)
                    )
                    .numeroCompras(
                            obtenerNumeroCompras(resumen)
                    )
                    .montoTotal(
                            obtenerMontoTotal(resumen)
                    )
                    .ticketPromedio(
                            obtenerTicketPromedio(resumen)
                    )
                    .compras(comprasResponse)
                    .build();

    log.info(
            "Historial de compras obtenido correctamente. "
                    + "IdCliente: {}, compras: {}",
            idCliente,
            comprasResponse.size()
    );

    return response;
}

    /**
     * Obtiene el detalle completo de un cliente.
     *
     * @param idCliente identificador del cliente.
     * @return detalle del cliente.
     */
    @Transactional(readOnly = true)
    public ClienteDetalleResponse obtenerDetalleCliente(
            Integer idCliente
    ) {

        log.info(
                "Obteniendo detalle del cliente con id: {}",
                idCliente
        );

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() ->
                        new RuntimeException("Cliente no encontrado")
                );

        if (cliente.getEstadoLogico() != EstadoLogico.ACTIVO) {
            throw new RuntimeException(
                    "El cliente no se encuentra activo"
            );
        }

        ClienteResumenVenta resumen =
                clienteResumenVentaRepository.findById(idCliente)
                        .orElse(null);

        String nombreCliente =
                obtenerNombreCliente(cliente);

        String tipoDocumento =
                obtenerTipoDocumento(cliente);

        String numeroDocumento =
                obtenerNumeroDocumento(cliente);

        Integer numeroCompras =
                obtenerNumeroCompras(resumen);

        BigDecimal montoTotal =
                obtenerMontoTotal(resumen);

        BigDecimal ticketPromedio =
                obtenerTicketPromedio(resumen);

        return ClienteDetalleResponse.builder()
                .idCliente(cliente.getIdCliente())
                .nombreCliente(nombreCliente)
                .tipoCliente(cliente.getTipoCliente().name())
                .tipoDocumento(tipoDocumento)
                .numeroDocumento(numeroDocumento)
                .telefono(cliente.getTelefono())
                .direccion(cliente.getDireccion())
                .estado(
                        cliente.getEstadoCliente()
                                == EstadoActivoInactivo.ACTIVO
                                        ? "ACTIVO"
                                        : "INACTIVO"
                )
                .numeroCompras(numeroCompras)
                .montoTotal(montoTotal)
                .ticketPromedio(ticketPromedio)
                .ultimaCompra(
                        resumen == null
                                ? null
                                : resumen.getUltimaCompra()
                )
                .build();
    }

    /**
     * Convierte una entidad Cliente en DTO para listado.
     */
    private ClienteListadoResponse mapearListado(
            Cliente cliente,
            ClienteResumenVenta resumen
    ) {

        String nombreCliente =
                obtenerNombreCliente(cliente);

        String tipoDocumento =
                obtenerTipoDocumento(cliente);

        String numeroDocumento =
                obtenerNumeroDocumento(cliente);

        return ClienteListadoResponse.builder()
                .idCliente(cliente.getIdCliente())
                .nombreCliente(nombreCliente)
                .iniciales(generarIniciales(nombreCliente))
                .tipoCliente(cliente.getTipoCliente().name())
                .tipoDocumento(tipoDocumento)
                .numeroDocumento(numeroDocumento)
                .telefono(cliente.getTelefono())
                .direccion(cliente.getDireccion())
                .estado(
                        cliente.getEstadoCliente()
                                == EstadoActivoInactivo.ACTIVO
                                        ? "ACTIVO"
                                        : "INACTIVO"
                )
                .numeroCompras(
                        obtenerNumeroCompras(resumen)
                )
                .montoTotal(
                        obtenerMontoTotal(resumen)
                )
                .ultimaCompra(
                        resumen == null
                                ? null
                                : resumen.getUltimaCompra()
                )
                .build();
    }

    /**
 * Convierte una compra del historial y sus productos
 * en el DTO correspondiente.
 */
private CompraHistorialResponse mapearCompraHistorial(
        ClienteHistorialCompra compra,
        List<ClienteHistorialProducto> productos
) {

    List<ProductoHistorialCompraResponse> productosResponse =
            productos.stream()
                    .map(producto ->
                            ProductoHistorialCompraResponse.builder()
                                    .idProducto(
                                            producto.getIdProducto()
                                    )
                                    .nombreProducto(
                                            producto.getNombreProducto()
                                    )
                                    .cantidad(
                                            producto.getCantidad()
                                    )
                                    .subtotal(
                                            producto.getSubtotal()
                                    )
                                    .build()
                    )
                    .collect(Collectors.toList());

    return CompraHistorialResponse.builder()
            .idVenta(compra.getIdVenta())
            .codigoVenta(compra.getCodigoVenta())
            .fechaHoraVenta(compra.getFechaHoraVenta())
            .estadoVenta(compra.getEstadoVenta())
            .total(compra.getTotal())
            .metodoPago(compra.getMetodoPago())
            .tipoComprobante(compra.getTipoComprobante())
            .numeroComprobante(
                    compra.getNumeroComprobante()
            )
            .atendidoPor(compra.getAtendidoPor())
            .productos(productosResponse)
            .build();
}

    /**
     * Obtiene la cantidad de compras del resumen.
     */
    private Integer obtenerNumeroCompras(
            ClienteResumenVenta resumen
    ) {

        if (resumen == null
                || resumen.getNumeroCompras() == null) {
            return 0;
        }

        return Math.toIntExact(
                resumen.getNumeroCompras()
        );
    }

    /**
     * Obtiene el monto total acumulado del cliente.
     */
    private BigDecimal obtenerMontoTotal(
            ClienteResumenVenta resumen
    ) {

        if (resumen == null
                || resumen.getMontoTotal() == null) {
            return BigDecimal.ZERO;
        }

        return resumen.getMontoTotal();
    }

    /**
     * Obtiene el ticket promedio del cliente.
     */
    private BigDecimal obtenerTicketPromedio(
            ClienteResumenVenta resumen
    ) {

        if (resumen == null
                || resumen.getTicketPromedio() == null) {
            return BigDecimal.ZERO;
        }

        return resumen.getTicketPromedio();
    }
  private String limpiarTextoOpcional(String texto) {

    if (texto == null || texto.trim().isBlank()) {
        return null;
    }

    return texto.trim();
}  

    /**
     * Filtra por tipo de cliente.
     */
    private boolean coincideTipo(
            Cliente cliente,
            String tipo
    ) {

        if (tipo == null || tipo.trim().isBlank()) {
            return true;
        }

        String tipoNormalizado =
                tipo.trim().toUpperCase(Locale.ROOT);

        if ("TODOS".equals(tipoNormalizado)) {
            return true;
        }

        try {
            TipoCliente tipoCliente =
                    TipoCliente.valueOf(tipoNormalizado);

            return cliente.getTipoCliente() == tipoCliente;

        } catch (IllegalArgumentException ex) {
            return true;
        }
    }

    /**
     * Filtra por nombre, razón social, DNI o RUC.
     */
    private boolean coincideBusqueda(
            Cliente cliente,
            String buscar
    ) {

        if (buscar == null || buscar.trim().isBlank()) {
            return true;
        }

        String texto = normalizar(buscar);

        return normalizar(
                obtenerNombreCliente(cliente)
        ).contains(texto)
                || normalizar(cliente.getDni()).contains(texto)
                || normalizar(cliente.getRuc()).contains(texto);
    }

    /**
     * Obtiene el nombre que se mostrará en pantalla.
     */
    private String obtenerNombreCliente(
            Cliente cliente
    ) {

        if (cliente.getTipoCliente() == TipoCliente.EMPRESA) {
            return cliente.getRazonSocial();
        }

        return cliente.getNombreCompleto();
    }

    /**
     * Obtiene el tipo de documento mostrado.
     */
    private String obtenerTipoDocumento(
            Cliente cliente
    ) {

        if (cliente.getTipoCliente() == TipoCliente.EMPRESA) {
            return "RUC";
        }

        return "DNI";
    }

    /**
     * Obtiene el número de documento mostrado.
     */
    private String obtenerNumeroDocumento(
            Cliente cliente
    ) {

        if (cliente.getTipoCliente() == TipoCliente.EMPRESA) {
            return cliente.getRuc();
        }

        return cliente.getDni();
    }

    /**
     * Genera iniciales para vista tarjeta.
     */
    private String generarIniciales(
            String nombre
    ) {

        if (nombre == null || nombre.trim().isBlank()) {
            return "CL";
        }

        String[] partes =
                nombre.trim().split("\\s+");

        if (partes.length == 1) {
            return partes[0]
                    .substring(
                            0,
                            Math.min(
                                    2,
                                    partes[0].length()
                            )
                    )
                    .toUpperCase(Locale.ROOT);
        }

        return (
                partes[0].substring(0, 1)
                        + partes[1].substring(0, 1)
        ).toUpperCase(Locale.ROOT);
    }

    /**
     * Normaliza texto para búsquedas sin distinguir
     * mayúsculas ni tildes.
     */
    private String normalizar(
            String texto
    ) {

        if (texto == null) {
            return "";
        }

        String textoNormalizado =
                Normalizer.normalize(
                        texto,
                        Normalizer.Form.NFD
                );

        return textoNormalizado
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }
    
}
