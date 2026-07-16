package com.proveperu.m03_compras.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Respuesta utilizada para mostrar proveedores
 * en el selector de registro de compras.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorOpcionResponse {
    private Integer idProveedor;

    private String ruc;

    private String razonSocial;
}
