package com.proveperu.m01_ventas.repository;

import com.proveperu.m01_ventas.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio principal de persistencia para la entidad {@link Venta}.
 *
 * <p>
 * Este componente actúa como punto de acceso a las ventas registradas en el
 * sistema, permitiendo operaciones CRUD y consultas dinámicas mediante
 * {@link JpaSpecificationExecutor}.
 * </p>
 *
 * <p>
 * También expone consultas específicas para cargar una venta con sus relaciones
 * esenciales cuando se requiere construir vistas detalladas o resúmenes
 * sin provocar cargas perezosas adicionales.
 * </p>
 */
@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer>, JpaSpecificationExecutor<Venta> {

    /**
     * Recupera una venta con sus relaciones completas para la vista de detalle.
     *
     * <p>
     * Carga en una sola operación:
     * cliente, usuario vendedor, rol del vendedor y comprobante.
     * Los detalles y pagos se cargan en consultas separadas para
     * evitar productos cartesianos.
     * </p>
     *
     * @param idVenta identificador técnico de la venta.
     * @return venta con relaciones cargadas, si existe.
     */
    @Query("""
        SELECT v FROM Venta v
        LEFT JOIN FETCH v.cliente c
        LEFT JOIN FETCH v.usuario u
        LEFT JOIN FETCH u.rol r
        LEFT JOIN FETCH v.comprobante co
        WHERE v.idVenta = :idVenta
        """)
    Optional<Venta> findDetalleCompletoById(@Param("idVenta") Integer idVenta);
}
