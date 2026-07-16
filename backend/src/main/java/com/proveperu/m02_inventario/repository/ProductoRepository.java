package com.proveperu.m02_inventario.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proveperu.m02_inventario.entity.Producto;
import com.proveperu.shared.enums.EstadoActivoInactivo;
import com.proveperu.shared.enums.EstadoLogico;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    /**
     * Lista productos activos ordenados desde el más reciente.
     *
     * @param estadoLogico estado lógico del producto.
     * @param estadoFisico estado físico del producto.
     * @return productos encontrados.
     */
    List<Producto> findAllByEstadoLogicoAndEstadoFisicoOrderByIdProductoDesc(
            EstadoLogico estadoLogico,
            EstadoActivoInactivo estadoFisico
    );

    /**
     * Busca un producto activo por su identificador.
     *
     * @param idProducto identificador del producto.
     * @param estadoLogico estado lógico del producto.
     * @return producto encontrado.
     */
    Optional<Producto> findByIdProductoAndEstadoLogico(
            Integer idProducto,
            EstadoLogico estadoLogico
    );

    /**
     * Busca un producto por su código.
     *
     * @param codigoProducto código del producto.
     * @return producto encontrado.
     */
    Optional<Producto> findByCodigoProducto(
            String codigoProducto
    );

    /**
     * Verifica si ya existe un producto con el código indicado.
     *
     * @param codigoProducto código del producto.
     * @return true si existe, false en caso contrario.
     */
    boolean existsByCodigoProducto(
            String codigoProducto
    );
}