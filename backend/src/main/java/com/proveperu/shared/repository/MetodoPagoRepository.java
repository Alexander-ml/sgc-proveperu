package com.proveperu.shared.repository;

import com.proveperu.shared.entity.MetodoPago;
import com.proveperu.shared.enums.EstadoActivoInactivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad {@link MetodoPago}.
 *
 * <p>
 * Provee acceso a la tabla {@code metodo_pago} que pertenece
 * al módulo compartido (shared) del sistema.
 * </p>
 *
 * <p>
 * Es consumido por el módulo de Ventas para listar métodos
 * activos y por el módulo de Compras para registrar pagos
 * a proveedores.
 * </p>
 */
@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Integer> {

    /**
     * Recupera todos los métodos de pago cuyo estado activo
     * corresponda al valor indicado.
     *
     * <p>
     * Utilizado en el endpoint {@code GET /ventas/metodos-pago}
     * para retornar únicamente los métodos habilitados.
     * </p>
     *
     * @param estadoLogico estado activo/inactivo a filtrar.
     * @return lista de métodos de pago con el estado indicado.
     */
    List<MetodoPago> findByEstadoFisico(EstadoActivoInactivo estadoLogico);
}