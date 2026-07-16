package com.proveperu.m05_gestion_clientes.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Respuesta para mostrar los indicadores principales
 * del módulo de gestión de clientes.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDashboardResponse {
     private Long totalClientes;

    private Long empresasTalleres;

    private Long personasNaturales;

    private Long clientesFrecuentes;
}
