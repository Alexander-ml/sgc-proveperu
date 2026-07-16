package com.proveperu.m05_gestion_clientes.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proveperu.m05_gestion_clientes.dto.request.EditarClienteRequest;
import com.proveperu.m05_gestion_clientes.dto.request.RegistrarClienteRequest;
import com.proveperu.m05_gestion_clientes.dto.response.ClienteDashboardResponse;
import com.proveperu.m05_gestion_clientes.dto.response.ClienteDetalleResponse;
import com.proveperu.m05_gestion_clientes.dto.response.ClienteHistorialDetalleResponse;
import com.proveperu.m05_gestion_clientes.dto.response.ClienteHistorialListadoResponse;
import com.proveperu.m05_gestion_clientes.dto.response.ClienteListadoResponse;
import com.proveperu.m05_gestion_clientes.service.ClienteService;
import com.proveperu.shared.dto.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
/**
 * Controlador REST para el módulo de gestión de clientes.
 */
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Slf4j
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
 * Registra un nuevo cliente en el sistema.
 *
 * @param request datos del cliente.
 * @return detalle del cliente registrado.
 */
@Operation(
        summary = "Registrar cliente",
        description = "Registra una persona natural con DNI o una empresa con RUC."
)
@PostMapping
public ResponseEntity<ApiResponse<ClienteDetalleResponse>>
        registrarCliente(
                @Valid @RequestBody RegistrarClienteRequest request
        ) {

    log.info(
            "Endpoint POST /api/clientes llamado. Tipo de cliente: {}",
            request.getTipoCliente()
    );

    ClienteDetalleResponse response =
            clienteService.registrarCliente(request);

    return ResponseEntity.ok(
            ApiResponse.success(
                    response,
                    "Cliente registrado correctamente"
            )
    );
}

/**
 * Edita los datos de un cliente existente.
 *
 * @param idCliente identificador del cliente.
 * @param request nuevos datos del cliente.
 * @return detalle actualizado del cliente.
 */
@Operation(
        summary = "Editar cliente",
        description = "Actualiza los datos de una persona natural o empresa."
)
@PutMapping("/{idCliente}")
public ResponseEntity<ApiResponse<ClienteDetalleResponse>>
        editarCliente(
                @PathVariable Integer idCliente,
                @Valid @RequestBody EditarClienteRequest request
        ) {

    log.info(
            "Endpoint PUT /api/clientes/{} llamado. Tipo de cliente: {}",
            idCliente,
            request.getTipoCliente()
    );

    ClienteDetalleResponse response =
            clienteService.editarCliente(
                    idCliente,
                    request
            );

    return ResponseEntity.ok(
            ApiResponse.success(
                    response,
                    "Cliente actualizado correctamente"
            )
    );
}

/**
 * Desactiva un cliente sin eliminar su información.
 *
 * @param idCliente identificador del cliente.
 * @return detalle del cliente desactivado.
 */
@Operation(
        summary = "Desactivar cliente",
        description = "Cambia el estado físico del cliente a INACTIVO."
)
@PatchMapping("/{idCliente}/desactivar")
public ResponseEntity<ApiResponse<ClienteDetalleResponse>>
        desactivarCliente(
                @PathVariable Integer idCliente
        ) {

    log.info(
            "Endpoint PATCH /api/clientes/{}/desactivar llamado",
            idCliente
    );

    ClienteDetalleResponse response =
            clienteService.desactivarCliente(idCliente);

    return ResponseEntity.ok(
            ApiResponse.success(
                    response,
                    "Cliente desactivado correctamente"
            )
    );
}

/**
 * Activa nuevamente un cliente desactivado.
 *
 * @param idCliente identificador del cliente.
 * @return detalle del cliente activado.
 */
@Operation(
        summary = "Activar cliente",
        description = "Cambia el estado físico del cliente a ACTIVO."
)
@PatchMapping("/{idCliente}/activar")
public ResponseEntity<ApiResponse<ClienteDetalleResponse>>
        activarCliente(
                @PathVariable Integer idCliente
        ) {

    log.info(
            "Endpoint PATCH /api/clientes/{}/activar llamado",
            idCliente
    );

    ClienteDetalleResponse response =
            clienteService.activarCliente(idCliente);

    return ResponseEntity.ok(
            ApiResponse.success(
                    response,
                    "Cliente activado correctamente"
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
 * Lista los clientes que poseen historial de compras.
 *
 * Al no enviar un texto de búsqueda, devuelve todos los clientes
 * que tengan al menos una venta registrada.
 *
 * @param buscar texto opcional para buscar por nombre, razón social,
 *               DNI o RUC.
 * @return clientes con historial de compras.
 */
@Operation(
        summary = "Listar clientes con historial",
        description = "Obtiene los clientes que tienen al menos una compra registrada. "
                + "Permite buscar por nombre, razón social, DNI o RUC."
)
@GetMapping("/historial")
public ResponseEntity<
        ApiResponse<List<ClienteHistorialListadoResponse>>>
        listarClientesConHistorial(
                @RequestParam(
                        required = false
                ) String buscar
        ) {

    log.info(
            "Endpoint GET /api/clientes/historial llamado. Buscar: {}",
            buscar
    );

    List<ClienteHistorialListadoResponse> response =
            clienteService.listarClientesConHistorial(
                    buscar
            );

    return ResponseEntity.ok(
            ApiResponse.success(
                    response,
                    "Clientes con historial obtenidos correctamente"
            )
    );
}

/**
 * Obtiene el historial completo de compras de un cliente.
 *
 * Incluye sus indicadores generales, ventas registradas
 * y productos incluidos en cada venta.
 *
 * @param idCliente identificador del cliente.
 * @return historial completo de compras.
 */
@Operation(
        summary = "Obtener historial de compras de un cliente",
        description = "Obtiene los datos del cliente, sus indicadores "
                + "y todas sus compras registradas con sus productos."
)
@GetMapping("/{idCliente}/historial-compras")
public ResponseEntity<ApiResponse<ClienteHistorialDetalleResponse>>
        obtenerHistorialCompras(
                @PathVariable Integer idCliente
        ) {

    log.info(
            "Endpoint GET /api/clientes/{}/historial-compras llamado",
            idCliente
    );

    ClienteHistorialDetalleResponse response =
            clienteService.obtenerHistorialCompras(
                    idCliente
            );

    return ResponseEntity.ok(
            ApiResponse.success(
                    response,
                    "Historial de compras obtenido correctamente"
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
