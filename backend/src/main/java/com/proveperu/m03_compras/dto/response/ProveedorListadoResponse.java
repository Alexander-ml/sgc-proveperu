package com.proveperu.m03_compras.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Respuesta utilizada para mostrar proveedores
 * dentro del módulo de compras.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorListadoResponse {
     private Integer idProveedor;

    private String ruc;

    private String razonSocial;

    private String telefono;

    private String direccion;

    private String estado;
}
