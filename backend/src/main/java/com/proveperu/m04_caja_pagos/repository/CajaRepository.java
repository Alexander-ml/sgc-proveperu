package com.proveperu.m04_caja_pagos.repository;

import com.proveperu.m04_caja_pagos.entity.Caja;
import com.proveperu.m04_caja_pagos.enums.EstadoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CajaRepository extends JpaRepository<Caja, Integer> {
    /**
     * Busca la primera caja con el estado físico indicado.
     *
     * <p>
     * Utilizado al confirmar una venta para encontrar la
     * caja abierta donde registrar el ingreso (RF-20).
     * Si no existe ninguna caja abierta se lanzará
     * {@code BusinessException} en el servicio.
     * </p>
     *
     * @param estadoFisico estado de la caja a buscar.
     * @return {@link Optional} con la primera caja encontrada.
     */
    Optional<Caja> findFirstByEstadoFisico(EstadoCaja estadoFisico);
}
