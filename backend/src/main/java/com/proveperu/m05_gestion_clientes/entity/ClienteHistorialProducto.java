package com.proveperu.m05_gestion_clientes.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad de solo lectura que representa un producto
 * incluido dentro de una venta del historial de compras.
 *
 * La información proviene de la vista
 * vw_historial_productos_cliente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Immutable
@Table(name = "vw_historial_productos_cliente")
public class ClienteHistorialProducto {
  
    /**
     * Identificador único generado por la vista.
     *
     * Está formado por el id de la venta y el id del producto.
     */
    @Id
    @Column(name = "id_detalle")
    private String idDetalle;

    /**
     * Identificador de la venta.
     */
    @Column(name = "id_venta")
    private Integer idVenta;

    /**
     * Identificador del producto.
     */
    @Column(name = "id_producto")
    private Integer idProducto;

    /**
     * Nombre del producto vendido.
     */
    @Column(name = "nombre_producto")
    private String nombreProducto;

    /**
     * Cantidad comprada del producto.
     */
    @Column(name = "cantidad")
    private BigDecimal cantidad;

    /**
     * Subtotal correspondiente al producto.
     */
    @Column(name = "subtotal")
    private BigDecimal subtotal;  
}
