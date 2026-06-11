package com.proveperu.m01_ventas.repository;

import com.proveperu.m01_ventas.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
    /**
     * Recupera los pagos activos de una venta con su método de pago.
     * Solo se muestran pagos con estado_logico = 1 (ACTIVO).
     */
    @Query("""
            SELECT p FROM Pago p
            JOIN FETCH p.metodoPago mp
            WHERE p.venta.idVenta = :idVenta
              AND p.estadoLogico = 1
            ORDER BY p.fechaHoraCreacion ASC
            """)
    List<Pago> findPagosActivosByVentaId(@Param("idVenta") Integer idVenta);

    /**
     * Recupera los pagos activos de múltiples ventas en una sola consulta
     * para evitar N+1 al paginar.
     */
    @Query("""
            SELECT p FROM Pago p
            JOIN FETCH p.metodoPago mp
            WHERE p.venta.idVenta IN :ventaIds
              AND p.estadoLogico = 1
            ORDER BY p.venta.idVenta ASC, p.fechaHoraCreacion ASC
            """)
    List<Pago> findPagosActivosByVentaIds(@Param("ventaIds") List<Integer> ventaIds);
}
