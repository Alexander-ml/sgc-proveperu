package com.proveperu.m01_ventas.repository;

import com.proveperu.m01_ventas.entity.Comprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComprobanteRepository extends JpaRepository<Comprobante, Integer> {

    Optional<Comprobante> findByVentaIdVenta(Integer idVenta);

    /**
     * Recupera comprobantes de múltiples ventas en una sola consulta
     * para evitar N+1 al paginar.
     */
    @Query("""
            SELECT c FROM Comprobante c
            WHERE c.venta.idVenta IN :ventaIds
            """)
    List<Comprobante> findByVentaIdIn(@Param("ventaIds") List<Integer> ventaIds);
}
