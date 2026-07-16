package com.proveperu.m01_ventas.repository;

import com.proveperu.m01_ventas.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de persistencia para la entidad {@link Pago}.
 *
 * <p>
 * Este repositorio se utiliza para recuperar pagos asociados a ventas,
 * incluyendo el método de pago utilizado y aplicando filtros por estado lógico
 * para mostrar únicamente registros activos en los listados funcionales.
 * </p>
 * </p>
 */
@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
    /**
     * Recupera los pagos activos de una venta específica junto con su método de pago.
     *
     * <p>
     * Este método se utiliza cuando se necesita construir el resumen de pagos
     * de una venta puntual, manteniendo la consulta optimizada mediante
     * {@code JOIN FETCH} para cargar el método de pago en la misma operación.
     * </p>
     *
     * <p>
     * Solo se devuelven pagos con estado lógico activo.
     * </p>
     *
     * @param idVenta identificador de la venta cuya información de pagos se desea consultar.
     * @return lista de pagos activos ordenados por fecha de creación ascendente.
     */
    @Query("""
            SELECT p FROM Pago p
            JOIN FETCH p.metodoPago mp
            WHERE p.venta.idVenta IN :idVenta
            AND p.estadoLogico = com.proveperu.shared.enums.EstadoLogico.ACTIVO
            ORDER BY p.venta.idVenta ASC, p.fechaHoraCreacion ASC
            """)
    List<Pago> findPagosActivosByVentaId(@Param("idVenta") Integer idVenta);

    /**
     * Recupera los pagos activos de múltiples ventas en una sola consulta.
     *
     * <p>
     * Este método está diseñado para escenarios de paginación donde se necesita
     * cargar los pagos de varias ventas simultáneamente, evitando consultas repetidas
     * por cada registro del listado.
     * </p>
     *
     * <p>
     * La consulta incluye el método de pago mediante {@code JOIN FETCH} y devuelve
     * los resultados ordenados primero por venta y luego por fecha de creación,
     * facilitando el agrupamiento en la capa de servicio.
     * </p>
     *
     * @param ventaIds lista de identificadores de ventas a consultar.
     * @return lista de pagos activos correspondientes a las ventas indicadas.
     */
    @Query("""
            SELECT p FROM Pago p
            JOIN FETCH p.metodoPago mp
            WHERE p.venta.idVenta IN :ventaIds
              AND p.estadoLogico = com.proveperu.shared.enums.EstadoLogico.ACTIVO
            ORDER BY p.venta.idVenta ASC, p.fechaHoraCreacion ASC
            """)
    List<Pago> findPagosActivosByVentaIds(@Param("ventaIds") List<Integer> ventaIds);
}
