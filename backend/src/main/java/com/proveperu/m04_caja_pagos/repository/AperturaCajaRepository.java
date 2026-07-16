package com.proveperu.m04_caja_pagos.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proveperu.m04_caja_pagos.entity.AperturaCaja;

@Repository
public interface AperturaCajaRepository
        extends JpaRepository<AperturaCaja, Integer> {

    Optional<AperturaCaja>
            findFirstByCaja_IdCajaAndCierreCajaIsNullOrderByFechaHoraAperturaDesc(
                    Integer idCaja
            );

    List<AperturaCaja> findByCaja_IdCajaOrderByFechaHoraAperturaDesc(
            Integer idCaja
    );

    @Procedure(procedureName = "sp_abrir_caja")
    void abrirCaja(
            @Param("p_id_caja") Integer idCaja,
            @Param("p_id_usuario") Integer idUsuario,
            @Param("p_monto_inicial") BigDecimal montoInicial
    );
}