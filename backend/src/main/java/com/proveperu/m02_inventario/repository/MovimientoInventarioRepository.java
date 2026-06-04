package com.proveperu.m02_inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proveperu.m02_inventario.entity.MovimientoInventario;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Integer> {

}