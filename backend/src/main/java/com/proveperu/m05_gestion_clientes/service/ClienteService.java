package com.proveperu.m05_gestion_clientes.service;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proveperu.m05_gestion_clientes.dto.response.ClienteDashboardResponse;
import com.proveperu.m05_gestion_clientes.dto.response.ClienteListadoResponse;
import com.proveperu.m05_gestion_clientes.entity.Cliente;
import com.proveperu.m05_gestion_clientes.enums.TipoCliente;
import com.proveperu.m05_gestion_clientes.repository.ClienteRepository;
import com.proveperu.shared.enums.EstadoActivoInactivo;
import com.proveperu.shared.enums.EstadoLogico;

import com.proveperu.m05_gestion_clientes.dto.response.ClienteDetalleResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio encargado de la lógica del módulo de gestión de clientes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteService {
      private final ClienteRepository clienteRepository;

    /**
     * Obtiene los indicadores principales del módulo de clientes.
     *
     * @return resumen de clientes registrados.
     */
    @Transactional(readOnly = true)
    public ClienteDashboardResponse obtenerDashboard() {

        log.info("Obteniendo dashboard del módulo de clientes");

        Long totalClientes =
                clienteRepository.countByEstadoLogico(EstadoLogico.ACTIVO);

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

        /*
         * Este dato depende de ventas.
         * Se actualizará cuando se conecte el historial de compras del cliente.
         */
        Long clientesFrecuentes = 0L;

        return ClienteDashboardResponse.builder()
                .totalClientes(totalClientes)
                .empresasTalleres(empresasTalleres)
                .personasNaturales(personasNaturales)
                .clientesFrecuentes(clientesFrecuentes)
                .build();
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

        List<ClienteListadoResponse> clientes =
                clienteRepository.findAllByOrderByIdClienteDesc()
                        .stream()
                        .filter(cliente ->
                                cliente.getEstadoLogico() == EstadoLogico.ACTIVO
                        )
                        .filter(cliente ->
                                coincideTipo(cliente, tipo)
                        )
                        .filter(cliente ->
                                coincideBusqueda(cliente, buscar)
                        )
                        .map(this::mapearListado)
                        .collect(Collectors.toList());

        log.info(
                "Clientes listados correctamente. Total: {}",
                clientes.size()
        );

        return clientes;
    }
    /**
 * Obtiene el detalle completo de un cliente.
 *
 * @param idCliente identificador del cliente.
 * @return detalle del cliente.
 */
@Transactional(readOnly = true)
public ClienteDetalleResponse obtenerDetalleCliente(Integer idCliente) {

    log.info(
            "Obteniendo detalle del cliente con id: {}",
            idCliente
    );

    Cliente cliente = clienteRepository.findById(idCliente)
            .orElseThrow(() ->
                    new RuntimeException("Cliente no encontrado")
            );

    if (cliente.getEstadoLogico() != EstadoLogico.ACTIVO) {
        throw new RuntimeException("El cliente no se encuentra activo");
    }

    String nombreCliente = obtenerNombreCliente(cliente);
    String tipoDocumento = obtenerTipoDocumento(cliente);
    String numeroDocumento = obtenerNumeroDocumento(cliente);

    /*
     * Estos valores dependen de ventas.
     * Más adelante se reemplazan por datos reales.
     */
    Integer numeroCompras = 0;
    BigDecimal montoTotal = BigDecimal.ZERO;
    BigDecimal ticketPromedio = BigDecimal.ZERO;

    return ClienteDetalleResponse.builder()
            .idCliente(cliente.getIdCliente())
            .nombreCliente(nombreCliente)
            .tipoCliente(cliente.getTipoCliente().name())
            .tipoDocumento(tipoDocumento)
            .numeroDocumento(numeroDocumento)
            .telefono(cliente.getTelefono())
            .direccion(cliente.getDireccion())
            .estado(
                    cliente.getEstadoCliente() == EstadoActivoInactivo.ACTIVO
                            ? "ACTIVO"
                            : "INACTIVO"
            )
            .numeroCompras(numeroCompras)
            .montoTotal(montoTotal)
            .ticketPromedio(ticketPromedio)
            .ultimaCompra(null)
            .build();
}

    /**
     * Convierte una entidad Cliente en DTO para listado.
     */
    private ClienteListadoResponse mapearListado(Cliente cliente) {

        String nombreCliente = obtenerNombreCliente(cliente);
        String tipoDocumento = obtenerTipoDocumento(cliente);
        String numeroDocumento = obtenerNumeroDocumento(cliente);

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
                        cliente.getEstadoCliente() == EstadoActivoInactivo.ACTIVO
                                ? "ACTIVO"
                                : "INACTIVO"
                )

                /*
                 * Estos valores dependen de ventas.
                 * Más adelante se reemplazan por datos reales.
                 */
                .numeroCompras(0)
                .montoTotal(BigDecimal.ZERO)
                .ultimaCompra(null)
                .build();
    }

    /**
     * Filtra por tipo de cliente.
     */
    private boolean coincideTipo(Cliente cliente, String tipo) {

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
    private boolean coincideBusqueda(Cliente cliente, String buscar) {

        if (buscar == null || buscar.trim().isBlank()) {
            return true;
        }

        String texto = normalizar(buscar);

        return normalizar(obtenerNombreCliente(cliente)).contains(texto)
                || normalizar(cliente.getDni()).contains(texto)
                || normalizar(cliente.getRuc()).contains(texto);
    }

    /**
     * Obtiene el nombre que se mostrará en pantalla.
     */
    private String obtenerNombreCliente(Cliente cliente) {

        if (cliente.getTipoCliente() == TipoCliente.EMPRESA) {
            return cliente.getRazonSocial();
        }

        return cliente.getNombreCompleto();
    }

    /**
     * Obtiene el tipo de documento mostrado.
     */
    private String obtenerTipoDocumento(Cliente cliente) {

        if (cliente.getTipoCliente() == TipoCliente.EMPRESA) {
            return "RUC";
        }

        return "DNI";
    }

    /**
     * Obtiene el número de documento mostrado.
     */
    private String obtenerNumeroDocumento(Cliente cliente) {

        if (cliente.getTipoCliente() == TipoCliente.EMPRESA) {
            return cliente.getRuc();
        }

        return cliente.getDni();
    }

    /**
     * Genera iniciales para vista tarjeta.
     */
    private String generarIniciales(String nombre) {

        if (nombre == null || nombre.trim().isBlank()) {
            return "CL";
        }

        String[] partes = nombre.trim().split("\\s+");

        if (partes.length == 1) {
            return partes[0]
                    .substring(0, Math.min(2, partes[0].length()))
                    .toUpperCase(Locale.ROOT);
        }

        return (
                partes[0].substring(0, 1)
                        + partes[1].substring(0, 1)
        ).toUpperCase(Locale.ROOT);
    }

    /**
     * Normaliza texto para búsquedas sin distinguir mayúsculas ni tildes.
     */
    private String normalizar(String texto) {

        if (texto == null) {
            return "";
        }

        String textoNormalizado =
                Normalizer.normalize(texto, Normalizer.Form.NFD);

        return textoNormalizado
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }
}
