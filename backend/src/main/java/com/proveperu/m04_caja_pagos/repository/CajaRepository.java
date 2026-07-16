package com.proveperu.m04_caja_pagos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proveperu.m04_caja_pagos.entity.Caja;
import com.proveperu.m04_caja_pagos.enums.EstadoCaja;

@Repository
public interface CajaRepository extends JpaRepository<Caja, Integer> {

    /**
     * Obtiene las cajas por estado operativo.
     *
     * @param estadoFisico estado de la caja.
     * @return cajas encontradas.
     */
    List<Caja> findByEstadoFisicoOrderByIdCajaAsc(
            EstadoCaja estadoFisico
    );

    /**
     * Obtiene la primera caja abierta disponible.
     *
     * Se usará cuando el frontend no envíe un idCaja específico.
     *
     * @param estadoFisico estado de la caja.
     * @return caja abierta encontrada.
     */
    Optional<Caja> findFirstByEstadoFisicoOrderByIdCajaAsc(
            EstadoCaja estadoFisico
    );
}
