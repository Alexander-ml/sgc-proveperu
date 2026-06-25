package com.proveperu.m05_gestion_clientes.repository;

import com.proveperu.m05_gestion_clientes.entity.Cliente;
import com.proveperu.shared.enums.EstadoActivoInactivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad {@link Cliente}.
 *
 * <p>
 * Provee acceso a la tabla {@code cliente} de PostgreSQL
 * mediante Spring Data JPA. Las consultas derivadas son
 * suficientes para los casos de uso actuales.
 * </p>
 *
 * <p>
 * La lógica de negocio nunca reside en esta capa;
 * solo se definen contratos de acceso a datos.
 * </p>
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    /**
     * Recupera todos los clientes cuyo estado físico
     * coincida con el valor proporcionado.
     *
     * <p>
     * Utilizado en el endpoint {@code GET /ventas/clientes}
     * para retornar únicamente los clientes activos.
     * </p>
     *
     * @param estadoFisico estado físico a filtrar (ACTIVO / INACTIVO).
     * @return lista de clientes con el estado indicado.
     */
    List<Cliente> findByEstadoCliente(EstadoActivoInactivo estadoFisico);

    /**
     * Verifica si ya existe un cliente con el DNI proporcionado.
     *
     * <p>
     * Utilizado para validar unicidad antes de registrar
     * un nuevo cliente (RF-142).
     * </p>
     *
     * @param dni número de DNI a verificar.
     * @return {@code true} si ya existe un cliente con ese DNI.
     */
    boolean existsByDni(String dni);

    /**
     * Verifica si ya existe un cliente con el RUC proporcionado.
     *
     * <p>
     * Utilizado para validar unicidad antes de registrar
     * una empresa cliente (RF-142).
     * </p>
     *
     * @param ruc número de RUC a verificar.
     * @return {@code true} si ya existe un cliente con ese RUC.
     */
    boolean existsByRuc(String ruc);
}