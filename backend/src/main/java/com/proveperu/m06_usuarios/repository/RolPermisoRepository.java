package com.proveperu.m06_usuarios.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proveperu.m06_usuarios.entity.Rol;
import com.proveperu.m06_usuarios.entity.RolPermiso;
import com.proveperu.m06_usuarios.entity.RolPermisoId;

@Repository
public interface RolPermisoRepository extends JpaRepository<RolPermiso, RolPermisoId> {
     List<RolPermiso> findByRol(Rol rol);

    void deleteByRol(Rol rol);
}

