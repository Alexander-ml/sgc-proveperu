package com.proveperu.m01_ventas.repository;

import com.proveperu.m01_ventas.entity.DetalleVenta;
import com.proveperu.m01_ventas.entity.DetalleVentaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de persistencia para la entidad {@link DetalleVenta}.
 *
 * <p>
 * Proporciona consultas optimizadas para recuperar los productos
 * incluidos en una venta, cargando las relaciones necesarias
 * en una sola operación para evitar el problema N+1.
 * </p>
 */
@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, DetalleVentaId> {
    /**
     * Recupera todos los detalles de una venta con su producto asociado.
     *
     * <p>
     * La consulta carga el producto completo mediante {@code JOIN FETCH}
     * para disponer del código y nombre sin necesidad de consultas adicionales.
     * </p>
     *
     * @param idVenta identificador de la venta.
     * @return lista de detalles con producto cargado.
     */
    @Query("""
            SELECT dv FROM DetalleVenta dv
            JOIN FETCH dv.producto p
            WHERE dv.venta.idVenta = :idVenta
            ORDER BY p.nombreProducto ASC
            """)
    List<DetalleVenta> findDetalleConProductoByVentaId(@Param("idVenta") Integer idVenta);
}
