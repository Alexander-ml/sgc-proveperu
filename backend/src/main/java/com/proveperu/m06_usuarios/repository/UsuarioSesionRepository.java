package com.proveperu.m06_usuarios.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proveperu.m06_usuarios.entity.Usuario;
import com.proveperu.m06_usuarios.entity.UsuarioSesion;

@Repository
public interface UsuarioSesionRepository extends JpaRepository<UsuarioSesion, Integer> {
    /**
     * Obtiene las sesiones registradas ordenadas desde
     * la más reciente hasta la más antigua.
     */
    List<UsuarioSesion> findAllByOrderByFechaHoraInicioDesc();
     /**
     * Busca la última sesión que todavía no ha sido cerrada
     * para un usuario determinado.
     */
    Optional<UsuarioSesion>
            findFirstByUsuarioAndFechaHoraFinIsNullOrderByFechaHoraInicioDesc(
                    Usuario usuario
            );
}
