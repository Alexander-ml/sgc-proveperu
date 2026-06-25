package com.proveperu.m06_usuarios.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proveperu.m06_usuarios.entity.Usuario;
import com.proveperu.m06_usuarios.enums.EstadoUsuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    @EntityGraph(attributePaths = {"rol"})
    Optional<Usuario> findByUsuarioLogin(String usuarioLogin);


    long countByEstadoUsuario(EstadoUsuario estadoUsuario);

    
    @EntityGraph(attributePaths = {"rol"})
    List<Usuario> findByNombreCompletoContainingIgnoreCase(String nombreCompleto);

    /**
     * Verifica si ya existe un usuario con el email indicado.
     *
     * <p>
     * Utilizado al registrar nuevos usuarios para
     * prevenir duplicados (RF-168).
     * </p>
     *
     * @param usuarioLogin email a verificar.
     * @return {@code true} si ya existe un usuario con ese login.
     */
    boolean existsByUsuarioLogin(String usuarioLogin);
}
