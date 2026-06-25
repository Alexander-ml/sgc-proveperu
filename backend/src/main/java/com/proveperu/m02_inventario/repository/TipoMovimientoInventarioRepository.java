package com.proveperu.m02_inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proveperu.m02_inventario.entity.TipoMovimientoInventario;

import java.util.Optional;

@Repository
public interface TipoMovimientoInventarioRepository extends JpaRepository<TipoMovimientoInventario, Integer> {
    /**
     * Busca un tipo de movimiento por su nombre exacto.
     *
     * <p>
     * Utilizado al crear movimientos de inventario: se busca
     * "EGRESO" al confirmar una venta o "INGRESO" al recepcionar
     * una compra. Evita IDs hardcodeados.
     * </p>
     *
     * @param nombre nombre del tipo de movimiento (INGRESO, EGRESO, etc.).
     * @return {@link Optional} con el tipo de movimiento encontrado.
     */
    Optional<TipoMovimientoInventario> findByNombre(String nombre);
}