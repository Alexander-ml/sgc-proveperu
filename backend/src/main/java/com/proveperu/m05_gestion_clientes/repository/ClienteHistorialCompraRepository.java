package com.proveperu.m05_gestion_clientes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proveperu.m05_gestion_clientes.entity.ClienteHistorialCompra;

/**
 * Repository de solo lectura para consultar las ventas
 * pertenecientes al historial de un cliente.
 */
@Repository
public interface ClienteHistorialCompraRepository 
    extends JpaRepository<ClienteHistorialCompra, Integer>{

    /**
     * Obtiene todas las compras de un cliente,
     * ordenadas desde la más reciente.
     *
     * @param idCliente identificador del cliente.
     * @return compras registradas del cliente.
     */
    List<ClienteHistorialCompra>
            findByIdClienteOrderByFechaHoraVentaDesc(
                    Integer idCliente
            );
    
}
