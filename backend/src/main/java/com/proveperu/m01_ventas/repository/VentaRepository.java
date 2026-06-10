package com.proveperu.m01_ventas.repository;

import com.proveperu.m01_ventas.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer>, JpaSpecificationExecutor<Venta> {
    /**
     * Consulta venta por id con todas las relaciones necesarias para la vista resumen,
     * evitando N+1 queries en el detalle individual.
     */
    @Query("""
            SELECT v FROM Venta v
            LEFT JOIN FETCH v.cliente c
            LEFT JOIN FETCH v.usuario u
            LEFT JOIN FETCH u.rol r
            LEFT JOIN FETCH v.comprobante co
            WHERE v.idVenta = :idVenta
            """)
    Optional<Venta> findByIdWithRelaciones(@Param("idVenta") Integer idVenta);
}
