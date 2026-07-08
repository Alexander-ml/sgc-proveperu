package com.proveperu.m05_gestion_clientes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proveperu.m05_gestion_clientes.entity.ClienteResumenVenta;

/**
 * Repositorio de consulta para las estadísticas de ventas por cliente.
 *
 * Utiliza la vista vw_resumen_ventas_cliente y no contiene
 * consultas SQL escritas dentro del módulo.
 */
@Repository
public interface ClienteResumenVentaRepository
        extends JpaRepository<ClienteResumenVenta, Integer> {

    long countByNumeroComprasGreaterThanEqual(Long minimoCompras);
}