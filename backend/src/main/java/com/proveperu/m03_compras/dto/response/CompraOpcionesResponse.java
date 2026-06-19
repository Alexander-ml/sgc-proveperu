package com.proveperu.m03_compras.dto.response;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Respuesta que agrupa las opciones necesarias
 * para registrar una nueva compra.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraOpcionesResponse {
    private List<ProveedorOpcionResponse> proveedores;

    private List<MetodoPagoOpcionResponse> metodosPago;

    private List<ProductoOpcionResponse> productos;
}
