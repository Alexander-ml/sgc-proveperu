package com.proveperu.m04_caja_pagos.repository;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proveperu.m04_caja_pagos.entity.MovimientoCaja;
import com.proveperu.m04_caja_pagos.enums.EstadoMovimientoCaja;

@Repository
public interface MovimientoCajaRepository
        extends JpaRepository<MovimientoCaja, Integer> {

    /**
     * Lista los movimientos registrados de una caja
     * desde una fecha determinada.
     *
     * Se usará para obtener los movimientos pertenecientes
     * a la apertura activa.
     *
     * @param idCaja identificador de la caja.
     * @param estadoFisico estado físico del movimiento.
     * @param fechaHoraDesde fecha y hora inicial.
     * @return movimientos encontrados.
     */
    List<MovimientoCaja>
            findByCaja_IdCajaAndEstadoFisicoAndFechaHoraMovimientoGreaterThanEqualOrderByFechaHoraMovimientoAsc(
                    Integer idCaja,
                    EstadoMovimientoCaja estadoFisico,
                    LocalDateTime fechaHoraDesde
            );

    /**
     * Lista los movimientos registrados de una caja
     * dentro de un rango de fechas.
     *
     * Puede servir para filtrar por día en la tabla.
     *
     * @param idCaja identificador de la caja.
     * @param estadoFisico estado físico del movimiento.
     * @param fechaHoraInicio fecha inicial.
     * @param fechaHoraFin fecha final.
     * @return movimientos encontrados.
     */
    List<MovimientoCaja>
            findByCaja_IdCajaAndEstadoFisicoAndFechaHoraMovimientoBetweenOrderByFechaHoraMovimientoAsc(
                    Integer idCaja,
                    EstadoMovimientoCaja estadoFisico,
                    LocalDateTime fechaHoraInicio,
                    LocalDateTime fechaHoraFin
            );

    /**
     * Ejecuta el procedimiento almacenado para registrar
     * un egreso manual de caja.
     *
     * No se usa @Query porque la lógica se encuentra
     * centralizada en PostgreSQL.
     *
     * @ se usa @Query porque la lógica se encuentra
     * centralizada enparam idCaja identificador de la caja.
     * @param idUsuario identificador del usuario autenticado.
     * @param idMetodoPago identificador del método de pago.
     * @param monto monto del egreso.
     * @param descripcion descripción del egreso.
     */
    @Procedure(procedureName = "sp_registrar_egreso_caja")
    void registrarEgresoCaja(
            @Param("p_id_caja") Integer idCaja,
            @Param("p_id_usuario") Integer idUsuario,
            @Param("p_id_metodo_pago") Integer idMetodoPago,
            @Param("p_monto") BigDecimal monto,
            @Param("p_descripcion") String descripcion
    );
}