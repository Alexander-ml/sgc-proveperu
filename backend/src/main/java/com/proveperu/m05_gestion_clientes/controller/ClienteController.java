package com.proveperu.m05_gestion_clientes.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proveperu.m05_gestion_clientes.dto.response.ClienteDashboardResponse;
import com.proveperu.m05_gestion_clientes.dto.response.ClienteListadoResponse;
import com.proveperu.m05_gestion_clientes.service.ClienteService;
import com.proveperu.shared.dto.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PathVariable;

import com.proveperu.m05_gestion_clientes.dto.response.ClienteDetalleResponse;

/**
 * Controlador REST para el módulo de gestión de clientes.
 */
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Tag(
        name = "Gestión de Clientes",
        description = "Endpoints para administrar clientes, indicadores e historial de compras."
)
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {
    
    private final ClienteService clienteService;

    /**
     * Obtiene los indicadores principales del módulo de clientes.
     *
     * @return dashboard de clientes.
     */
    @Operation(
            summary = "Dashboard de clientes",
            description = "Obtiene indicadores como total de clientes, empresas, personas naturales y clientes frecuentes."
    )
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<ClienteDashboardResponse>>
            obtenerDashboard() {

        ClienteDashboardResponse response =
                clienteService.obtenerDashboard();

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Dashboard de clientes obtenido correctamente"
                )
        );
    }

    /**
     * Lista clientes con búsqueda y filtro por tipo.
     *
     * @param buscar texto para buscar por nombre, razón social, DNI o RUC.
     * @param tipo filtro: TODOS, EMPRESA o PERSONA.
     * @return lista de clientes.
     */
    @Operation(
            summary = "Listar clientes",
            description = "Lista clientes activos permitiendo búsqueda por nombre, razón social, DNI o RUC y filtro por tipo de cliente."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<ClienteListadoResponse>>>
            listarClientes(
                    @RequestParam(required = false) String buscar,
                    @RequestParam(required = false) String tipo
            ) {

        List<ClienteListadoResponse> response =
                clienteService.listarClientes(buscar, tipo);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Clientes obtenidos correctamente"
                )
        );
    }
    /**
 * Obtiene el detalle completo de un cliente.
 *
 * @param idCliente identificador del cliente.
 * @return detalle del cliente.
 */
@Operation(
        summary = "Detalle de cliente",
        description = "Obtiene los datos completos de un cliente para mostrar el detalle desde la vista tabla o tarjeta."
)
@GetMapping("/{idCliente}")
public ResponseEntity<ApiResponse<ClienteDetalleResponse>>
        obtenerDetalleCliente(
                @PathVariable Integer idCliente
        ) {

    ClienteDetalleResponse response =
            clienteService.obtenerDetalleCliente(idCliente);

    return ResponseEntity.ok(
            ApiResponse.success(
                    response,
                    "Detalle del cliente obtenido correctamente"
            )
    );
}
}
