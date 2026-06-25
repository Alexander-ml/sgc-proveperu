package com.proveperu.m02_inventario.repository;

import com.proveperu.m02_inventario.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Stock}.
 *
 * <p>
 * Gestiona el stock actual de cada producto. Dado que
 * la tabla {@code stock} usa el {@code id_producto}
 * como clave primaria (relación 1:1 con {@code producto}),
 * las consultas usan ese identificador directamente.
 * </p>
 *
 * <p>
 * Las operaciones de modificación de stock están anotadas
 * con {@code @Modifying} y deben ejecutarse dentro de
 * una transacción activa.
 * </p>
 */
@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {

    /**
     * Busca el stock de un producto por su identificador.
     *
     * @param idProducto identificador del producto.
     * @return {@link Optional} con el stock si existe.
     */
    Optional<Stock> findByIdProducto(Integer idProducto);

    /**
     * Recupera el stock de múltiples productos en una sola
     * consulta para evitar N+1 al crear una venta.
     *
     * @param idsProducto lista de IDs de productos.
     * @return lista de stocks correspondientes.
     */
    @Query("SELECT s FROM Stock s WHERE s.idProducto IN :ids")
    List<Stock> findByIdProductoIn(@Param("ids") List<Integer> idsProducto);

    /**
     * Decrementa el stock de un producto de forma atómica.
     *
     * <p>
     * Usado al confirmar una venta para descontar la cantidad
     * vendida del stock disponible (RF-11). La operación actualiza
     * también la fecha de última actualización.
     * </p>
     *
     * @param idProducto identificador del producto.
     * @param cantidad   cantidad a decrementar (debe ser positiva).
     */
    @Modifying
    @Query("UPDATE Stock s SET s.cantidadActual = s.cantidadActual - :cantidad, " +
            "s.fechaHoraActualizacion = CURRENT_TIMESTAMP " +
            "WHERE s.idProducto = :idProducto")
    void decrementarStock(
            @Param("idProducto") Integer idProducto,
            @Param("cantidad") BigDecimal cantidad
    );
}