package com.proveperu.m04_caja_pagos.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proveperu.m04_caja_pagos.dto.request.AbrirCajaRequest;
import com.proveperu.m04_caja_pagos.dto.request.CerrarCajaRequest;
import com.proveperu.m04_caja_pagos.dto.request.RegistrarEgresoCajaRequest;
import com.proveperu.m04_caja_pagos.dto.response.CajaDashboardResponse;
import com.proveperu.m04_caja_pagos.dto.response.CierreCajaResponse;
import com.proveperu.m04_caja_pagos.dto.response.MovimientoCajaResponse;
import com.proveperu.m04_caja_pagos.entity.AperturaCaja;
import com.proveperu.m04_caja_pagos.entity.Caja;
import com.proveperu.m04_caja_pagos.entity.CierreCaja;
import com.proveperu.m04_caja_pagos.entity.MovimientoCaja;
import com.proveperu.m04_caja_pagos.enums.EstadoCaja;
import com.proveperu.m04_caja_pagos.enums.EstadoMovimientoCaja;
import com.proveperu.m04_caja_pagos.enums.NombreTipoMovimientoCaja;
import com.proveperu.m04_caja_pagos.repository.AperturaCajaRepository;
import com.proveperu.m04_caja_pagos.repository.CajaRepository;
import com.proveperu.m04_caja_pagos.repository.CierreCajaRepository;
import com.proveperu.m04_caja_pagos.repository.MovimientoCajaRepository;
import com.proveperu.m06_usuarios.entity.Usuario;
import com.proveperu.m06_usuarios.enums.EstadoUsuario;
import com.proveperu.m06_usuarios.repository.UsuarioRepository;
import com.proveperu.shared.exception.ResourceNotFoundException;
import com.proveperu.shared.exception.ValidationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@RequiredArgsConstructor
@Slf4j
public class CajaService {

    private final CajaRepository cajaRepository;
    private final AperturaCajaRepository aperturaCajaRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CierreCajaRepository cierreCajaRepository;
    @PersistenceContext
private EntityManager entityManager;

    @Transactional(readOnly = true)
    public CajaDashboardResponse obtenerDashboard(
            Integer idCaja
    ) {

        log.info(
                "Consultando dashboard de caja. IdCaja: {}",
                idCaja
        );

        Caja caja = obtenerCajaOperativa(idCaja);

        AperturaCaja aperturaActiva =
                obtenerAperturaActiva(caja.getIdCaja());

        List<MovimientoCaja> movimientos =
                movimientoCajaRepository
                        .findByCaja_IdCajaAndEstadoFisicoAndFechaHoraMovimientoGreaterThanEqualOrderByFechaHoraMovimientoAsc(
                                caja.getIdCaja(),
                                EstadoMovimientoCaja.REGISTRADO,
                                aperturaActiva.getFechaHoraApertura()
                        );

        BigDecimal totalIngresos =
                calcularTotalPorTipo(
                        movimientos,
                        NombreTipoMovimientoCaja.INGRESO
                );

        BigDecimal totalEgresos =
                calcularTotalPorTipo(
                        movimientos,
                        NombreTipoMovimientoCaja.EGRESO
                );

        BigDecimal saldoActual =
                aperturaActiva.getMontoInicial()
                        .add(totalIngresos)
                        .subtract(totalEgresos);

        log.info(
                "Dashboard de caja consultado. IdCaja: {}, ingresos: {}, egresos: {}, saldo: {}",
                caja.getIdCaja(),
                totalIngresos,
                totalEgresos,
                saldoActual
        );

        return CajaDashboardResponse.builder()
                .idCaja(caja.getIdCaja())
                .nombreCaja(caja.getNombreCaja())
                .estadoCaja(caja.getEstadoFisico().name())
                .idAperturaCaja(
                        aperturaActiva.getIdAperturaCaja()
                )
                .montoApertura(
                        aperturaActiva.getMontoInicial()
                )
                .saldoActual(saldoActual)
                .totalIngresos(totalIngresos)
                .totalEgresos(totalEgresos)
                .cantidadMovimientos(
                        movimientos.size()
                )
                .abiertaPor(
                        aperturaActiva.getUsuarioRegistro()
                                .getNombreCompleto()
                )
                .fechaHoraApertura(
                        aperturaActiva.getFechaHoraApertura()
                )
                .build();
    }

    @Transactional(readOnly = true)
    public List<MovimientoCajaResponse> listarMovimientos(
            Integer idCaja
    ) {

        log.info(
                "Listando movimientos de caja. IdCaja: {}",
                idCaja
        );

        Caja caja = obtenerCajaOperativa(idCaja);

        AperturaCaja aperturaActiva =
                obtenerAperturaActiva(caja.getIdCaja());

        List<MovimientoCajaResponse> movimientos =
                movimientoCajaRepository
                        .findByCaja_IdCajaAndEstadoFisicoAndFechaHoraMovimientoGreaterThanEqualOrderByFechaHoraMovimientoAsc(
                                caja.getIdCaja(),
                                EstadoMovimientoCaja.REGISTRADO,
                                aperturaActiva.getFechaHoraApertura()
                        )
                        .stream()
                        .map(this::mapearMovimiento)
                        .collect(Collectors.toList());

        log.info(
                "Movimientos de caja listados correctamente. IdCaja: {}, total: {}",
                caja.getIdCaja(),
                movimientos.size()
        );

        return movimientos;
    }

    @Transactional
public CajaDashboardResponse registrarEgresoCaja(
        Integer idCaja,
        RegistrarEgresoCajaRequest request,
        String usuarioLogin
) {

    log.info(
            "Registrando egreso de caja. IdCaja: {}, Usuario: {}, Monto: {}",
            idCaja,
            usuarioLogin,
            request.getMonto()
    );

    if (idCaja == null) {
        throw new ValidationException(
                "Debe indicar la caja donde se registrará el egreso"
        );
    }

    Caja caja = obtenerCajaOperativa(idCaja);

    obtenerAperturaActiva(
            caja.getIdCaja()
    );

    Usuario usuario =
            obtenerUsuarioAutenticado(
                    usuarioLogin
            );

    try {

        movimientoCajaRepository.registrarEgresoCaja(
                caja.getIdCaja(),
                usuario.getIdUsuario(),
                request.getIdMetodoPago(),
                request.getMonto(),
                limpiarTextoOpcional(
                        request.getDescripcion()
                )
        );

    } catch (DataAccessException ex) {

        log.warn(
                "No se pudo registrar el egreso de caja. IdCaja: {}, Motivo: {}",
                idCaja,
                ex.getMostSpecificCause().getMessage()
        );

        throw new ValidationException(
                ex.getMostSpecificCause().getMessage(),
                ex
        );
    }

    log.info(
            "Egreso de caja registrado correctamente. IdCaja: {}, Usuario: {}",
            caja.getIdCaja(),
            usuario.getUsuarioLogin()
    );

    return obtenerDashboard(
            caja.getIdCaja()
    );
}

@Transactional
public CierreCajaResponse cerrarCaja(
        Integer idCaja,
        CerrarCajaRequest request,
        String usuarioLogin
) {

    log.info(
            "Cerrando caja. IdCaja: {}, Usuario: {}, Saldo real: {}",
            idCaja,
            usuarioLogin,
            request.getSaldoReal()
    );

    if (idCaja == null) {
        throw new ValidationException(
                "Debe indicar la caja que se cerrará"
        );
    }

    Caja caja = obtenerCajaOperativa(idCaja);

    AperturaCaja aperturaActiva =
            obtenerAperturaActiva(
                    caja.getIdCaja()
            );

    Usuario usuario =
            obtenerUsuarioAutenticado(
                    usuarioLogin
            );

    try {

        cierreCajaRepository.cerrarCaja(
                caja.getIdCaja(),
                usuario.getIdUsuario(),
                request.getSaldoReal()
        );

    } catch (DataAccessException ex) {

        log.warn(
                "No se pudo cerrar la caja. IdCaja: {}, Motivo: {}",
                idCaja,
                ex.getMostSpecificCause().getMessage()
        );

        throw new ValidationException(
                ex.getMostSpecificCause().getMessage(),
                ex
        );
    }

    CierreCaja cierreCaja =
            cierreCajaRepository
                    .findByAperturaCaja_IdAperturaCaja(
                            aperturaActiva.getIdAperturaCaja()
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Cierre de caja",
                                    aperturaActiva.getIdAperturaCaja()
                            )
                    );

    log.info(
            "Caja cerrada correctamente. IdCaja: {}, IdCierreCaja: {}",
            caja.getIdCaja(),
            cierreCaja.getIdCierreCaja()
    );

    return mapearCierreCaja(
            cierreCaja
    );
}

@Transactional
public CajaDashboardResponse abrirCaja(
        Integer idCaja,
        AbrirCajaRequest request,
        String usuarioLogin
) {

    log.info(
            "Abriendo caja. IdCaja: {}, Usuario: {}, Monto inicial: {}",
            idCaja,
            usuarioLogin,
            request.getMontoInicial()
    );

    if (idCaja == null) {
        throw new ValidationException(
                "Debe indicar la caja que se abrirá"
        );
    }

    Caja caja = cajaRepository.findById(idCaja)
            .orElseThrow(() -> {

                log.warn(
                        "Caja no encontrada para apertura. IdCaja: {}",
                        idCaja
                );

                return new ResourceNotFoundException(
                        "Caja",
                        idCaja
                );
            });

    Usuario usuario =
            obtenerUsuarioAutenticado(
                    usuarioLogin
            );

    try {

        aperturaCajaRepository.abrirCaja(
                caja.getIdCaja(),
                usuario.getIdUsuario(),
                request.getMontoInicial()
        );

        entityManager.flush();
        entityManager.clear();

    } catch (DataAccessException ex) {

        log.warn(
                "No se pudo abrir la caja. IdCaja: {}, Motivo: {}",
                idCaja,
                ex.getMostSpecificCause().getMessage()
        );

        throw new ValidationException(
                ex.getMostSpecificCause().getMessage(),
                ex
        );
    }

    AperturaCaja aperturaActiva =
            aperturaCajaRepository
                    .findFirstByCaja_IdCajaAndCierreCajaIsNullOrderByFechaHoraAperturaDesc(
                            caja.getIdCaja()
                    )
                    .orElseThrow(() ->
                            new ValidationException(
                                    "La caja fue abierta, pero no se encontró la apertura activa"
                            )
                    );

    log.info(
            "Caja abierta correctamente. IdCaja: {}, IdAperturaCaja: {}, Usuario: {}",
            caja.getIdCaja(),
            aperturaActiva.getIdAperturaCaja(),
            usuario.getUsuarioLogin()
    );

    return CajaDashboardResponse.builder()
            .idCaja(caja.getIdCaja())
            .nombreCaja(caja.getNombreCaja())
            .estadoCaja("ABIERTA")
            .idAperturaCaja(aperturaActiva.getIdAperturaCaja())
            .montoApertura(aperturaActiva.getMontoInicial())
            .saldoActual(aperturaActiva.getMontoInicial())
            .totalIngresos(BigDecimal.ZERO)
            .totalEgresos(BigDecimal.ZERO)
            .cantidadMovimientos(0)
            .abiertaPor(usuario.getNombreCompleto())
            .fechaHoraApertura(aperturaActiva.getFechaHoraApertura())
            .build();
}

    private Caja obtenerCajaOperativa(
            Integer idCaja
    ) {
    Caja caja;

    if (idCaja != null) {
        caja = cajaRepository.findById(idCaja)
                .orElseThrow(() -> {

                    log.warn(
                            "Caja no encontrada. IdCaja: {}",
                            idCaja
                    );

                    return new ResourceNotFoundException(
                            "Caja",
                            idCaja
                    );
                });
    } else {
        caja = cajaRepository
                .findFirstByEstadoFisicoOrderByIdCajaAsc(
                        EstadoCaja.ABIERTA
                )
                .orElseThrow(() -> {

                    log.warn(
                            "No existe una caja abierta disponible"
                    );

                    return new ValidationException(
                            "No existe una caja abierta"
                    );
                });
    }

    if (caja.getEstadoFisico() != EstadoCaja.ABIERTA) {

        log.warn(
                "La caja no está abierta. IdCaja: {}, Estado: {}",
                caja.getIdCaja(),
                caja.getEstadoFisico()
        );

        throw new ValidationException(
                "La caja no se encuentra abierta"
        );
    }

    return caja;
    }

    private AperturaCaja obtenerAperturaActiva(
            Integer idCaja
    ) {

         return aperturaCajaRepository
            .findFirstByCaja_IdCajaAndCierreCajaIsNullOrderByFechaHoraAperturaDesc(
                    idCaja
            )
            .orElseThrow(() -> {

                log.warn(
                        "No se encontró apertura activa para la caja {}",
                        idCaja
                );

                return new ValidationException(
                        "La caja no tiene una apertura activa"
                );
            });
    }

    private BigDecimal calcularTotalPorTipo(
            List<MovimientoCaja> movimientos,
            NombreTipoMovimientoCaja tipo
    ) {

        return movimientos.stream()
                .filter(movimiento ->
                        movimiento.getTipoMovimientoCaja()
                                .getNombreTipoMovimiento() == tipo
                )
                .map(MovimientoCaja::getMonto)
                .filter(Objects::nonNull)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    private MovimientoCajaResponse mapearMovimiento(
            MovimientoCaja movimiento
    ) {

        return MovimientoCajaResponse.builder()
                .idMovimientoCaja(
                        movimiento.getIdMovimientoCaja()
                )
                .fechaHoraMovimiento(
                        movimiento.getFechaHoraMovimiento()
                )
                .tipoMovimiento(
                        movimiento.getTipoMovimientoCaja()
                                .getNombreTipoMovimiento()
                                .name()
                )
                .descripcion(
                        movimiento.getDescripcion()
                )
                .metodoPago(
                        movimiento.getMetodoPago() == null
                                ? null
                                : movimiento.getMetodoPago()
                                        .getNombreMetodoPago()
                )
                .monto(
                        movimiento.getMonto()
                )
                .estadoMovimiento(
                        movimiento.getEstadoFisico().name()
                )
                .registradoPor(
                        movimiento.getUsuarioRegistra()
                                .getNombreCompleto()
                )
                .idVenta(
                        movimiento.getVenta() == null
                                ? null
                                : movimiento.getVenta()
                                        .getIdVenta()
                )
                .idCompra(
                        movimiento.getCompra() == null
                                ? null
                                : movimiento.getCompra()
                                        .getIdCompra()
                )
                .build();
    }
private Usuario obtenerUsuarioAutenticado(
        String usuarioLogin
) {

    if (usuarioLogin == null
            || usuarioLogin.trim().isBlank()
            || "anonymousUser".equals(usuarioLogin)) {

        throw new ValidationException(
                "Debe iniciar sesión para registrar el egreso"
        );
    }

    Usuario usuario = usuarioRepository
            .findByUsuarioLogin(
                    usuarioLogin
            )
            .orElseThrow(() -> {

                log.warn(
                        "Usuario autenticado no encontrado. Login: {}",
                        usuarioLogin
                );

                return new ResourceNotFoundException(
                        "Usuario",
                        usuarioLogin
                );
            });

    if (usuario.getEstadoUsuario() != EstadoUsuario.ACTIVO) {

        log.warn(
                "Usuario inactivo intentando registrar egreso. Login: {}",
                usuarioLogin
        );

        throw new ValidationException(
                "El usuario autenticado no se encuentra activo"
        );
    }

    return usuario;
}

private String limpiarTextoOpcional(
        String texto
) {

    if (texto == null || texto.trim().isBlank()) {
        return null;
    }

    return texto.trim();
}

private CierreCajaResponse mapearCierreCaja(
        CierreCaja cierreCaja
) {

    Caja caja =
            cierreCaja.getAperturaCaja()
                    .getCaja();

    return CierreCajaResponse.builder()
            .idCierreCaja(
                    cierreCaja.getIdCierreCaja()
            )
            .idAperturaCaja(
                    cierreCaja.getAperturaCaja()
                            .getIdAperturaCaja()
            )
            .idCaja(
                    caja.getIdCaja()
            )
            .nombreCaja(
                    caja.getNombreCaja()
            )
            .saldoTeorico(
                    cierreCaja.getSaldoTeorico()
            )
            .saldoReal(
                    cierreCaja.getSaldoReal()
            )
            .diferencia(
                    cierreCaja.getDiferencia()
            )
            .fechaHoraCierre(
                    cierreCaja.getFechaHoraCierre()
            )
            .cerradaPor(
                    cierreCaja.getUsuarioRegistro()
                            .getNombreCompleto()
            )
            .estadoCaja(
                    caja.getEstadoFisico().name()
            )
            .build();
}
}
