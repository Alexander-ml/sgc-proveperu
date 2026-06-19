package com.proveperu.m03_compras.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proveperu.m03_compras.entity.Compra;
import com.proveperu.m03_compras.enums.EstadoCompra;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Integer> {
        /**
     * Cuenta las compras según su estado físico.
     *
     * Ejemplo:
     * PENDIENTE, PARCIAL, RECIBIDO o ANULADO.
     */
    long countByEstadoFisico(EstadoCompra estadoFisico);

    /**
     * Lista todas las compras ordenadas desde la más reciente.
     */
    List<Compra> findAllByOrderByFechaHoraCreacionDesc();
}
