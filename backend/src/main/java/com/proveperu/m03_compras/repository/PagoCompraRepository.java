package com.proveperu.m03_compras.repository;

import com.proveperu.m03_compras.entity.PagoCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoCompraRepository extends JpaRepository<PagoCompra, Integer> {
}
