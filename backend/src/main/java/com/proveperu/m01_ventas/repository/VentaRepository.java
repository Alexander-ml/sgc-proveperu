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
     * Recupera una venta junto con sus relaciones principales para la vista resumen.
     *
     * <p>
     * Esta consulta carga en una sola operación:
     * </p>
     * <ul>
     *     <li>El cliente asociado.</li>
     *     <li>El usuario vendedor.</li>
     *     <li>El rol del usuario vendedor.</li>
     *     <li>El comprobante emitido, si existe.</li>
     * </ul>
     *
     * <p>
     * Se utiliza principalmente para evitar el problema N+1 al consultar
     * el detalle individual de una venta o al construir un resumen funcional
     * con sus relaciones ya resueltas.
     * </p>
     *
     * @param idVenta identificador técnico de la venta a consultar.
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
    Optional<Venta> findByIdWithRelaciones(@Param("idVenta") Integer idVenta);
}
