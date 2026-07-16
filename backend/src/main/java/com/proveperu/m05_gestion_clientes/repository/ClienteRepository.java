package com.proveperu.m05_gestion_clientes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proveperu.m05_gestion_clientes.entity.Cliente;
import com.proveperu.m05_gestion_clientes.enums.TipoCliente;
import com.proveperu.shared.enums.EstadoLogico;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
       /**
     * Cuenta clientes por estado lógico.
     */
    long countByEstadoLogico(EstadoLogico estadoLogico);

    /**
     * Cuenta clientes por tipo y estado lógico.
     */
    long countByTipoClienteAndEstadoLogico(
            TipoCliente tipoCliente,
            EstadoLogico estadoLogico
    );

    /**
     * Lista clientes desde el más reciente.
     */
    List<Cliente> findAllByOrderByIdClienteDesc();

    /**
     * Verifica duplicidad por DNI.
     */
    boolean existsByDni(String dni);

    /**
     * Verifica duplicidad por RUC.
     */
    boolean existsByRuc(String ruc);

    /**
     * Busca cliente por DNI.
     */
    Optional<Cliente> findByDni(String dni);

    /**
     * Busca cliente por RUC.
     */
    Optional<Cliente> findByRuc(String ruc);
}
