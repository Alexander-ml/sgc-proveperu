package com.proveperu.m02_inventario.repository;

import com.proveperu.m02_inventario.entity.Producto;
import com.proveperu.shared.enums.EstadoActivoInactivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Producto}.
 *
 * <p>
 * Provee acceso a la tabla {@code producto} del módulo
 * de Inventario. Define consultas de búsqueda usadas
 * tanto en el módulo de Ventas como en el de Compras.
 * </p>
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    /**
     * Busca productos cuyo nombre contenga el texto indicado,
     * ignorando mayúsculas/minúsculas, y filtrando por estado físico.
     *
     * <p>
     * Utilizado en el endpoint {@code GET /ventas/productos?nombre=...}
     * para la búsqueda dinámica de productos al crear una venta.
     * </p>
     *
     * @param nombre      texto parcial a buscar en el nombre del producto.
     * @param estadoFisico estado físico para filtrar (ACTIVO / INACTIVO).
     * @return lista de productos que coinciden con la búsqueda.
     */
    List<Producto> findByNombreProductoContainingIgnoreCaseAndEstadoFisico(
            String nombre,
            EstadoActivoInactivo estadoFisico
    );

    /**
     * Busca un producto por su código único.
     *
     * @param codigoProducto código del producto sin espacios.
     * @return {@link Optional} con el producto si existe.
     */
    Optional<Producto> findByCodigoProducto(String codigoProducto);

    /**
     * Verifica si ya existe un producto con el código proporcionado.
     *
     * @param codigoProducto código a verificar.
     * @return {@code true} si ya existe un producto con ese código.
     */
    boolean existsByCodigoProducto(String codigoProducto);

    /**
     * Recupera una lista de productos por sus IDs con sus stocks
     * en una sola consulta para evitar N+1 queries.
     *
     * <p>
     * Optimización utilizada al crear una venta con múltiples
     * productos: se cargan todos los productos y sus stocks
     * en un único viaje a la base de datos.
     * </p>
     *
     * @param ids lista de identificadores de productos.
     * @return lista de productos cuyos IDs estén en la lista proporcionada.
     */
    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.movimientosInventario " +
            "WHERE p.idProducto IN :ids AND p.estadoFisico = :estado")
    List<Producto> findByIdProductoInAndEstadoFisico(
            @Param("ids") List<Integer> ids,
            @Param("estado") EstadoActivoInactivo estado
    );
}