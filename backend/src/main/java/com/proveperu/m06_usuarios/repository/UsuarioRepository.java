package com.proveperu.m06_usuarios.repository;

import com.proveperu.m06_usuarios.entity.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    @EntityGraph(attributePaths = {"rol"})
    Optional<Usuario> findByUsuarioLogin(String usuarioLogin);
}
