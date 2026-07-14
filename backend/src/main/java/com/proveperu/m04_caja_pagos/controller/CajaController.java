package com.proveperu.m04_caja_pagos.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proveperu.m04_caja_pagos.dto.request.AbrirCajaRequest;
import com.proveperu.m04_caja_pagos.dto.request.CerrarCajaRequest;
import com.proveperu.m04_caja_pagos.dto.request.RegistrarEgresoCajaRequest;
import com.proveperu.m04_caja_pagos.dto.response.CajaDashboardResponse;
import com.proveperu.m04_caja_pagos.dto.response.CierreCajaResponse;
import com.proveperu.m04_caja_pagos.dto.response.MovimientoCajaResponse;
import com.proveperu.m04_caja_pagos.service.CajaService;
import com.proveperu.shared.dto.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/caja")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Caja y pagos",
        description = "Gestión de caja operativa, movimientos, ingresos y egresos"
)
@SecurityRequirement(name = "bearerAuth")
public class CajaController {
    
    private final CajaService cajaService;

    /**
     * Obtiene los indicadores principales de caja.
     *
     * Si no se envía idCaja, se toma la primera caja abierta.
     *
     * @param idCaja identificador opcional de la caja.
     * @return dashboard de caja.
     */
    @Operation(
            summary = "Obtener dashboard de caja",
            description = "Obtiene saldo actual, ingresos, egresos, monto de apertura "
                    + "y estado de la caja operativa."
    )
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<CajaDashboardResponse>>
            obtenerDashboard(
                    @RequestParam(
                            required = false
                    ) Integer idCaja
            ) {

        log.info(
                "Endpoint GET /api/caja/dashboard llamado. IdCaja: {}",
                idCaja
        );

        CajaDashboardResponse response =
                cajaService.obtenerDashboard(
                        idCaja
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Dashboard de caja obtenido correctamente"
                )
        );
    }

    /**
     * Lista los movimientos registrados durante la apertura activa.
     *
     * Si no se envía idCaja, se toma la primera caja abierta.
     *
     * @param idCaja identificador opcional de la caja.
     * @return movimientos de caja.
     */
    @Operation(
            summary = "Listar movimientos de caja",
            description = "Lista los movimientos registrados durante la apertura activa "
                    + "de la caja, incluyendo ingresos y egresos."
    )
    @GetMapping("/movimientos")
    public ResponseEntity<ApiResponse<List<MovimientoCajaResponse>>>
            listarMovimientos(
                    @RequestParam(
                            required = false
                    ) Integer idCaja
            ) {

        log.info(
                "Endpoint GET /api/caja/movimientos llamado. IdCaja: {}",
                idCaja
        );

        List<MovimientoCajaResponse> response =
                cajaService.listarMovimientos(
                        idCaja
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Movimientos de caja obtenidos correctamente"
                )
        );
    }
    @Operation(
        summary = "Registrar egreso de caja",
        description = "Registra un egreso manual en una caja abierta. "
                + "El sistema valida saldo suficiente, método de pago activo "
                + "y usuario autenticado."
)
@PostMapping("/{idCaja}/egresos")
public ResponseEntity<ApiResponse<CajaDashboardResponse>>
        registrarEgresoCaja(
                @PathVariable Integer idCaja,
                @Valid @RequestBody RegistrarEgresoCajaRequest request,
                Authentication authentication
        ) {

    log.info(
            "Endpoint POST /api/caja/{}/egresos llamado. Monto: {}",
            idCaja,
            request.getMonto()
    );

    String usuarioLogin =
            authentication == null
                    ? null
                    : authentication.getName();

    CajaDashboardResponse response =
            cajaService.registrarEgresoCaja(
                    idCaja,
                    request,
                    usuarioLogin
            );

    return ResponseEntity.ok(
            ApiResponse.success(
                    response,
                    "Egreso de caja registrado correctamente"
            )
    );
}

@Operation(
        summary = "Cerrar caja",
        description = "Cierra una caja abierta registrando el saldo teórico, "
                + "saldo real contado y la diferencia encontrada."
)
@PostMapping("/{idCaja}/cerrar")
public ResponseEntity<ApiResponse<CierreCajaResponse>>
        cerrarCaja(
                @PathVariable Integer idCaja,
                @Valid @RequestBody CerrarCajaRequest request,
                Authentication authentication
        ) {

    log.info(
            "Endpoint POST /api/caja/{}/cerrar llamado. Saldo real: {}",
            idCaja,
            request.getSaldoReal()
    );

    String usuarioLogin =
            authentication == null
                    ? null
                    : authentication.getName();

    CierreCajaResponse response =
            cajaService.cerrarCaja(
                    idCaja,
                    request,
                    usuarioLogin
            );

    return ResponseEntity.ok(
            ApiResponse.success(
                    response,
                    "Caja cerrada correctamente"
            )
    );
}
@Operation(
        summary = "Abrir caja",
        description = "Abre una caja cerrada registrando el monto inicial "
                + "y creando una nueva apertura de caja."
)
@PostMapping("/{idCaja}/abrir")
public ResponseEntity<ApiResponse<CajaDashboardResponse>>
        abrirCaja(
                @PathVariable Integer idCaja,
                @Valid @RequestBody AbrirCajaRequest request,
                Authentication authentication
        ) {

    log.info(
            "Endpoint POST /api/caja/{}/abrir llamado. Monto inicial: {}",
            idCaja,
            request.getMontoInicial()
    );

    String usuarioLogin =
            authentication == null
                    ? null
                    : authentication.getName();

    CajaDashboardResponse response =
            cajaService.abrirCaja(
                    idCaja,
                    request,
                    usuarioLogin
            );

    return ResponseEntity.ok(
            ApiResponse.success(
                    response,
                    "Caja abierta correctamente"
            )
    );
}
}
