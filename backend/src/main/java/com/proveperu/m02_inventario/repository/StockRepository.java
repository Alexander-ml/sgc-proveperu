package com.proveperu.m02_inventario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proveperu.m02_inventario.entity.Stock;
import com.proveperu.shared.enums.EstadoActivoInactivo;
import com.proveperu.shared.enums.EstadoLogico;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {

    /**
     * Lista el stock de productos activos.
     *
     * Se usa para mostraradoActivoInactivo;
import com.proveperu.shared.enums.EstadoLogico;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {

    /**
     * Lista el stock de productos activos.
     *
     * Se usa para mostrar la tabla principal del módulo inventario.
     *
     * @param estadoLogico estado lógico del producto.
     * @param estadoFisico estado físico del producto.
     * @return lista de stock encontrada.
     */
    List<Stock>
            findAllByProducto_EstadoLogicoAndProducto_EstadoFisicoOrderByProducto_NombreProductoAsc(
                    EstadoLogico estadoLogico,
                    EstadoActivoInactivo estadoFisico
            );

    /**
     * Busca el stock de un producto activo por su identificador.
     *
     * @param idProducto identificador del producto.
     * @param estadoLogico estado lógico del producto.
     * @return stock encontrado.
     */
    Optional<Stock> findByProducto_IdProductoAndProducto_EstadoLogico(
            Integer idProducto,
            EstadoLogico estadoLogico
    );
}
