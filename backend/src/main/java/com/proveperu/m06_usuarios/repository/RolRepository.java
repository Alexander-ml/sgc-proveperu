package com.proveperu.m06_usuarios.repository;

import com.proveperu.m06_usuarios.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
}
