package com.proveperu.m06_usuarios.dto.response;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO utilizado para mostrar los indicadores principales
 * del dashboard de usuarios.
 *
 * Contiene información consolidada sobre la cantidad
 * de usuarios registrados, usuarios activos,
 * usuarios inactivos y roles definidos dentro
 * del sistema.
 *
 * @author David Sanchez
 */
@Getter
@Builder
public class UsuarioDashboardResponse {
      /**
     * Total de usuarios registrados.
     */
    private Long totalUsuarios;

    /**
     * Cantidad de usuarios activos.
     */
    private Long usuariosActivos;

    /**
     * Cantidad de usuarios inactivos.
     */
  private Long usuariosSuspendidos;

    /**
     * Cantidad de roles definidos.
     */
    private Long rolesDefinidos;
}
