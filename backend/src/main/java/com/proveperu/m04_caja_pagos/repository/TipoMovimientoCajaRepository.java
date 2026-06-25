package com.proveperu.m04_caja_pagos.repository;

import com.proveperu.m04_caja_pagos.entity.TipoMovimientoCaja;
import com.proveperu.m04_caja_pagos.enums.NombreTipoMovimientoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoMovimientoCajaRepository extends JpaRepository<TipoMovimientoCaja, Integer> {
    /**
     * Busca un tipo de movimiento de caja por su nombre.
     *
     * <p>
     * Utilizado al registrar movimientos: se busca INGRESO
     * al confirmar una venta o EGRESO al pagar una compra.
     * </p>
     *
     * @param nombre nombre del tipo de movimiento.
     * @return {@link Optional} con el tipo de movimiento encontrado.
     */
    Optional<TipoMovimientoCaja> findByNombreTipoMovimiento(NombreTipoMovimientoCaja nombre);
}
