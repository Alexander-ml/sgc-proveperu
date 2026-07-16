package com.proveperu.m04_caja_pagos.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proveperu.m04_caja_pagos.entity.CierreCaja;

@Repository
public interface CierreCajaRepository
        extends JpaRepository<CierreCaja, Integer> {

    boolean existsByAperturaCaja_IdAperturaCaja(
            Integer idAperturaCaja
    );

    Optional<CierreCaja> findByAperturaCaja_IdAperturaCaja(
            Integer idAperturaCaja
    );

    @Procedure(procedureName = "sp_cerrar_caja")
    void cerrarCaja(
            @Param("p_id_caja") Integer idCaja,
            @Param("p_id_usuario") Integer idUsuario,
            @Param("p_saldo_real") BigDecimal saldoReal
    );
}