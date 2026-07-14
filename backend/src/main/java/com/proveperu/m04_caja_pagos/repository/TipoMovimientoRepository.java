package com.proveperu.m04_caja_pagos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proveperu.m04_caja_pagos.entity.TipoMovimientoCaja;
import com.proveperu.m04_caja_pagos.enums.NombreTipoMovimientoCaja;

@Repository
public interface TipoMovimientoRepository extends JpaRepository<TipoMovimientoCaja, Integer> {
    /**
     * Busca un tipo de movimiento por su nombre funcional.
     *
     * Ejemplos:
     * INGRESO
     * EGRESO
     *
     * @param nombreTipoMovimiento nombre del tipo de movimiento.
     * @return tipo de movimiento encontrado.
     */
    Optional<TipoMovimientoCaja> findByNombreTipoMovimiento(
            NombreTipoMovimientoCaja nombreTipoMovimiento
    );
}
