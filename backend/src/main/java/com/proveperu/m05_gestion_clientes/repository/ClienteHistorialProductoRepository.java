package com.proveperu.m05_gestion_clientes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proveperu.m05_gestion_clientes.entity.ClienteHistorialProducto;

/**
 * Repository de solo lectura para consultar los productos
 * incluidos dentro de las ventas del historial.
 */
@Repository
public interface ClienteHistorialProductoRepository 
 extends JpaRepository<ClienteHistorialProducto, String> {
    
    /**
     * Obtiene todos los productos pertenecientes a una venta.
     *
     * @param idVenta identificador de la venta.
     * @return productos incluidos en la venta.
     */
    List<ClienteHistorialProducto> findByIdVenta(
            Integer idVenta
    );

    /**
     * Obtiene los productos pertenecientes a varias ventas.
     *
     * @param idsVentas identificadores de las ventas.
     * @return productos incluidos en las ventas indicadas.
     */
    List<ClienteHistorialProducto> findByIdVentaIn(
            List<Integer> idsVentas
    );
    
}
