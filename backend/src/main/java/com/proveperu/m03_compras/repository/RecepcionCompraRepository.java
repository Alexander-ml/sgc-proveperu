package com.proveperu.m03_compras.repository;

import com.proveperu.m03_compras.entity.RecepcionCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecepcionCompraRepository extends JpaRepository<RecepcionCompra, Integer> {
}
