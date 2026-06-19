package com.proveperu.m03_compras.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Respuesta utilizada para mostrar productos
 * disponibles en el registro de compras.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoOpcionResponse {
       private Integer idProducto;

    private String codigoProducto;

    private String nombreProducto;

    private String unidadMedida; 
}
