package com.proveperu.m01_ventas.repository;

import com.proveperu.m01_ventas.entity.Comprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de persistencia para la entidad {@link Comprobante}.
 *
 * <p>
 * Este componente centraliza el acceso a los comprobantes emitidos
 * por ventas registradas en el sistema.
 * </p>
 */
@Repository
public interface ComprobanteRepository extends JpaRepository<Comprobante, Integer> {

    /**
     * Busca el comprobante asociado a una venta específica.
     *
     * <p>
     * La búsqueda se realiza a partir del identificador técnico de la venta,
     * retornando un {@link Optional} para representar correctamente la posible
     * ausencia de comprobante formal.
     * </p>
     *
     * @param idVenta identificador de la venta relacionada.
     * @return comprobante asociado a la venta, si existe.
     */
    Optional<Comprobante> findByVentaIdVenta(Integer idVenta);

    /**
     * Recupera los comprobantes asociados a múltiples ventas en una sola consulta.
     *
     * <p>
     * La consulta filtra por la colección de identificadores de venta recibidos
     * y retorna todos los comprobantes encontrados para esos registros.
     * </p>
     *
     * @param ventaIds lista de identificadores de ventas a consultar.
     * @return lista de comprobantes asociados a las ventas indicadas.
     */
    @Query("""
            SELECT c FROM Comprobante c
            WHERE c.venta.idVenta IN :ventaIds
            """)
    List<Comprobante> findByVentaIdIn(@Param("ventaIds") List<Integer> ventaIds);

    /**
     * Verifica si ya existe un comprobante con la serie
     * y correlativo indicados para prevenir duplicados.
     *
     * @param serie       serie del comprobante.
     * @param correlativo correlativo del comprobante.
     * @return {@code true} si ya existe el comprobante.
     */
    boolean existsBySerieAndCorrelativo(String serie, String correlativo);
}
